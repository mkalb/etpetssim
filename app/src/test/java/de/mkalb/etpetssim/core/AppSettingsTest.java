package de.mkalb.etpetssim.core;

import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AppSettingsTest {

    @TempDir
    Path tempDir;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testRoundTrip() {
        Path file = tempDir.resolve(AppSettings.FILE_NAME);
        assertTrue(AppSettings.writeLocale(file, CountryLocale.DE_DE));
        assertEquals(Optional.of(CountryLocale.DE_DE), AppSettings.readLocale(file));
        assertTrue(AppSettings.writeLocale(file, CountryLocale.EN_US));
        assertEquals(Optional.of(CountryLocale.EN_US), AppSettings.readLocale(file));
    }

    @Test
    void testWriteStoresLocaleCode() throws IOException {
        Path file = tempDir.resolve(AppSettings.FILE_NAME);
        AppSettings.writeLocale(file, CountryLocale.DE_DE);
        assertTrue(Files.readString(file).contains("locale=de_DE"));
    }

    @Test
    void testWritePreservesOtherKeys() throws IOException {
        Path file = tempDir.resolve(AppSettings.FILE_NAME);
        Files.writeString(file, "other=value\nlocale=en_US\n");
        assertTrue(AppSettings.writeLocale(file, CountryLocale.DE_DE));
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(file)) {
            properties.load(reader);
        }
        assertEquals("value", properties.getProperty("other"));
        assertEquals("de_DE", properties.getProperty(AppSettings.KEY_LOCALE));
    }

    @Test
    void testMissingFile() {
        assertEquals(Optional.empty(), AppSettings.readLocale(tempDir.resolve(AppSettings.FILE_NAME)));
    }

    @Test
    void testMissingKey() throws IOException {
        Path file = tempDir.resolve(AppSettings.FILE_NAME);
        Files.writeString(file, "other=value\n");
        assertEquals(Optional.empty(), AppSettings.readLocale(file));
    }

    @Test
    void testUnreadableContent() throws IOException {
        Path file = tempDir.resolve(AppSettings.FILE_NAME);
        Files.writeString(file, "locale=de_DE\\uZZZZ\n");
        assertEquals(Optional.empty(), AppSettings.readLocale(file));
        assertTrue(AppSettings.writeLocale(file, CountryLocale.EN_US));
        assertEquals(Optional.of(CountryLocale.EN_US), AppSettings.readLocale(file));
    }

    @Test
    void testUnknownLocaleCode() throws IOException {
        Path file = tempDir.resolve(AppSettings.FILE_NAME);
        Files.writeString(file, "locale=fr_FR\n");
        assertEquals(Optional.empty(), AppSettings.readLocale(file));
        Files.writeString(file, "locale=de\n");
        assertEquals(Optional.empty(), AppSettings.readLocale(file));
    }

    @Test
    void testUnwritableFile() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("dir"));
        assertFalse(AppSettings.writeLocale(directory, CountryLocale.DE_DE));
        assertEquals(Optional.empty(), AppSettings.readLocale(directory));
    }

}
