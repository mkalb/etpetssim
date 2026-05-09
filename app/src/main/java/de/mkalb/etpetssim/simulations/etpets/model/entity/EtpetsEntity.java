package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Common contract for all entity categories in the ET Pets simulation.
 */
public sealed interface EtpetsEntity extends GridEntity
        permits TerrainEntity, ResourceEntity, AgentEntity {

    // TerrainEntity
    String DESCRIPTOR_ID_GROUND = "ground";
    String DESCRIPTOR_ID_ROCK = "rock";
    String DESCRIPTOR_ID_WATER = "water";
    String DESCRIPTOR_ID_TRAIL = "trail";

    // ResourceEntity
    String DESCRIPTOR_ID_NO_RESOURCE = "no_resource";
    String DESCRIPTOR_ID_PLANT = "plant";
    String DESCRIPTOR_ID_INSECT = "insect";

    // AgentEntity
    String DESCRIPTOR_ID_NO_AGENT = "no_agent";
    String DESCRIPTOR_ID_PET = "pet";
    String DESCRIPTOR_ID_PET_EGG = "pet_egg";

    /**
     * Indicates whether this entity belongs to the terrain category.
     *
     * @return {@code true} when this entity is terrain
     */
    boolean isTerrain();

    /**
     * Indicates whether this entity belongs to the resource category.
     *
     * @return {@code true} when this entity is a resource
     */
    boolean isResource();

    /**
     * Indicates whether this entity belongs to the agent category.
     *
     * @return {@code true} when this entity is an agent
     */
    boolean isAgent();

    /**
     * Indicates whether this entity is considered empty for simulation logic.
     *
     * @return {@code true} when this entity is treated as empty
     */
    boolean isEmpty();

    /**
     * Indicates whether this entity is not considered empty.
     *
     * @return {@code true} when this entity is not empty
     */
    default boolean isNotEmpty() {
        return !isEmpty();
    }

}
