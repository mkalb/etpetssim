package de.mkalb.etpetssim.testsupport;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ConstantTestSupportTest {

    @Test
    void testStringConstants() {
        assertEquals(List.of(Map.entry("BETA", "beta"), Map.entry("ALPHA", "alpha")),
                List.copyOf(ConstantTestSupport.stringConstants(UnsortedConstants.class).sequencedEntrySet()));
    }

    @Test
    void testAssertConstantNamesSorted() {
        assertDoesNotThrow(() -> ConstantTestSupport.assertConstantNamesSorted(SortedConstants.class));
    }

    @Test
    void testAssertConstantNamesSortedRejectsUnsortedNames() {
        assertThrows(AssertionError.class,
                () -> ConstantTestSupport.assertConstantNamesSorted(UnsortedConstants.class));
    }

    @Test
    void testAssertConstantNamesSortedRejectsClassWithoutStringConstants() {
        assertThrows(AssertionError.class,
                () -> ConstantTestSupport.assertConstantNamesSorted(NoStringConstants.class));
    }

    @Test
    void testAssertConstantValuesUniqueAndNonBlank() {
        assertDoesNotThrow(() -> ConstantTestSupport.assertConstantValuesUniqueAndNonBlank(SortedConstants.class));
    }

    @Test
    void testAssertConstantValuesUniqueAndNonBlankRejectsDuplicateValue() {
        assertThrows(AssertionError.class,
                () -> ConstantTestSupport.assertConstantValuesUniqueAndNonBlank(DuplicateConstants.class));
    }

    @Test
    void testAssertConstantValuesUniqueAndNonBlankRejectsBlankValue() {
        assertThrows(AssertionError.class,
                () -> ConstantTestSupport.assertConstantValuesUniqueAndNonBlank(BlankConstants.class));
    }

    @Test
    void testAssertConstantValuesMatch() {
        assertDoesNotThrow(() -> ConstantTestSupport.assertConstantValuesMatch(SortedConstants.class, "[a-z]+"));
    }

    @Test
    void testAssertConstantValuesMatchRejectsNonMatchingValue() {
        assertThrows(AssertionError.class,
                () -> ConstantTestSupport.assertConstantValuesMatch(SortedConstants.class, "a[a-z]*"));
    }

    static final class SortedConstants {

        public static final String ALPHA = "alpha";
        public static final String BETA = "beta";
        public static final int NUMBER = 1;
        static final String HIDDEN = "Hidden value";

        private SortedConstants() {
        }

    }

    static final class UnsortedConstants {

        public static final String BETA = "beta";
        public static final String ALPHA = "alpha";

        private UnsortedConstants() {
        }

    }

    static final class DuplicateConstants {

        public static final String ALPHA = "same";
        public static final String BETA = "same";

        private DuplicateConstants() {
        }

    }

    static final class BlankConstants {

        public static final String ALPHA = "alpha";
        public static final String BETA = " ";

        private BlankConstants() {
        }

    }

    static final class NoStringConstants {

        public static final int NUMBER = 1;

        private NoStringConstants() {
        }

    }

}
