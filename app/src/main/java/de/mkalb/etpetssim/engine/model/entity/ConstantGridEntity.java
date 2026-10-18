package de.mkalb.etpetssim.engine.model.entity;

/**
 * Marker interface for grid entities whose state does not change during a simulation.
 *
 * @see GridEntity#isConstant(GridEntity)
 */
@SuppressWarnings({"MarkerInterface", "InterfaceMayBeAnnotatedFunctional"})
public interface ConstantGridEntity extends GridEntity {
}
