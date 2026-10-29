package de.mkalb.etpetssim.simulations.forest.model.entity;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertDescriptorKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class ForestEntityTest {

    @Test
    void testDescriptorSpec() {
        List<ForestEntity> entities = List.of(ForestEntity.values());
        List<String> descriptorIds = entities.stream().map(ForestEntity::descriptorId).toList();
        assertAll(
                () -> assertEquals(descriptorIds.size(), Set.copyOf(descriptorIds).size()),
                () -> assertDescriptorKeysInAllBundles(entities)
        );
    }

}
