package de.mkalb.etpetssim.engine.model;

import java.util.*;

/**
 * Functional interface for determining whether a simulation has finished.
 * <p>
 * Implementations define the termination condition based on the current state
 * of the simulation model and the current simulation step.
 *
 * @param <T> the type of {@link GridEntity} in the simulation
 */
@FunctionalInterface
public interface SimulationTerminationCondition<T extends GridEntity> {

    /**
     * Checks whether the simulation is finished.
     *
     * @param model the current simulation model
     * @param step the current simulation step (starting from 0)
     * @return {@code true} if the simulation should terminate, {@code false} otherwise
     */
    boolean isFinished(GridModel<T> model, long step);

    /**
     * Returns a composed termination condition that is satisfied only if both
     * this and the {@code other} condition are satisfied.
     *
     * @param other another termination condition to combine with
     * @return a new termination condition representing the logical AND of both conditions
     */
    default SimulationTerminationCondition<T> and(SimulationTerminationCondition<T> other) {
        Objects.requireNonNull(other);
        return (model, step) -> isFinished(model, step) && other.isFinished(model, step);
    }

    /**
     * Returns a composed termination condition that is satisfied if either
     * this or the {@code other} condition is satisfied.
     *
     * @param other another termination condition to combine with
     * @return a new termination condition representing the logical OR of both conditions
     */
    default SimulationTerminationCondition<T> or(SimulationTerminationCondition<T> other) {
        Objects.requireNonNull(other);
        return (model, step) -> isFinished(model, step) || other.isFinished(model, step);
    }

    /**
     * Returns a termination condition that is satisfied when this condition is not.
     *
     * @return a new termination condition representing the logical negation of this condition
     */
    default SimulationTerminationCondition<T> negate() {
        return (model, step) -> !isFinished(model, step);
    }

}