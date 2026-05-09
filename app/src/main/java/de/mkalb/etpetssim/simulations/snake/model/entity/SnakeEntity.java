package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Common contract for entities in the Snake simulation.
 */
public sealed interface SnakeEntity extends GridEntity
        permits TerrainConstant, SnakeHead {

    String DESCRIPTOR_ID_GROUND = "ground";
    String DESCRIPTOR_ID_WALL = "wall";
    String DESCRIPTOR_ID_GROWTH_FOOD = "growth_food";
    String DESCRIPTOR_ID_SNAKE_SEGMENT = "snake_segment";
    String DESCRIPTOR_ID_SNAKE_HEAD = "snake_head";

    /**
     * Indicates whether this entity is controlled by the snake agent logic.
     *
     * @return {@code true} when this entity is an agent entity
     */
    boolean isAgent();

    /**
     * Indicates whether this entity represents plain ground.
     *
     * @return {@code true} when this entity is ground
     */
    default boolean isGround() {
        return DESCRIPTOR_ID_GROUND.equals(descriptorId());
    }

    /**
     * Indicates whether this entity is static terrain (ground or wall).
     *
     * @return {@code true} when this entity is static terrain
     */
    default boolean isStaticTerrain() {
        return DESCRIPTOR_ID_GROUND.equals(descriptorId()) ||
                DESCRIPTOR_ID_WALL.equals(descriptorId());
    }

}
