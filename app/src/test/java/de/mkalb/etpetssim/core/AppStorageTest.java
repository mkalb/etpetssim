package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class AppStorageTest {

    private static final String FILE_PREFIX = "AppStorageTest_";
    private static final String APP_FILE_SUFFIX = ".txt";
    private static final String LOG_FILE_NAME = "logfile.log";
    private static final String TEMP_SUFFIX = ".tmp";

    private static final AppStorage.OperatingSystem OS = AppStorage.OperatingSystem.detect();

    private Path createdFile;
    private Path tempFile;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @BeforeEach
    void setUpBeforeEach() {
        createdFile = null;
        tempFile = null;
    }

    @AfterEach
    void tearDownAfterEach() throws IOException {
        if ((createdFile != null) && Files.exists(createdFile)) {
            Files.deleteIfExists(createdFile);
        }
        if ((tempFile != null) && Files.exists(tempFile)) {
            Files.deleteIfExists(tempFile);
        }
    }

    private String createUniqueAppFileName() {
        return FILE_PREFIX + UUID.randomUUID() + APP_FILE_SUFFIX;
    }

    @Test
    void testGetAppDataFileReturnsPath() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();

        Path path = AppStorage.getAppDataFile(uniqueAppFileName, OS);
        assertNotNull(path);
        assertTrue(path.toString().endsWith(uniqueAppFileName));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetAppDataFileRejectsInvalidFileName() {
        String pathTraversalName = Path.of("..", "outside.txt").toString();
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> AppStorage.getAppDataFile(null, OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getAppDataFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getAppDataFile("   ", OS)),
                () -> assertThrows(IOException.class, () -> AppStorage.getAppDataFile(pathTraversalName, OS))
        );
    }

    @Test
    void testCreateAppDataFileCreatesWritableFile() throws IOException {
        createdFile = AppStorage.createAppDataFile(createUniqueAppFileName(), OS);
        assertTrue(Files.exists(createdFile), "File should exist after creation");
        assertTrue(Files.isRegularFile(createdFile), "File should be a regular file");
        assertTrue(Files.isWritable(createdFile), "File should be writable");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testCreateAppDataFileRejectsInvalidFileName() {
        String pathTraversalName = Path.of("..", "outside.txt").toString();
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> AppStorage.createAppDataFile(null, OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.createAppDataFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.createAppDataFile("   ", OS)),
                () -> assertThrows(IOException.class, () -> AppStorage.createAppDataFile(pathTraversalName, OS))
        );
    }

    @Test
    void testDeleteAppDataFileReturnsTrueForExistingFile() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();
        createdFile = AppStorage.createAppDataFile(uniqueAppFileName, OS);

        boolean deleted = AppStorage.deleteAppDataFile(uniqueAppFileName, OS);
        assertTrue(deleted, "File should be deleted successfully");
        assertFalse(Files.exists(createdFile), "File should no longer exist");
    }

    @Test
    void testDeleteAppDataFileReturnsFalseForMissingFile() throws IOException {
        assertFalse(Files.exists(AppStorage.getAppDataFile("nonexistent.txt", OS)));
        boolean deleted = AppStorage.deleteAppDataFile("nonexistent.txt", OS);
        assertFalse(deleted, "Deleting a non-existent file should return false");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testDeleteAppDataFileRejectsInvalidFileName() {
        String pathTraversalName = Path.of("..", "outside.txt").toString();
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> AppStorage.deleteAppDataFile(null, OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.deleteAppDataFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.deleteAppDataFile("   ", OS)),
                () -> assertThrows(IOException.class, () -> AppStorage.deleteAppDataFile(pathTraversalName, OS))
        );
    }

    @Test
    void testGetLogFileReturnsPath() throws IOException {
        Path path = AppStorage.getLogFile(LOG_FILE_NAME, OS);
        assertNotNull(path);
        assertTrue(path.toString().endsWith(LOG_FILE_NAME));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetLogFileRejectsInvalidFileName() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> AppStorage.getLogFile(null, OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getLogFile("", OS)),
                () -> assertThrows(IllegalArgumentException.class, () -> AppStorage.getLogFile("   ", OS))
        );
    }

    @Test
    void testGetLogFileRejectsAbsolutePath() {
        String absolutePathName = Path.of(System.getProperty("java.io.tmpdir"), "outside.log").toString();
        assertThrows(IOException.class, () -> AppStorage.getLogFile(absolutePathName, OS));
    }

    @Test
    void testCreateTempCacheFileCreatesWritableFile() throws IOException {
        String uniqueFilePrefix = FILE_PREFIX + UUID.randomUUID();

        tempFile = AppStorage.createTempCacheFile(uniqueFilePrefix, TEMP_SUFFIX, OS);
        assertNotNull(tempFile);
        assertTrue(Files.exists(tempFile));
        assertTrue(Files.isRegularFile(tempFile));
        assertTrue(Files.isWritable(tempFile));
        assertTrue(tempFile.getFileName().toString().startsWith(uniqueFilePrefix));
        assertTrue(tempFile.getFileName().toString().endsWith(TEMP_SUFFIX));
        assertTrue(Files.deleteIfExists(tempFile));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testCreateTempCacheFileRejectsNullPrefix() {
        assertThrows(NullPointerException.class, () -> AppStorage.createTempCacheFile(null, TEMP_SUFFIX, OS));
    }

}
