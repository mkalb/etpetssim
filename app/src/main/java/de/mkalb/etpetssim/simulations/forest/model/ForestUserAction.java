package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import org.jspecify.annotations.Nullable;

/**
 * User action of the Forest-fire model that cycles a cell from empty to tree to burning and back to empty.
 */
public final class ForestUserAction
        implements SimulationUserAction<
        ForestEntity,
        WritableGridModel<ForestEntity>,
        ForestConfig,
        ForestStatistics,
        ForestSimulationManager,
        NoUserActionContext> {

    @Override
    public void apply(ForestSimulationManager manager,
                      NoUserActionContext context,
                      @Nullable GridCellView<ForestEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var model = manager.currentGridModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        var entity = model.getEntity(coordinate);

        ForestEntity newEntity = switch (entity) {
            case ForestEntity.EMPTY -> ForestEntity.TREE;
            case ForestEntity.TREE -> ForestEntity.BURNING;
            case ForestEntity.BURNING -> ForestEntity.EMPTY;
        };

        model.setEntity(coordinate, newEntity);

        int treeCells = statistics.treeCells();
        int burningCells = statistics.burningCells();
        switch (entity) {
            case ForestEntity.EMPTY -> statistics.setCellCounts(treeCells + 1, burningCells);
            case ForestEntity.TREE -> statistics.setCellCounts(treeCells - 1, burningCells + 1);
            case ForestEntity.BURNING -> statistics.setCellCounts(treeCells, burningCells - 1);
        }
    }

}
