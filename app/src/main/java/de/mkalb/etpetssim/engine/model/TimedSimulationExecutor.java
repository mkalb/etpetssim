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

    private long minStepMillis = Long.MAX_VALUE;
    private long maxStepMillis = Long.MIN_VALUE;
    private long currentStepMillis = Long.MIN_VALUE;

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
    public boolean isRunning() {
        return delegate.isRunning();
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
    }

    @Override
    public void executeSteps(int count) {
        for (int i = 0; (i < count) && isRunning(); i++) {
            executeStep();
        }
    }

    /**
     * Returns the minimum execution time (ms) of a simulation step so far.
     *
     * @return the minimum step duration in milliseconds, or {@link Long#MAX_VALUE} if no steps have been executed
     */
    public long minStepMillis() {
        return minStepMillis;
    }

    /**
     * Returns the maximum execution time (ms) of a simulation step so far.
     *
     * @return the maximum step duration in milliseconds, or {@link Long#MIN_VALUE} if no steps have been executed
     */
    public long maxStepMillis() {
        return maxStepMillis;
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