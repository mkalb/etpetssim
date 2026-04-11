package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

public sealed interface EtpetsEntity extends GridEntity
        permits TerrainEntity, ResourceEntity, AgentEntity {

    String DESCRIPTOR_ID_TERRAIN_GROUND = "terrain_ground";
    String DESCRIPTOR_ID_TERRAIN_ROCK = "terrain_rock";
    String DESCRIPTOR_ID_TERRAIN_WATER = "terrain_water";
    String DESCRIPTOR_ID_TERRAIN_TRAIL = "terrain_trail";

    String DESCRIPTOR_ID_RESOURCE_NONE = "resource_none";
    String DESCRIPTOR_ID_RESOURCE_PLANT = "resource_plant";
    String DESCRIPTOR_ID_RESOURCE_INSECT = "resource_insect";

    String DESCRIPTOR_ID_AGENT_NONE = "agent_none";
    String DESCRIPTOR_ID_AGENT_PET = "agent_pet";
    String DESCRIPTOR_ID_AGENT_PET_EGG = "agent_pet_egg";

    boolean isTerrain();

    boolean isResource();

    boolean isAgent();

    boolean isNone();

}
