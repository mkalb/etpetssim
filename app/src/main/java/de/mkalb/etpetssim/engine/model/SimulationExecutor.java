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
     * Checks whether the simulation has finished.
     * <p>
     * Returns {@code true} if the simulation has reached its termination condition;
     * further calls to {@code executeStep} or {@code executeSteps} may still be possible.
     *
     * @return {@code true} if the simulation is finished, {@code false} otherwise
     */
    boolean isFinished();

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
     * The method will terminate early if {@code checkTermination} is {@code true} and the simulation has reached its termination condition
     * (as determined by {@link #isFinished()}), or if the current thread is interrupted.
     *
     * @param count the maximum number of steps to execute
     * @param checkTermination if {@code true}, the method will terminate early when the simulation is finished
     * @param onStep a {@link Runnable} to be called after each executed step
     * @return an {@link ExecutionResult} containing:
     * <ul>
     *   <li>the current step count</li>
     *   <li>the number of steps executed in this call</li>
     *   <li>whether the simulation has reached its termination condition</li>
     *   <li>whether the thread was interrupted</li>
     * </ul>
     */
    default ExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep) {
        int stepBefore = stepCount();
        for (int i = 0; i < count; i++) {
            if (Thread.currentThread().isInterrupted()) {
                int stepAfter = stepCount();
                return new ExecutionResult(stepAfter, stepAfter - stepBefore, false, true);
            }
            executeStep();
            onStep.run();
            if (Thread.currentThread().isInterrupted()) {
                int stepAfter = stepCount();
                return new ExecutionResult(stepAfter, stepAfter - stepBefore, false, true);
            }
            if (checkTermination && isFinished()) {
                int stepAfter = stepCount();
                return new ExecutionResult(stepAfter, stepAfter - stepBefore, true, false);
            }
        }
        int stepAfter = stepCount();
        return new ExecutionResult(stepAfter, stepAfter - stepBefore, false, false);
    }

    record ExecutionResult(
            int stepCount,
            int executedSteps,
            boolean isFinished,
            boolean isInterrupted
    ) {}

}
