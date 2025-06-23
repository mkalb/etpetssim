package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LanguageManagerTest {

    @BeforeEach
    void setUp() {
        // Reset LanguageManager before each test
        LanguageManager.resetForTesting();
    }

    @Test
    void testSupportedLocales() {
        var supportedLocales = LanguageManager.supportedLocales();
        assertEquals(LanguageManager.SupportedCountryLocales.values().length, supportedLocales.size());
        for (int i = 0; i < LanguageManager.SupportedCountryLocales.values().length; i++) {
            var localeEnum = LanguageManager.SupportedCountryLocales.values()[i];
            assertEquals(localeEnum, supportedLocales.get(i));
        }
    }

    @Test
    void testBeforeInitialization() {
        assertFalse(LanguageManager.isInitialized());
        assertThrows(NullPointerException.class, LanguageManager::locale);
        assertThrows(NullPointerException.class, LanguageManager::bundle);
        assertThrows(NullPointerException.class, () -> LanguageManager.getText("greeting"));
        assertThrows(NullPointerException.class, () -> LanguageManager.getFormattedText("welcome", "John"));
    }

    @Test
    void testInitializeTwoParameters() {
        LanguageManager.initialize("en_US", Locale.GERMANY);
        assertTrue(LanguageManager.isInitialized());
        assertEquals(Locale.US, LanguageManager.locale());
        assertNotNull(LanguageManager.bundle());
        assertEquals(Locale.US, LanguageManager.bundle().getLocale());

        LanguageManager.resetForTesting();
        LanguageManager.initialize("de_DE", Locale.US);
        assertTrue(LanguageManager.isInitialized());
        assertEquals(Locale.GERMANY, LanguageManager.locale());
        assertNotNull(LanguageManager.bundle());
        assertEquals(Locale.GERMANY, LanguageManager.bundle().getLocale());
    }

    @Test
    void testInitializeOneParameter() {
        LanguageManager.initialize("en_US");
        assertTrue(LanguageManager.isInitialized());
        assertEquals(Locale.US, LanguageManager.locale());
        assertNotNull(LanguageManager.bundle());
        assertEquals(Locale.US, LanguageManager.bundle().getLocale());

        LanguageManager.resetForTesting();
        LanguageManager.initialize("de_DE");
        assertTrue(LanguageManager.isInitialized());
        assertEquals(Locale.GERMANY, LanguageManager.locale());
        assertNotNull(LanguageManager.bundle());
        assertEquals(Locale.GERMANY, LanguageManager.bundle().getLocale());
    }

    @Test
    void testResolveLocaleFromArgument() {
        assertEquals(Optional.of(Locale.US), LanguageManager.resolveLocaleFromArgument("en_US"));
        assertEquals(Optional.of(Locale.GERMANY), LanguageManager.resolveLocaleFromArgument("de_DE"));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument(null));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument(""));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument("invalid_locale"));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument("de_AT"));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument("en_UK"));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument("DE"));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument("EN"));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument("US"));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromArgument("GERMANY"));
        assertEquals(Optional.of(Locale.US), LanguageManager.resolveLocaleFromArgument("en"));
        assertEquals(Optional.of(Locale.GERMANY), LanguageManager.resolveLocaleFromArgument("de"));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testResolveLocaleFromDefault() {
        assertEquals(Optional.of(Locale.US), LanguageManager.resolveLocaleFromDefault(Locale.US));
        assertEquals(Optional.of(Locale.GERMANY), LanguageManager.resolveLocaleFromDefault(Locale.GERMANY));
        assertEquals(Optional.of(Locale.US), LanguageManager.resolveLocaleFromDefault(Locale.CANADA));
        assertEquals(Optional.of(Locale.US), LanguageManager.resolveLocaleFromDefault(Locale.ENGLISH));
        assertEquals(Optional.of(Locale.US), LanguageManager.resolveLocaleFromDefault(Locale.UK));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromDefault(Locale.FRANCE));
        assertEquals(Optional.empty(), LanguageManager.resolveLocaleFromDefault(Locale.CANADA_FRENCH));
        assertThrows(NullPointerException.class, () -> LanguageManager.resolveLocaleFromDefault(null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetText() {
        LanguageManager.initialize("en_US");
        assertEquals("Hello", LanguageManager.getText("greeting"));

        LanguageManager.resetForTesting();
        LanguageManager.initialize("de_DE");
        assertEquals("Hallo", LanguageManager.getText("greeting"));

        assertEquals(LanguageManager.PLACEHOLDER_FOR_EXCEPTIONS, LanguageManager.getText("non_existent_key"));
        assertThrows(NullPointerException.class, () -> LanguageManager.getText(null));
    }

    @SuppressWarnings({"SpellCheckingInspection", "DataFlowIssue"})
    @Test
    void testGetFormattedText() {
        LanguageManager.initialize("en_US");
        assertEquals("Welcome, John!", LanguageManager.getFormattedText("welcome", "John"));

        LanguageManager.resetForTesting();
        LanguageManager.initialize("de_DE");
        assertEquals("Willkommen, John!", LanguageManager.getFormattedText("welcome", "John"));

        assertEquals(LanguageManager.PLACEHOLDER_FOR_EXCEPTIONS, LanguageManager.getFormattedText("non_existent_key", "John"));
        assertThrows(NullPointerException.class, () -> LanguageManager.getFormattedText(null, "John"));
    }

    @Test
    void testEnumCountryLocale() {
        // Test that enum country locales are not null or empty
        for (LanguageManager.SupportedCountryLocales locale : LanguageManager.SupportedCountryLocales.values()) {
            Locale country = locale.countryLocale();
            assertNotNull(country);
        }

        assertEquals(Locale.US, LanguageManager.SupportedCountryLocales.EN_US.countryLocale());
        assertEquals(Locale.GERMANY, LanguageManager.SupportedCountryLocales.DE_DE.countryLocale());
    }

    @Test
    void testEnumDisplayName() {
        // Test that enum display names are not null or empty
        for (LanguageManager.SupportedCountryLocales locale : LanguageManager.SupportedCountryLocales.values()) {
            String displayName = locale.displayName();
            assertNotNull(displayName);
            assertFalse(displayName.isEmpty());
        }

        assertEquals("English (US)", LanguageManager.SupportedCountryLocales.EN_US.displayName());
        assertEquals("Deutsch (Deutschland)", LanguageManager.SupportedCountryLocales.DE_DE.displayName());
    }

    @Test
    void testEnumLanguageCode() {
        // Test that enum language codes are not null or empty
        for (LanguageManager.SupportedCountryLocales locale : LanguageManager.SupportedCountryLocales.values()) {
            String code = locale.languageCode();
            assertNotNull(code);
            assertFalse(code.isEmpty());
            assertTrue(code.matches("[a-z]{2}"), "Language code should match pattern xx: " + code);
        }

        assertEquals("en", LanguageManager.SupportedCountryLocales.EN_US.languageCode());
        assertEquals("de", LanguageManager.SupportedCountryLocales.DE_DE.languageCode());
    }

    @Test
    void testEnumCountryCode() {
        // Test that enum country codes are not null or empty
        for (LanguageManager.SupportedCountryLocales locale : LanguageManager.SupportedCountryLocales.values()) {
            String code = locale.countryCode();
            assertNotNull(code);
            assertFalse(code.isEmpty());
            assertTrue(code.matches("[A-Z]{2}"), "Country code should match pattern XX: " + code);
        }

        assertEquals("US", LanguageManager.SupportedCountryLocales.EN_US.countryCode());
        assertEquals("DE", LanguageManager.SupportedCountryLocales.DE_DE.countryCode());
    }

    @Test
    void testEnumLocaleCode() {
        // Test that enum locale codes match the expected format
        for (LanguageManager.SupportedCountryLocales locale : LanguageManager.SupportedCountryLocales.values()) {
            String code = locale.localeCode();
            assertNotNull(code);
            assertFalse(code.isEmpty());
            assertTrue(code.matches("[a-z]{2}_[A-Z]{2}"), "Locale code should match pattern xx_XX: " + code);
        }

        assertEquals("en_US", LanguageManager.SupportedCountryLocales.EN_US.localeCode());
        assertEquals("de_DE", LanguageManager.SupportedCountryLocales.DE_DE.localeCode());
    }

}