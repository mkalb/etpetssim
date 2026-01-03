package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

public sealed interface SugarEntity extends GridEntity
        permits SugarResourceEntity, SugarAgentEntity {

    String DESCRIPTOR_ID_AGENT = "agent";
    String DESCRIPTOR_ID_AGENT_NONE = "agent_none";
    String DESCRIPTOR_ID_RESOURCE_NONE = "resource_none";
    String DESCRIPTOR_ID_RESOURCE_SUGAR = "resource_sugar";
    String DESCRIPTOR_ID_TERRAIN = "terrain";

    @SuppressWarnings("SameReturnValue")
    boolean isTerrain();

    boolean isResource();

    boolean isAgent();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isNone();

}
