package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

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

    boolean isTerrain();

    boolean isResource();

    boolean isAgent();

    boolean isNone();

}
