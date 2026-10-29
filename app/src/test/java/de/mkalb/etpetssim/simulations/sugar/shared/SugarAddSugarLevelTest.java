package de.mkalb.etpetssim.simulations.sugar.shared;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class SugarAddSugarLevelTest {

    @Test
    void testLabelKey() {
        assertKeysInAllBundles(Arrays.stream(SugarAddSugarLevel.values()).map(SugarAddSugarLevel::labelKey).toList());
    }

    @Test
    void testComputeSugarAmount() {
        assertAll(
                () -> assertEquals(3, SugarAddSugarLevel.LOW.computeSugarAmount(10)),
                () -> assertEquals(5, SugarAddSugarLevel.MEDIUM.computeSugarAmount(10)),
                () -> assertEquals(10, SugarAddSugarLevel.HIGH.computeSugarAmount(10))
        );
    }

    @Test
    void testComputeSugarAmountReturnsAtLeastOne() {
        assertAll(
                () -> assertEquals(1, SugarAddSugarLevel.LOW.computeSugarAmount(1)),
                () -> assertEquals(1, SugarAddSugarLevel.MEDIUM.computeSugarAmount(1)),
                () -> assertEquals(1, SugarAddSugarLevel.HIGH.computeSugarAmount(1))
        );
    }

}
