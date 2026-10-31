package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class AppLauncherTest {

    @Test
    void testValidArgumentOverridesSavedLocale() {
        assertEquals("en", AppLauncher.resolveLocaleArgument("en", CountryLocale.DE_DE));
        assertEquals("de_DE", AppLauncher.resolveLocaleArgument("de_DE", CountryLocale.EN_US));
    }

    @Test
    void testSavedLocaleWithoutArgument() {
        assertEquals("de_DE", AppLauncher.resolveLocaleArgument(null, CountryLocale.DE_DE));
    }

    @Test
    void testInvalidArgumentFallsBackToSavedLocale() {
        assertEquals("de_DE", AppLauncher.resolveLocaleArgument("fr", CountryLocale.DE_DE));
    }

    @Test
    void testNoArgumentAndNoSavedLocaleUsesSystemLocale() {
        assertNull(AppLauncher.resolveLocaleArgument(null, null));
        assertNull(AppLauncher.resolveLocaleArgument("fr", null));
    }

}
