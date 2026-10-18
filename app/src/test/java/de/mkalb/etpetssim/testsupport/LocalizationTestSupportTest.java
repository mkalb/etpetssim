package de.mkalb.etpetssim.testsupport;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.entity.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class LocalizationTestSupportTest {

    @Test
    void testAssertKeysInAllBundles() {
        assertDoesNotThrow(() -> LocalizationTestSupport.assertKeysInAllBundles(
                List.of(AppLocalizationKeys.ENUM_LABEL_CELLSHAPE)));
    }

    @Test
    void testAssertKeysInAllBundlesRejectsMissingKey() {
        assertThrows(AssertionError.class, () -> LocalizationTestSupport.assertKeysInAllBundles(
                List.of(AppLocalizationKeys.ENUM_LABEL_CELLSHAPE, "missing.key.for.test")));
    }

    @Test
    void testAssertDescriptorKeysInAllBundlesRejectsMissingKey() {
        SpecBackedGridEntityDescriptorProvider provider = () -> new GridEntityDescriptorSpec(
                "test", true, AppLocalizationKeys.ENUM_LABEL_CELLSHAPE, AppLocalizationKeys.ENUM_LABEL_CELLSHAPE,
                AppLocalizationKeys.ENUM_LABEL_CELLSHAPE, "missing.emoji.key.for.test", null, null);
        assertThrows(AssertionError.class,
                () -> LocalizationTestSupport.assertDescriptorKeysInAllBundles(List.of(provider)));
    }

}
