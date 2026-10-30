package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AppLocalizationTest {

    private static final String MAIN_BUNDLE_KEY = AppLocalizationKeys.ABOUT_RESOURCE_NOT_FOUND;
    private static final String TEST_BUNDLE_BASE_NAME = "i18n.test_messages";

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @BeforeEach
    void setUpBeforeEach() {
        AppLocalization.resetForTesting();
    }

    @Test
    void testSupportedLocales() {
        assertEquals(List.of(AppLocalization.CountryLocale.values()), AppLocalization.supportedLocales());
    }

    @Test
    void testIsInitialized() {
        assertFalse(AppLocalization.isInitialized());

        AppLocalization.initialize("en_US");

        assertTrue(AppLocalization.isInitialized());
    }

    @Test
    void testInitialize() {
        AppLocalization.initialize("en_US");
        Locale englishLocale = AppLocalization.locale();
        Locale englishBundleLocale = AppLocalization.bundle().getLocale();

        AppLocalization.resetForTesting();
        AppLocalization.initialize("de_DE");

        assertAll(
                () -> assertEquals(Locale.US, englishLocale),
                () -> assertEquals(Locale.US, englishBundleLocale),
                () -> assertEquals(Locale.GERMANY, AppLocalization.locale()),
                () -> assertEquals(Locale.GERMANY, AppLocalization.bundle().getLocale())
        );
    }

    @Test
    void testInitializeThrowsWhenInitializedTwice() {
        AppLocalization.initialize("en_US");

        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> AppLocalization.initialize("de_DE")),
                () -> assertThrows(IllegalStateException.class,
                        () -> AppLocalization.initialize("de_DE", Locale.GERMANY)),
                () -> assertEquals(Locale.US, AppLocalization.locale())
        );
    }

    @Test
    void testInitializeWithDefaultLocale() {
        AppLocalization.initialize("de_DE", Locale.US);

        assertAll(
                () -> assertEquals(Locale.GERMANY, AppLocalization.locale()),
                () -> assertEquals(Locale.GERMANY, AppLocalization.bundle().getLocale())
        );
    }

    @Test
    void testInitializeWithDefaultLocaleFallsBackToDefaultLocale() {
        AppLocalization.initialize("fr_FR", Locale.GERMAN);
        Locale localeForUnsupportedArgument = AppLocalization.locale();

        AppLocalization.resetForTesting();
        AppLocalization.initialize(null, Locale.GERMANY);

        assertAll(
                () -> assertEquals(Locale.GERMANY, localeForUnsupportedArgument),
                () -> assertEquals(Locale.GERMANY, AppLocalization.locale())
        );
    }

    @Test
    void testInitializeWithDefaultLocaleFallsBackToApplicationDefault() {
        AppLocalization.initialize("fr_FR", Locale.FRANCE);

        assertEquals(AppLocalization.DEFAULT_LOCALE, AppLocalization.locale());
    }

    @Test
    void testLocaleThrowsBeforeInitialization() {
        assertThrows(IllegalStateException.class, AppLocalization::locale);
    }

    @Test
    void testBundleThrowsBeforeInitialization() {
        assertThrows(IllegalStateException.class, AppLocalization::bundle);
    }

    @Test
    void testGetText() {
        assertThrows(IllegalStateException.class, () -> AppLocalization.getText(MAIN_BUNDLE_KEY));

        AppLocalization.initialize("en_US");
        String englishText = AppLocalization.getText(MAIN_BUNDLE_KEY);
        String englishBundleText = AppLocalization.bundle().getString(MAIN_BUNDLE_KEY);

        AppLocalization.resetForTesting();
        AppLocalization.initialize("de_DE");

        assertAll(
                () -> assertEquals(englishBundleText, englishText),
                () -> assertEquals(AppLocalization.bundle().getString(MAIN_BUNDLE_KEY),
                        AppLocalization.getText(MAIN_BUNDLE_KEY)),
                () -> assertEquals(AppLocalization.PLACEHOLDER_FOR_EXCEPTIONS,
                        AppLocalization.getText("non_existent_key"))
        );
    }

    @Test
    void testGetOptionalText() {
        assertThrows(IllegalStateException.class, () -> AppLocalization.getOptionalText("greeting"));

        AppLocalization.initializeForTesting(Locale.US, TEST_BUNDLE_BASE_NAME);
        Optional<String> englishGreeting = AppLocalization.getOptionalText("greeting");
        Optional<String> englishEmpty = AppLocalization.getOptionalText("empty");
        Optional<String> englishOnlyUS = AppLocalization.getOptionalText("onlyUS");

        AppLocalization.resetForTesting();
        AppLocalization.initializeForTesting(Locale.GERMANY, TEST_BUNDLE_BASE_NAME);

        assertAll(
                () -> assertEquals(Optional.of("Hello"), englishGreeting),
                () -> assertEquals(Optional.empty(), englishEmpty),
                () -> assertEquals(Optional.of("OnlyUS"), englishOnlyUS),
                () -> assertEquals(Optional.of("Hallo"), AppLocalization.getOptionalText("greeting")),
                () -> assertEquals(Optional.empty(), AppLocalization.getOptionalText("empty")),
                () -> assertEquals(Optional.empty(), AppLocalization.getOptionalText("onlyUS")),
                () -> assertEquals(Optional.empty(), AppLocalization.getOptionalText("non_existent_key"))
        );
    }

    @Test
    void testGetFormattedText() {
        assertThrows(IllegalStateException.class, () -> AppLocalization.getFormattedText("formatted", 1));

        AppLocalization.initializeForTesting(Locale.US, TEST_BUNDLE_BASE_NAME);
        String englishText = AppLocalization.getFormattedText("formatted", 1);

        AppLocalization.resetForTesting();
        AppLocalization.initializeForTesting(Locale.GERMANY, TEST_BUNDLE_BASE_NAME);

        assertAll(
                () -> assertEquals("Hello 1", englishText),
                () -> assertEquals("Hallo 2", AppLocalization.getFormattedText("formatted", 2)),
                () -> assertEquals(AppLocalization.PLACEHOLDER_FOR_EXCEPTIONS,
                        AppLocalization.getFormattedText("non_existent_key", 1))
        );
    }

    @Test
    void testGetFormattedTextReturnsPlaceholderForInvalidArguments() {
        AppLocalization.initializeForTesting(Locale.US, TEST_BUNDLE_BASE_NAME);

        assertEquals(AppLocalization.PLACEHOLDER_FOR_EXCEPTIONS, AppLocalization.getFormattedText("formatted", "text"));
    }

    @Test
    void testResolveLocaleFromArgument() {
        assertAll(
                () -> assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromArgument("en_US")),
                () -> assertEquals(Optional.of(Locale.GERMANY), AppLocalization.resolveLocaleFromArgument("de_DE")),
                () -> assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromArgument("en")),
                () -> assertEquals(Optional.of(Locale.GERMANY), AppLocalization.resolveLocaleFromArgument("de")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument(null)),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("invalid_locale")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("de_AT")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("en_UK")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("DE")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("EN")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("US")),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("GERMANY"))
        );
    }

    @Test
    void testResolveLocaleFromDefault() {
        assertAll(
                () -> assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.US)),
                () -> assertEquals(Optional.of(Locale.GERMANY),
                        AppLocalization.resolveLocaleFromDefault(Locale.GERMANY)),
                () -> assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.CANADA)),
                () -> assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.ENGLISH)),
                () -> assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.UK)),
                () -> assertEquals(Optional.of(Locale.GERMANY),
                        AppLocalization.resolveLocaleFromDefault(Locale.GERMAN)),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromDefault(Locale.FRANCE)),
                () -> assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromDefault(Locale.CANADA_FRENCH))
        );
    }

    @Test
    void testInitializeForTesting() {
        AppLocalization.initializeForTesting(Locale.GERMANY, TEST_BUNDLE_BASE_NAME);

        assertAll(
                () -> assertEquals(Locale.GERMANY, AppLocalization.locale()),
                () -> assertTrue(AppLocalization.bundle().containsKey("greeting")),
                () -> assertFalse(AppLocalization.bundle().containsKey(MAIN_BUNDLE_KEY))
        );
    }

    @Test
    void testResetForTesting() {
        AppLocalization.initialize("en_US");

        AppLocalization.resetForTesting();

        assertFalse(AppLocalization.isInitialized());
    }

    @Test
    void testCountryLocaleDeclarationOrder() {
        assertArrayEquals(
                new AppLocalization.CountryLocale[]{
                        AppLocalization.CountryLocale.EN_US,
                        AppLocalization.CountryLocale.DE_DE
                },
                AppLocalization.CountryLocale.values()
        );
    }

    @Test
    void testCountryLocale() {
        assertAll(
                () -> assertEquals(Locale.US, AppLocalization.CountryLocale.EN_US.countryLocale()),
                () -> assertEquals(Locale.GERMANY, AppLocalization.CountryLocale.DE_DE.countryLocale())
        );
    }

    @Test
    void testDisplayName() {
        assertAll(
                () -> assertEquals("English (US)", AppLocalization.CountryLocale.EN_US.displayName()),
                () -> assertEquals("Deutsch (Deutschland)", AppLocalization.CountryLocale.DE_DE.displayName())
        );
    }

    @Test
    void testLanguageCode() {
        assertAll(
                () -> assertEquals("en", AppLocalization.CountryLocale.EN_US.languageCode()),
                () -> assertEquals("de", AppLocalization.CountryLocale.DE_DE.languageCode())
        );
    }

    @Test
    void testCountryCode() {
        assertAll(
                () -> assertEquals("US", AppLocalization.CountryLocale.EN_US.countryCode()),
                () -> assertEquals("DE", AppLocalization.CountryLocale.DE_DE.countryCode())
        );
    }

    @Test
    void testLocaleCode() {
        assertAll(
                () -> assertEquals("en_US", AppLocalization.CountryLocale.EN_US.localeCode()),
                () -> assertEquals("de_DE", AppLocalization.CountryLocale.DE_DE.localeCode())
        );
    }

}
