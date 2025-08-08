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
    int stepCount();

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
     * Executes up to the specified number of simulation steps, or until the simulation is finished or the thread is interrupted.
     * <p>
     * After each executed step, the provided {@code onStep} callback is invoked. This allows external code to track progress,
     * update the UI, or perform other actions after each step.
     * <p>
     * The method will terminate early if the simulation is no longer running (as determined by {@link #isRunning()})
     * or if the current thread is interrupted.
     *
     * @param count  the maximum number of steps to execute
     * @param onStep a {@link Runnable} to be called after each executed step
     */
    default void executeSteps(int count, Runnable onStep) {
        for (int i = 0; (i < count) && isRunning(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            executeStep();
            onStep.run();
        }
    }

}
