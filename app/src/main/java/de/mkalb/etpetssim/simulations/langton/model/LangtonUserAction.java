package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;
import de.mkalb.etpetssim.simulations.langton.shared.*;
import org.jspecify.annotations.Nullable;

public final class LangtonUserAction
        implements SimulationUserAction<
        LangtonEntity,
        LangtonGridModel,
        LangtonConfig,
        LangtonStatistics,
        LangtonSimulationManager,
        LangtonUserActionContext> {

    @Override
    public void apply(LangtonSimulationManager manager,
                      LangtonUserActionContext context,
                      @Nullable GridCellView<LangtonEntity> selectedCell) {
        if (selectedCell == null) {
            // This user action works only if a cell is selected, so do nothing if no cell is selected.
            return;
        }

        var model = manager.currentModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        var antEntity = model.antModel().getEntity(coordinate);
        var terrainEntity = model.groundModel().getEntity(coordinate);
        var config = manager.config();

        if ((terrainEntity == TerrainConstant.UNVISITED) && (antEntity instanceof NoAgent)) {
            var direction = switch (context) {
                case LangtonUserActionContext.AddAnt addAnt -> addAnt.direction();
            };
            if (!isValidInitialDirection(config, coordinate, direction)) {
                return;
            }
            Ant ant = new Ant(direction);
            model.antModel().setEntity(coordinate, ant);
            model.groundModel().setEntity(coordinate, TerrainConstant.COLOR_1);

            statistics.adjustCellCounts(1, 1);
        }
    }

    private boolean isValidInitialDirection(LangtonConfig config,
                                            GridCoordinate coordinate,
                                            CompassDirection direction) {
        return LangtonDirectionOptions.validInitialDirections(config.cellShape(), coordinate).contains(direction);
    }

}
