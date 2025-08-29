package de.mkalb.etpetssim.engine.model;

/**
 * Decorator for {@link SimulationExecutor} that measures the execution time of each simulation step.
 * <p>
 * This executor tracks the minimum, maximum, and most recent step durations (in milliseconds).
 * <p>
 * Timing statistics are accessible via getter methods.
 *
 * @param <T> the type of {@link GridEntity} in the simulation
 */
public final class TimedSimulationExecutor<T extends GridEntity> implements SimulationExecutor<T> {

    private final SimulationExecutor<T> delegate;

    private long currentStepMillis = Long.MIN_VALUE;
    private long minStepMillis = Long.MAX_VALUE;
    private long maxStepMillis = Long.MIN_VALUE;
    private long sumStepMillis = 0;

    /**
     * Creates a new {@code TimedSimulationExecutor}.
     *
     * @param delegate the underlying {@link SimulationExecutor} to decorate
     */
    public TimedSimulationExecutor(SimulationExecutor<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public GridModel<T> currentModel() {
        return delegate.currentModel();
    }

    @Override
    public int stepCount() {
        return delegate.stepCount();
    }

    @Override
    public boolean isFinished() {
        return delegate.isFinished();
    }

    @Override
    public boolean isExecutorFinished() {
        return delegate.isExecutorFinished();
    }

    /**
     * Executes a single simulation step, increments the step counter,
     * measures its duration, and updates timing statistics.
     */
    @Override
    public void executeStep() {
        long start = System.currentTimeMillis();
        delegate.executeStep();
        currentStepMillis = System.currentTimeMillis() - start;

        // Update min/max
        if (currentStepMillis < minStepMillis) {
            minStepMillis = currentStepMillis;
        }
        if (currentStepMillis > maxStepMillis) {
            maxStepMillis = currentStepMillis;
        }
        sumStepMillis += currentStepMillis;
    }

    /**
     * Returns an immutable record containing timing statistics for simulation steps.
     * <p>
     * The statistics include the minimum, maximum, and most recent step durations,
     * as well as the sum and average of all step durations (in milliseconds).
     * <p>
     * If no steps have been executed yet, all values in the returned record are {@code 0}.
     *
     * @return a {@link StepTimingStatistics} record with the current timing statistics
     */
    public StepTimingStatistics stepTimingStatistics() {
        int steps = stepCount();
        if (steps > 0) {
            return new StepTimingStatistics(
                    currentStepMillis,
                    minStepMillis,
                    maxStepMillis,
                    sumStepMillis,
                    (sumStepMillis / steps)
            );
        }
        return StepTimingStatistics.empty();
    }

    /**
     * Returns the duration (ms) of the most recently executed simulation step.
     *
     * @return the last step duration in milliseconds, or {@link Long#MIN_VALUE} if no steps have been executed
     */
    public long currentStepMillis() {
        return currentStepMillis;
    }

}
