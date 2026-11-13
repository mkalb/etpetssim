package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.executor.SynchronousStepLogic;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;

/**
 * Synchronous step logic of the Simulation Lab that keeps every cell unchanged.
 */
public final class LabStepLogic implements SynchronousStepLogic<LabEntity, LabStatistics> {

    @Override
    public void performSynchronousStep(ReadableGridModel<LabEntity> currentGridModel,
                                       WritableGridModel<LabEntity> nextGridModel,
                                       int stepIndex,
                                       LabStatistics statistics) {
        var nonDefaultCells = currentGridModel.nonDefaultCells();
        for (var cell : nonDefaultCells) {
            nextGridModel.setEntity(cell.coordinate(), cell.entity());
        }
        // NORMAL is the default entity, so every non-default cell is highlighted.
        statistics.setCellCounts(nonDefaultCells.size());
    }

}
