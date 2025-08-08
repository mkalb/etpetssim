package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;

/**
 * Extension of {@link SimulationStatistics} that adds timing-related statistics.
 */
public interface TimedSimulationStatistics extends SimulationStatistics {

    /**
     * Returns the configured timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    long timeOutMillis();

    /**
     * Returns timing statistics for simulation steps.
     *
     * @return a {@link de.mkalb.etpetssim.engine.model.StepTimingStatistics} record with timing statistics
     */
    StepTimingStatistics stepTimingStatistics();

}