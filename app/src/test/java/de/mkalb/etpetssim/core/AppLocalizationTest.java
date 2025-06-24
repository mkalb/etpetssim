package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AppLocalizationTest {

    @BeforeEach
    void setUp() {
        // Reset AppLocalization before each test
        AppLocalization.resetForTesting();
    }

    @Test
    void testSupportedLocales() {
        var supportedLocales = AppLocalization.supportedLocales();
        assertEquals(AppLocalization.CountryLocale.values().length, supportedLocales.size());
        for (int i = 0; i < AppLocalization.CountryLocale.values().length; i++) {
            var localeEnum = AppLocalization.CountryLocale.values()[i];
            assertEquals(localeEnum, supportedLocales.get(i));
        }
    }

    @Test
    void testBeforeInitialization() {
        assertFalse(AppLocalization.isInitialized());
        assertThrows(NullPointerException.class, AppLocalization::locale);
        assertThrows(NullPointerException.class, AppLocalization::bundle);
        assertThrows(NullPointerException.class, () -> AppLocalization.getText("greeting"));
        assertThrows(NullPointerException.class, () -> AppLocalization.getFormattedText("welcome", "John"));
    }

    @Test
    void testInitializeTwoParameters() {
        AppLocalization.initialize("en_US", Locale.GERMANY);
        assertTrue(AppLocalization.isInitialized());
        assertEquals(Locale.US, AppLocalization.locale());
        assertNotNull(AppLocalization.bundle());
        assertEquals(Locale.US, AppLocalization.bundle().getLocale());

        AppLocalization.resetForTesting();
        AppLocalization.initialize("de_DE", Locale.US);
        assertTrue(AppLocalization.isInitialized());
        assertEquals(Locale.GERMANY, AppLocalization.locale());
        assertNotNull(AppLocalization.bundle());
        assertEquals(Locale.GERMANY, AppLocalization.bundle().getLocale());
    }

    @Test
    void testInitializeOneParameter() {
        AppLocalization.initialize("en_US");
        assertTrue(AppLocalization.isInitialized());
        assertEquals(Locale.US, AppLocalization.locale());
        assertNotNull(AppLocalization.bundle());
        assertEquals(Locale.US, AppLocalization.bundle().getLocale());

        AppLocalization.resetForTesting();
        AppLocalization.initialize("de_DE");
        assertTrue(AppLocalization.isInitialized());
        assertEquals(Locale.GERMANY, AppLocalization.locale());
        assertNotNull(AppLocalization.bundle());
        assertEquals(Locale.GERMANY, AppLocalization.bundle().getLocale());
    }

    @Test
    void testResolveLocaleFromArgument() {
        assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromArgument("en_US"));
        assertEquals(Optional.of(Locale.GERMANY), AppLocalization.resolveLocaleFromArgument("de_DE"));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument(null));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument(""));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("invalid_locale"));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("de_AT"));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("en_UK"));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("DE"));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("EN"));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("US"));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromArgument("GERMANY"));
        assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromArgument("en"));
        assertEquals(Optional.of(Locale.GERMANY), AppLocalization.resolveLocaleFromArgument("de"));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testResolveLocaleFromDefault() {
        assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.US));
        assertEquals(Optional.of(Locale.GERMANY), AppLocalization.resolveLocaleFromDefault(Locale.GERMANY));
        assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.CANADA));
        assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.ENGLISH));
        assertEquals(Optional.of(Locale.US), AppLocalization.resolveLocaleFromDefault(Locale.UK));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromDefault(Locale.FRANCE));
        assertEquals(Optional.empty(), AppLocalization.resolveLocaleFromDefault(Locale.CANADA_FRENCH));
        assertThrows(NullPointerException.class, () -> AppLocalization.resolveLocaleFromDefault(null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetText() {
        AppLocalization.initialize("en_US");
        assertEquals("Hello", AppLocalization.getText("greeting"));

        AppLocalization.resetForTesting();
        AppLocalization.initialize("de_DE");
        assertEquals("Hallo", AppLocalization.getText("greeting"));

        assertEquals(AppLocalization.PLACEHOLDER_FOR_EXCEPTIONS, AppLocalization.getText("non_existent_key"));
        assertThrows(NullPointerException.class, () -> AppLocalization.getText(null));
    }

    @SuppressWarnings({"SpellCheckingInspection", "DataFlowIssue"})
    @Test
    void testGetFormattedText() {
        AppLocalization.initialize("en_US");
        assertEquals("Welcome, John!", AppLocalization.getFormattedText("welcome", "John"));

        AppLocalization.resetForTesting();
        AppLocalization.initialize("de_DE");
        assertEquals("Willkommen, John!", AppLocalization.getFormattedText("welcome", "John"));

        assertEquals(AppLocalization.PLACEHOLDER_FOR_EXCEPTIONS, AppLocalization.getFormattedText("non_existent_key", "John"));
        assertThrows(NullPointerException.class, () -> AppLocalization.getFormattedText(null, "John"));
    }

    @Test
    void testEnumCountryLocale() {
        // Test that enum country locales are not null or empty
        for (AppLocalization.CountryLocale locale : AppLocalization.CountryLocale.values()) {
            Locale country = locale.countryLocale();
            assertNotNull(country);
        }

        assertEquals(Locale.US, AppLocalization.CountryLocale.EN_US.countryLocale());
        assertEquals(Locale.GERMANY, AppLocalization.CountryLocale.DE_DE.countryLocale());
    }

    @Test
    void testEnumDisplayName() {
        // Test that enum display names are not null or empty
        for (AppLocalization.CountryLocale locale : AppLocalization.CountryLocale.values()) {
            String displayName = locale.displayName();
            assertNotNull(displayName);
            assertFalse(displayName.isEmpty());
        }

        assertEquals("English (US)", AppLocalization.CountryLocale.EN_US.displayName());
        assertEquals("Deutsch (Deutschland)", AppLocalization.CountryLocale.DE_DE.displayName());
    }

    @Test
    void testEnumLanguageCode() {
        // Test that enum language codes are not null or empty
        for (AppLocalization.CountryLocale locale : AppLocalization.CountryLocale.values()) {
            String code = locale.languageCode();
            assertNotNull(code);
            assertFalse(code.isEmpty());
            assertTrue(code.matches("[a-z]{2}"), "Language code should match pattern xx: " + code);
        }

        assertEquals("en", AppLocalization.CountryLocale.EN_US.languageCode());
        assertEquals("de", AppLocalization.CountryLocale.DE_DE.languageCode());
    }

    @Test
    void testEnumCountryCode() {
        // Test that enum country codes are not null or empty
        for (AppLocalization.CountryLocale locale : AppLocalization.CountryLocale.values()) {
            String code = locale.countryCode();
            assertNotNull(code);
            assertFalse(code.isEmpty());
            assertTrue(code.matches("[A-Z]{2}"), "Country code should match pattern XX: " + code);
        }

        assertEquals("US", AppLocalization.CountryLocale.EN_US.countryCode());
        assertEquals("DE", AppLocalization.CountryLocale.DE_DE.countryCode());
    }

    @Test
    void testEnumLocaleCode() {
        // Test that enum locale codes match the expected format
        for (AppLocalization.CountryLocale locale : AppLocalization.CountryLocale.values()) {
            String code = locale.localeCode();
            assertNotNull(code);
            assertFalse(code.isEmpty());
            assertTrue(code.matches("[a-z]{2}_[A-Z]{2}"), "Locale code should match pattern xx_XX: " + code);
        }

        assertEquals("en_US", AppLocalization.CountryLocale.EN_US.localeCode());
        assertEquals("de_DE", AppLocalization.CountryLocale.DE_DE.localeCode());
    }

}