package de.mkalb.etpetssim.engine.model;

/**
 * Functional interface for determining whether a simulation has finished.
 * <p>
 * Implementations define the termination condition based on the current state
 * of the simulation model, the simulation step count, and a context object.
 *
 * @param <ENT> the type of {@link GridEntity} in the simulation
 * @param <GM> the type of {@link GridModel} in the simulation
 * @param <C> the type of the context object used to share or accumulate state or statistics during the simulation
 */
@FunctionalInterface
public interface SimulationTerminationCondition<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        C> {

    /**
     * Checks whether the simulation is finished.
     *
     * @param model the current simulation model
     * @param stepCount the number of simulation steps completed (i.e., the next step to be executed, starting from 0)
     * @param context a context object with additional information
     * @return {@code true} if the simulation should terminate, {@code false} otherwise
     */
    boolean isFinished(GM model, int stepCount, C context);

    /**
     * Returns a composed termination condition that is satisfied only if both
     * this and the {@code other} condition are satisfied.
     *
     * @param other another termination condition to combine with
     * @return a new termination condition representing the logical AND of both conditions
     */
    default SimulationTerminationCondition<ENT, GM, C> and(SimulationTerminationCondition<ENT, GM, C> other) {
        return (model, stepCount, context) -> isFinished(model, stepCount, context) && other.isFinished(model, stepCount, context);
    }

    /**
     * Returns a composed termination condition that is satisfied if either
     * this or the {@code other} condition is satisfied.
     *
     * @param other another termination condition to combine with
     * @return a new termination condition representing the logical OR of both conditions
     */
    default SimulationTerminationCondition<ENT, GM, C> or(SimulationTerminationCondition<ENT, GM, C> other) {
        return (model, stepCount, context) -> isFinished(model, stepCount, context) || other.isFinished(model, stepCount, context);
    }

    /**
     * Returns a termination condition that is satisfied when this condition is not.
     *
     * @return a new termination condition representing the logical negation of this condition
     */
    default SimulationTerminationCondition<ENT, GM, C> negate() {
        return (model, stepCount, context) -> !isFinished(model, stepCount, context);
    }

}
