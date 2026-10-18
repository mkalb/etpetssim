package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

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
