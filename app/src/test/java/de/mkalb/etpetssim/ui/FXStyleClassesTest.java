package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.testsupport.ConstantTestSupport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void testConstantNamesMatchValues() {
        List<String> mismatchedNames = ConstantTestSupport.stringConstants(FXStyleClasses.class)
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(entry.getValue().toUpperCase(Locale.ROOT).replace('-', '_')))
                .map(Map.Entry::getKey)
                .toList();

        assertEquals(List.of(), mismatchedNames);
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testConstantNamesAreAlphabeticallySorted() {
        ConstantTestSupport.assertConstantNamesSorted(FXStyleClasses.class);
    }

}
