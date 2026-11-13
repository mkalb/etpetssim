package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;

/**
 * Base class for simulation statistics that track step timing metrics.
 *
 * <p>Subclasses inherit the standard {@link SimulationStatistics} implementation
 * and call {@link #setStepData} to advance the shared counters.
 */
public class BaseTimedSimulationStatistics implements SimulationStatistics {

    private final GridStructure gridStructure;

    private int stepCount;
    private StepTimingStatistics stepTimingStatistics;

    protected BaseTimedSimulationStatistics(GridStructure gridStructure) {
        this.gridStructure = gridStructure;
        stepTimingStatistics = StepTimingStatistics.empty();
    }

    @Override
    public final int stepCount() {
        return stepCount;
    }

    @Override
    public final GridStructure gridStructure() {
        return gridStructure;
    }

    @Override
    public final StepTimingStatistics stepTimingStatistics() {
        return stepTimingStatistics;
    }

    /**
     * Sets the step count and timing statistics to the given values.
     *
     * @param newStepCount            the current simulation step count; must be &gt;= 0
     * @param newStepTimingStatistics the current step timing statistics
     * @throws IllegalArgumentException if {@code newStepCount} is negative
     */
    public final void setStepData(int newStepCount,
                                  StepTimingStatistics newStepTimingStatistics) {
        if (newStepCount < 0) {
            throw new IllegalArgumentException("newStepCount must be >= 0, but was: " + newStepCount);
        }
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
        return "gridStructure=" + gridStructure +
                ", stepCount=" + stepCount +
                ", stepTimingStatistics=" + stepTimingStatistics;
    }

}
