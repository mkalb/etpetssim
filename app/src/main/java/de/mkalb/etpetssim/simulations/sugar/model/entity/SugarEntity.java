package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.GridEntity;

public sealed interface SugarEntity extends GridEntity
        permits SugarTerrainEntity, SugarResourceEntity, SugarAgentEntity {

    String DESCRIPTOR_ID_TERRAIN = "terrain";
    String DESCRIPTOR_ID_RESOURCE_SUGAR = "resource_sugar";
    String DESCRIPTOR_ID_RESOURCE_NONE = "resource_none";
    String DESCRIPTOR_ID_AGENT = "agent";
    String DESCRIPTOR_ID_AGENT_NONE = "agent_none";

    boolean isTerrain();

    boolean isResource();

    boolean isAgent();

    boolean isNone();

}
