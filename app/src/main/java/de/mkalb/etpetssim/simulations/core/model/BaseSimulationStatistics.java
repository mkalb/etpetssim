package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;

/**
 * Base class for simulation statistics.
 *
 * <p>Subclasses inherit the standard {@link SimulationStatistics} implementation
 * and call {@link #setStepCount} to advance the shared step count.
 */
public class BaseSimulationStatistics implements SimulationStatistics {

    private final GridStructure gridStructure;

    private int stepCount;

    protected BaseSimulationStatistics(GridStructure gridStructure) {
        this.gridStructure = gridStructure;
    }

    @Override
    public final int stepCount() {
        return stepCount;
    }

    @Override
    public final GridStructure gridStructure() {
        return gridStructure;
    }

    /**
     * Sets the step count to the given value.
     *
     * @param newStepCount the current simulation step count; must be &gt;= 0
     * @throws IllegalArgumentException if {@code newStepCount} is negative
     */
    public final void setStepCount(int newStepCount) {
        if (newStepCount < 0) {
            throw new IllegalArgumentException("newStepCount must be >= 0, but was: " + newStepCount);
        }
        stepCount = newStepCount;
    }

    /**
     * Returns a formatted string of the common statistics fields for use in
     * {@link Object#toString()} implementations of subclasses.
     *
     * @return comma-separated key-value pairs for the shared statistics fields
     */
    protected final String baseToString() {
        return "gridStructure=" + gridStructure +
                ", stepCount=" + stepCount;
    }

}
