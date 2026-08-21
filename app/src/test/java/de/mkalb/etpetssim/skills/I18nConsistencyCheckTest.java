package de.mkalb.etpetssim.skills;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

final class I18nConsistencyCheckTest {

    private static final Path EN_US_RELATIVE_PATH = Path.of(
            "app", "src", "main", "resources", "i18n", "messages_en_US.properties"
    );
    private static final Path DE_DE_RELATIVE_PATH = Path.of(
            "app", "src", "main", "resources", "i18n", "messages_de_DE.properties"
    );
    private static final Path HELPER_SOURCE = resolveHelperSource();

    @TempDir
    Path temporaryDirectory;

    private static Path resolveHelperSource() {
        String configuredPath = System.getProperty("i18nConsistencyCheck.source");
        assertTrue(
                (configuredPath != null) && !configuredPath.isBlank(),
                "Gradle must provide the i18nConsistencyCheck.source system property"
        );
        Path helperSource = Path.of(configuredPath).toAbsolutePath().normalize();
        assertTrue(Files.isRegularFile(helperSource), "I18n helper source not found: " + helperSource);
        return helperSource;
    }

    private static Path javaExecutable() {
        String executableName = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path executable = Path.of(System.getProperty("java.home"), "bin", executableName);
        assertTrue(Files.isRegularFile(executable), "Java executable not found: " + executable);
        return executable;
    }

    private static String expectedOutput(String... lines) {
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }

    private static byte[] bytesWithLineEnding(String lineEnding, String... lines) {
        return (String.join(lineEnding, lines) + lineEnding).getBytes(StandardCharsets.UTF_8);
    }

    private static String expectedMalformedUtf8Output() {
        return expectedOutput(
                "i18n consistency report",
                "Mode: fix",
                "Bundles:",
                "- " + EN_US_RELATIVE_PATH,
                "- " + DE_DE_RELATIVE_PATH,
                "",
                "Rule: UTF-8 BOM",
                "PASS UTF-8 BOM: messages_en_US.properties (en_US) has no UTF-8 byte order mark",
                "PASS UTF-8 BOM: messages_de_DE.properties (de_DE) has no UTF-8 byte order mark",
                "",
                "Rule: UTF-8 encoding",
                "PASS UTF-8 encoding: messages_en_US.properties (en_US) is valid UTF-8",
                "FAIL UTF-8 encoding: messages_de_DE.properties (de_DE) is not valid UTF-8: Input length = 1",
                "",
                "Rule: invisible characters",
                "PASS invisible characters: messages_en_US.properties (en_US) contains no invisible characters other than regular spaces",
                "",
                "Rule: line ending consistency",
                "PASS line ending consistency: messages_en_US.properties (en_US) consistently uses LF line endings",
                "",
                "Rule: trailing newline",
                "PASS trailing newline: messages_en_US.properties (en_US) ends with exactly one trailing line break",
                "",
                "Overall: FAIL"
        );
    }

    private void writeBundles(byte[] enUsBytes, byte[] deDeBytes) throws IOException {
        Path i18nDirectory = temporaryDirectory.resolve(EN_US_RELATIVE_PATH).getParent();
        Files.createDirectories(i18nDirectory);
        Files.write(temporaryDirectory.resolve(EN_US_RELATIVE_PATH), enUsBytes);
        Files.write(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH), deDeBytes);
    }

    private ProcessResult runHelper(String mode) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(javaExecutable().toString(), HELPER_SOURCE.toString(), mode)
                .directory(temporaryDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return new ProcessResult(process.waitFor(), output);
    }

    @Test
    void testReportPrintsKeyAndPlaceholderMismatchesWithoutChangingBundles() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding("\n", "alpha   = Hello %", "beta    = World", "only.en = English");
        byte[] deDeBytes = bytesWithLineEnding("\n", "alpha   = Hallo", "beta    = Welt", "only.de = Deutsch");
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult result = runHelper("report");

        String expected = expectedOutput(
                "i18n consistency report",
                "Mode: report",
                "Bundles:",
                "- " + EN_US_RELATIVE_PATH,
                "- " + DE_DE_RELATIVE_PATH,
                "",
                "Rule: UTF-8 BOM",
                "PASS UTF-8 BOM: messages_en_US.properties (en_US) has no UTF-8 byte order mark",
                "PASS UTF-8 BOM: messages_de_DE.properties (de_DE) has no UTF-8 byte order mark",
                "",
                "Rule: UTF-8 encoding",
                "PASS UTF-8 encoding: messages_en_US.properties (en_US) is valid UTF-8",
                "PASS UTF-8 encoding: messages_de_DE.properties (de_DE) is valid UTF-8",
                "",
                "Rule: invisible characters",
                "PASS invisible characters: messages_en_US.properties (en_US) contains no invisible characters other than regular spaces",
                "PASS invisible characters: messages_de_DE.properties (de_DE) contains no invisible characters other than regular spaces",
                "",
                "Rule: line ending consistency",
                "PASS line ending consistency: messages_en_US.properties (en_US) consistently uses LF line endings",
                "PASS line ending consistency: messages_de_DE.properties (de_DE) consistently uses LF line endings",
                "",
                "Rule: trailing newline",
                "PASS trailing newline: messages_en_US.properties (en_US) ends with exactly one trailing line break",
                "PASS trailing newline: messages_de_DE.properties (de_DE) ends with exactly one trailing line break",
                "",
                "Rule: key parity",
                "FAIL key parity: messages_de_DE.properties (de_DE) is missing keys present in messages_en_US.properties (en_US): only.en",
                "FAIL key parity: messages_en_US.properties (en_US) is missing keys present in messages_de_DE.properties (de_DE): only.de",
                "",
                "Rule: alphabetical ordering",
                "PASS alphabetical ordering: messages_en_US.properties (en_US) is sorted by key",
                "PASS alphabetical ordering: messages_de_DE.properties (de_DE) is sorted by key",
                "",
                "Rule: = alignment",
                "PASS = alignment: messages_en_US.properties (en_US) aligns the '=' column",
                "PASS = alignment: messages_de_DE.properties (de_DE) aligns the '=' column",
                "",
                "Rule: placeholder count",
                "FAIL placeholder count: key alpha has 1 '%' in en_US but 0 '%' in de_DE",
                "",
                "Rule: Unicode escapes",
                "PASS Unicode escapes: messages_en_US.properties (en_US) stores localized characters directly",
                "PASS Unicode escapes: messages_de_DE.properties (de_DE) stores localized characters directly",
                "",
                "Overall: FAIL"
        );
        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertEquals(expected, result.output()),
                () -> assertArrayEquals(enUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(deDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testFixPrintsPostFixReportAndWritesExactLfAndCrlfBytes() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding(
                "\n", "\uFEFFgamma = C:\\\\temp", "beta = Caf\\u00E9", "alpha = A\u200BB"
        );
        byte[] deDeBytes = bytesWithLineEnding(
                "\r\n", "gamma = C:\\\\temp", "beta = Kaffee", "alpha = AB"
        );
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult result = runHelper("fix");

        byte[] expectedEnUsBytes = bytesWithLineEnding("\n", "alpha = AB", "beta  = Café", "gamma = C:\\\\temp");
        byte[] expectedDeDeBytes = bytesWithLineEnding("\r\n", "alpha = AB", "beta  = Kaffee", "gamma = C:\\\\temp");
        String expected = expectedOutput(
                "i18n consistency auto-fix",
                "- " + EN_US_RELATIVE_PATH + ": updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes",
                "- " + DE_DE_RELATIVE_PATH + ": updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes",
                "",
                "i18n consistency report",
                "Mode: fix",
                "Bundles:",
                "- " + EN_US_RELATIVE_PATH,
                "- " + DE_DE_RELATIVE_PATH,
                "",
                "Rule: UTF-8 BOM",
                "PASS UTF-8 BOM: messages_en_US.properties (en_US) has no UTF-8 byte order mark",
                "PASS UTF-8 BOM: messages_de_DE.properties (de_DE) has no UTF-8 byte order mark",
                "",
                "Rule: UTF-8 encoding",
                "PASS UTF-8 encoding: messages_en_US.properties (en_US) is valid UTF-8",
                "PASS UTF-8 encoding: messages_de_DE.properties (de_DE) is valid UTF-8",
                "",
                "Rule: invisible characters",
                "PASS invisible characters: messages_en_US.properties (en_US) contains no invisible characters other than regular spaces",
                "PASS invisible characters: messages_de_DE.properties (de_DE) contains no invisible characters other than regular spaces",
                "",
                "Rule: line ending consistency",
                "PASS line ending consistency: messages_en_US.properties (en_US) consistently uses LF line endings",
                "PASS line ending consistency: messages_de_DE.properties (de_DE) consistently uses CRLF line endings",
                "",
                "Rule: trailing newline",
                "PASS trailing newline: messages_en_US.properties (en_US) ends with exactly one trailing line break",
                "PASS trailing newline: messages_de_DE.properties (de_DE) ends with exactly one trailing line break",
                "",
                "Rule: key parity",
                "PASS key parity: production bundles contain the same keys (.url keys exempt)",
                "",
                "Rule: alphabetical ordering",
                "PASS alphabetical ordering: messages_en_US.properties (en_US) is sorted by key",
                "PASS alphabetical ordering: messages_de_DE.properties (de_DE) is sorted by key",
                "",
                "Rule: = alignment",
                "PASS = alignment: messages_en_US.properties (en_US) aligns the '=' column",
                "PASS = alignment: messages_de_DE.properties (de_DE) aligns the '=' column",
                "",
                "Rule: placeholder count",
                "PASS placeholder count: shared keys use the same number of '%' characters (.url keys exempt)",
                "",
                "Rule: Unicode escapes",
                "PASS Unicode escapes: messages_en_US.properties (en_US) stores localized characters directly",
                "PASS Unicode escapes: messages_de_DE.properties (de_DE) stores localized characters directly",
                "",
                "Overall: PASS"
        );
        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertEquals(expected, result.output()),
                () -> assertArrayEquals(expectedEnUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(expectedDeDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testFixRejectsMalformedUtf8InSecondBundleWithoutChangingEitherBundle() throws Exception {
        byte[] enUsBytes = "alpha = valid\n".getBytes(StandardCharsets.UTF_8);
        byte[] deDeBytes = new byte[]{'a', 'l', 'p', 'h', 'a', ' ', '=', ' ', (byte) 0xC3, 0x28, '\n'};
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult result = runHelper("fix");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertEquals(expectedMalformedUtf8Output(), result.output()),
                () -> assertArrayEquals(enUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(deDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    // Pending Block 4: F6 duplicate keys and F8 malformed-entry preflight fixtures.
    // Pending Block 5: F7 unsafe escapes, F11 idempotence, and F12 empty-bundle validation fixtures.

    private record ProcessResult(int exitCode, String output) {
    }

}
