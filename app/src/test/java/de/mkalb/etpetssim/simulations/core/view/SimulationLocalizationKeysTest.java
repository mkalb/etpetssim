package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.testsupport.ConstantTestSupport;
import de.mkalb.etpetssim.testsupport.LocalizationTestSupport;
import org.junit.jupiter.api.Test;

final class SimulationLocalizationKeysTest {

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsExistInAllBundles() {
        LocalizationTestSupport.assertKeysInAllBundles(
                ConstantTestSupport.stringConstants(SimulationLocalizationKeys.class).values());
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsAreUniqueAndNonBlank() {
        ConstantTestSupport.assertConstantValuesUniqueAndNonBlank(SimulationLocalizationKeys.class);
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantNamesAreAlphabeticallySorted() {
        ConstantTestSupport.assertConstantNamesSorted(SimulationLocalizationKeys.class);
    }

}
