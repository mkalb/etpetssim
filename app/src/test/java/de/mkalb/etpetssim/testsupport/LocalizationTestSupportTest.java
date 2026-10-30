package de.mkalb.etpetssim.testsupport;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class LocalizationTestSupportTest {

    @Test
    void testAssertKeysInAllBundles() {
        assertDoesNotThrow(() -> LocalizationTestSupport.assertKeysInAllBundles(
                List.of(CellShape.labelResourceKey())));
    }

    @Test
    void testAssertKeysInAllBundlesRejectsMissingKey() {
        assertThrows(AssertionError.class, () -> LocalizationTestSupport.assertKeysInAllBundles(
                List.of(CellShape.labelResourceKey(), "missing.key.for.test")));
    }

    @Test
    void testAssertDescriptorKeysInAllBundlesRejectsMissingKey() {
        SpecBackedGridEntityDescriptorProvider provider = () -> new GridEntityDescriptorSpec(
                "test", true, CellShape.labelResourceKey(), CellShape.labelResourceKey(),
                CellShape.labelResourceKey(), "missing.emoji.key.for.test", null, null);
        assertThrows(AssertionError.class,
                () -> LocalizationTestSupport.assertDescriptorKeysInAllBundles(List.of(provider)));
    }

}
