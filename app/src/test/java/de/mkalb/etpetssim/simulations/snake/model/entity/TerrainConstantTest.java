package de.mkalb.etpetssim.simulations.snake.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void testIsGround() {
        assertAll(
                () -> assertTrue(TerrainConstant.GROUND.isGround()),
                () -> assertFalse(TerrainConstant.WALL.isGround())
        );
    }

    @Test
    void testIsWall() {
        assertAll(
                () -> assertTrue(TerrainConstant.WALL.isWall()),
                () -> assertFalse(TerrainConstant.GROUND.isWall())
        );
    }

    @Test
    void testIsFood() {
        assertAll(
                () -> assertTrue(TerrainConstant.GROWTH_FOOD.isFood()),
                () -> assertFalse(TerrainConstant.SNAKE_SEGMENT.isFood())
        );
    }

}
