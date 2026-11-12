package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import org.jspecify.annotations.Nullable;

/**
 * User action of Rebounding Entities that adds or removes walls and rebounders.
 */
public final class ReboundingUserAction
        implements SimulationUserAction<
        ReboundingEntity,
        WritableGridModel<ReboundingEntity>,
        ReboundingConfig,
        ReboundingStatistics,
        ReboundingSimulationManager,
        ReboundingUserActionContext> {

    @Override
    public void apply(ReboundingSimulationManager manager,
                      ReboundingUserActionContext context,
                      @Nullable GridCellView<ReboundingEntity> selectedCell) {
        var model = manager.currentGridModel();
        var statistics = manager.statistics();

        if (context == ReboundingUserActionContext.FixedAction.FILL_WALLS) {
            for (var coordinate : model.filteredCoordinates(ReboundingEntity::isGround)) {
                model.setEntity(coordinate, TerrainConstant.WALL);
                statistics.incrementWallCells();
            }
            return;
        }
        if (selectedCell == null) {
            return;
        }

        var coordinate = selectedCell.coordinate();
        var entity = model.getEntity(coordinate);
        switch (context) {
            case ReboundingUserActionContext.FixedAction fixedAction -> {
                switch (fixedAction) {
                    case ADD_WALL -> {
                        if (entity.isGround()) {
                            model.setEntity(coordinate, TerrainConstant.WALL);
                            statistics.incrementWallCells();
                        }
                    }
                    case REMOVE_WALL -> {
                        if (entity.isWall()) {
                            model.setEntityToDefault(coordinate);
                            statistics.decrementWallCells();
                        }
                    }
                    case REMOVE_REBOUNDER -> {
                        if (entity.isRebounder()) {
                            model.setEntityToDefault(coordinate);
                            statistics.decrementRebounderCells();
                        }
                    }
                }
            }
            case ReboundingUserActionContext.AddRebounder addRebounder -> {
                if (entity.isGround()) {
                    model.setEntity(coordinate, new Rebounder(addRebounder.direction()));
                    statistics.incrementRebounderCells();
                }
            }
        }
    }

}
