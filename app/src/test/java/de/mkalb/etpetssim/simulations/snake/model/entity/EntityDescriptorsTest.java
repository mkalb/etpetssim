package de.mkalb.etpetssim.simulations.snake.model.entity;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertDescriptorKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.*;

final class EntityDescriptorsTest {

    @Test
    void testDescriptorSpec() {
        List<EntityDescriptors> descriptors = List.of(EntityDescriptors.values());
        List<String> descriptorIds = descriptors.stream().map(EntityDescriptors::descriptorId).toList();
        assertAll(
                () -> assertEquals(descriptorIds.size(), Set.copyOf(descriptorIds).size()),
                () -> assertDescriptorKeysInAllBundles(descriptors)
        );
    }

}
