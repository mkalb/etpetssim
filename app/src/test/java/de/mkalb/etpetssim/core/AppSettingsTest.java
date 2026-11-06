package de.mkalb.etpetssim.core;

import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AppSettingsTest {

    @TempDir
    Path tempDir;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static Properties loadProperties(Path file) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            properties.load(reader);
        }
        return properties;
    }

    private Path settingsFile() {
        return tempDir.resolve(AppSettings.FILE_NAME);
    }

    @Test
    void testReadLocaleWithFile() throws IOException {
        Files.writeString(settingsFile(), "locale=de_DE\n");

        assertEquals(Optional.of(CountryLocale.DE_DE), AppSettings.readLocale(settingsFile()));
    }

    @Test
    void testReadLocaleWithFileReturnsEmptyForMissingFile() {
        assertEquals(Optional.empty(), AppSettings.readLocale(settingsFile()));
    }

    @Test
    void testReadLocaleWithFileReturnsEmptyForMissingKey() throws IOException {
        Files.writeString(settingsFile(), "other=value\n");

        assertEquals(Optional.empty(), AppSettings.readLocale(settingsFile()));
    }

    @Test
    void testReadLocaleWithFileReturnsEmptyForUnknownLocaleCode() throws IOException {
        Path unsupportedFile = tempDir.resolve("unsupported.properties");
        Path languageOnlyFile = tempDir.resolve("languageOnly.properties");
        Files.writeString(unsupportedFile, "locale=fr_FR\n");
        Files.writeString(languageOnlyFile, "locale=de\n");

        assertAll(
                () -> assertEquals(Optional.empty(), AppSettings.readLocale(unsupportedFile)),
                () -> assertEquals(Optional.empty(), AppSettings.readLocale(languageOnlyFile))
        );
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    @Test
    void testReadLocaleWithFileReturnsEmptyForUnreadableContent() throws IOException {
        Files.write(settingsFile(), new byte[]{'l', 'o', 'c', 'a', 'l', 'e', '=', (byte) 0xFF, (byte) 0xFE});

        assertEquals(Optional.empty(), AppSettings.readLocale(settingsFile()));
    }

    @Test
    void testReadLocaleWithFileReturnsEmptyForMalformedEscape() throws IOException {
        Files.writeString(settingsFile(), "locale=\\uXYZ\n");

        assertEquals(Optional.empty(), AppSettings.readLocale(settingsFile()));
    }

    @Test
    void testReadLocaleWithFileReturnsEmptyForDirectory() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("directory"));

        assertEquals(Optional.empty(), AppSettings.readLocale(directory));
    }

    @Test
    void testWriteLocaleWithFile() {
        boolean written = AppSettings.writeLocale(settingsFile(), CountryLocale.DE_DE);

        assertAll(
                () -> assertTrue(written),
                () -> assertEquals("de_DE", loadProperties(settingsFile()).getProperty("locale")),
                () -> assertEquals(Optional.of(CountryLocale.DE_DE), AppSettings.readLocale(settingsFile()))
        );
    }

    @Test
    void testWriteLocaleWithFileReplacesSavedLocale() throws IOException {
        AppSettings.writeLocale(settingsFile(), CountryLocale.DE_DE);
        AppSettings.writeLocale(settingsFile(), CountryLocale.EN_US);

        assertEquals("en_US", loadProperties(settingsFile()).getProperty("locale"));
    }

    @Test
    void testWriteLocaleWithFileKeepsOtherKeys() throws IOException {
        Files.writeString(settingsFile(), "other=value\nlocale=en_US\n");

        AppSettings.writeLocale(settingsFile(), CountryLocale.DE_DE);
        Properties properties = loadProperties(settingsFile());

        assertAll(
                () -> assertEquals("value", properties.getProperty("other")),
                () -> assertEquals("de_DE", properties.getProperty("locale"))
        );
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    @Test
    void testWriteLocaleWithFileReplacesUnreadableContent() throws IOException {
        Files.write(settingsFile(), new byte[]{'o', 't', 'h', 'e', 'r', '=', (byte) 0xFF, (byte) 0xFE});

        boolean written = AppSettings.writeLocale(settingsFile(), CountryLocale.EN_US);

        assertAll(
                () -> assertTrue(written),
                () -> assertEquals(Optional.of(CountryLocale.EN_US), AppSettings.readLocale(settingsFile()))
        );
    }

    @Test
    void testWriteLocaleWithFileReturnsFalseForDirectory() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("directory"));

        assertFalse(AppSettings.writeLocale(directory, CountryLocale.DE_DE));
    }

    @Test
    void testResolveSettingsFile() {
        Path file = AppSettings.resolveSettingsFile().orElseThrow();

        assertAll(
                () -> assertTrue(file.isAbsolute()),
                () -> assertEquals(AppSettings.FILE_NAME, file.getFileName().toString()),
                () -> assertTrue(Files.isDirectory(file.getParent()))
        );
    }

}
