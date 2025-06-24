package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppStorageTest {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_FILE_NAME = "testfile.txt";
    private static final String LOG_FILE_NAME = "logfile.log";
    private static final String TEMP_PREFIX = "temp";
    private static final String TEMP_SUFFIX = ".tmp";

    private static final AppStorage.OperatingSystem OS = AppStorage.OperatingSystem.detect();

    @Test
    void testCreateAndDeleteAppDataFile() throws IOException {
        Path file = AppStorage.createAppDataFile(TEST_FILE_NAME, OS);
        assertTrue(Files.exists(file), "File should exist after creation");
        assertTrue(Files.isRegularFile(file), "File should be a regular file");

        boolean deleted = AppStorage.deleteAppDataFile(TEST_FILE_NAME, OS);
        assertTrue(deleted, "File should be deleted successfully");
        assertFalse(Files.exists(file), "File should no longer exist");
    }

    @Test
    void testGetAppDataFilePath() throws IOException {
        Path path = AppStorage.getAppDataFile(TEST_FILE_NAME, OS);
        assertNotNull(path);
        assertTrue(path.toString().endsWith(TEST_FILE_NAME));
    }

    @Test
    void testDeleteNonExistentFileReturnsFalse() throws IOException {
        assertFalse(Files.exists(AppStorage.getAppDataFile("nonexistent.txt", OS)), "Non-existent file should not exist");

        boolean deleted = AppStorage.deleteAppDataFile("nonexistent.txt", OS);
        assertFalse(deleted, "Deleting a non-existent file should return false");
    }

    @Test
    void testGetLogFilePath() throws IOException {
        Path path = AppStorage.getLogFile(LOG_FILE_NAME, OS);
        assertNotNull(path);
        assertTrue(path.toString().endsWith(LOG_FILE_NAME));
    }

    @Test
    void testCreateTempCacheFile() throws IOException {
        Path tempFile = AppStorage.createTempCacheFile(TEMP_PREFIX, TEMP_SUFFIX, OS);
        assertNotNull(tempFile);
        assertTrue(Files.exists(tempFile));
        assertTrue(tempFile.getFileName().toString().startsWith(TEMP_PREFIX));
        assertTrue(tempFile.getFileName().toString().endsWith(TEMP_SUFFIX));

        Files.deleteIfExists(tempFile);
    }

}
