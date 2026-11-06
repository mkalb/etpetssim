package de.mkalb.etpetssim.core;

import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Reads and writes persistent application-wide settings.
 *
 * <p>Settings are stored as a {@link Properties} file named {@link #FILE_NAME} in the application data directory.
 * Reading and writing never throw: a missing file is normal, and every failure is logged with
 * {@link AppLogger#warn(String)} so that the application can continue with its defaults.
 *
 * <p>Currently the only setting is the UI locale, stored as the stable {@link CountryLocale#localeCode()}.
 * Writing a setting keeps all other keys already present in the file.
 */
public final class AppSettings {

    /**
     * File name of the settings file in the application data directory.
     */
    public static final String FILE_NAME = "settings.properties";

    private static final String KEY_LOCALE = "locale";

    private AppSettings() {
    }

    /**
     * Reads the saved locale from the settings file in the application data directory.
     *
     * @return the saved locale, or empty if it is missing, unknown, or the file cannot be resolved or read
     */
    public static Optional<CountryLocale> readLocale() {
        return resolveSettingsFile().flatMap(AppSettings::readLocale);
    }

    /**
     * Reads the saved locale from the given settings file.
     *
     * @param file the settings file
     * @return the saved locale, or empty if the file or key is missing, the code is unknown, or the file is unreadable
     */
    static Optional<CountryLocale> readLocale(Path file) {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        Optional<Properties> properties = load(file);
        if (properties.isEmpty()) {
            return Optional.empty();
        }
        String code = properties.get().getProperty(KEY_LOCALE);
        if (code == null) {
            return Optional.empty();
        }
        Optional<CountryLocale> locale = CountryLocale.fromLocaleCode(code);
        if (locale.isEmpty()) {
            AppLogger.warnf("AppSettings: Ignoring unknown locale code '%s' in %s", code, file);
        }
        return locale;
    }

    /**
     * Saves the locale to the settings file in the application data directory.
     *
     * @param locale the locale to save
     * @return {@code true} if the setting was written; otherwise {@code false}
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean writeLocale(CountryLocale locale) {
        return resolveSettingsFile().map(file -> writeLocale(file, locale)).orElse(false);
    }

    /**
     * Writes the locale to the given settings file and keeps all other keys already present.
     *
     * <p>If the existing file is unreadable, its content is replaced.
     *
     * @param file   the settings file
     * @param locale the locale to save
     * @return {@code true} if the setting was written; otherwise {@code false}
     */
    static boolean writeLocale(Path file, CountryLocale locale) {
        Properties properties = Files.exists(file) ? load(file).orElseGet(Properties::new) : new Properties();
        properties.setProperty(KEY_LOCALE, locale.localeCode());
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            properties.store(writer, null);
            return true;
        } catch (IOException e) {
            AppLogger.warnf("AppSettings: Failed to write %s: %s", file, e);
            return false;
        }
    }

    /**
     * Resolves the absolute path of the settings file in the application data directory.
     *
     * <p>The file itself may not exist yet; it is created when a setting is saved for the first time. Resolving the path
     * creates the application data directory if it is missing.
     *
     * @return the path of the settings file, or empty if the application data directory cannot be resolved; the
     * failure is logged
     */
    public static Optional<Path> resolveSettingsFile() {
        try {
            return Optional.of(AppStorage.getAppDataFile(FILE_NAME, AppStorage.OperatingSystem.detect()));
        } catch (IOException | IllegalArgumentException e) {
            AppLogger.warnf("AppSettings: Failed to resolve %s: %s", FILE_NAME, e);
            return Optional.empty();
        }
    }

    private static Optional<Properties> load(Path file) {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
            return Optional.of(properties);
        } catch (IOException | IllegalArgumentException e) {
            AppLogger.warnf("AppSettings: Failed to read %s: %s", file, e);
            return Optional.empty();
        }
    }

}
