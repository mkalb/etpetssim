package de.mkalb.etpetssim.engine.model;

/**
 * Represents a single step in a simulation.
 * <p>
 * Implementations define how a simulation step is performed,
 * such as updating the state of a model or processing agents.
 *
 * @param <T> the type of {@link GridEntity} processed in the simulation step
 */
@FunctionalInterface
public interface SimulationStep<T extends GridEntity> {

    /**
     * Performs a single simulation step.
     *
     * @param currentStep the current simulation step number
     */
    void performStep(long currentStep);

}
