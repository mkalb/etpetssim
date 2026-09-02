package de.mkalb.etpetssim.skills;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
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
    void testGeneratesInventoryForExplicitMainAndTestDeclarations() throws Exception {
        writeSource("main", "Sample.java", """
                package example;
                
                import java.io.IOException;
                
                @interface Marker {
                    String value();
                }
                
                public record Sample<T>(String value) {
                    @Marker("compact")
                    public Sample {
                    }
                
                    @Marker("convert")
                    public static <R extends Number> R convert(java.util.List<String> values, String... names) throws IOException {
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
                """);
        writeSource("test", "TestFixture.java", """
                package example;
                
                final class TestFixture {
                    TestFixture() {
                    }
                }
                """);

        ProcessResult result = runHelper();
        String inventory = Files.readString(temporaryDirectory.resolve(OUTPUT_PATH), StandardCharsets.UTF_8);
        String expectedHeader = "source_set,source_path,package_name,declaring_type,declaring_type_kind,member_name,member_kind,"
                + "parameter_types,visibility,modifiers,return_type,throws_types,annotations,type_parameters,line_number"
                + System.lineSeparator();
        int headerLineFeed = inventory.indexOf('\n');
        String headerLineSeparator = inventory.substring(
                (headerLineFeed - System.lineSeparator().length()) + 1,
                headerLineFeed + 1
        );

        assertAll(
                () -> assertEquals(0, result.exitCode()),
                () -> assertEquals(
                        "Generated " + OUTPUT_PATH + " with 6 declarations." + System.lineSeparator(),
                        result.output()
                ),
                () -> assertTrue(inventory.startsWith(expectedHeader)),
                () -> assertTrue(inventory.contains(
                        "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Sample\",\"RECORD\",\"<init>\",\"COMPACT_CONSTRUCTOR\""
                )),
                () -> assertTrue(inventory.contains(
                        "\"convert\",\"METHOD\",\"java.util.List<String>, String...\",\"public\",\"static\",\"R\",\"IOException\",\"@Marker(\"\"convert\"\")\",\"R extends Number\""
                )),
                () -> assertTrue(inventory.contains(
                        "\"main\",\"app/src/main/java/example/Sample.java\",\"example\",\"Sample.Nested\",\"CLASS\",\"convert\",\"METHOD\",\"int\",\"protected\""
                )),
                () -> assertTrue(inventory.contains(
                        "\"test\",\"app/src/test/java/example/TestFixture.java\",\"example\",\"TestFixture\",\"CLASS\",\"<init>\",\"CONSTRUCTOR\",\"\",\"package-private\""
                )),
                () -> assertFalse(inventory.contains("\"Sample\",\"RECORD\",\"value\",\"METHOD\"")),
                () -> assertEquals(System.lineSeparator(), headerLineSeparator),
                () -> assertTrue(inventory.endsWith(System.lineSeparator()))
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
