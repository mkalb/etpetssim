package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;
import org.jspecify.annotations.Nullable;

public class LangtonUserAction
        implements SimulationUserAction<
        LangtonEntity,
        LangtonGridModel,
        LangtonConfig,
        LangtonStatistics,
        NoUserActionContext> {

    @Override
    public void apply(LangtonGridModel model, LangtonStatistics statistics, LangtonConfig config, NoUserActionContext context, @Nullable GridCell<LangtonEntity> selectedCell) {
        if (selectedCell == null) {
            // This user action works only if a cell is selected, so do nothing if no cell is selected.
            return;
        }

        var coordinate = selectedCell.coordinate();

        AntEntity antEntity = model.antModel().getEntity(coordinate);
        TerrainConstant terrain = model.groundModel().getEntity(coordinate);

        if ((terrain == TerrainConstant.UNVISITED) && (antEntity instanceof NoAgent)) {
            var direction = switch (config.cellShape()) {
                case SQUARE, HEXAGON -> CompassDirection.N;
                case TRIANGLE -> coordinate.isTriangleCellPointingDown() ? CompassDirection.N : CompassDirection.S;
            };
            Ant ant = new Ant(direction);
            model.antModel().setEntity(coordinate, ant);
            model.groundModel().setEntity(coordinate, TerrainConstant.COLOR_1);

            statistics.updateCells(1, 1);
        }
    }

}
