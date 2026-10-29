package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Provides application-wide localization services.
 *
 * <p>Manages the active {@link Locale} and its corresponding {@link ResourceBundle}. It must be initialized exactly
 * once during startup.
 */
public final class AppLocalization {

    /**
     * Default locale used by the application if no specific locale is provided.
     */
    public static final Locale DEFAULT_LOCALE = Locale.US;
    /**
     * Placeholder for exceptions when a key is not found in the ResourceBundle or when formatting fails.
     */
    public static final String PLACEHOLDER_FOR_EXCEPTIONS = "########";

    @SuppressWarnings("FieldNamingConvention")
    private static volatile @Nullable LocalizationState state;

    private AppLocalization() {
    }

    /**
     * Returns all supported locales.
     *
     * @return an immutable list containing all supported locale descriptors
     */
    public static List<CountryLocale> supportedLocales() {
        return List.of(CountryLocale.values());
    }

    /**
     * Returns whether localization has been initialized.
     *
     * @return {@code true} when the locale and its bundle are available
     */
    public static boolean isInitialized() {
        return state != null;
    }

    /**
     * Initializes localization using a user argument and {@link Locale#getDefault()}.
     *
     * @param localeArgument optional user-provided locale code
     * @throws IllegalStateException if already initialized
     * @see #initialize(String, Locale)
     */
    public static synchronized void initialize(@Nullable String localeArgument) {
        initialize(localeArgument, Locale.getDefault());
    }

    /**
     * Initializes localization using a user argument and fallback locale.
     *
     * <p>Resolution order is: locale argument, {@code defaultLocale}, then {@link #DEFAULT_LOCALE}. The first matching
     * supported locale is selected.
     *
     * @param localeArgument optional user-provided locale code (for example {@code en_US} or {@code en})
     * @param defaultLocale  fallback locale used when {@code localeArgument} does not resolve
     * @throws IllegalStateException if already initialized or no bundle can be loaded for the resolved locale
     */
    public static synchronized void initialize(@Nullable String localeArgument, Locale defaultLocale) {
        if (isInitialized()) {
            throw new IllegalStateException("AppLocalization is already initialized.");
        }

        Locale resolvedLocale = resolveLocaleFromArgument(localeArgument)
                .or(() -> resolveLocaleFromDefault(defaultLocale))
                .orElse(DEFAULT_LOCALE);
        ResourceBundle resolvedBundle = AppResources.getBundle(resolvedLocale).orElseThrow(() ->
                new IllegalStateException("ResourceBundle for locale " + resolvedLocale + " could not be loaded."));
        state = new LocalizationState(resolvedLocale, resolvedBundle);
        AppLogger.info("AppLocalization: Initialized with locale: " + resolvedLocale + " and loaded ResourceBundle");
    }

    /**
     * Returns the active locale.
     *
     * @return the active locale
     * @throws IllegalStateException if localization has not been initialized
     */
    public static Locale locale() {
        return requireState().locale();
    }

    /**
     * Returns the active resource bundle.
     *
     * @return the active bundle
     * @throws IllegalStateException if localization has not been initialized
     */
    public static ResourceBundle bundle() {
        return requireState().bundle();
    }

    /**
     * Returns localized text for a key.
     *
     * @param key the resource key
     * @return the localized text, or {@link #PLACEHOLDER_FOR_EXCEPTIONS} if the key is missing
     * @throws IllegalStateException if localization has not been initialized
     */
    public static String getText(String key) {
        ResourceBundle activeBundle = bundle();
        try {
            return activeBundle.getString(key);
        } catch (MissingResourceException e) {
            AppLogger.error(e, "AppLocalization: Key not found in ResourceBundle: " + key);
            return PLACEHOLDER_FOR_EXCEPTIONS;
        }
    }

    /**
     * Returns localized text for a key if present and non-blank.
     *
     * @param key the resource key
     * @return an {@link Optional} containing non-blank localized text, or empty otherwise
     * @throws IllegalStateException if localization has not been initialized
     */
    public static Optional<String> getOptionalText(String key) {
        ResourceBundle activeBundle = bundle();
        try {
            String text = activeBundle.getString(key);
            if (text.isBlank()) {
                // AppLogger.info("AppLocalization: Key found in ResourceBundle but text is empty or blank: " + key);
                return Optional.empty();
            }
            return Optional.of(text);
        } catch (MissingResourceException e) {
            // AppLogger.info("AppLocalization: Key not found in ResourceBundle: " + key);
            return Optional.empty();
        }
    }

    /**
     * Returns localized text for a key formatted with the active locale.
     *
     * @param key  the resource key
     * @param args formatting arguments passed to {@link String#format(Locale, String, Object...)}
     * @return the formatted localized text, or {@link #PLACEHOLDER_FOR_EXCEPTIONS} on lookup/format errors
     * @throws IllegalStateException if localization has not been initialized
     */
    public static String getFormattedText(String key, Object... args) {
        LocalizationState activeState = requireState();
        try {
            return String.format(activeState.locale(), activeState.bundle().getString(key), args);
        } catch (MissingResourceException e) {
            AppLogger.error(e, "AppLocalization: Key not found in ResourceBundle: " + key);
            return PLACEHOLDER_FOR_EXCEPTIONS;
        } catch (IllegalFormatException e) {
            AppLogger.error(e, "AppLocalization: Formatting failed for key: " + key
                    + " with arguments: " + Arrays.toString(args));
            return PLACEHOLDER_FOR_EXCEPTIONS;
        }
    }

    /**
     * Resolves a supported locale from a locale argument.
     *
     * <p>Matching is attempted first by the exact locale code ({@code language_COUNTRY}, e.g., {@code en_US}), then by
     * the exact language code only (e.g., {@code en}).
     *
     * @param localeArgument optional locale argument
     * @return an {@link Optional} containing the resolved locale, or empty if unmatched
     */
    static Optional<Locale> resolveLocaleFromArgument(@Nullable String localeArgument) {
        if ((localeArgument == null) || localeArgument.isBlank()) {
            return Optional.empty();
        }
        return findSupportedLocale(supported -> supported.localeCode().equals(localeArgument))
                .or(() -> findSupportedLocale(supported -> supported.languageCode().equals(localeArgument)));
    }

    /**
     * Resolves a supported locale from a default locale.
     *
     * <p>Matching is attempted first by exact locale, then by language code.
     *
     * @param defaultLocale the default locale to evaluate
     * @return an {@link Optional} containing the resolved locale, or empty if unmatched
     */
    static Optional<Locale> resolveLocaleFromDefault(Locale defaultLocale) {
        return findSupportedLocale(supported -> supported.countryLocale().equals(defaultLocale))
                .or(() -> findSupportedLocale(
                        supported -> supported.languageCode().equals(defaultLocale.getLanguage())));
    }

    /**
     * Initializes localization with a test resource bundle.
     *
     * <p>Tests use this method to check text lookup against dedicated test keys instead of the application bundle.
     *
     * @param testLocale     locale of the test bundle
     * @param bundleBaseName base name of the test bundle
     * @throws IllegalStateException if the test bundle cannot be loaded
     */
    static synchronized void initializeForTesting(Locale testLocale, String bundleBaseName) {
        ResourceBundle testBundle = AppResources.getBundle(bundleBaseName, testLocale).orElseThrow(() ->
                new IllegalStateException("Test ResourceBundle " + bundleBaseName + " could not be loaded."));
        state = new LocalizationState(testLocale, testBundle);
    }

    /**
     * Resets the localization state for tests, so that each test can initialize localization again.
     */
    static synchronized void resetForTesting() {
        state = null;
    }

    private static Optional<Locale> findSupportedLocale(Predicate<CountryLocale> matcher) {
        return Arrays.stream(CountryLocale.values())
                .filter(matcher)
                .findFirst()
                .map(CountryLocale::countryLocale);
    }

    private static LocalizationState requireState() {
        LocalizationState activeState = state;
        if (activeState == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        return activeState;
    }

    /**
     * Supported locales with display labels.
     *
     * <p>Declaration order defines matching precedence. Display labels are intentionally hardcoded here because this
     * enum is bootstrap localization metadata and must not depend on already-loaded i18n resource bundles.
     */
    public enum CountryLocale {
        EN_US(Locale.US, "English (US)"),
        DE_DE(Locale.GERMANY, "Deutsch (Deutschland)");

        private final Locale countryLocale;
        private final String displayName;

        CountryLocale(Locale countryLocale, String displayName) {
            this.countryLocale = countryLocale;
            this.displayName = displayName;
        }

        /**
         * Returns the locale value.
         *
         * @return the locale including language and country
         */
        public Locale countryLocale() {
            return countryLocale;
        }

        /**
         * Returns the display name.
         *
         * @return the display label in the locale's own language
         */
        public String displayName() {
            return displayName;
        }

        /**
         * Returns the ISO language code.
         *
         * @return the language code, for example {@code en}
         */
        public String languageCode() {
            return countryLocale.getLanguage();
        }

        /**
         * Returns the ISO country code.
         *
         * @return the country code, for example {@code US}
         */
        public String countryCode() {
            return countryLocale.getCountry();
        }

        /**
         * Returns the locale code in {@code language_COUNTRY} format.
         *
         * @return the locale code, for example {@code en_US}
         */
        public String localeCode() {
            return countryLocale.getLanguage() + "_" + countryLocale.getCountry();
        }

    }

    /**
     * The active locale and its bundle, replaced as a whole so that readers always see a matching pair.
     */
    private record LocalizationState(Locale locale, ResourceBundle bundle) {
    }

}
