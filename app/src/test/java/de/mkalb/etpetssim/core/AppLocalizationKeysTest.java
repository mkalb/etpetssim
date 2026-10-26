package de.mkalb.etpetssim.core;

import de.mkalb.etpetssim.testsupport.*;
import org.junit.jupiter.api.Test;

final class AppLocalizationKeysTest {

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsExistInAllBundles() {
        LocalizationTestSupport.assertKeysInAllBundles(
                ConstantTestSupport.stringConstants(AppLocalizationKeys.class).values());
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsAreUniqueAndNonBlank() {
        ConstantTestSupport.assertConstantValuesUniqueAndNonBlank(AppLocalizationKeys.class);
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantNamesAreAlphabeticallySorted() {
        ConstantTestSupport.assertConstantNamesSorted(AppLocalizationKeys.class);
    }

}
