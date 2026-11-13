package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;

/**
 * Termination condition of the Simulation Lab that is always fulfilled.
 *
 * <p>With the termination check of the control panel active, the simulation pauses after every step.
 */
public final class LabTerminationCondition
        implements SimulationTerminationCondition<LabEntity, ReadableGridModel<LabEntity>, LabStatistics> {

    @Override
    public boolean isFinished(ReadableGridModel<LabEntity> model, int stepCount, LabStatistics statistics) {
        return true;
    }

}
