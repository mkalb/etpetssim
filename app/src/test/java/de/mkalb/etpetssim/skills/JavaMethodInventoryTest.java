package de.mkalb.etpetssim.skills;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("skill")
@SuppressWarnings({"MagicNumber", "HardcodedLineSeparator"})
final class JavaMethodInventoryTest {

    private static final Path OUTPUT_PATH = Path.of("docs", "planning", "JavaMethodInventory.csv");
    private static final Path HELPER_SOURCE = resolveHelperSource();

    @SuppressWarnings("NotNullFieldNotInitialized")
    @TempDir
    Path temporaryDirectory;

    private static Path resolveHelperSource() {
        String configuredPath = System.getProperty("javaMethodInventory.source");
        assertTrue(
                (configuredPath != null) && !configuredPath.isBlank(),
                "Gradle must provide the javaMethodInventory.source system property"
        );
        Path helperSource = Path.of(configuredPath).toAbsolutePath().normalize();
        assertTrue(Files.isRegularFile(helperSource), "Java method inventory source not found: " + helperSource);
        return helperSource;
    }

    private static Path javaExecutable() {
        String executableName = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path executable = Path.of(System.getProperty("java.home"), "bin", executableName);
        assertTrue(Files.isRegularFile(executable), "Java executable not found: " + executable);
        return executable;
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

    private void writeSource(String sourceSet, String fileName, String content) throws IOException {
        Path sourceFile = temporaryDirectory.resolve("app/src/" + sourceSet + "/java/example/" + fileName);
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, content, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("UseOfProcessBuilder")
    private ProcessResult runHelper() throws IOException, InterruptedException {
        Path logFile = Files.createTempFile(temporaryDirectory, "java-method-inventory-", ".log");
        Process process = new ProcessBuilder(javaExecutable().toString(), HELPER_SOURCE.toString())
                .directory(temporaryDirectory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(logFile.toFile())
                .start();
        try {
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                destroyProcessTree(process);
                String output = Files.readString(logFile, StandardCharsets.UTF_8);
                throw new IOException("Java method inventory timed out after 30 seconds; output:" + System.lineSeparator() + output);
            }
            return new ProcessResult(process.exitValue(), Files.readString(logFile, StandardCharsets.UTF_8));
        } finally {
            if (process.isAlive()) {
                destroyProcessTree(process);
            }
            Files.deleteIfExists(logFile);
        }
    }

    @Test
    void testGeneratesCompleteIdempotentInventory() throws Exception {
        writeSource("main", "Sample.java", """
                package example;
                
                import java.io.IOException;
                
                @interface Marker {
                    String value();
                }
                
                public record Sample<T>(String value) {
                    @Marker("Sample(")
                    public Sample {
                    }
                
                    @Marker("convert")
                    public static <R extends Number> R convert(java.util.List<String> values, @Marker("...") String[] names, String... labels) throws IOException {
                        return null;
                    }
                
                    public static <R extends Number> R convert(String value) throws IOException {
                        return null;
                    }
                
                    class Nested {
                        protected int convert(int value) {
                            return value;
                        }
                    }
                }
                
                enum CellShape {
                    SQUARE(4, "square");
                
                    CellShape(int sides, String label) {
                    }
                }
                
                record Canonical(String value) {
                    @Marker("canonical")
                    Canonical /* comment */ (String value) {
                    }
                }
                """);
        writeSource("test", "TestFixture.java", """
                package example;
                
                final class TestFixture {
                    TestFixture() {
                    }
                }
                """);

        String expectedInventory = String.join(System.lineSeparator(), List.of(
                "source_set,source_path,package_name,declaring_type,declaring_type_kind,member_name,member_kind,parameter_types,visibility,modifiers,return_type,throws_types,annotations,type_parameters,line_number",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Marker\",\"ANNOTATION_TYPE\",\"value\",\"METHOD\",\"\",\"public\",\"\",\"String\",\"\",\"\",\"\",\"6\"",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Sample\",\"RECORD\",\"<init>\",\"COMPACT_CONSTRUCTOR\",\"\",\"public\",\"\",\"\",\"\",\"@Marker(\"\"Sample(\"\")\",\"\",\"10\"",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Sample\",\"RECORD\",\"convert\",\"METHOD\",\"java.util.List<String>, String[], String...\",\"public\",\"static\",\"R\",\"IOException\",\"@Marker(\"\"convert\"\")\",\"R extends Number\",\"14\"",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Sample\",\"RECORD\",\"convert\",\"METHOD\",\"String\",\"public\",\"static\",\"R\",\"IOException\",\"\",\"R extends Number\",\"19\"",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Sample.Nested\",\"CLASS\",\"convert\",\"METHOD\",\"int\",\"protected\",\"\",\"int\",\"\",\"\",\"\",\"24\"",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"CellShape\",\"ENUM\",\"<init>\",\"CONSTRUCTOR\",\"int, String\",\"private\",\"\",\"\",\"\",\"\",\"\",\"33\"",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Canonical\",\"RECORD\",\"<init>\",\"CONSTRUCTOR\",\"String\",\"package-private\",\"\",\"\",\"\",\"@Marker(\"\"canonical\"\")\",\"\",\"38\"",
                "\"test\",\"app/src/test/java/example/TestFixture.java\",\"example\",\"TestFixture\",\"CLASS\",\"<init>\",\"CONSTRUCTOR\",\"\",\"package-private\",\"\",\"\",\"\",\"\",\"\",\"4\""
        )) + System.lineSeparator();
        ProcessResult firstResult = runHelper();
        byte[] firstInventory = Files.readAllBytes(temporaryDirectory.resolve(OUTPUT_PATH));
        ProcessResult secondResult = runHelper();
        byte[] secondInventory = Files.readAllBytes(temporaryDirectory.resolve(OUTPUT_PATH));

        assertAll(
                () -> assertEquals(0, firstResult.exitCode()),
                () -> assertEquals(
                        "Generated " + OUTPUT_PATH + " with 8 declarations." + System.lineSeparator(),
                        firstResult.output()
                ),
                () -> assertArrayEquals(
                        expectedInventory.getBytes(StandardCharsets.UTF_8),
                        firstInventory,
                        () -> "Actual inventory:" + System.lineSeparator() + new String(firstInventory, StandardCharsets.UTF_8)
                ),
                () -> assertEquals(0, secondResult.exitCode()),
                () -> assertEquals(firstResult.output(), secondResult.output()),
                () -> assertArrayEquals(firstInventory, secondInventory)
        );
    }

    @Test
    void testDistinguishesRecordConstructorVisibility() throws Exception {
        writeSource("main", "PublicRecord.java", """
                package example;
                
                public record PublicRecord(String value) {
                    PublicRecord {
                    }
                
                    PublicRecord() {
                        this("");
                    }
                }
                """);
        Files.createDirectories(temporaryDirectory.resolve("app/src/test/java"));
        String expectedInventory = String.join(System.lineSeparator(), List.of(
                "source_set,source_path,package_name,declaring_type,declaring_type_kind,member_name,member_kind,parameter_types,visibility,modifiers,return_type,throws_types,annotations,type_parameters,line_number",
                "\"main\",\"app/src/main/java/example/PublicRecord.java\",\"example\",\"PublicRecord\",\"RECORD\",\"<init>\",\"COMPACT_CONSTRUCTOR\",\"\",\"public\",\"\",\"\",\"\",\"\",\"\",\"4\"",
                "\"main\",\"app/src/main/java/example/PublicRecord.java\",\"example\",\"PublicRecord\",\"RECORD\",\"<init>\",\"CONSTRUCTOR\",\"\",\"package-private\",\"\",\"\",\"\",\"\",\"\",\"7\""
        )) + System.lineSeparator();

        ProcessResult result = runHelper();
        byte[] inventory = Files.readAllBytes(temporaryDirectory.resolve(OUTPUT_PATH));

        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertEquals(
                        "Generated " + OUTPUT_PATH + " with 2 declarations." + System.lineSeparator(),
                        result.output()
                ),
                () -> assertArrayEquals(expectedInventory.getBytes(StandardCharsets.UTF_8), inventory)
        );
    }

    @Test
    void testRecognizesCompactConstructorAfterQualifiedSameNameAnnotation() throws Exception {
        writeSource("main", "Sample.java", """
                package example;
                
                final class audit {
                    @interface Sample {
                    }
                }
                
                record Sample(int value) {
                    @audit.Sample()
                    Sample {
                    }
                }
                """);
        Files.createDirectories(temporaryDirectory.resolve("app/src/test/java"));
        String expectedInventory = String.join(System.lineSeparator(), List.of(
                "source_set,source_path,package_name,declaring_type,declaring_type_kind,member_name,member_kind,parameter_types,visibility,modifiers,return_type,throws_types,annotations,type_parameters,line_number",
                "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Sample\",\"RECORD\",\"<init>\",\"COMPACT_CONSTRUCTOR\",\"\",\"package-private\",\"\",\"\",\"\",\"@audit.Sample\",\"\",\"9\""
        )) + System.lineSeparator();

        ProcessResult result = runHelper();
        byte[] inventory = Files.readAllBytes(temporaryDirectory.resolve(OUTPUT_PATH));

        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertEquals(
                        "Generated " + OUTPUT_PATH + " with 1 declarations." + System.lineSeparator(),
                        result.output()
                ),
                () -> assertArrayEquals(
                        expectedInventory.getBytes(StandardCharsets.UTF_8),
                        inventory,
                        () -> "Actual inventory:" + System.lineSeparator() + new String(inventory, StandardCharsets.UTF_8)
                )
        );
    }

    @Test
    void testRejectsParserErrorsWithoutReplacingExistingInventory() throws Exception {
        writeSource("main", "Broken.java", """
                package example;
                
                class Broken {
                    void broken( {
                    }
                }
                """);
        Files.createDirectories(temporaryDirectory.resolve("app/src/test/java"));
        Path outputPath = temporaryDirectory.resolve(OUTPUT_PATH);
        Files.createDirectories(outputPath.getParent());
        byte[] existingInventory = "existing inventory\n".getBytes(StandardCharsets.UTF_8);
        Files.write(outputPath, existingInventory);

        ProcessResult result = runHelper();

        assertAll(
                () -> assertEquals(2, result.exitCode()),
                () -> assertTrue(result.output().contains("FAIL parse:")),
                () -> assertTrue(result.output().contains("Broken.java")),
                () -> assertArrayEquals(existingInventory, Files.readAllBytes(outputPath))
        );
    }

    private record ProcessResult(int exitCode, String output) {
    }

}
