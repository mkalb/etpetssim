package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Manages localization and internationalization for the application.
 * <p>
 * It must be initialized exactly once at the beginning of the application.
 *
 * @see java.util.Locale
 * @see java.util.ResourceBundle
 */
public final class AppLocalization {

    /**
     * Default locale used by the application if no specific locale is provided.
     * This is set to "English (US)".
     */
    public static final Locale DEFAULT_LOCALE = Locale.US;
    /**
     * Placeholder for exceptions when a key is not found in the ResourceBundle or when formatting fails.
     */
    public static final String PLACEHOLDER_FOR_EXCEPTIONS = "########";

    @SuppressWarnings("FieldNamingConvention")
    private static @Nullable Locale locale;
    @SuppressWarnings("FieldNamingConvention")
    private static @Nullable ResourceBundle bundle;

    /**
     * Private constructor to prevent instantiation.
     */
    private AppLocalization() {
    }

    /**
     * Resets the AppLocalization for testing purposes.
     * This method clears the locale and bundle, allowing for re-initialization in tests.
     */
    static void resetForTesting() {
        locale = null;
        bundle = null;
    }

    /**
     * Returns a list of all supported country locales.
     *
     * @return a list of CountryLocale enums representing all supported locales
     */
    public static List<CountryLocale> supportedLocales() {
        return List.of(CountryLocale.values());
    }

    /**
     * Checks if the AppLocalization has been initialized.
     *
     * @return true if the AppLocalization is initialized, false otherwise
     */
    public static synchronized boolean isInitialized() {
        return (locale != null) && (bundle != null);
    }

    /**
     * Initializes the AppLocalization with the specified locale argument and a default locale.
     * The AppLocalization must be initialized exactly once at the beginning.
     *
     * @param localeArgument the locale argument provided by the user, can be null or empty
     *                       if null or empty, the default locale will be used to determine the locale
     *                       if not null, it should match one of the supported locales or their language codes
     * @param defaultLocale  the default locale to use if the locale argument does not match any supported locale
     *                       if the default locale is null, an IllegalArgumentException will be thrown
     *                       if the default locale does not match any of the supported locales,
     *                       the default locale will be set to DEFAULT_LOCALE (US English)
     * @throws IllegalStateException if the AppLocalization is already initialized or
     *                               if the resource bundle for the locale cannot be loaded
     */
    public static synchronized void initialize(@Nullable String localeArgument, Locale defaultLocale) {
        if (isInitialized()) {
            throw new IllegalStateException("AppLocalization is already initialized.");
        }
        Objects.requireNonNull(defaultLocale, "Default locale must not be null");

        locale = resolveLocaleFromArgument(localeArgument)
                .or(() -> resolveLocaleFromDefault(defaultLocale))
                .orElse(DEFAULT_LOCALE);
        bundle = AppResources.getBundle(locale).orElseThrow(() ->
                new IllegalStateException("ResourceBundle for locale " + locale + " could not be loaded.")); // This should never happen
        AppLogger.info("AppLocalization: Initialized with locale: " + locale + " and loaded ResourceBundle");
    }

    /**
     * Initializes the AppLocalization with the specified language argument and the JVM default locale.
     * The AppLocalization must be initialized exactly once at the beginning.
     *
     * @param localeArgument the locale argument provided by the user, can be null or empty
     * @throws IllegalStateException if the AppLocalization is already initialized
     * @see #initialize(String, Locale)
     * @see java.util.Locale#getDefault()
     */
    public static synchronized void initialize(@Nullable String localeArgument) {
        if (isInitialized()) {
            throw new IllegalStateException("AppLocalization is already initialized.");
        }
        initialize(localeArgument, Locale.getDefault());
    }

    /**
     * Resolves the locale from the provided locale argument.
     *
     * @param localeArgument the locale argument provided by the user, can be null or empty
     * @return an Optional containing the resolved Locale if found, or an empty Optional if not found
     */
    static Optional<Locale> resolveLocaleFromArgument(@Nullable String localeArgument) {
        Locale resolvedLocale = null;
        if ((localeArgument != null) && !localeArgument.isBlank()) {
            for (CountryLocale supportedLocale : CountryLocale.values()) {
                // It must match exactly the locale code (e.g., "en_US" or "de_DE")
                if (supportedLocale.localeCode().equals(localeArgument)) {
                    resolvedLocale = supportedLocale.countryLocale();
                    break;
                }
            }
            if (resolvedLocale == null) {
                for (CountryLocale supportedLocale : CountryLocale.values()) {
                    // It must match exactly the language code (e.g., "en" or "de").
                    // Locale argument must not contain country code.
                    if (supportedLocale.languageCode().equals(localeArgument)) {
                        resolvedLocale = supportedLocale.countryLocale();
                        break;
                    }
                }
            }
        }
        return Optional.ofNullable(resolvedLocale);
    }

    /**
     * Resolves the locale from the default locale.
     *
     * @param defaultLocale the default locale
     * @return an Optional containing the resolved Locale if found, or an empty Optional if not found
     */
    static Optional<Locale> resolveLocaleFromDefault(Locale defaultLocale) {
        Objects.requireNonNull(defaultLocale, "Default locale must not be null");
        Locale resolvedLocale = null;
        for (CountryLocale supportedLocale : CountryLocale.values()) {
            // It must match exactly the country locale (e.g., Locale.US or Locale.GERMANY)
            if (supportedLocale.countryLocale().equals(defaultLocale)) {
                resolvedLocale = supportedLocale.countryLocale();
                break;
            }
        }
        for (CountryLocale supportedLocale : CountryLocale.values()) {
            // It must match the language code (e.g., "en" or "de").
            // Default locale can contain country code, but we only check the language code.
            if (supportedLocale.languageCode().equals(defaultLocale.getLanguage())) {
                resolvedLocale = supportedLocale.countryLocale();
                break;
            }
        }
        return Optional.ofNullable(resolvedLocale);
    }

    /**
     * Returns the locale currently used by the application.
     *
     * @return the current locale
     * @throws IllegalStateException if the AppLocalization has not been initialized
     */
    public static Locale locale() {
        if (locale == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        return locale;
    }

    /**
     * Returns the ResourceBundle currently used by the application.
     *
     * @return the current ResourceBundle
     * @throws IllegalStateException if the AppLocalization has not been initialized
     */
    public static ResourceBundle bundle() {
        if (bundle == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        return bundle;
    }

    /**
     * Returns the text for the given key from the ResourceBundle.
     *
     * @param key the key for the desired text
     * @return the text associated with the key, or a placeholder if the key is not found
     * @throws IllegalStateException if the AppLocalization has not been initialized
     */
    public static String getText(String key) {
        Objects.requireNonNull(key, "Key must not be null");
        if (bundle == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            AppLogger.error("AppLocalization: Key not found in ResourceBundle: " + key, e);
            return PLACEHOLDER_FOR_EXCEPTIONS;
        }
    }

    /**
     * Returns an Optional containing the text for the given key from the ResourceBundle.
     *
     * @param key the key for the desired text
     * @return an Optional containing the text associated with the key if found,
     * or an empty Optional if the key is not found or if the text is empty or blank
     * @throws IllegalStateException if the AppLocalization has not been initialized
     */
    public static Optional<String> getOptionalText(String key) {
        Objects.requireNonNull(key, "Key must not be null");
        if (bundle == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        try {
            String text = bundle.getString(key);
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
     * Returns the formatted text for the given key from the ResourceBundle.
     *
     * @param key  the key for the desired text
     * @param args the arguments to format the text with
     * @return the formatted text associated with the key, or a placeholder if the key is not found or if formatting fails
     * @throws IllegalStateException if the AppLocalization has not been initialized
     */
    public static String getFormattedText(String key, Object... args) {
        Objects.requireNonNull(key, "Key must not be null");
        if (locale == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        if (bundle == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        try {
            return String.format(locale, bundle.getString(key), args);
        } catch (MissingResourceException e) {
            AppLogger.error("AppLocalization: Key not found in ResourceBundle: " + key, e);
            return PLACEHOLDER_FOR_EXCEPTIONS;
        } catch (IllegalFormatException e) {
            AppLogger.error("AppLocalization: Formatting failed for key: " + key + " with arguments: " + Arrays.toString(args), e);
            return PLACEHOLDER_FOR_EXCEPTIONS;
        }
    }

    /**
     * Enum representing supported country locales with their display names.
     * The order of the enum constants is important as it determines the precedence when matching locales.
     * EN_US has the highest precedence.
     */
    public enum CountryLocale {
        EN_US(Locale.US, "English (US)"),
        DE_DE(Locale.GERMANY, "Deutsch (Deutschland)");

        private final Locale countryLocale;
        private final String displayName;

        /**
         * Constructs a CountryLocale with the provided locale and display name.
         *
         * @param countryLocale the Locale containing non-empty language and country codes
         * @param displayName the human-readable display name for the locale
         * @throws IllegalArgumentException if the locale lacks a valid language or country code
         */
        CountryLocale(Locale countryLocale, String displayName) {
            Objects.requireNonNull(countryLocale, "Country locale must not be null");
            Objects.requireNonNull(displayName, "Display name must not be null");
            if (countryLocale.getLanguage().isEmpty()) {
                throw new IllegalArgumentException("Country locale must have a valid language code.");
            }
            if (countryLocale.getCountry().isEmpty()) {
                throw new IllegalArgumentException("Country locale must have a valid country code.");
            }
            this.countryLocale = countryLocale;
            this.displayName = displayName;
        }

        /**
         * Returns the Locale object representing the country locale.
         * Example: "Locale.US" for "EN_US" or "Locale.GERMANY" for "DE_DE".
         *
         * @return the Locale object with language and country codes
         */
        public Locale countryLocale() {
            return countryLocale;
        }

        /**
         * Returns the display name of the country locale.
         * Example: "English (US)" for "EN_US" or "Deutsch (Deutschland)" for "DE_DE".
         *
         * @return the display name as a string
         */
        public String displayName() {
            return displayName;
        }

        /**
         * Returns the language code of the country locale.
         * Example: "en" for "EN_US" or "de" for "DE_DE".
         *
         * @return the language code as a string
         */
        public String languageCode() {
            return countryLocale.getLanguage();
        }

        /**
         * Returns the country code of the country locale.
         * Example: "US" for "EN_US" or "DE" for "DE_DE".
         *
         * @return the country code as a string
         */
        public String countryCode() {
            return countryLocale.getCountry();
        }

        /**
         * Returns the locale code of the country locale in the format "language_COUNTRY".
         * Example: "en_US" for "EN_US" or "de_DE" for "DE_DE".
         *
         * @return the locale code as a string
         */
        public String localeCode() {
            return countryLocale.getLanguage() + "_" + countryLocale.getCountry();
        }

    }

}
