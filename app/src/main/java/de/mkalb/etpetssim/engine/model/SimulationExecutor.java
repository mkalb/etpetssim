package de.mkalb.etpetssim.engine.model;

/**
 * Executes simulation steps for a given model until a specified termination condition is met.
 * <p>
 * This interface defines the contract for managing the simulation lifecycle,
 * including stepwise execution, batch execution, and running the simulation to completion.
 * It also provides access to the current model state and step count, and supports resetting the simulation.
 *
 * @param <T> the type of {@link GridEntity} in the simulation
 */
public interface SimulationExecutor<T extends GridEntity> {

    /**
     * Returns the current simulation model.
     *
     * @return the current {@link GridModel}
     */
    GridModel<T> currentModel();

    /**
     * Returns the current simulation step count.
     *
     * @return the current step (starting from 0)
     */
    long currentStep();

    /**
     * Checks whether the simulation is currently running.
     * <p>
     * Returns {@code true} if the simulation has not yet reached its termination condition
     * and can continue to execute steps; returns {@code false} if the simulation is finished.
     *
     * @return {@code true} if the simulation is still running, {@code false} otherwise
     */
    boolean isRunning();

    /**
     * Executes a single simulation step and increments the step counter.
     */
    void executeStep();

    /**
     * Executes up to the specified number of simulation steps, or until the simulation is finished.
     *
     * @param count the maximum number of steps to execute
     */
    void executeSteps(int count);

    /**
     * Executes simulation steps until the termination condition is met.
     */
    void executeAllSteps();

    /**
     * Resets the simulation step counter to zero.
     */
    void reset();

}
