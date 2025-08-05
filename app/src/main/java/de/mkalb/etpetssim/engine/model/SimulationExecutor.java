package de.mkalb.etpetssim.engine.model;

/**
 * Executes simulation steps for a given model until a specified termination condition is met.
 * <p>
 * This interface defines the contract for managing the simulation lifecycle,
 * including stepwise and batch execution. It also provides access to the current model state
 * and the simulation step count.
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
     * Returns the number of simulation steps completed so far.
     * This value represents the next step to be executed (starting from 0).
     *
     * @return the number of completed simulation steps
     */
    long stepCount();

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

}
