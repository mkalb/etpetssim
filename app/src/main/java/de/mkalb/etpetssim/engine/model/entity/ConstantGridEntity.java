package de.mkalb.etpetssim.engine.model.entity;

/**
 * Marker interface for grid entities that are considered constant in the simulation engine.
 * <p>
 * All implementations of this interface represent entities whose state does not change during the simulation.
 * </p>
 */
@SuppressWarnings({"MarkerInterface", "InterfaceMayBeAnnotatedFunctional"})
public interface ConstantGridEntity extends GridEntity {
}
