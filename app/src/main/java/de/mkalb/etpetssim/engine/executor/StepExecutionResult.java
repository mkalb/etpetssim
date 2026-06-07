package de.mkalb.etpetssim.engine.executor;

/**
 * Result object returned by {@link SimulationExecutor#executeSteps(int, boolean, Runnable)}.
 *
 * @param stepCount     the step counter after execution
 * @param executedSteps the number of steps executed in this call
 * @param isFinished    whether execution ended in a finished state (logical via {@link SimulationExecutor#isFinished()}
 *                      or technical via {@link SimulationExecutor#isExecutorFinished()})
 * @param isInterrupted whether execution ended because the current thread was interrupted
 *                      <p>
 *                      The canonical constructor rejects negative values and also rejects
 *                      {@code executedSteps > stepCount}.
 */
public record StepExecutionResult(
        int stepCount,
        int executedSteps,
        boolean isFinished,
        boolean isInterrupted
) {

    public StepExecutionResult {
        if (stepCount < 0) {
            throw new IllegalArgumentException("stepCount must be >= 0");
        }
        if (executedSteps < 0) {
            throw new IllegalArgumentException("executedSteps must be >= 0");
        }
        if (executedSteps > stepCount) {
            throw new IllegalArgumentException("executedSteps must be <= stepCount");
        }
    }

}
