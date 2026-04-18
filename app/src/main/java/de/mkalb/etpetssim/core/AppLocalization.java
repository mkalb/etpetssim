package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Provides application-wide localization services.
 * <p>
 * This class manages the active {@link Locale} and its corresponding
 * {@link ResourceBundle}. It must be initialized exactly once during startup.
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
     * Resets the localization state for tests.
     */
    static void resetForTesting() {
        locale = null;
        bundle = null;
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
     * @return {@code true} when both locale and bundle are available
     */
    public static synchronized boolean isInitialized() {
        return (locale != null) && (bundle != null);
    }

    /**
     * Initializes localization using a user argument and fallback locale.
     * <p>
     * Resolution order is: locale argument, {@code defaultLocale}, then
     * {@link #DEFAULT_LOCALE}. The first matching supported locale is selected.
     *
     * @param localeArgument optional user-provided locale code (for example {@code en_US} or {@code en})
     * @param defaultLocale fallback locale used when {@code localeArgument} does not resolve
     * @throws NullPointerException if {@code defaultLocale} is {@code null}
     * @throws IllegalStateException if already initialized or no bundle can be loaded for the resolved locale
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
                new IllegalStateException("ResourceBundle for locale " + locale + " could not be loaded.")); // This should never happen.
        AppLogger.info("AppLocalization: Initialized with locale: " + locale + " and loaded ResourceBundle");
    }

    /**
     * Initializes localization using a user argument and {@link Locale#getDefault()}.
     *
     * @param localeArgument optional user-provided locale code
     * @throws IllegalStateException if already initialized
     * @see #initialize(String, Locale)
     */
    public static synchronized void initialize(@Nullable String localeArgument) {
        if (isInitialized()) {
            throw new IllegalStateException("AppLocalization is already initialized.");
        }
        initialize(localeArgument, Locale.getDefault());
    }

    /**
     * Resolves a supported locale from a locale argument.
     * <p>
     * Matching is attempted first by full locale code ({@code language_COUNTRY}),
     * then by language code only.
     *
     * @param localeArgument optional locale argument
     * @return an {@link Optional} containing the resolved locale, or empty if unmatched
     */
    static Optional<Locale> resolveLocaleFromArgument(@Nullable String localeArgument) {
        Locale resolvedLocale = null;
        if ((localeArgument != null) && !localeArgument.isBlank()) {
            for (CountryLocale supportedLocale : CountryLocale.values()) {
                // It must match exactly the locale code (e.g., "en_US" or "de_DE").
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
     * Resolves a supported locale from a default locale.
     * <p>
     * Matching is attempted first by exact locale, then by language code.
     *
     * @param defaultLocale the default locale to evaluate
     * @return an {@link Optional} containing the resolved locale, or empty if unmatched
     * @throws NullPointerException if {@code defaultLocale} is {@code null}
     */
    static Optional<Locale> resolveLocaleFromDefault(Locale defaultLocale) {
        Objects.requireNonNull(defaultLocale, "Default locale must not be null");
        Locale resolvedLocale = null;
        for (CountryLocale supportedLocale : CountryLocale.values()) {
            // It must match exactly the country locale (e.g., Locale.US or Locale.GERMANY).
            if (supportedLocale.countryLocale().equals(defaultLocale)) {
                resolvedLocale = supportedLocale.countryLocale();
                break;
            }
        }
        if (resolvedLocale == null) {
            for (CountryLocale supportedLocale : CountryLocale.values()) {
                // It must match the language code (e.g., "en" or "de").
                // Default locale can contain country code, but we only check the language code.
                if (supportedLocale.languageCode().equals(defaultLocale.getLanguage())) {
                    resolvedLocale = supportedLocale.countryLocale();
                    break;
                }
            }
        }
        return Optional.ofNullable(resolvedLocale);
    }

    /**
     * Returns the active locale.
     *
     * @return the active locale
     * @throws IllegalStateException if localization has not been initialized
     */
    public static Locale locale() {
        if (locale == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        return locale;
    }

    /**
     * Returns the active resource bundle.
     *
     * @return the active bundle
     * @throws IllegalStateException if localization has not been initialized
     */
    public static ResourceBundle bundle() {
        if (bundle == null) {
            throw new IllegalStateException("AppLocalization is not initialized.");
        }
        return bundle;
    }

    /**
     * Returns localized text for a key.
     *
     * @param key the resource key
     * @return the localized text, or {@link #PLACEHOLDER_FOR_EXCEPTIONS} if the key is missing
     * @throws NullPointerException if {@code key} is {@code null}
     * @throws IllegalStateException if localization has not been initialized
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
     * Returns localized text for a key if present and non-blank.
     *
     * @param key the resource key
     * @return an {@link Optional} containing non-blank localized text, or empty otherwise
     * @throws NullPointerException if {@code key} is {@code null}
     * @throws IllegalStateException if localization has not been initialized
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
     * Returns localized text for a key formatted with the active locale.
     *
     * @param key the resource key
     * @param args formatting arguments passed to {@link String#format(Locale, String, Object...)}
     * @return the formatted localized text, or {@link #PLACEHOLDER_FOR_EXCEPTIONS} on lookup/format errors
     * @throws NullPointerException if {@code key} is {@code null}
     * @throws IllegalStateException if localization has not been initialized
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
     * Supported locales with display labels.
     * <p>
     * Declaration order defines matching precedence.
     */
    public enum CountryLocale {
        EN_US(Locale.US, "English (US)"),
        DE_DE(Locale.GERMANY, "Deutsch (Deutschland)");

        private final Locale countryLocale;
        private final String displayName;

        /**
         * Creates a locale descriptor.
         *
         * @param countryLocale locale with non-empty language and country codes
         * @param displayName human-readable display name
         * @throws NullPointerException if {@code countryLocale} or {@code displayName} is {@code null}
         * @throws IllegalArgumentException if {@code countryLocale} has an empty language or country code
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
         * @return the localized display label
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

}
