package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AppStorageTest {

    private static final String FILE_PREFIX = "AppStorageTest_";
    private static final String APP_FILE_SUFFIX = ".txt";
    private static final String LOG_FILE_NAME = "logfile.log";
    private static final String TEMP_SUFFIX = ".tmp";

    private static final AppStorage.OperatingSystem OS = AppStorage.OperatingSystem.detect();

    private Path createdFile;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static String createUniqueAppFileName() {
        return FILE_PREFIX + UUID.randomUUID() + APP_FILE_SUFFIX;
    }

    @AfterEach
    void tearDownAfterEach() throws IOException {
        if (createdFile != null) {
            Files.deleteIfExists(createdFile);
        }
    }

    @Test
    void testGetAppDataFile() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();

        Path path = AppStorage.getAppDataFile(uniqueAppFileName, OS);

        assertAll(
                () -> assertTrue(path.isAbsolute()),
                () -> assertTrue(path.endsWith(uniqueAppFileName)),
                () -> assertFalse(Files.exists(path))
        );
    }

    @Test
    void testGetAppDataFileRejectsInvalidFileName() {
        String pathTraversalName = Path.of("..", "outside.txt").toString();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getAppDataFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getAppDataFile("   ", OS)),
                () -> assertThrows(IOException.class, () -> AppStorage.getAppDataFile(pathTraversalName, OS))
        );
    }

    @Test
    void testCreateAppDataFile() throws IOException {
        createdFile = AppStorage.createAppDataFile(createUniqueAppFileName(), OS);

        assertAll(
                () -> assertTrue(Files.isRegularFile(createdFile)),
                () -> assertTrue(Files.isWritable(createdFile))
        );
    }

    @Test
    void testCreateAppDataFileThrowsForExistingFile() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();
        createdFile = AppStorage.createAppDataFile(uniqueAppFileName, OS);

        assertThrows(FileAlreadyExistsException.class, () -> AppStorage.createAppDataFile(uniqueAppFileName, OS));
    }

    @Test
    void testCreateAppDataFileRejectsInvalidFileName() {
        String pathTraversalName = Path.of("..", "outside.txt").toString();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.createAppDataFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.createAppDataFile("   ", OS)),
                () -> assertThrows(IOException.class, () -> AppStorage.createAppDataFile(pathTraversalName, OS))
        );
    }

    @Test
    void testDeleteAppDataFile() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();
        createdFile = AppStorage.createAppDataFile(uniqueAppFileName, OS);

        boolean deleted = AppStorage.deleteAppDataFile(uniqueAppFileName, OS);

        assertAll(
                () -> assertTrue(deleted),
                () -> assertFalse(Files.exists(createdFile))
        );
    }

    @Test
    void testDeleteAppDataFileReturnsFalseForMissingFile() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();

        assertFalse(AppStorage.deleteAppDataFile(uniqueAppFileName, OS));
    }

    @Test
    void testDeleteAppDataFileRejectsInvalidFileName() {
        String pathTraversalName = Path.of("..", "outside.txt").toString();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.deleteAppDataFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.deleteAppDataFile("   ", OS)),
                () -> assertThrows(IOException.class, () -> AppStorage.deleteAppDataFile(pathTraversalName, OS))
        );
    }

    @Test
    void testGetLogFile() throws IOException {
        Path path = AppStorage.getLogFile(LOG_FILE_NAME, OS);

        assertAll(
                () -> assertTrue(path.isAbsolute()),
                () -> assertTrue(path.endsWith(LOG_FILE_NAME))
        );
    }

    @Test
    void testGetLogFileRejectsInvalidFileName() {
        String pathTraversalName = Path.of("..", "outside.log").toString();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getLogFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getLogFile("   ", OS)),
                () -> assertThrows(IOException.class, () -> AppStorage.getLogFile(pathTraversalName, OS))
        );
    }

    @Test
    void testGetLogFileRejectsAbsolutePath() {
        String absolutePathName = Path.of(System.getProperty("java.io.tmpdir"), "outside.log").toString();
        assertThrows(IOException.class, () -> AppStorage.getLogFile(absolutePathName, OS));
    }

    @Test
    void testCreateTempCacheFile() throws IOException {
        String uniqueFilePrefix = FILE_PREFIX + UUID.randomUUID();

        createdFile = AppStorage.createTempCacheFile(uniqueFilePrefix, TEMP_SUFFIX, OS);

        String fileName = createdFile.getFileName().toString();
        assertAll(
                () -> assertTrue(Files.isRegularFile(createdFile)),
                () -> assertTrue(Files.isWritable(createdFile)),
                () -> assertTrue(fileName.startsWith(uniqueFilePrefix)),
                () -> assertTrue(fileName.endsWith(TEMP_SUFFIX))
        );
    }

    @Test
    void testCreateTempCacheFileUsesDefaultSuffixForNullSuffix() throws IOException {
        createdFile = AppStorage.createTempCacheFile(FILE_PREFIX + UUID.randomUUID(), null, OS);

        assertTrue(createdFile.getFileName().toString().endsWith(TEMP_SUFFIX));
    }

}
