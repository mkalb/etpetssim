import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class I18nConsistencyCheck {

    private static final Path EN_US_RELATIVE_PATH = Path.of("app", "src", "main", "resources", "i18n", "messages_en_US.properties");
    private static final Path DE_DE_RELATIVE_PATH = Path.of("app", "src", "main", "resources", "i18n", "messages_de_DE.properties");

    private static final Pattern UNICODE_ESCAPE_PATTERN = Pattern.compile("\\\\u([0-9a-fA-F]{4})");

    private static final Comparator<String> KEY_COMPARATOR = Comparator.<String, String>comparing(
            key -> key.toLowerCase(Locale.ROOT),
            String::compareTo
    ).thenComparing(Comparator.naturalOrder());

    public static void main(String[] args) {
        try {
            Mode mode = parseMode(args);
            Path repositoryRoot = findRepositoryRoot();

            if (mode == Mode.FIX) {
                FixResult enUsFix = applyFix(repositoryRoot, EN_US_RELATIVE_PATH);
                FixResult deDeFix = applyFix(repositoryRoot, DE_DE_RELATIVE_PATH);

                System.out.println("i18n consistency auto-fix");
                System.out.println("- " + enUsFix.message());
                System.out.println("- " + deDeFix.message());
                System.out.println();
            }

            Bundle enUs = Bundle.load(repositoryRoot, "en_US", EN_US_RELATIVE_PATH);
            Bundle deDe = Bundle.load(repositoryRoot, "de_DE", DE_DE_RELATIVE_PATH);
            Report report = analyze(enUs, deDe);

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
        String originalContent = Files.readString(path, StandardCharsets.UTF_8);
        Bundle bundle = Bundle.parse(repositoryRoot, path, relativePath, localeFrom(relativePath), originalContent);
        String fixedContent = formatEntries(bundle.entries().values(), lineSeparatorOf(originalContent), originalContent.endsWith("\n"));

        if (Objects.equals(originalContent, fixedContent)) {
            return new FixResult(relativePath + ": no changes");
        }

        Files.writeString(path, fixedContent, StandardCharsets.UTF_8);
        return new FixResult(relativePath + ": updated sorting, alignment, or Unicode escapes");
    }

    private static String localeFrom(Path relativePath) {
        String fileName = relativePath.getFileName().toString();
        int start = fileName.indexOf('_') + 1;
        int end = fileName.indexOf(".properties");
        return fileName.substring(start, end);
    }

    private static String lineSeparatorOf(String content) {
        return content.contains("\r\n") ? "\r\n" : "\n";
    }

    private static String formatEntries(Collection<Entry> entries, String lineSeparator, boolean finalNewline) {
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
        return finalNewline ? content + lineSeparator : content;
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

    private static Report analyze(Bundle enUs, Bundle deDe) {
        List<Finding> findings = new ArrayList<>();

        findings.addAll(analyzeKeyParity(enUs, deDe));
        findings.addAll(analyzeOrdering(enUs));
        findings.addAll(analyzeOrdering(deDe));
        findings.addAll(analyzeAlignment(enUs));
        findings.addAll(analyzeAlignment(deDe));
        findings.addAll(analyzePlaceholderCount(enUs, deDe));
        findings.addAll(analyzeUnicodeEscapes(enUs));
        findings.addAll(analyzeUnicodeEscapes(deDe));

        return new Report(findings.toArray(Finding[]::new));
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
                                       .map(key -> {
                                           long enUsCount = percentCount(enUs.entries().get(key).value());
                                           long deDeCount = percentCount(deDe.entries().get(key).value());
                                           if (enUsCount == deDeCount) {
                                               return null;
                                           }
                                           return Finding.fail(
                                                   "placeholder count",
                                                   "key " + key + " has " + enUsCount + " '%' in en_US but " + deDeCount + " '%' in de_DE"
                                           );
                                       })
                                       .filter(Objects::nonNull)
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

    private record Bundle(String locale, Path relativePath, SequencedMap<String, Entry> entries) {

        private static Bundle load(Path repositoryRoot, String locale, Path relativePath) throws IOException {
            Path path = repositoryRoot.resolve(relativePath);
            return parse(repositoryRoot, path, relativePath, locale, Files.readString(path, StandardCharsets.UTF_8));
        }

        private static Bundle parse(Path repositoryRoot, Path path, Path relativePath, String locale, String content) throws IOException {
            if (!Files.isRegularFile(path)) {
                throw new IOException("bundle not found: " + repositoryRoot.relativize(path));
            }

            SequencedMap<String, Entry> entries = new java.util.LinkedHashMap<>();
            String[] lines = content.split("\\R", -1);
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
            return new Bundle(locale, relativePath, entries);
        }

        private String fileName() {
            return relativePath.getFileName().toString() + " (" + locale + ")";
        }

    }

    private record Report(Finding[] findings) {

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

            Arrays.stream(findings)
                  .collect(Collectors.groupingBy(Finding::rule, java.util.LinkedHashMap::new, Collectors.toList()))
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
            return Arrays.stream(findings)
                         .map(Finding::severity)
                         .max(Comparator.comparingInt(severity -> severity.exitCode))
                         .orElse(Severity.PASS);
        }

    }

}
