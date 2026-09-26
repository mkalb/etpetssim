package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;

/**
 * Base class for simulation statistics that track step timing metrics.
 *
 * <p>Subclasses inherit the standard {@link TimedSimulationStatistics} implementation
 * and call {@link #update} to advance the shared counters. This class is {@code abstract}
 * to signal that it is intended to be extended rather than instantiated directly, even
 * though it declares no abstract methods.
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class BaseTimedSimulationStatistics
        implements TimedSimulationStatistics {

    private final GridStructure gridStructure;

    private int stepCount;
    private StepTimingStatistics stepTimingStatistics;

    /**
     * Initializes the statistics with the given grid structure.
     * Step count and timing statistics are set to their initial values.
     *
     * @param gridStructure grid structure used by the simulation
     */
    protected BaseTimedSimulationStatistics(GridStructure gridStructure) {
        int totalCells = gridStructure.cellCount();
        if (totalCells < 0) {
            throw new IllegalArgumentException("totalCells must be >= 0");
        }
        this.gridStructure = gridStructure;
        stepCount = 0;
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
     * Updates the step count and timing statistics to the given values.
     *
     * @param newStepCount            the current simulation step count; must be &gt;= 0
     * @param newStepTimingStatistics the current step timing statistics
     * @throws IllegalArgumentException if {@code newStepCount} is negative
     */
    public final void update(int newStepCount,
                             StepTimingStatistics newStepTimingStatistics) {
        if (newStepCount < 0) {
            throw new IllegalArgumentException("newStepCount must be >= 0");
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
