package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import org.jspecify.annotations.Nullable;

/**
 * User action of the Simulation Lab that switches a cell between normal and highlighted.
 */
public final class LabUserAction
        implements SimulationUserAction<
        LabEntity,
        WritableGridModel<LabEntity>,
        LabConfig,
        LabStatistics,
        LabSimulationManager,
        NoUserActionContext> {

    @Override
    public void apply(LabSimulationManager manager,
                      NoUserActionContext context,
                      @Nullable GridCellView<LabEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var model = manager.currentGridModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();

        switch (model.getEntity(coordinate)) {
            case LabEntity.NORMAL -> {
                model.setEntity(coordinate, LabEntity.HIGHLIGHTED);
                statistics.setCellCounts(statistics.highlightedCells() + 1);
            }
            case LabEntity.HIGHLIGHTED -> {
                model.setEntity(coordinate, LabEntity.NORMAL);
                statistics.setCellCounts(statistics.highlightedCells() - 1);
            }
        }
    }

}
