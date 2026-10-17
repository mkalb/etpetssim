package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Decorator for {@link SimulationExecutor} that measures the execution time of each simulation step.
 * <p>
 * Tracks the minimum, maximum, and most recent step durations in nanoseconds.
 * <p>
 * Timing statistics are accessible via {@link #stepTimingStatistics()} and {@link #currentStepNanos()}.
 *
 * @param <ENT> the type of {@link GridEntity} in the simulation
 * @param <GM>  the type of {@link GridModel} in the simulation
 */
public final class TimedSimulationExecutor<
        ENT extends GridEntity,
        GM extends GridModel<ENT>>
        implements SimulationExecutor<ENT, GM> {

    private final SimulationExecutor<ENT, GM> delegate;

    private long currentStepNanos = Long.MIN_VALUE;
    private long minStepNanos = Long.MAX_VALUE;
    private long maxStepNanos = Long.MIN_VALUE;
    private long sumStepNanos = 0;

    /**
     * Creates a new {@code TimedSimulationExecutor}.
     *
     * @param delegate the underlying {@link SimulationExecutor} to decorate
     */
    public TimedSimulationExecutor(SimulationExecutor<ENT, GM> delegate) {
        this.delegate = delegate;
    }

    @Override
    public GM currentGridModel() {
        return delegate.currentGridModel();
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
     * Executes a single simulation step via the delegate and measures its duration,
     * updating the timing statistics accordingly.
     */
    @Override
    public void executeStep() {
        long startNanos = System.nanoTime();
        delegate.executeStep();
        currentStepNanos = System.nanoTime() - startNanos;

        if (currentStepNanos < minStepNanos) {
            minStepNanos = currentStepNanos;
        }
        if (currentStepNanos > maxStepNanos) {
            maxStepNanos = currentStepNanos;
        }
        sumStepNanos += currentStepNanos;
    }

    /**
     * Returns an immutable record containing timing statistics for simulation steps.
     * <p>
     * The statistics include the minimum, maximum, and most recent step durations,
     * as well as the sum and average of all step durations in nanoseconds.
     * <p>
     * The average is the sum divided by {@link #stepCount()}, which counts all steps of the delegate.
     * Steps executed by the delegate before it was wrapped are counted but not timed,
     * so the average is only exact if this executor is created before the first step.
     * <p>
     * If no steps have been executed yet, all values in the returned record are {@code 0}.
     *
     * @return a {@link StepTimingStatistics} record with the current timing statistics
     */
    public StepTimingStatistics stepTimingStatistics() {
        int steps = stepCount();
        if (steps > 0) {
            return new StepTimingStatistics(
                    currentStepNanos,
                    minStepNanos,
                    maxStepNanos,
                    sumStepNanos,
                    (sumStepNanos / steps)
            );
        }
        return StepTimingStatistics.empty();
    }

    /**
     * Returns the duration of the most recently executed simulation step in nanoseconds.
     *
     * @return the last step duration in nanoseconds, or {@link Long#MIN_VALUE} if no steps have been executed
     */
    public long currentStepNanos() {
        return currentStepNanos;
    }

}
