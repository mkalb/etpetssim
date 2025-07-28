package de.mkalb.etpetssim.engine.model;

/**
 * Functional interface for determining whether a simulation has finished.
 * <p>
 * Implementations define the termination condition based on the current state
 * of the simulation model, the current simulation step, and a context object.
 *
 * @param <T> the type of {@link GridEntity} in the simulation
 * @param <C> the type of the context object (e.g., statistics or additional state)
 */
@FunctionalInterface
public interface SimulationTerminationCondition<T extends GridEntity, C> {

    /**
     * Checks whether the simulation is finished.
     *
     * @param model the current simulation model
     * @param step the current simulation step (starting from 0)
     * @param context a context object with additional information
     * @return {@code true} if the simulation should terminate, {@code false} otherwise
     */
    boolean isFinished(GridModel<T> model, long step, C context);

    /**
     * Returns a composed termination condition that is satisfied only if both
     * this and the {@code other} condition are satisfied.
     *
     * @param other another termination condition to combine with
     * @return a new termination condition representing the logical AND of both conditions
     */
    default SimulationTerminationCondition<T, C> and(SimulationTerminationCondition<T, C> other) {
        return (model, step, context) -> isFinished(model, step, context) && other.isFinished(model, step, context);
    }

    /**
     * Returns a composed termination condition that is satisfied if either
     * this or the {@code other} condition is satisfied.
     *
     * @param other another termination condition to combine with
     * @return a new termination condition representing the logical OR of both conditions
     */
    default SimulationTerminationCondition<T, C> or(SimulationTerminationCondition<T, C> other) {
        return (model, step, context) -> isFinished(model, step, context) || other.isFinished(model, step, context);
    }

    /**
     * Returns a termination condition that is satisfied when this condition is not.
     *
     * @return a new termination condition representing the logical negation of this condition
     */
    default SimulationTerminationCondition<T, C> negate() {
        return (model, step, context) -> !isFinished(model, step, context);
    }

}
