package de.mkalb.etpetssim.simulations.langton.model.entity;

/**
 * Marker contract for ant-related agent entities in the Langton simulation.
 */
public sealed interface AntEntity extends LangtonEntity
        permits Ant, NoAgent {

    String DESCRIPTOR_ID_ANT = "ant";
    String DESCRIPTOR_ID_NO_AGENT = "no_agent";

}
