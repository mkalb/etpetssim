package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.testsupport.ConstantTestSupport;
import org.junit.jupiter.api.Test;

final class ConwayStyleClassesTest {

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsAreUniqueAndNonBlank() {
        ConstantTestSupport.assertConstantValuesUniqueAndNonBlank(ConwayStyleClasses.class);
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsUseCssTokenFormat() {
        ConstantTestSupport.assertConstantValuesMatch(ConwayStyleClasses.class, "conway-[a-z0-9-]+");
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantNamesAreAlphabeticallySorted() {
        ConstantTestSupport.assertConstantNamesSorted(ConwayStyleClasses.class);
    }

}
