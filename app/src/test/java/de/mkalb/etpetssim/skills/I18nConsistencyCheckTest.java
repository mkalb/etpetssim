package de.mkalb.etpetssim.skills;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.*;

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

    private static void destroyProcessTree(Process process) throws InterruptedException {
        process.descendants().forEach(ProcessHandle::destroy);
        process.destroy();
        if (!process.waitFor(2, TimeUnit.SECONDS)) {
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
            process.waitFor(2, TimeUnit.SECONDS);
        }
    }

    private void writeBundles(byte[] enUsBytes, byte[] deDeBytes) throws IOException {
        Path i18nDirectory = temporaryDirectory.resolve(EN_US_RELATIVE_PATH).getParent();
        Files.createDirectories(i18nDirectory);
        Files.write(temporaryDirectory.resolve(EN_US_RELATIVE_PATH), enUsBytes);
        Files.write(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH), deDeBytes);
    }

    private ProcessResult runHelper(String mode) throws IOException, InterruptedException {
        Path outputPath = Files.createTempFile(temporaryDirectory, "i18n-consistency-", ".log");
        Process process = new ProcessBuilder(javaExecutable().toString(), HELPER_SOURCE.toString(), mode)
                .directory(temporaryDirectory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(outputPath.toFile())
                .start();
        try {
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                destroyProcessTree(process);
                String output = Files.readString(outputPath, StandardCharsets.UTF_8);
                throw new IOException("i18n helper timed out after 30 seconds; output:" + System.lineSeparator() + output);
            }
            return new ProcessResult(process.exitValue(), Files.readString(outputPath, StandardCharsets.UTF_8));
        } finally {
            if (process.isAlive()) {
                destroyProcessTree(process);
            }
            Files.deleteIfExists(outputPath);
        }
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
                "Rule: empty bundle",
                "PASS empty bundle: messages_en_US.properties (en_US) contains semantic entries",
                "PASS empty bundle: messages_de_DE.properties (de_DE) contains semantic entries",
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
                "Rule: trailing whitespace",
                "PASS trailing whitespace: messages_en_US.properties (en_US) has no decoded trailing whitespace in values",
                "PASS trailing whitespace: messages_de_DE.properties (de_DE) has no decoded trailing whitespace in values",
                "",
                "Rule: placeholder count",
                "FAIL placeholder count: key alpha has 1 '%' in en_US but 0 '%' in de_DE",
                "",
                "Rule: Unicode escapes",
                "PASS Unicode escapes: messages_en_US.properties (en_US) stores localized characters directly",
                "PASS Unicode escapes: messages_de_DE.properties (de_DE) stores localized characters directly",
                "",
                "Rule: Unicode escapes in keys",
                "PASS Unicode escapes in keys: messages_en_US.properties (en_US) contains no \\uXXXX escapes in keys",
                "PASS Unicode escapes in keys: messages_de_DE.properties (de_DE) contains no \\uXXXX escapes in keys",
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
                "- " + EN_US_RELATIVE_PATH + ": updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes; removed 1 invisible character",
                "- " + DE_DE_RELATIVE_PATH + ": updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes; removed 0 invisible characters",
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
                "Rule: empty bundle",
                "PASS empty bundle: messages_en_US.properties (en_US) contains semantic entries",
                "PASS empty bundle: messages_de_DE.properties (de_DE) contains semantic entries",
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
                "Rule: trailing whitespace",
                "PASS trailing whitespace: messages_en_US.properties (en_US) has no decoded trailing whitespace in values",
                "PASS trailing whitespace: messages_de_DE.properties (de_DE) has no decoded trailing whitespace in values",
                "",
                "Rule: placeholder count",
                "PASS placeholder count: shared keys use the same number of '%' characters (.url keys exempt)",
                "",
                "Rule: Unicode escapes",
                "PASS Unicode escapes: messages_en_US.properties (en_US) stores localized characters directly",
                "PASS Unicode escapes: messages_de_DE.properties (de_DE) stores localized characters directly",
                "",
                "Rule: Unicode escapes in keys",
                "PASS Unicode escapes in keys: messages_en_US.properties (en_US) contains no \\uXXXX escapes in keys",
                "PASS Unicode escapes in keys: messages_de_DE.properties (de_DE) contains no \\uXXXX escapes in keys",
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

    @Test
    void testReportParsesPropertiesGrammarWithDecodedKeysValuesAndSourceLines() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding(
                "\n",
                "# Source syntax intentionally varies.",
                "colon.key: value",
                "continued = hello\\",
                "    world %",
                "escaped\\=key = 100%%",
                "path = C:\\\\temp",
                "space.key value %"
        );
        byte[] deDeBytes = bytesWithLineEnding(
                "\n",
                "! Source syntax intentionally varies.",
                "colon.key = Wert",
                "continued = hallo\\",
                "    Welt",
                "escaped\\=key = 100%",
                "path = C:\\\\temp",
                "space.key = Wert %"
        );
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult result = runHelper("report");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "PASS key parity: production bundles contain the same keys (.url keys exempt)"
                )),
                () -> assertTrue(result.output().contains(
                        "FAIL placeholder count: key continued has 1 '%' in en_US but 0 '%' in de_DE"
                )),
                () -> assertTrue(result.output().contains(
                        "FAIL placeholder count: key escaped=key has 2 '%' in en_US but 1 '%' in de_DE"
                )),
                () -> assertTrue(result.output().contains(
                        "line 3 key continued"
                )),
                () -> assertFalse(result.output().contains("FAIL io:")),
                () -> assertTrue(result.output().endsWith("Overall: FAIL" + System.lineSeparator())),
                () -> assertArrayEquals(enUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(deDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testReportKeepsContinuedKeyEscapesOutOfRawValues() throws Exception {
        byte[] bundleBytes = bytesWithLineEnding("\n", "foo\\", "  \\u0062ar = value");
        writeBundles(bundleBytes, bundleBytes);

        ProcessResult result = runHelper("report");

        assertAll(
                () -> assertEquals(1, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "PASS key parity: production bundles contain the same keys (.url keys exempt)"
                )),
                () -> assertTrue(result.output().contains(
                        "PASS Unicode escapes: messages_en_US.properties (en_US) stores localized characters directly"
                )),
                () -> assertTrue(result.output().contains(
                        "PASS Unicode escapes: messages_de_DE.properties (de_DE) stores localized characters directly"
                )),
                () -> assertFalse(result.output().contains(
                        "contains \\uXXXX escapes in values for keys: foobar"
                )),
                () -> assertTrue(result.output().endsWith("Overall: WARN" + System.lineSeparator())),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testDuplicateDecodedKeysInBothLocalesAreReportedAndBlockFix() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding("\n", "alpha = first", "beta = shared", "\\u0061lpha = override");
        byte[] deDeBytes = bytesWithLineEnding("\n", "alpha = erste", "alpha = überschrieben", "beta = gemeinsam");
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult reportResult = runHelper("report");
        ProcessResult fixResult = runHelper("fix");

        assertAll(
                () -> assertEquals(2, reportResult.exitCode()),
                () -> assertTrue(reportResult.output().contains(
                        "FAIL duplicate keys: messages_en_US.properties (en_US) has duplicate decoded key alpha at lines 1 and 3"
                )),
                () -> assertTrue(reportResult.output().contains(
                        "FAIL duplicate keys: messages_de_DE.properties (de_DE) has duplicate decoded key alpha at lines 1 and 2"
                )),
                () -> assertTrue(reportResult.output().endsWith("Overall: FAIL" + System.lineSeparator())),
                () -> assertEquals(2, fixResult.exitCode()),
                () -> assertTrue(fixResult.output().contains("Rule: duplicate keys")),
                () -> assertFalse(fixResult.output().contains("i18n consistency auto-fix")),
                () -> assertArrayEquals(enUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(deDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testMalformedUnicodeEscapesAreReportedWithSourceLinesAndBlockFix() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding(
                "\n",
                "nonhex = \\u12G4",
                "repeated = \\uu0041",
                "truncated = \\u123",
                "unicode-digits = \\u１２３４"
        );
        byte[] deDeBytes = bytesWithLineEnding(
                "\n", "nonhex = valid", "repeated = valid", "truncated = valid", "unicode-digits = valid"
        );
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult result = runHelper("fix");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "FAIL properties syntax: messages_en_US.properties (en_US) has malformed Unicode escape at line 1"
                )),
                () -> assertTrue(result.output().contains(
                        "FAIL properties syntax: messages_en_US.properties (en_US) has malformed Unicode escape at line 2"
                )),
                () -> assertTrue(result.output().contains(
                        "FAIL properties syntax: messages_en_US.properties (en_US) has malformed Unicode escape at line 3"
                )),
                () -> assertTrue(result.output().contains(
                        "FAIL properties syntax: messages_en_US.properties (en_US) has malformed Unicode escape at line 4"
                )),
                () -> assertFalse(result.output().contains("FAIL usage:")),
                () -> assertFalse(result.output().contains("FAIL io:")),
                () -> assertTrue(result.output().endsWith("Overall: FAIL" + System.lineSeparator())),
                () -> assertArrayEquals(enUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(deDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testFixCanonicalizesValidNonCanonicalSyntaxAndIsIdempotent() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding("\n", "alpha: value", "beta = continued\\", "    value");
        byte[] deDeBytes = bytesWithLineEnding("\n", "alpha value", "beta = value");
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult firstResult = runHelper("fix");
        byte[] firstEnUsBytes = Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH));
        byte[] firstDeDeBytes = Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH));
        ProcessResult secondResult = runHelper("fix");

        byte[] expectedEnUsBytes = bytesWithLineEnding("\n", "alpha = value", "beta  = continuedvalue");
        byte[] expectedDeDeBytes = bytesWithLineEnding("\n", "alpha = value", "beta  = value");

        assertAll(
                () -> assertEquals(0, firstResult.exitCode()),
                () -> assertTrue(firstResult.output().contains("i18n consistency auto-fix")),
                () -> assertTrue(firstResult.output().endsWith("Overall: PASS" + System.lineSeparator())),
                () -> assertEquals(0, secondResult.exitCode()),
                () -> assertTrue(secondResult.output().contains(EN_US_RELATIVE_PATH + ": no changes")),
                () -> assertTrue(secondResult.output().contains(DE_DE_RELATIVE_PATH + ": no changes")),
                () -> assertArrayEquals(expectedEnUsBytes, firstEnUsBytes),
                () -> assertArrayEquals(expectedDeDeBytes, firstDeDeBytes),
                () -> assertArrayEquals(firstEnUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(firstDeDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testFixPreservesUnsafeAndLiteralUnicodeLookingEscapes() throws Exception {
        byte[] bundleBytes = bytesWithLineEnding(
                "\n",
                "backslash = x\\u005Cy",
                "escaped = \\\\u0041",
                "line-break = x\\u000Ay",
                "lone-surrogate = \\uD800",
                "non-breaking = x\\u00A0y",
                "pair = \\uD83D\\uDE00"
        );
        writeBundles(bundleBytes, bundleBytes);

        ProcessResult firstResult = runHelper("fix");
        byte[] firstBytes = Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH));
        ProcessResult secondResult = runHelper("fix");

        byte[] expectedBytes = bytesWithLineEnding(
                "\n",
                "backslash      = x\\\\y",
                "escaped        = \\\\u0041",
                "line-break     = x\\ny",
                "lone-surrogate = \\uD800",
                "non-breaking   = x\\u00A0y",
                "pair           = 😀"
        );
        assertAll(
                () -> assertEquals(1, firstResult.exitCode()),
                () -> assertTrue(firstResult.output().endsWith("Overall: WARN" + System.lineSeparator())),
                () -> assertEquals(1, secondResult.exitCode()),
                () -> assertArrayEquals(expectedBytes, firstBytes),
                () -> assertArrayEquals(firstBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(firstBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testFixReportsLiteralInvisibleCharacterRemovalAndThenMakesNoChanges() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding("\n", "alpha = A\u200BB", "beta = value");
        byte[] deDeBytes = bytesWithLineEnding("\n", "alpha = AB", "beta = va\u200Blue");
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult firstResult = runHelper("fix");
        ProcessResult secondResult = runHelper("fix");

        assertAll(
                () -> assertEquals(0, firstResult.exitCode()),
                () -> assertTrue(firstResult.output().contains(EN_US_RELATIVE_PATH + ": updated encoding")),
                () -> assertTrue(firstResult.output().contains(DE_DE_RELATIVE_PATH + ": updated encoding")),
                () -> assertTrue(firstResult.output().contains("removed 1 invisible character")),
                () -> assertEquals(0, secondResult.exitCode()),
                () -> assertTrue(secondResult.output().contains(EN_US_RELATIVE_PATH + ": no changes; removed 0 invisible characters")),
                () -> assertTrue(secondResult.output().contains(DE_DE_RELATIVE_PATH + ": no changes; removed 0 invisible characters")),
                () -> assertArrayEquals(
                        bytesWithLineEnding("\n", "alpha = AB", "beta  = value"),
                        Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))
                ),
                () -> assertArrayEquals(
                        bytesWithLineEnding("\n", "alpha = AB", "beta  = value"),
                        Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH))
                )
        );
    }

    @Test
    void testFixDoesNotCreateEscapesWhenRemovingLiteralInvisibleCharacters() throws Exception {
        byte[] bundleBytes = bytesWithLineEnding(
                "\n",
                "key\\\u200Bu0041 = value",
                "value = x\\\u200Bu0041y"
        );
        writeBundles(bundleBytes, bundleBytes);

        ProcessResult result = runHelper("fix");

        byte[] expectedBytes = bytesWithLineEnding(
                "\n",
                "keyu0041 = value",
                "value    = xu0041y"
        );
        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        EN_US_RELATIVE_PATH + ": updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes; removed 2 invisible characters"
                )),
                () -> assertTrue(result.output().contains(
                        DE_DE_RELATIVE_PATH + ": updated encoding (BOM/invisible characters), line endings, sorting, alignment, or Unicode escapes; removed 2 invisible characters"
                )),
                () -> assertArrayEquals(expectedBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(expectedBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testFixRejectsEmptyBundleWithoutChangingEitherBundle() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding("\n", "# header", "# only comments");
        byte[] deDeBytes = bytesWithLineEnding("\n", "# header");
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult result = runHelper("fix");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "FAIL empty bundle: messages_en_US.properties (en_US) contains no semantic entries"
                )),
                () -> assertTrue(result.output().contains(
                        "FAIL empty bundle: messages_de_DE.properties (de_DE) contains no semantic entries"
                )),
                () -> assertFalse(result.output().contains("i18n consistency auto-fix")),
                () -> assertTrue(result.output().endsWith("Overall: FAIL" + System.lineSeparator())),
                () -> assertArrayEquals(enUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(deDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testReportWarnsAboutDecodedTrailingWhitespaceWithoutChangingBundles() throws Exception {
        byte[] enUsBytes = bytesWithLineEnding(
                "\n",
                "escaped-space      = value\\ ",
                "escaped-tab        = value\\t",
                "literal-space      = value  ",
                "literal-tab        = value\t",
                "terminal-backslash = value\\\\"
        );
        byte[] deDeBytes = bytesWithLineEnding(
                "\n",
                "escaped-space      = value",
                "escaped-tab        = value",
                "literal-space      = value",
                "literal-tab        = value",
                "terminal-backslash = value\\\\"
        );
        writeBundles(enUsBytes, deDeBytes);

        ProcessResult result = runHelper("report");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "WARN trailing whitespace: messages_en_US.properties (en_US) has decoded trailing whitespace"
                                + " for keys: escaped-space (line 1), escaped-tab (line 2),"
                                + " literal-space (line 3), literal-tab (line 4)"
                )),
                () -> assertTrue(result.output().contains(
                        "PASS trailing whitespace: messages_de_DE.properties (de_DE) has no decoded trailing whitespace in values"
                )),
                () -> assertFalse(result.output().contains("terminal-backslash (line 5)")),
                () -> assertArrayEquals(enUsBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(deDeBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testReportCannotVerifyTrailingNewlineForUnsupportedCrLineEndings() throws Exception {
        byte[] bundleBytes = bytesWithLineEnding("\r", "alpha = one", "beta  = two");
        writeBundles(bundleBytes, bundleBytes);

        ProcessResult result = runHelper("report");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "FAIL line ending consistency: messages_en_US.properties (en_US) uses unsupported CR line endings; use LF or CRLF"
                )),
                () -> assertTrue(result.output().contains(
                        "FAIL trailing newline: messages_en_US.properties (en_US): cannot verify the trailing line break"
                                + " because line endings are inconsistent, missing, or unsupported"
                )),
                () -> assertFalse(result.output().contains(
                        "PASS trailing newline: messages_en_US.properties (en_US) ends with exactly one trailing line break"
                )),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testReportWarnsAboutValidUnicodeEscapesInKeys() throws Exception {
        byte[] bundleBytes = bytesWithLineEnding(
                "\n",
                "\\u0061lpha          = value",
                "literal\\\\u0062eta = value",
                "plain                = value"
        );
        writeBundles(bundleBytes, bundleBytes);

        ProcessResult result = runHelper("report");

        assertAll(
                () -> assertEquals(1, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "WARN Unicode escapes in keys: messages_en_US.properties (en_US) contains \\uXXXX escapes"
                                + " in keys: alpha (line 1)" + System.lineSeparator()
                )),
                () -> assertTrue(result.output().contains(
                        "PASS Unicode escapes: messages_en_US.properties (en_US) stores localized characters directly"
                )),
                () -> assertTrue(result.output().endsWith("Overall: WARN" + System.lineSeparator())),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testFixCanonicalizesUnicodeEscapesInKeys() throws Exception {
        byte[] bundleBytes = bytesWithLineEnding("\n", "\\u0061lpha = value", "plain = value");
        writeBundles(bundleBytes, bundleBytes);

        ProcessResult result = runHelper("fix");

        byte[] expectedBytes = bytesWithLineEnding("\n", "alpha = value", "plain = value");
        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertTrue(result.output().endsWith("Overall: PASS" + System.lineSeparator())),
                () -> assertArrayEquals(expectedBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(expectedBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    @Test
    void testReportCapsInvisibleCharacterPositionsAndPrintsTotalCount() throws Exception {
        byte[] bundleBytes = bytesWithLineEnding("\n", "alpha = x" + "\t".repeat(25));
        writeBundles(bundleBytes, bundleBytes);

        ProcessResult result = runHelper("report");

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.output().contains(
                        "FAIL invisible characters: messages_en_US.properties (en_US) contains 25 invisible characters:"
                )),
                () -> assertTrue(result.output().contains("line 1 column 29 (U+0009), ... and 5 more")),
                () -> assertFalse(result.output().contains("line 1 column 30 (U+0009)")),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(EN_US_RELATIVE_PATH))),
                () -> assertArrayEquals(bundleBytes, Files.readAllBytes(temporaryDirectory.resolve(DE_DE_RELATIVE_PATH)))
        );
    }

    private record ProcessResult(int exitCode, String output) {
    }

}
