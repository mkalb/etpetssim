package de.mkalb.etpetssim.simulations.langton.model.entity;

/**
 * Marker contract for ant-related agent entities in the Langton simulation.
 */
public sealed interface AntEntity extends LangtonEntity
        permits Ant, NoAgent {
}
