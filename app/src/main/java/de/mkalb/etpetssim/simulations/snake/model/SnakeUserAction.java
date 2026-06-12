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

        var coordinate = selectedCell.coordinate();
        SnakeEntity entity = manager.currentModel().getEntity(coordinate);

        switch (context) {
            case ADD_WALL -> {
                if (entity.isGround()) {
                    manager.currentModel().setEntity(coordinate, TerrainConstant.WALL);
                    manager.statistics().adjustWallCells(1);
                }
            }
            case REMOVE_WALL -> {
                if (entity.isWall()) {
                    manager.currentModel().setEntity(coordinate, TerrainConstant.GROUND);
                    manager.statistics().adjustWallCells(-1);
                }
            }
            case ADD_FOOD -> {
                if (entity.isGround()) {
                    manager.currentModel().setEntity(coordinate, TerrainConstant.GROWTH_FOOD);
                    manager.statistics().adjustFoodCells(1);
                }
            }
            case REMOVE_FOOD -> {
                if (entity.isFood()) {
                    manager.currentModel().setEntity(coordinate, TerrainConstant.GROUND);
                    manager.statistics().adjustFoodCells(-1);
                }
            }
        }
    }

}
