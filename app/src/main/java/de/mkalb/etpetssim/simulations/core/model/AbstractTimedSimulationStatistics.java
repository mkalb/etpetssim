package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;

/**
 * Shared base for statistics that also track step timing metrics.
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractTimedSimulationStatistics
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
    protected AbstractTimedSimulationStatistics(GridStructure gridStructure) {
        int totalCells = gridStructure.cellCount();
        if (totalCells < 0) {
            throw new IllegalArgumentException("totalCells must be >= 0");
        }
        this.gridStructure = gridStructure;
        stepCount = 0;
        stepTimingStatistics = StepTimingStatistics.empty();
    }

    @Override
    public final int getStepCount() {
        return stepCount;
    }

    @Override
    public final GridStructure getGridStructure() {
        return gridStructure;
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
