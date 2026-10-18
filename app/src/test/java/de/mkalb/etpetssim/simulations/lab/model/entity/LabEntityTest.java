package de.mkalb.etpetssim.simulations.lab.model.entity;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertDescriptorKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.*;

final class LabEntityTest {

    @Test
    void testDescriptorSpec() {
        List<LabEntity> entities = List.of(LabEntity.values());
        List<String> descriptorIds = entities.stream().map(LabEntity::descriptorId).toList();
        assertAll(
                () -> assertEquals(descriptorIds.size(), Set.copyOf(descriptorIds).size()),
                () -> assertDescriptorKeysInAllBundles(entities)
        );
    }

}
