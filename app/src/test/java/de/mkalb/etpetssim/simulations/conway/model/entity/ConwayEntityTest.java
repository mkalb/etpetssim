package de.mkalb.etpetssim.simulations.conway.model.entity;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertDescriptorKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class ConwayEntityTest {

    @Test
    void testDescriptorSpec() {
        List<ConwayEntity> entities = List.of(ConwayEntity.values());
        List<String> descriptorIds = entities.stream().map(ConwayEntity::descriptorId).toList();
        assertAll(
                () -> assertEquals(descriptorIds.size(), Set.copyOf(descriptorIds).size()),
                () -> assertDescriptorKeysInAllBundles(entities)
        );
    }

}
