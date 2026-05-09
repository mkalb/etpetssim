package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Common contract for entities in the Rebounding simulation.
 */
public sealed interface ReboundingEntity extends GridEntity
        permits TerrainConstant, Rebounder {

    String DESCRIPTOR_ID_GROUND = "ground";
    String DESCRIPTOR_ID_WALL = "wall";
    String DESCRIPTOR_ID_REBOUNDER = "rebounder";

    /**
     * Indicates whether this entity is ground.
     *
     * @return {@code true} when this entity represents ground
     */
    boolean isGround();

    /**
     * Indicates whether this entity is a wall.
     *
     * @return {@code true} when this entity represents a wall
     */
    boolean isWall();

    /**
     * Indicates whether this entity is a rebounder.
     *
     * @return {@code true} when this entity represents a rebounder
     */
    boolean isRebounder();

}
