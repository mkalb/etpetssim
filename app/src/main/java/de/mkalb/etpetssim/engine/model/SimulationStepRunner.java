package de.mkalb.etpetssim.engine.model;

/**
 * Represents a runner for a single step in a simulation.
 * <p>
 * Implementations define how a simulation step is performed,
 * using a context object to maintain or update simulation state across steps.
 *
 * @param <C> the type of the context object provided to the simulation step
 */
@FunctionalInterface
public interface SimulationStepRunner<C> {

    /**
     * Performs a single simulation step.
     *
     * @param stepIndex the index of the current simulation step
     * @param context the context object used to share or accumulate state during the simulation
     */
    void performStep(long stepIndex, C context);

}
