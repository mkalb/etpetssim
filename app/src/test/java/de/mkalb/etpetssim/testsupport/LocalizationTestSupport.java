package de.mkalb.etpetssim.testsupport;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppResources;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorProvider;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utility class for checking resource keys against the main resource bundles.
 */
public final class LocalizationTestSupport {

    private LocalizationTestSupport() {
    }

    /**
     * Asserts that every key exists in the main resource bundle of each supported locale.
     *
     * @param keys the resource keys to check
     */
    public static void assertKeysInAllBundles(Collection<String> keys) {
        List<Executable> checks = new ArrayList<>();
        for (AppLocalization.CountryLocale countryLocale : AppLocalization.CountryLocale.values()) {
            ResourceBundle bundle = AppResources.getBundle(countryLocale.countryLocale()).orElseThrow();
            checks.add(() -> assertEquals(countryLocale.countryLocale(), bundle.getLocale()));
            for (String key : keys) {
                checks.add(() -> assertTrue(bundle.containsKey(key),
                        () -> "Missing key '" + key + "' for locale " + countryLocale.localeCode()));
            }
        }
        assertAll(checks);
    }

    /**
     * Asserts that all localization keys of the given descriptor providers exist in the main resource bundle
     * of each supported locale.
     *
     * <p>Checks the short name, long name, and description keys, and the emoji key when it is set.
     *
     * @param providers the descriptor providers to check
     */
    public static void assertDescriptorKeysInAllBundles(Collection<? extends GridEntityDescriptorProvider> providers) {
        List<String> keys = new ArrayList<>();
        for (GridEntityDescriptorProvider provider : providers) {
            keys.add(provider.shortNameKey());
            keys.add(provider.longNameKey());
            keys.add(provider.descriptionKey());
            String emojiKey = provider.emojiKey();
            if (emojiKey != null) {
                keys.add(emojiKey);
            }
        }
        assertKeysInAllBundles(keys);
    }

}
