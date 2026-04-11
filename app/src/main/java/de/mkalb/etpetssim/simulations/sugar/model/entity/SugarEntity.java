package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

public sealed interface SugarEntity extends GridEntity
        permits ResourceEntity, AgentEntity {

    String DESCRIPTOR_ID_AGENT = "agent";
    String DESCRIPTOR_ID_NO_AGENT = "no_agent";
    String DESCRIPTOR_ID_NO_RESOURCE = "no_resource";
    String DESCRIPTOR_ID_SUGAR = "sugar";
    String DESCRIPTOR_ID_TERRAIN = "terrain";

    boolean isResource();

    boolean isAgent();

    boolean isEmpty();

    default boolean isNotEmpty() {
        return !isEmpty();
    }

}
