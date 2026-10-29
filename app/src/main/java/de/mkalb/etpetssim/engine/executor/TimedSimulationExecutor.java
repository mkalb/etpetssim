package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Decorator for {@link SimulationExecutor} that measures the execution time of each simulation step.
 * <p>
 * Tracks the most recent, minimum, maximum, sum, and average step durations in nanoseconds; they are accessible via
 * {@link #stepTimingStatistics()}.
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
    private long sumStepNanos;

    /**
     * Creates a timed executor for the given delegate.
     * <p>
     * The delegate must not have executed any step yet, because the average divides the timed durations by the step
     * count of the delegate (see {@link #stepTimingStatistics()}).
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
     * <p>
     * If no steps have been executed yet, all values in the returned record are {@code 0}.
     *
     * @return a {@link StepTimingStatistics} record with the current timing statistics
     * @throws IllegalArgumentException if the delegate executed steps before it was wrapped and these counted but
     *                                  untimed steps make the values inconsistent
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

}
