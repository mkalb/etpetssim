package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;

/**
 * Statistics of the Simulation Lab without metrics; the step count is always {@code 0}.
 *
 * @param gridStructure the grid structure of the Lab
 */
public record LabStatistics(GridStructure gridStructure) implements SimulationStatistics {

    @Override
    public int stepCount() {
        return 0;
    }

}
