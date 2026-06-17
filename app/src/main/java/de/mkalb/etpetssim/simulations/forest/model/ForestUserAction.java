package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import org.jspecify.annotations.Nullable;

public final class ForestUserAction
        implements SimulationUserAction<
        ForestEntity,
        WritableGridModel<ForestEntity>,
        ForestConfig,
        ForestStatistics,
        ForestSimulationManager,
        NoUserActionContext> {

    public ForestUserAction() {
    }

    @Override
    public void apply(ForestSimulationManager manager,
                      NoUserActionContext context,
                      @Nullable GridCellView<ForestEntity> selectedCell) {
        if (selectedCell == null) {
            // This user action works only if a cell is selected, so do nothing if no cell is selected.
            return;
        }

        var model = manager.currentModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        var entity = model.getEntity(coordinate);

        ForestEntity newEntity = switch (entity) {
            case ForestEntity.EMPTY -> ForestEntity.TREE;
            case ForestEntity.TREE -> ForestEntity.BURNING;
            case ForestEntity.BURNING -> ForestEntity.EMPTY;
        };

        model.setEntity(coordinate, newEntity);

        int treeDelta = switch (entity) {
            case ForestEntity.EMPTY -> 1;
            case ForestEntity.TREE -> -1;
            case ForestEntity.BURNING -> 0;
        };
        int burningDelta = switch (entity) {
            case ForestEntity.EMPTY -> 0;
            case ForestEntity.TREE -> 1;
            case ForestEntity.BURNING -> -1;
        };
        statistics.updateCellCounts(
                statistics.getTreeCells() + treeDelta,
                statistics.getBurningCells() + burningDelta
        );
    }

}
