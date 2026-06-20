package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import org.jspecify.annotations.Nullable;

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
        var model = manager.currentModel();
        var statistics = manager.statistics();

        switch (context) {
            case ReboundingUserActionContext.FixedAction fixedAction -> {
                switch (fixedAction) {
                    case ADD_WALL -> {
                        if (selectedCell == null) {
                            return;
                        }
                        var coordinate = selectedCell.coordinate();
                        var entity = model.getEntity(coordinate);
                        if (entity.isGround()) {
                            model.setEntity(coordinate, TerrainConstant.WALL);
                            statistics.increaseWallCells();
                        }
                    }
                    case FILL_WALLS -> {
                        var groundCoordinates = model.filteredCoordinates(ReboundingEntity::isGround);
                        if (groundCoordinates.isEmpty()) {
                            return;
                        }
                        for (var coordinate : groundCoordinates) {
                            model.setEntity(coordinate, TerrainConstant.WALL);
                            statistics.increaseWallCells();
                        }
                    }
                    case REMOVE_WALL -> {
                        if (selectedCell == null) {
                            return;
                        }
                        var coordinate = selectedCell.coordinate();
                        var entity = model.getEntity(coordinate);
                        if (entity.isWall()) {
                            model.setEntityToDefault(coordinate);
                            statistics.decreaseWallCells();
                        }
                    }
                    case REMOVE_REBOUNDER -> {
                        if (selectedCell == null) {
                            return;
                        }
                        var coordinate = selectedCell.coordinate();
                        var entity = model.getEntity(coordinate);
                        if (entity.isRebounder()) {
                            model.setEntityToDefault(coordinate);
                            statistics.decreaseMovingEntityCells();
                        }
                    }
                }
            }
            case ReboundingUserActionContext.AddRebounder addRebounder -> {
                if (selectedCell == null) {
                    return;
                }
                var coordinate = selectedCell.coordinate();
                var entity = model.getEntity(coordinate);
                if (entity.isGround()) {
                    model.setEntity(coordinate, new Rebounder(addRebounder.direction()));
                    statistics.increaseMovingEntityCells();
                }
            }
        }
    }

}
