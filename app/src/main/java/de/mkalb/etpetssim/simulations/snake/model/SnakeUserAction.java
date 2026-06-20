package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeUserActionContext;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeUserAction
        implements SimulationUserAction<
        SnakeEntity,
        WritableGridModel<SnakeEntity>,
        SnakeConfig,
        SnakeStatistics,
        SnakeSimulationManager,
        SnakeUserActionContext> {

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
            case SnakeUserActionContext.FixedAction fixedAction ->
                    applyFixedAction(model, statistics, coordinate, entity, fixedAction);
            case SnakeUserActionContext.AddSnake addSnake -> {
                if (entity.isGround()) {
                    int nextSnakeId = manager.nextSnakeId();
                    int stepIndexOfSpawn = manager.stepCount() - 1;
                    model.setEntity(coordinate,
                            new SnakeHead(nextSnakeId, addSnake.strategy(), manager.config().initialPendingGrowth(), stepIndexOfSpawn));
                    manager.incrementNextSnakeId();
                    statistics.increaseSnakeHeadCells();
                    statistics.increaseLivingSnakeHeadCells();
                }
            }
        }
    }

    private void applyFixedAction(WritableGridModel<SnakeEntity> model,
                                  SnakeStatistics statistics,
                                  GridCoordinate coordinate,
                                  SnakeEntity entity,
                                  SnakeUserActionContext.FixedAction fixedAction) {
        switch (fixedAction) {
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
            case REMOVE_SNAKE -> {
                if (entity instanceof SnakeHead head) {
                    removeSnake(model, statistics, coordinate, head);
                } else if (entity == TerrainConstant.SNAKE_SEGMENT) {
                    findSnakeHeadCoordinateBySegment(model, coordinate)
                            .ifPresent(headCoordinate -> {
                                SnakeEntity headEntity = model.getEntity(headCoordinate);
                                if (headEntity instanceof SnakeHead head) {
                                    removeSnake(model, statistics, headCoordinate, head);
                                }
                            });
                }
            }
        }
    }

    private Optional<GridCoordinate> findSnakeHeadCoordinateBySegment(ReadableGridModel<SnakeEntity> model,
                                                                      GridCoordinate segmentCoordinate) {
        return model.filteredCells(e -> e instanceof SnakeHead)
                    .stream()
                    .filter(cell -> (cell.entity() instanceof SnakeHead head)
                            && head.currentSegments().contains(segmentCoordinate))
                    .map(GridCell::coordinate)
                    .findFirst();
    }

    private void removeSnake(WritableGridModel<SnakeEntity> model,
                             SnakeStatistics statistics,
                             GridCoordinate headCoordinate,
                             SnakeHead head) {
        model.setEntityToDefault(headCoordinate);
        head.currentSegments().forEach(model::setEntityToDefault);
        statistics.decreaseSnakeHeadCells();
        if (!head.isDead()) {
            statistics.decreaseLivingSnakeHeadCells();
        }
    }

}
