package de.mkalb.etpetssim.simulations.langton.model.entity;

/**
 * Marker contract for ant-related agent entities in the Langton simulation.
 */
@SuppressWarnings("MarkerInterface")
public sealed interface AntEntity extends LangtonEntity
        permits Ant, NoAgent {
}
