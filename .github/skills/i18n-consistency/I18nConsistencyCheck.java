import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.SequencedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class I18nConsistencyCheck {

    private static final Path EN_US_RELATIVE_PATH = Path.of("app", "src", "main", "resources", "i18n", "messages_en_US.properties");
    private static final Path DE_DE_RELATIVE_PATH = Path.of("app", "src", "main", "resources", "i18n", "messages_de_DE.properties");

    private static final Pattern UNICODE_ESCAPE_PATTERN = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
    private static final int MAX_REPORTED_INVISIBLE_CHARACTERS = 20;

    private static final Pattern LINE_ENDING_PATTERN = Pattern.compile("\r\n|\n|\r");

    private static final Comparator<String> KEY_COMPARATOR =
            Comparator.comparing((String key) -> key.toLowerCase(Locale.ROOT))
                      .thenComparing(Comparator.naturalOrder());

    public static void main(String[] args) {
        try {
            Mode mode = parseMode(args);
            Path repositoryRoot = findRepositoryRoot();

            BundleSource enUsSource = BundleSource.load(repositoryRoot, EN_US_RELATIVE_PATH);
            BundleSource deDeSource = BundleSource.load(repositoryRoot, DE_DE_RELATIVE_PATH);
            List<Finding> encodingFindings = new ArrayList<>();
            encodingFindings.addAll(analyzeEncoding(enUsSource.relativePath(), enUsSource.originalBytes()));
            encodingFindings.addAll(analyzeEncoding(deDeSource.relativePath(), deDeSource.originalBytes()));
            if (hasInvalidUtf8(encodingFindings)) {
                Report report = new Report(encodingFindings);
                report.print(mode);
                System.exit(report.exitCode());
            }

            ParseResult enUsParseResult = enUsSource.parse();
            ParseResult deDeParseResult = deDeSource.parse();
            Report report = analyze(encodingFindings, enUsParseResult, deDeParseResult);

            if (mode == Mode.FIX) {
                if (enUsParseResult.hasFailures() || deDeParseResult.hasFailures()
                        || report.hasFailure("empty bundle")) {
                    report.print(mode);
                    System.exit(report.exitCode());
                }

                PreparedFix enUsFix = prepareFix(enUsSource, enUsParseResult.bundle());
                PreparedFix deDeFix = prepareFix(deDeSource, deDeParseResult.bundle());
                commitFixes(List.of(enUsFix, deDeFix));

                System.out.println("i18n consistency auto-fix");
                System.out.println("- " + enUsFix.result().message());
                System.out.println("- " + deDeFix.result().message());
                System.out.println();

                enUsSource = BundleSource.load(repositoryRoot, EN_US_RELATIVE_PATH);
                deDeSource = BundleSource.load(repositoryRoot, DE_DE_RELATIVE_PATH);
                encodingFindings = new ArrayList<>();
                encodingFindings.addAll(analyzeEncoding(enUsSource.relativePath(), enUsSource.originalBytes()));
                encodingFindings.addAll(analyzeEncoding(deDeSource.relativePath(), deDeSource.originalBytes()));

                enUsParseResult = enUsSource.parse();
                deDeParseResult = deDeSource.parse();
                report = analyze(encodingFindings, enUsParseResult, deDeParseResult);
            }

            report.print(mode);
            System.exit(report.exitCode());
        } catch (UsageException exception) {
            System.err.println("FAIL usage: " + exception.getMessage());
            System.err.println("Usage: java .github/skills/i18n-consistency/I18nConsistencyCheck.java [report|fix]");
            System.exit(3);
        } catch (IOException exception) {
            System.err.println("FAIL io: " + exception.getMessage());
            for (Throwable suppressed : exception.getSuppressed()) {
                System.err.println("FAIL io recovery: " + suppressed.getMessage());
            }
            System.exit(4);
        } catch (RuntimeException exception) {
            System.err.println("FAIL unexpected: " + exception.getMessage());
            System.exit(4);
        }
    }

    private static Mode parseMode(String[] args) {
        if (args.length == 0) {
            return Mode.REPORT;
        }
        if (args.length == 1 && Objects.equals(args[0], "report")) {
            return Mode.REPORT;
        }
        if (args.length == 1 && Objects.equals(args[0], "fix")) {
            return Mode.FIX;
        }
        throw new UsageException("expected no argument, 'report', or 'fix'");
    }

    private static Path findRepositoryRoot() throws IOException {
        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            if (Files.isRegularFile(current.resolve(EN_US_RELATIVE_PATH))
                    && Files.isRegularFile(current.resolve(DE_DE_RELATIVE_PATH))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IOException("could not locate repository root containing production i18n bundles");
    }

    private static PreparedFix prepareFix(BundleSource source, Bundle bundle) throws IOException {
        Path relativePath = source.relativePath();
        Path path = source.path();
        byte[] originalBytes = source.originalBytes();
        String originalContent = decodeUtf8(stripUtf8Bom(originalBytes));
        SanitizedEntries sanitized = sanitizeEntries(bundle);
        String fixedContent = formatEntries(sanitized.entries(), lineSeparatorOf(originalContent));
        byte[] fixedBytes = encodeUtf8(fixedContent);
        validateRenderedBundle(relativePath, sanitized.entries(), fixedBytes);

        boolean changed = !Arrays.equals(originalBytes, fixedBytes);
        String action = changed
                ? "updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes"
                : "no changes";
        String noun = sanitized.removedInvisibleCharacters() == 1 ? "character" : "characters";
        FixResult result = new FixResult(
                relativePath + ": " + action + "; removed " + sanitized.removedInvisibleCharacters()
                        + " invisible " + noun
        );
        return new PreparedFix(path, originalBytes, fixedBytes, changed, result);
    }

    private static SanitizedEntries sanitizeEntries(Bundle bundle) throws IOException {
        List<Entry> entries = new ArrayList<>();
        LinkedHashSet<String> sanitizedKeys = new LinkedHashSet<>();
        int removedInvisibleCharacters = 0;
        int marker = sanitizationMarker(bundle);

        for (Entry entry : bundle.entries().values()) {
            String markedRawKey = markInvisibleCharacters(entry.rawKey(), marker);
            String markedRawValue = markInvisibleCharacters(entry.rawValue(), marker);
            Entry sanitizedEntry = decodeSanitizedEntry(entry, markedRawKey, markedRawValue, marker);
            if (!sanitizedKeys.add(sanitizedEntry.key())) {
                throw new IOException(fileLabel(bundle.relativePath())
                        + ": removing invisible characters creates duplicate key " + sanitizedEntry.key());
            }
            entries.add(sanitizedEntry);
            removedInvisibleCharacters += countInvisibleCharacters(entry.key())
                    - countInvisibleCharacters(sanitizedEntry.key());
            removedInvisibleCharacters += countInvisibleCharacters(entry.value())
                    - countInvisibleCharacters(sanitizedEntry.value());
        }
        return new SanitizedEntries(entries, removedInvisibleCharacters);
    }

    private static int sanitizationMarker(Bundle bundle) throws IOException {
        LinkedHashSet<Integer> usedCodePoints = new LinkedHashSet<>();
        for (Entry entry : bundle.entries().values()) {
            entry.rawKey().codePoints().forEach(usedCodePoints::add);
            entry.rawValue().codePoints().forEach(usedCodePoints::add);
            entry.key().codePoints().forEach(usedCodePoints::add);
            entry.value().codePoints().forEach(usedCodePoints::add);
        }
        int marker = unusedCodePoint(usedCodePoints, 0xE000, 0xF8FF);
        if (marker < 0) {
            marker = unusedCodePoint(usedCodePoints, 0xF0000, 0xFFFFD);
        }
        if (marker < 0) {
            marker = unusedCodePoint(usedCodePoints, 0x100000, 0x10FFFD);
        }
        if (marker < 0) {
            throw new IOException(fileLabel(bundle.relativePath()) + ": no sanitization marker is available");
        }
        return marker;
    }

    private static int unusedCodePoint(Collection<Integer> usedCodePoints, int start, int end) {
        for (int codePoint = start; codePoint <= end; codePoint++) {
            if (!usedCodePoints.contains(codePoint)) {
                return codePoint;
            }
        }
        return -1;
    }

    private static String markInvisibleCharacters(String source, int marker) {
        StringBuilder builder = new StringBuilder(source.length());
        for (int index = 0; index < source.length(); ) {
            int codePoint = source.codePointAt(index);
            if ((codePoint == '\n') || (codePoint == '\r') || !isInvisibleCharacter(codePoint)) {
                builder.appendCodePoint(codePoint);
            } else {
                builder.appendCodePoint(marker);
            }
            index += Character.charCount(codePoint);
        }
        return builder.toString();
    }

    private static int countInvisibleCharacters(String text) {
        int count = 0;
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            if (isInvisibleCharacter(codePoint)) {
                count++;
            }
            index += Character.charCount(codePoint);
        }
        return count;
    }

    private static Entry decodeSanitizedEntry(
            Entry sourceEntry,
            String markedRawKey,
            String markedRawValue,
            int marker
    ) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(markedRawKey + "=" + markedRawValue));
        if (properties.size() != 1) {
            throw new IOException("could not decode sanitized entry at line " + sourceEntry.lineNumber());
        }
        var decodedEntry = properties.entrySet().iterator().next();
        String key = removeCodePoint((String) decodedEntry.getKey(), marker);
        String value = removeCodePoint((String) decodedEntry.getValue(), marker);
        return new Entry(
                sourceEntry.lineNumber(),
                markedRawKey,
                markedRawValue,
                key,
                value,
                sourceEntry.rawSource()
        );
    }

    private static String removeCodePoint(String text, int removedCodePoint) {
        StringBuilder builder = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            if (codePoint != removedCodePoint) {
                builder.appendCodePoint(codePoint);
            }
            index += Character.charCount(codePoint);
        }
        return builder.toString();
    }

    private static byte[] encodeUtf8(String content) throws CharacterCodingException {
        ByteBuffer encoded = StandardCharsets.UTF_8.newEncoder()
                                                   .onMalformedInput(CodingErrorAction.REPORT)
                                                   .onUnmappableCharacter(CodingErrorAction.REPORT)
                                                   .encode(CharBuffer.wrap(content));
        byte[] bytes = new byte[encoded.remaining()];
        encoded.get(bytes);
        return bytes;
    }

    private static void validateRenderedBundle(Path relativePath, List<Entry> expectedEntries, byte[] fixedBytes)
            throws IOException {
        String content = decodeUtf8(fixedBytes);
        Finding invisibleFinding = analyzeInvisibleCharacters(fileLabel(relativePath), content);
        if (invisibleFinding.severity() == Severity.FAIL) {
            throw new IOException(relativePath + ": rendered output contains invisible characters");
        }

        ParseResult rendered = Bundle.parse(relativePath, content);
        if (rendered.hasFailures()) {
            throw new IOException(relativePath + ": rendered output does not parse cleanly");
        }
        SequencedMap<String, Entry> actualEntries = rendered.bundle().entries();
        if (actualEntries.size() != expectedEntries.size()) {
            throw new IOException(relativePath + ": rendered output changed the entry count");
        }
        for (Entry expected : expectedEntries) {
            Entry actual = actualEntries.get(expected.key());
            if ((actual == null) || !Objects.equals(actual.value(), expected.value())) {
                throw new IOException(relativePath + ": rendered output changed key or value semantics for " + expected.key());
            }
        }
    }

    private static void commitFixes(List<PreparedFix> fixes) throws IOException {
        Path[] temporaryFiles = new Path[fixes.size()];
        IOException failure = null;
        int lastReplacementAttempt = -1;
        try {
            for (int index = 0; index < fixes.size(); index++) {
                PreparedFix fix = fixes.get(index);
                if (!fix.changed()) {
                    continue;
                }
                Path parent = fix.path().getParent();
                temporaryFiles[index] = Files.createTempFile(parent, fix.path().getFileName().toString(), ".tmp");
                Files.write(temporaryFiles[index], fix.fixedBytes());
            }
            for (int index = 0; index < fixes.size(); index++) {
                if (temporaryFiles[index] == null) {
                    continue;
                }
                lastReplacementAttempt = index;
                replaceFile(temporaryFiles[index], fixes.get(index).path());
                temporaryFiles[index] = null;
            }
        } catch (IOException exception) {
            failure = exception;
            for (int index = 0; index <= lastReplacementAttempt; index++) {
                PreparedFix fix = fixes.get(index);
                if (!fix.changed()) {
                    continue;
                }
                try {
                    restoreFile(fix.path(), fix.originalBytes());
                } catch (IOException restoreException) {
                    failure.addSuppressed(restoreException);
                }
            }
        } finally {
            for (Path temporaryFile : temporaryFiles) {
                if (temporaryFile == null) {
                    continue;
                }
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException cleanupException) {
                    if (failure == null) {
                        failure = cleanupException;
                    } else {
                        failure.addSuppressed(cleanupException);
                    }
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    private static void replaceFile(Path source, Path destination) throws IOException {
        try {
            Files.move(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void restoreFile(Path destination, byte[] originalBytes) throws IOException {
        Path temporaryFile = Files.createTempFile(
                destination.getParent(), destination.getFileName().toString(), ".restore.tmp"
        );
        IOException failure = null;
        try {
            Files.write(temporaryFile, originalBytes);
            replaceFile(temporaryFile, destination);
        } catch (IOException exception) {
            failure = exception;
            throw exception;
        } finally {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                if (failure == null) {
                    throw cleanupException;
                }
                failure.addSuppressed(cleanupException);
            }
        }
    }

    private static byte[] stripUtf8Bom(byte[] bytes) {
        boolean hasBom = (bytes.length >= 3)
                && (bytes[0] == (byte) 0xEF) && (bytes[1] == (byte) 0xBB) && (bytes[2] == (byte) 0xBF);
        return hasBom ? Arrays.copyOfRange(bytes, 3, bytes.length) : bytes;
    }

    private static String decodeUtf8(byte[] bytes) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder()
                                     .onMalformedInput(CodingErrorAction.REPORT)
                                     .onUnmappableCharacter(CodingErrorAction.REPORT)
                                     .decode(ByteBuffer.wrap(bytes))
                                     .toString();
    }

    private static String localeFrom(Path relativePath) {
        String fileName = relativePath.getFileName().toString();
        int start = fileName.indexOf('_') + 1;
        int end = fileName.indexOf(".properties");
        return fileName.substring(start, end);
    }

    private static String fileLabel(Path relativePath) {
        return relativePath.getFileName().toString() + " (" + localeFrom(relativePath) + ")";
    }

    private static String lineSeparatorOf(String content) {
        return content.contains("\r\n") ? "\r\n" : "\n";
    }

    private static String formatEntries(Collection<Entry> entries, String lineSeparator) {
        List<RenderedEntry> sortedEntries = entries.stream()
                                                   .map(I18nConsistencyCheck::renderEntry)
                                                   .sorted(Comparator.comparing(RenderedEntry::decodedKey, KEY_COMPARATOR))
                                                   .toList();
        int maxKeyLength = sortedEntries.stream()
                                        .mapToInt(entry -> entry.key().length())
                                        .max()
                                        .orElse(0);

        String content = sortedEntries.stream()
                                      .map(entry -> formatEntry(entry, maxKeyLength))
                                      .collect(Collectors.joining(lineSeparator));
        return content + lineSeparator;
    }

    private static String formatEntry(RenderedEntry entry, int maxKeyLength) {
        String alignedKey = entry.key() + " ".repeat(maxKeyLength - entry.key().length() + 1) + "=";
        return entry.value().isEmpty() ? alignedKey : alignedKey + " " + entry.value();
    }

    private static RenderedEntry renderEntry(Entry entry) {
        return new RenderedEntry(entry.key(), escapePropertyText(entry.key(), true), escapePropertyText(entry.value(), false));
    }

    private static String escapePropertyText(String text, boolean key) {
        StringBuilder builder = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); ) {
            int codePoint = text.codePointAt(index);
            int charCount = Character.charCount(codePoint);
            boolean unpairedSurrogate = (charCount == 1) && Character.isSurrogate(text.charAt(index));
            if (unpairedSurrogate || isInvisibleCharacter(codePoint)) {
                appendEscapedCodePoint(builder, codePoint);
            } else if (codePoint == '\\') {
                builder.append("\\\\");
            } else if (key && ((codePoint == ' ') || (codePoint == '=') || (codePoint == ':')
                    || (codePoint == '#') || (codePoint == '!'))) {
                builder.append('\\').appendCodePoint(codePoint);
            } else if (!key && (codePoint == ' ') && (index == 0)) {
                builder.append("\\ ");
            } else {
                builder.appendCodePoint(codePoint);
            }
            index += charCount;
        }
        return builder.toString();
    }

    private static void appendEscapedCodePoint(StringBuilder builder, int codePoint) {
        switch (codePoint) {
            case '\t' -> builder.append("\\t");
            case '\n' -> builder.append("\\n");
            case '\r' -> builder.append("\\r");
            case '\f' -> builder.append("\\f");
            default -> {
                char[] characters = Character.toChars(codePoint);
                for (char character : characters) {
                    builder.append("\\u%04X".formatted((int) character));
                }
            }
        }
    }

    private static Report analyze(
            List<Finding> encodingFindings,
            ParseResult enUsParseResult,
            ParseResult deDeParseResult
    ) {
        List<Finding> findings = new ArrayList<>(encodingFindings);
        findings.addAll(enUsParseResult.findings());
        findings.addAll(deDeParseResult.findings());

        Bundle enUs = enUsParseResult.bundle();
        Bundle deDe = deDeParseResult.bundle();

        findings.add(analyzeEmptyBundle(enUs));
        findings.add(analyzeEmptyBundle(deDe));
        findings.addAll(analyzeKeyParity(enUs, deDe));
        findings.addAll(analyzeOrdering(enUs));
        findings.addAll(analyzeOrdering(deDe));
        findings.addAll(analyzeAlignment(enUs));
        findings.addAll(analyzeAlignment(deDe));
        findings.add(analyzeTrailingWhitespace(enUs));
        findings.add(analyzeTrailingWhitespace(deDe));
        findings.addAll(analyzePlaceholderCount(enUs, deDe));
        findings.addAll(analyzeUnicodeEscapes(enUs));
        findings.addAll(analyzeUnicodeEscapes(deDe));
        findings.add(analyzeUnicodeEscapesInKeys(enUs));
        findings.add(analyzeUnicodeEscapesInKeys(deDe));

        return new Report(findings);
    }

    private static Finding analyzeEmptyBundle(Bundle bundle) {
        if (bundle.entries().isEmpty()) {
            return Finding.fail("empty bundle", bundle.fileName() + " contains no semantic entries");
        }
        return Finding.pass("empty bundle", bundle.fileName() + " contains semantic entries");
    }

    private static List<Finding> analyzeKeyParity(Bundle enUs, Bundle deDe) {
        List<Finding> findings = new ArrayList<>();
        List<String> missingInDe = missingKeys(enUs, deDe);
        List<String> missingInEn = missingKeys(deDe, enUs);

        if (missingInDe.isEmpty() && missingInEn.isEmpty()) {
            findings.add(Finding.pass("key parity", "production bundles contain the same keys (.url keys exempt)"));
            return findings;
        }

        if (!missingInDe.isEmpty()) {
            findings.add(Finding.fail(
                    "key parity",
                    deDe.fileName() + " is missing keys present in " + enUs.fileName() + ": " + String.join(", ", missingInDe)
            ));
        }
        if (!missingInEn.isEmpty()) {
            findings.add(Finding.fail(
                    "key parity",
                    enUs.fileName() + " is missing keys present in " + deDe.fileName() + ": " + String.join(", ", missingInEn)
            ));
        }
        return findings;
    }

    private static List<Finding> analyzeEncoding(Path relativePath, byte[] bytes) {
        String fileName = fileLabel(relativePath);
        List<Finding> findings = new ArrayList<>();

        boolean hasBom = (bytes.length >= 3)
                && (bytes[0] == (byte) 0xEF) && (bytes[1] == (byte) 0xBB) && (bytes[2] == (byte) 0xBF);
        if (hasBom) {
            findings.add(Finding.fail("UTF-8 BOM", fileName + " starts with a UTF-8 byte order mark (EF BB BF); save it as UTF-8 without BOM"));
        } else {
            findings.add(Finding.pass("UTF-8 BOM", fileName + " has no UTF-8 byte order mark"));
        }

        String content;
        try {
            content = decodeUtf8(stripUtf8Bom(bytes));
        } catch (CharacterCodingException exception) {
            findings.add(Finding.fail("UTF-8 encoding", fileName + " is not valid UTF-8: " + exception.getMessage()));
            return findings;
        }
        findings.add(Finding.pass("UTF-8 encoding", fileName + " is valid UTF-8"));

        findings.add(analyzeInvisibleCharacters(fileName, content));
        findings.addAll(analyzeLineEndings(fileName, content));

        return findings;
    }

    private static boolean hasInvalidUtf8(List<Finding> findings) {
        return findings.stream().anyMatch(finding -> (finding.severity() == Severity.FAIL)
                && Objects.equals(finding.rule(), "UTF-8 encoding"));
    }

    private static List<Finding> analyzeLineEndings(String fileName, String content) {
        List<Finding> findings = new ArrayList<>();

        LinkedHashSet<String> styles = new LinkedHashSet<>();
        Matcher matcher = LINE_ENDING_PATTERN.matcher(content);
        while (matcher.find()) {
            styles.add(lineEndingStyleName(matcher.group()));
        }
        String consistentStyle = ((styles.size() == 1) && !styles.contains("CR"))
                ? styles.iterator().next()
                : null;

        if (styles.contains("CR")) {
            findings.add(Finding.fail("line ending consistency", fileName + " uses unsupported CR line endings; use LF or CRLF"));
        } else if (styles.size() > 1) {
            findings.add(Finding.fail("line ending consistency", fileName + " mixes line ending styles: " + String.join(", ", styles)));
        } else if (styles.isEmpty()) {
            findings.add(Finding.fail("line ending consistency", fileName + " has no line endings"));
        } else {
            findings.add(Finding.pass("line ending consistency", fileName + " consistently uses " + consistentStyle + " line endings"));
        }

        findings.add(analyzeTrailingNewline(fileName, content, consistentStyle));
        return findings;
    }

    private static String lineEndingStyleName(String separator) {
        return switch (separator) {
            case "\r\n" -> "CRLF";
            case "\n" -> "LF";
            default -> "CR";
        };
    }

    private static Finding analyzeTrailingNewline(String fileName, String content, String consistentStyle) {
        if (consistentStyle == null) {
            return Finding.fail("trailing newline", fileName
                    + ": cannot verify the trailing line break because line endings are inconsistent, missing, or unsupported");
        }
        String separator = Objects.equals(consistentStyle, "CRLF") ? "\r\n" : "\n";
        if (!content.endsWith(separator)) {
            return Finding.fail("trailing newline", fileName + " does not end with a trailing line break");
        }
        if (content.substring(0, content.length() - separator.length()).endsWith(separator)) {
            return Finding.fail("trailing newline", fileName + " has extra blank lines at the end of the file");
        }
        return Finding.pass("trailing newline", fileName + " ends with exactly one trailing line break");
    }

    private static Finding analyzeInvisibleCharacters(String fileName, String content) {
        List<String> occurrences = new ArrayList<>();
        int occurrenceCount = 0;
        int line = 1;
        int column = 1;
        for (int index = 0; index < content.length(); ) {
            int codePoint = content.codePointAt(index);
            int charCount = Character.charCount(codePoint);
            if (codePoint == '\r') {
                index += charCount;
                if ((index < content.length()) && (content.charAt(index) == '\n')) {
                    index++;
                }
                line++;
                column = 1;
                continue;
            }
            if (codePoint == '\n') {
                index += charCount;
                line++;
                column = 1;
                continue;
            }
            if (isInvisibleCharacter(codePoint)) {
                occurrenceCount++;
                if (occurrences.size() < MAX_REPORTED_INVISIBLE_CHARACTERS) {
                    occurrences.add("line " + line + " column " + column + " (U+%04X)".formatted(codePoint));
                }
            }
            index += charCount;
            column++;
        }

        if (occurrences.isEmpty()) {
            return Finding.pass("invisible characters", fileName + " contains no invisible characters other than regular spaces");
        }
        int omittedCount = occurrenceCount - occurrences.size();
        String omitted = (omittedCount == 0) ? "" : ", ... and " + omittedCount + " more";
        String noun = (occurrenceCount == 1) ? "character" : "characters";
        return Finding.fail("invisible characters", fileName + " contains " + occurrenceCount + " invisible " + noun
                + ": " + String.join(", ", occurrences) + omitted);
    }

    // Only the regular space (U+0020) is treated as visible whitespace; \n/\r are handled as line boundaries above.
    private static boolean isInvisibleCharacter(int codePoint) {
        if (codePoint == ' ') {
            return false;
        }
        int type = Character.getType(codePoint);
        return (codePoint == '\uFEFF')
                || (type == Character.CONTROL)
                || (type == Character.FORMAT)
                || (type == Character.SPACE_SEPARATOR)
                || (type == Character.LINE_SEPARATOR)
                || (type == Character.PARAGRAPH_SEPARATOR);
    }

    private static List<String> missingKeys(Bundle source, Bundle target) {
        return source.entries().keySet().stream()
                     .filter(key -> !isUrlKey(key))
                     .filter(key -> !target.entries().containsKey(key))
                     .sorted(KEY_COMPARATOR)
                     .toList();
    }

    private static boolean isUrlKey(String key) {
        return key.endsWith(".url");
    }

    private static List<Finding> analyzeOrdering(Bundle bundle) {
        List<Finding> findings = new ArrayList<>();
        List<Entry> entries = List.copyOf(bundle.entries().values());
        List<String> outOfOrder = new ArrayList<>();

        for (int index = 1; index < entries.size(); index++) {
            Entry previous = entries.get(index - 1);
            Entry current = entries.get(index);
            if (KEY_COMPARATOR.compare(previous.key(), current.key()) > 0) {
                outOfOrder.add("line " + current.lineNumber() + " key " + current.key()
                        + " should sort before previous key " + previous.key());
            }
        }

        if (outOfOrder.isEmpty()) {
            findings.add(Finding.pass("alphabetical ordering", bundle.fileName() + " is sorted by key"));
        } else {
            findings.add(Finding.warn("alphabetical ordering", bundle.fileName() + ": " + String.join("; ", outOfOrder)));
        }
        return findings;
    }

    private static List<Finding> analyzeAlignment(Bundle bundle) {
        List<Entry> entries = List.copyOf(bundle.entries().values());
        int maxKeyLength = entries.stream()
                                  .map(I18nConsistencyCheck::renderEntry)
                                  .mapToInt(entry -> entry.key().length())
                                  .max()
                                  .orElse(0);
        List<String> misaligned = new ArrayList<>();

        for (Entry entry : entries) {
            String expected = formatEntry(renderEntry(entry), maxKeyLength);
            if (!Objects.equals(entry.rawSource(), expected)) {
                misaligned.add("line " + entry.lineNumber() + " key " + entry.key());
            }
        }

        if (misaligned.isEmpty()) {
            return List.of(Finding.pass("= alignment", bundle.fileName() + " aligns the '=' column"));
        }
        return List.of(Finding.warn(
                "= alignment",
                bundle.fileName() + " has a misaligned '=' column or spacing for keys: " + String.join(", ", misaligned)
        ));
    }

    private static List<Finding> analyzePlaceholderCount(Bundle enUs, Bundle deDe) {
        List<Finding> mismatches = enUs.entries().keySet().stream()
                                       .filter(key -> !isUrlKey(key))
                                       .filter(key -> deDe.entries().containsKey(key))
                                       .sorted(KEY_COMPARATOR)
                                       .flatMap(key -> {
                                           long enUsCount = percentCount(enUs.entries().get(key).value());
                                           long deDeCount = percentCount(deDe.entries().get(key).value());
                                           if (enUsCount == deDeCount) {
                                               return Stream.empty();
                                           }
                                           return Stream.of(Finding.fail(
                                                   "placeholder count",
                                                   "key " + key + " has " + enUsCount + " '%' in en_US but " + deDeCount + " '%' in de_DE"
                                           ));
                                       })
                                       .toList();

        if (mismatches.isEmpty()) {
            return List.of(Finding.pass("placeholder count", "shared keys use the same number of '%' characters (.url keys exempt)"));
        }
        return mismatches;
    }

    private static Finding analyzeTrailingWhitespace(Bundle bundle) {
        List<String> affectedEntries = bundle.entries().values().stream()
                                             .filter(entry -> hasTrailingWhitespace(entry.value()))
                                             .map(entry -> entry.key() + " (line " + entry.lineNumber() + ")")
                                             .toList();
        if (affectedEntries.isEmpty()) {
            return Finding.pass(
                    "trailing whitespace",
                    bundle.fileName() + " has no decoded trailing whitespace in values"
            );
        }
        return Finding.warn(
                "trailing whitespace",
                bundle.fileName() + " has decoded trailing whitespace for keys: " + String.join(", ", affectedEntries)
        );
    }

    private static boolean hasTrailingWhitespace(String value) {
        if (value.isEmpty()) {
            return false;
        }
        int codePoint = value.codePointBefore(value.length());
        return Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint);
    }

    private static long percentCount(String value) {
        return value.chars().filter(character -> character == '%').count();
    }

    private static List<Finding> analyzeUnicodeEscapes(Bundle bundle) {
        List<String> keysWithEscapes = bundle.entries().values().stream()
                                             .filter(entry -> UNICODE_ESCAPE_PATTERN.matcher(entry.rawValue()).find())
                                             .map(Entry::key)
                                             .sorted(KEY_COMPARATOR)
                                             .toList();

        if (keysWithEscapes.isEmpty()) {
            return List.of(Finding.pass("Unicode escapes", bundle.fileName() + " stores localized characters directly"));
        }
        return List.of(Finding.warn(
                "Unicode escapes",
                bundle.fileName() + " contains \\uXXXX escapes in values for keys: " + String.join(", ", keysWithEscapes)
        ));
    }

    private static Finding analyzeUnicodeEscapesInKeys(Bundle bundle) {
        List<String> affectedEntries = bundle.entries().values().stream()
                                             .filter(entry -> containsValidUnicodeEscape(entry.rawKey()))
                                             .sorted(Comparator.comparing(Entry::key, KEY_COMPARATOR))
                                             .map(entry -> entry.key() + " (line " + entry.lineNumber() + ")")
                                             .toList();
        if (affectedEntries.isEmpty()) {
            return Finding.pass(
                    "Unicode escapes in keys",
                    bundle.fileName() + " contains no \\uXXXX escapes in keys"
            );
        }
        return Finding.warn(
                "Unicode escapes in keys",
                bundle.fileName() + " contains \\uXXXX escapes in keys: " + String.join(", ", affectedEntries)
        );
    }

    private static boolean containsValidUnicodeEscape(String rawText) {
        Matcher matcher = UNICODE_ESCAPE_PATTERN.matcher(rawText);
        for (int index = 0; index < rawText.length(); ) {
            if (rawText.charAt(index) != '\\') {
                index++;
                continue;
            }
            matcher.region(index, rawText.length());
            if (matcher.lookingAt()) {
                return true;
            }
            index += 2;
        }
        return false;
    }

    private enum Mode {
        REPORT,
        FIX
    }

    private enum Severity {
        PASS(0),
        WARN(1),
        FAIL(2);

        private final int exitCode;

        Severity(int exitCode) {
            this.exitCode = exitCode;
        }
    }

    private static final class UsageException extends RuntimeException {

        private UsageException(String message) {
            super(message);
        }

    }

    private record Entry(
            int lineNumber,
            String rawKey,
            String rawValue,
            String key,
            String value,
            String rawSource
    ) {
    }

    private record Finding(Severity severity, String rule, String message) {

        private static Finding pass(String rule, String message) {
            return new Finding(Severity.PASS, rule, message);
        }

        private static Finding warn(String rule, String message) {
            return new Finding(Severity.WARN, rule, message);
        }

        private static Finding fail(String rule, String message) {
            return new Finding(Severity.FAIL, rule, message);
        }

    }

    private record FixResult(String message) {
    }

    private record PreparedFix(
            Path path,
            byte[] originalBytes,
            byte[] fixedBytes,
            boolean changed,
            FixResult result
    ) {
    }

    private record SanitizedEntries(List<Entry> entries, int removedInvisibleCharacters) {

        private SanitizedEntries {
            entries = List.copyOf(entries);
        }

    }

    private record RenderedEntry(String decodedKey, String key, String value) {
    }

    private record BundleSource(Path relativePath, Path path, byte[] originalBytes) {

        private static BundleSource load(Path repositoryRoot, Path relativePath) throws IOException {
            Path path = repositoryRoot.resolve(relativePath);
            return new BundleSource(relativePath, path, Files.readAllBytes(path));
        }

        private ParseResult parse() throws CharacterCodingException {
            String content = decodeUtf8(stripUtf8Bom(originalBytes));
            return Bundle.parse(relativePath, content);
        }

    }

    private record ParseResult(
            Bundle bundle,
            List<Finding> findings,
            boolean requiresCanonicalRenderer
    ) {

        private ParseResult {
            findings = List.copyOf(findings);
        }

        private boolean hasFailures() {
            return findings.stream().anyMatch(finding -> finding.severity() == Severity.FAIL);
        }

    }

    private record Bundle(Path relativePath, SequencedMap<String, Entry> entries) {

        private static ParseResult parse(Path relativePath, String content) {
            SequencedMap<String, Entry> entries = new LinkedHashMap<>();
            List<Finding> findings = new ArrayList<>();
            List<PhysicalLine> lines = physicalLines(content);
            boolean requiresCanonicalRenderer = false;

            for (int index = 0; index < lines.size(); index++) {
                PhysicalLine firstLine = lines.get(index);
                if (isBlankOrComment(firstLine.text())) {
                    continue;
                }

                StringBuilder sourceBuilder = new StringBuilder(firstLine.text());
                boolean continued = false;
                PhysicalLine currentLine = firstLine;
                while (hasContinuation(currentLine.text())
                        && !currentLine.separator().isEmpty()
                        && (index + 1 < lines.size())) {
                    continued = true;
                    sourceBuilder.append(currentLine.separator());
                    currentLine = lines.get(++index);
                    sourceBuilder.append(currentLine.text());
                }
                String rawSource = sourceBuilder.toString();

                int malformedEscapeIndex = malformedUnicodeEscapeIndex(rawSource);
                if (malformedEscapeIndex >= 0) {
                    int lineNumber = sourceLineNumber(firstLine.lineNumber(), rawSource, malformedEscapeIndex);
                    findings.add(Finding.fail(
                            "properties syntax",
                            fileLabel(relativePath) + " has malformed Unicode escape at line " + lineNumber
                    ));
                    continue;
                }

                Properties properties = new Properties();
                try {
                    properties.load(new StringReader(rawSource));
                } catch (IllegalArgumentException | IOException exception) {
                    findings.add(Finding.fail(
                            "properties syntax",
                            fileLabel(relativePath) + " has malformed properties syntax at line " + firstLine.lineNumber()
                    ));
                    continue;
                }
                if (properties.size() != 1) {
                    findings.add(Finding.fail(
                            "properties syntax",
                            fileLabel(relativePath) + " has an invalid logical entry at line " + firstLine.lineNumber()
                    ));
                    continue;
                }

                var decodedEntry = properties.entrySet().iterator().next();
                String key = (String) decodedEntry.getKey();
                String value = (String) decodedEntry.getValue();
                RawRegions rawRegions = rawRegions(rawSource);
                Entry entry = new Entry(
                        firstLine.lineNumber(),
                        rawRegions.key(),
                        rawRegions.value(),
                        key,
                        value,
                        rawSource
                );
                Entry previous = entries.putIfAbsent(key, entry);
                if (previous != null) {
                    findings.add(Finding.fail(
                            "duplicate keys",
                            fileLabel(relativePath) + " has duplicate decoded key " + key
                                    + " at lines " + previous.lineNumber() + " and " + entry.lineNumber()
                    ));
                }
                requiresCanonicalRenderer |= continued || rawRegions.requiresCanonicalRenderer();
            }
            return new ParseResult(new Bundle(relativePath, entries), findings, requiresCanonicalRenderer);
        }

        private static List<PhysicalLine> physicalLines(String content) {
            List<PhysicalLine> lines = new ArrayList<>();
            Matcher matcher = LINE_ENDING_PATTERN.matcher(content);
            int start = 0;
            int lineNumber = 1;
            while (matcher.find()) {
                lines.add(new PhysicalLine(content.substring(start, matcher.start()), matcher.group(), lineNumber++));
                start = matcher.end();
            }
            if (start < content.length()) {
                lines.add(new PhysicalLine(content.substring(start), "", lineNumber));
            }
            return lines;
        }

        private static boolean isBlankOrComment(String line) {
            int index = 0;
            while ((index < line.length()) && isPropertiesWhitespace(line.charAt(index))) {
                index++;
            }
            return (index == line.length()) || (line.charAt(index) == '#') || (line.charAt(index) == '!');
        }

        private static boolean hasContinuation(String line) {
            int backslashCount = 0;
            for (int index = line.length() - 1; (index >= 0) && (line.charAt(index) == '\\'); index--) {
                backslashCount++;
            }
            return (backslashCount % 2) == 1;
        }

        private static int malformedUnicodeEscapeIndex(String source) {
            for (int index = 0; index < source.length(); ) {
                if (source.charAt(index) != '\\') {
                    index++;
                    continue;
                }
                if (index + 1 >= source.length()) {
                    return -1;
                }
                char escaped = source.charAt(index + 1);
                if (escaped != 'u') {
                    index += 2;
                    continue;
                }
                if (index + 6 > source.length()) {
                    return index;
                }
                for (int digitIndex = index + 2; digitIndex < index + 6; digitIndex++) {
                    if (!isAsciiHexDigit(source.charAt(digitIndex))) {
                        return index;
                    }
                }
                index += 6;
            }
            return -1;
        }

        private static boolean isAsciiHexDigit(char character) {
            return ((character >= '0') && (character <= '9'))
                    || ((character >= 'A') && (character <= 'F'))
                    || ((character >= 'a') && (character <= 'f'));
        }

        private static int sourceLineNumber(int firstLineNumber, String source, int sourceIndex) {
            Matcher matcher = LINE_ENDING_PATTERN.matcher(source.substring(0, sourceIndex));
            int lineNumber = firstLineNumber;
            while (matcher.find()) {
                lineNumber++;
            }
            return lineNumber;
        }

        private static RawRegions rawRegions(String source) {
            int keyStart = 0;
            while ((keyStart < source.length()) && isPropertiesWhitespace(source.charAt(keyStart))) {
                keyStart++;
            }

            int keyEnd = source.length();
            boolean escaped = false;
            for (int index = keyStart; index < source.length(); ) {
                char character = source.charAt(index);
                if (escaped) {
                    escaped = false;
                    index++;
                } else if (character == '\\') {
                    int lineEndingLength = lineEndingLengthAt(source, index + 1);
                    if (lineEndingLength == 0) {
                        escaped = true;
                        index++;
                    } else {
                        index += lineEndingLength + 1;
                        while ((index < source.length()) && isPropertiesWhitespace(source.charAt(index))) {
                            index++;
                        }
                    }
                } else if ((character == '=') || (character == ':') || isPropertiesWhitespace(character)) {
                    keyEnd = index;
                    break;
                } else {
                    index++;
                }
            }

            int valueStart = keyEnd;
            while ((valueStart < source.length()) && isPropertiesWhitespace(source.charAt(valueStart))) {
                valueStart++;
            }
            char separator = '\0';
            if ((valueStart < source.length())
                    && ((source.charAt(valueStart) == '=') || (source.charAt(valueStart) == ':'))) {
                separator = source.charAt(valueStart++);
            }
            while ((valueStart < source.length()) && isPropertiesWhitespace(source.charAt(valueStart))) {
                valueStart++;
            }

            String rawKey = source.substring(keyStart, keyEnd);
            String rawValue = source.substring(valueStart);
            boolean requiresCanonicalRenderer = (separator != '=') || rawKey.contains("\\");
            return new RawRegions(rawKey, rawValue, requiresCanonicalRenderer);
        }

        private static int lineEndingLengthAt(String source, int index) {
            if ((index >= source.length()) || ((source.charAt(index) != '\r') && (source.charAt(index) != '\n'))) {
                return 0;
            }
            return ((source.charAt(index) == '\r')
                    && (index + 1 < source.length())
                    && (source.charAt(index + 1) == '\n')) ? 2 : 1;
        }

        private static boolean isPropertiesWhitespace(char character) {
            return (character == ' ') || (character == '\t') || (character == '\f');
        }

        private String fileName() {
            return I18nConsistencyCheck.fileLabel(relativePath);
        }

    }

    private record PhysicalLine(String text, String separator, int lineNumber) {
    }

    private record RawRegions(String key, String value, boolean requiresCanonicalRenderer) {
    }

    private record Report(List<Finding> findings) {

        private Report {
            findings = List.copyOf(findings);
        }

        private int exitCode() {
            return highestSeverity().exitCode;
        }

        private void print(Mode mode) {
            System.out.println("i18n consistency report");
            System.out.println("Mode: " + mode.name().toLowerCase(Locale.ROOT));
            System.out.println("Bundles:");
            System.out.println("- " + EN_US_RELATIVE_PATH);
            System.out.println("- " + DE_DE_RELATIVE_PATH);
            System.out.println();

            findings.stream()
                    .collect(Collectors.groupingBy(Finding::rule, LinkedHashMap::new, Collectors.toList()))
                    .forEach((rule, ruleFindings) -> {
                        System.out.println("Rule: " + rule);
                        ruleFindings.forEach(finding -> System.out.println(
                                finding.severity().name() + " " + finding.rule() + ": " + finding.message()
                        ));
                        System.out.println();
                    });

            Severity highestSeverity = highestSeverity();
            System.out.println("Overall: " + highestSeverity.name());
        }

        private Severity highestSeverity() {
            return findings.stream()
                           .map(Finding::severity)
                           .max(Comparator.comparingInt(severity -> severity.exitCode))
                           .orElse(Severity.PASS);
        }

        private boolean hasFailure(String rule) {
            return findings.stream().anyMatch(finding -> (finding.severity() == Severity.FAIL)
                    && Objects.equals(finding.rule(), rule));
        }

    }

}
