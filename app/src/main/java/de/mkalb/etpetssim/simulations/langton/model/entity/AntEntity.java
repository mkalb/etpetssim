package de.mkalb.etpetssim.simulations.langton.model.entity;

/**
 * Marker contract for ant-related agent entities in Langton's Ant.
 */
public sealed interface AntEntity extends LangtonEntity
        permits Ant, NoAgent {
}
