package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AppStorage utility class.
 */
class AppStorageTest {

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

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUpBeforeEach() {
        createdFile = null;
        tempFile = null;
    }

    /**
     * Cleans up the test environment after each test.
     * @throws IOException if an I/O error occurs while deleting files
     */
    @AfterEach
    void tearDownAfterEach() throws IOException {
        if ((createdFile != null) && Files.exists(createdFile)) {
            Files.deleteIfExists(createdFile);
        }
        if ((tempFile != null) && Files.exists(tempFile)) {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Creates a unique application data file name for testing.
     * @return a unique file name with a prefix, a random UUID, and a suffix
     */
    private String createUniqueAppFileName() {
        return FILE_PREFIX + UUID.randomUUID() + APP_FILE_SUFFIX;
    }

    /**
     * Tests creation and deletion of an application data file.
     */
    @Test
    void testCreateAndDeleteAppDataFile() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();

        createdFile = AppStorage.createAppDataFile(uniqueAppFileName, OS);
        assertTrue(Files.exists(createdFile), "File should exist after creation");
        assertTrue(Files.isRegularFile(createdFile), "File should be a regular file");
        assertTrue(Files.isWritable(createdFile), "File should be writable");

        boolean deleted = AppStorage.deleteAppDataFile(uniqueAppFileName, OS);
        assertTrue(deleted, "File should be deleted successfully");
        assertFalse(Files.exists(createdFile), "File should no longer exist");
    }

    /**
     * Tests retrieval of the application data file path.
     */
    @Test
    void testGetAppDataFilePath() throws IOException {
        String uniqueAppFileName = createUniqueAppFileName();

        Path path = AppStorage.getAppDataFile(uniqueAppFileName, OS);
        assertNotNull(path);
        assertTrue(path.toString().endsWith(uniqueAppFileName));
    }

    /**
     * Tests deletion of a non-existent file.
     */
    @Test
    void testDeleteNonExistentFileReturnsFalse() throws IOException {
        assertFalse(Files.exists(AppStorage.getAppDataFile("nonexistent.txt", OS)));
        boolean deleted = AppStorage.deleteAppDataFile("nonexistent.txt", OS);
        assertFalse(deleted, "Deleting a non-existent file should return false");
    }

    /**
     * Tests retrieval of the log file path.
     */
    @Test
    void testGetLogFilePath() throws IOException {
        Path path = AppStorage.getLogFile(LOG_FILE_NAME, OS);
        assertNotNull(path);
        assertTrue(path.toString().endsWith(LOG_FILE_NAME));
    }

    /**
     * Tests creation of a temporary cache file.
     */
    @Test
    void testCreateTempCacheFile() throws IOException {
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

    /**
     * Tests that null parameters throw NullPointerException.
     */
    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullParametersThrowException() {
        assertThrows(NullPointerException.class, () -> AppStorage.getAppDataFile(null, OS));
        assertThrows(NullPointerException.class, () -> AppStorage.createAppDataFile(null, OS));
        assertThrows(NullPointerException.class, () -> AppStorage.deleteAppDataFile(null, OS));
        assertThrows(NullPointerException.class, () -> AppStorage.getLogFile(null, OS));
        assertThrows(NullPointerException.class, () -> AppStorage.createTempCacheFile(null, TEMP_SUFFIX, OS));
    }

}
