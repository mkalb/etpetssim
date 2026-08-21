import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
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
import java.util.SequencedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class I18nConsistencyCheck {

    private static final Path EN_US_RELATIVE_PATH = Path.of("app", "src", "main", "resources", "i18n", "messages_en_US.properties");
    private static final Path DE_DE_RELATIVE_PATH = Path.of("app", "src", "main", "resources", "i18n", "messages_de_DE.properties");

    private static final Pattern UNICODE_ESCAPE_PATTERN = Pattern.compile("\\\\u([0-9a-fA-F]{4})");

    private static final Pattern LINE_ENDING_PATTERN = Pattern.compile("\r\n|\n|\r");

    private static final Comparator<String> KEY_COMPARATOR =
            Comparator.comparing((String key) -> key.toLowerCase(Locale.ROOT))
                      .thenComparing(Comparator.naturalOrder());

    public static void main(String[] args) {
        try {
            Mode mode = parseMode(args);
            Path repositoryRoot = findRepositoryRoot();

            List<Finding> encodingFindings = new ArrayList<>();
            encodingFindings.addAll(analyzeEncoding(repositoryRoot, EN_US_RELATIVE_PATH));
            encodingFindings.addAll(analyzeEncoding(repositoryRoot, DE_DE_RELATIVE_PATH));
            if (hasInvalidUtf8(encodingFindings)) {
                Report report = new Report(encodingFindings);
                report.print(mode);
                System.exit(report.exitCode());
            }

            if (mode == Mode.FIX) {
                FixResult enUsFix = applyFix(repositoryRoot, EN_US_RELATIVE_PATH);
                FixResult deDeFix = applyFix(repositoryRoot, DE_DE_RELATIVE_PATH);

                System.out.println("i18n consistency auto-fix");
                System.out.println("- " + enUsFix.message());
                System.out.println("- " + deDeFix.message());
                System.out.println();

                encodingFindings = new ArrayList<>();
                encodingFindings.addAll(analyzeEncoding(repositoryRoot, EN_US_RELATIVE_PATH));
                encodingFindings.addAll(analyzeEncoding(repositoryRoot, DE_DE_RELATIVE_PATH));
            }

            Bundle enUs = Bundle.load(repositoryRoot, EN_US_RELATIVE_PATH);
            Bundle deDe = Bundle.load(repositoryRoot, DE_DE_RELATIVE_PATH);
            Report report = analyze(encodingFindings, enUs, deDe);

            report.print(mode);
            System.exit(report.exitCode());
        } catch (IllegalArgumentException exception) {
            System.err.println("FAIL usage: " + exception.getMessage());
            System.err.println("Usage: java .github/skills/i18n-consistency/I18nConsistencyCheck.java [report|fix]");
            System.exit(2);
        } catch (IOException exception) {
            System.err.println("FAIL io: " + exception.getMessage());
            System.exit(2);
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
        throw new IllegalArgumentException("expected no argument, 'report', or 'fix'");
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

    private static FixResult applyFix(Path repositoryRoot, Path relativePath) throws IOException {
        Path path = repositoryRoot.resolve(relativePath);
        byte[] originalBytes = Files.readAllBytes(path);
        String cleanedContent = cleanContent(originalBytes);
        Bundle bundle = Bundle.parse(relativePath, cleanedContent);
        String fixedContent = formatEntries(bundle.entries().values(), lineSeparatorOf(cleanedContent));
        byte[] fixedBytes = fixedContent.getBytes(StandardCharsets.UTF_8);

        if (Arrays.equals(originalBytes, fixedBytes)) {
            return new FixResult(relativePath + ": no changes");
        }

        Files.write(path, fixedBytes);
        return new FixResult(relativePath + ": updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes");
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

    private static String cleanContent(byte[] bytes) throws CharacterCodingException {
        return stripInvisibleCharacters(decodeUtf8(stripUtf8Bom(bytes)));
    }

    // \n and \r are kept to preserve line structure even though they are CONTROL characters.
    private static String stripInvisibleCharacters(String content) {
        StringBuilder builder = new StringBuilder(content.length());
        for (int i = 0; i < content.length(); ) {
            int codePoint = content.codePointAt(i);
            if ((codePoint == '\n') || (codePoint == '\r') || !isInvisibleCharacter(codePoint)) {
                builder.appendCodePoint(codePoint);
            }
            i += Character.charCount(codePoint);
        }
        return builder.toString();
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
        List<Entry> sortedEntries = entries.stream()
                                           .map(entry -> new Entry(entry.lineNumber(), entry.key(), convertUnicodeEscapes(entry.value()), entry.rawLine()))
                                           .sorted(Comparator.comparing(Entry::key, KEY_COMPARATOR))
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

    private static String formatEntry(Entry entry, int maxKeyLength) {
        String alignedKey = entry.key() + " ".repeat(maxKeyLength - entry.key().length() + 1) + "=";
        return entry.value().isEmpty() ? alignedKey : alignedKey + " " + entry.value();
    }

    private static String convertUnicodeEscapes(String value) {
        Matcher matcher = UNICODE_ESCAPE_PATTERN.matcher(value);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            char decoded = (char) Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(builder, Matcher.quoteReplacement(String.valueOf(decoded)));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private static Report analyze(List<Finding> encodingFindings, Bundle enUs, Bundle deDe) {
        List<Finding> findings = new ArrayList<>(encodingFindings);

        findings.addAll(analyzeKeyParity(enUs, deDe));
        findings.addAll(analyzeOrdering(enUs));
        findings.addAll(analyzeOrdering(deDe));
        findings.addAll(analyzeAlignment(enUs));
        findings.addAll(analyzeAlignment(deDe));
        findings.addAll(analyzePlaceholderCount(enUs, deDe));
        findings.addAll(analyzeUnicodeEscapes(enUs));
        findings.addAll(analyzeUnicodeEscapes(deDe));

        return new Report(findings);
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

    private static List<Finding> analyzeEncoding(Path repositoryRoot, Path relativePath) throws IOException {
        Path path = repositoryRoot.resolve(relativePath);
        byte[] bytes = Files.readAllBytes(path);
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
        String consistentStyle = (styles.size() == 1) ? styles.iterator().next() : null;

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
            return Finding.fail("trailing newline", fileName + ": cannot verify the trailing line break because line endings are inconsistent or missing");
        }
        String separator = switch (consistentStyle) {
            case "CRLF" -> "\r\n";
            case "LF" -> "\n";
            default -> "\r";
        };
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
                occurrences.add("line " + line + " column " + column + " (U+%04X)".formatted(codePoint));
            }
            index += charCount;
            column++;
        }

        if (occurrences.isEmpty()) {
            return Finding.pass("invisible characters", fileName + " contains no invisible characters other than regular spaces");
        }
        return Finding.fail("invisible characters", fileName + " contains invisible characters: " + String.join(", ", occurrences));
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
                                  .mapToInt(entry -> entry.key().length())
                                  .max()
                                  .orElse(0);
        List<String> misaligned = new ArrayList<>();

        for (Entry entry : entries) {
            String expected = formatEntry(entry, maxKeyLength);
            if (!Objects.equals(entry.rawLine(), expected)) {
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

    private static long percentCount(String value) {
        return value.chars().filter(character -> character == '%').count();
    }

    private static List<Finding> analyzeUnicodeEscapes(Bundle bundle) {
        List<String> keysWithEscapes = bundle.entries().values().stream()
                                             .filter(entry -> UNICODE_ESCAPE_PATTERN.matcher(entry.value()).find())
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

    private record Entry(int lineNumber, String key, String value, String rawLine) {
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

    private record Bundle(Path relativePath, SequencedMap<String, Entry> entries) {

        private static Bundle load(Path repositoryRoot, Path relativePath) throws IOException {
            Path path = repositoryRoot.resolve(relativePath);
            return parse(relativePath, cleanContent(Files.readAllBytes(path)));
        }

        private static Bundle parse(Path relativePath, String content) throws IOException {
            SequencedMap<String, Entry> entries = new LinkedHashMap<>();
            String[] lines = LINE_ENDING_PATTERN.split(content, -1);
            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                if (index == lines.length - 1 && line.isEmpty()) {
                    continue;
                }
                if (line.isBlank() || line.startsWith("#") || line.startsWith("!")) {
                    continue;
                }

                int separatorIndex = line.indexOf('=');
                if (separatorIndex < 0) {
                    throw new IOException("invalid properties entry without '=' in " + relativePath + " at line " + (index + 1));
                }

                String key = line.substring(0, separatorIndex).trim();
                String value = line.substring(separatorIndex + 1).stripLeading();
                entries.put(key, new Entry(index + 1, key, value, line));
            }
            return new Bundle(relativePath, entries);
        }

        private String fileName() {
            return I18nConsistencyCheck.fileLabel(relativePath);
        }

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

    }

}
