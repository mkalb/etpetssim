package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeUserActionContext;
import org.jspecify.annotations.Nullable;

public final class SnakeUserAction
        implements SimulationUserAction<
        SnakeEntity,
        WritableGridModel<SnakeEntity>,
        SnakeConfig,
        SnakeStatistics,
        SnakeSimulationManager,
        SnakeUserActionContext> {

    public SnakeUserAction() {
    }

    @Override
    public void apply(SnakeSimulationManager manager,
                      SnakeUserActionContext context,
                      @Nullable GridCellView<SnakeEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var model = manager.currentModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        var entity = model.getEntity(coordinate);

        switch (context) {
            case ADD_WALL -> {
                if (entity.isGround()) {
                    model.setEntity(coordinate, TerrainConstant.WALL);
                    statistics.adjustWallCells(1);
                }
            }
            case REMOVE_WALL -> {
                if (entity.isWall()) {
                    model.setEntity(coordinate, TerrainConstant.GROUND);
                    statistics.adjustWallCells(-1);
                }
            }
            case ADD_FOOD -> {
                if (entity.isGround()) {
                    model.setEntity(coordinate, TerrainConstant.GROWTH_FOOD);
                    statistics.adjustFoodCells(1);
                }
            }
            case REMOVE_FOOD -> {
                if (entity.isFood()) {
                    model.setEntity(coordinate, TerrainConstant.GROUND);
                    statistics.adjustFoodCells(-1);
                }
            }
        }
    }

}
