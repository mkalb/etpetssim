package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;

/**
 * Extension of {@link SimulationStatistics} that adds timing-related statistics.
 */
public interface TimedSimulationStatistics extends SimulationStatistics {

    /**
     * Returns timing statistics for simulation steps.
     *
     * @return a {@link de.mkalb.etpetssim.engine.model.StepTimingStatistics} record with timing statistics
     */
    StepTimingStatistics stepTimingStatistics();

}