package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;

/**
 * Shared base for statistics that also track step timing metrics.
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractTimedSimulationStatistics
        implements TimedSimulationStatistics {

    private final int totalCells;

    private int stepCount;
    private StepTimingStatistics stepTimingStatistics;

    /**
     * Initializes the statistics with the given total cell count.
     * Step count and timing statistics are set to their initial values.
     *
     * @param totalCells total number of cells in the simulation grid
     */
    protected AbstractTimedSimulationStatistics(int totalCells) {
        this.totalCells = totalCells;
        stepCount = 0;
        stepTimingStatistics = StepTimingStatistics.empty();
    }

    @Override
    public final int getStepCount() {
        return stepCount;
    }

    @Override
    public final int getTotalCells() {
        return totalCells;
    }

    @Override
    public final StepTimingStatistics stepTimingStatistics() {
        return stepTimingStatistics;
    }

    /**
     * Updates the shared statistics fields.
     * Subclasses must call this method from their own update logic
     * to keep the common counters in sync.
     *
     * @param newStepCount            the current simulation step count
     * @param newStepTimingStatistics the current step timing statistics
     */
    protected final void updateCommon(int newStepCount,
                                      StepTimingStatistics newStepTimingStatistics) {
        stepCount = newStepCount;
        stepTimingStatistics = newStepTimingStatistics;
    }

    /**
     * Returns a formatted string of the common statistics fields for use in
     * {@link Object#toString()} implementations of subclasses.
     *
     * @return comma-separated key-value pairs for the shared statistics fields
     */
    protected final String baseToString() {
        return "totalCells=" + totalCells +
                ", stepCount=" + stepCount +
                ", stepTimingStatistics=" + stepTimingStatistics;
    }

}
