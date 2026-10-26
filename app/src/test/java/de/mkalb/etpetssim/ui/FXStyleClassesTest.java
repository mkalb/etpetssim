package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.testsupport.ConstantTestSupport;
import org.junit.jupiter.api.Test;

final class FXStyleClassesTest {

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsAreUniqueAndNonBlank() {
        ConstantTestSupport.assertConstantValuesUniqueAndNonBlank(FXStyleClasses.class);
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantsUseCssTokenFormat() {
        ConstantTestSupport.assertConstantValuesMatch(FXStyleClasses.class, "[a-z][a-z0-9-]*");
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantNamesAreAlphabeticallySorted() {
        ConstantTestSupport.assertConstantNamesSorted(FXStyleClasses.class);
    }

}
