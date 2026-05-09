package de.mkalb.etpetssim.simulations.wator.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Common contract for entities in the Wa-Tor simulation.
 */
public sealed interface WatorEntity extends GridEntity
        permits CreatureBase, TerrainConstant {

    String DESCRIPTOR_ID_WATER = "water";
    String DESCRIPTOR_ID_FISH = "fish";
    String DESCRIPTOR_ID_SHARK = "shark";

    /**
     * Indicates whether this entity is an agent.
     *
     * @return {@code true} when this entity is an agent
     */
    boolean isAgent();

    /**
     * Indicates whether this entity is a fish.
     *
     * @return {@code true} when this entity is a fish
     */
    boolean isFish();

    /**
     * Indicates whether this entity is a shark.
     *
     * @return {@code true} when this entity is a shark
     */
    boolean isShark();

    /**
     * Indicates whether this entity is water.
     *
     * @return {@code true} when this entity is water
     */
    boolean isWater();

}
