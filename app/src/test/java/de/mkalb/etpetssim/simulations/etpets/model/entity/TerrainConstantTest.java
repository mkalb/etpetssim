package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class TerrainConstantTest {

    @Test
    void testDescriptorId() {
        Set<String> registeredIds = new HashSet<>();
        for (EntityDescriptors descriptor : EntityDescriptors.values()) {
            registeredIds.add(descriptor.descriptorId());
        }
        Stream<Executable> checks = Arrays.stream(TerrainConstant.values())
                .map(constant -> () -> assertTrue(
                        registeredIds.contains(constant.descriptorId()), constant.name()));
        assertAll(checks);
    }

}
