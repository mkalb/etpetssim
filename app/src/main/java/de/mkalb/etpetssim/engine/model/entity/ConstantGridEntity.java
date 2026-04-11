package de.mkalb.etpetssim.engine.model.entity;

/**
 * Marker interface for grid entities that are considered constant in the simulation engine.
 * <p>
 * All implementations of this interface represent entities whose state does not change during the simulation.
 * </p>
 * <p>
 * Note: {@code InterfaceMayBeAnnotatedFunctional} is suppressed because this interface inherits
 * the abstract method {@link GridEntity#descriptorId()} from {@link GridEntity}, technically making
 * it eligible for {@code @FunctionalInterface}. However, its purpose is purely as a marker.
 * </p>
 */
@SuppressWarnings({"MarkerInterface", "InterfaceMayBeAnnotatedFunctional"})
public interface ConstantGridEntity extends GridEntity {
}
