package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import de.mkalb.etpetssim.simulations.snake.shared.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SnakeUserActionTest {

    private static SnakeConfig createConfig() {
        return new SnakeConfig(
                SnakeConstraints.CELL_SHAPE_DEFAULT,
                SnakeConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SnakeConstraints.GRID_WIDTH_DEFAULT,
                SnakeConstraints.GRID_HEIGHT_DEFAULT,
                SnakeConstraints.CELL_EDGE_LENGTH_DEFAULT,
                SnakeConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                0,
                0,
                0,
                SnakeConstraints.INITIAL_PENDING_GROWTH_DEFAULT,
                SnakeConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                SnakeDeathMode.PERMADEATH,
                SnakeConstraints.GROWTH_PER_FOOD_DEFAULT,
                SnakeConstraints.BASE_POINTS_PER_FOOD_DEFAULT,
                SnakeConstraints.SEGMENT_LENGTH_MULTIPLIER_DEFAULT
        );
    }

    private static GridCell<SnakeEntity> selectedCell(SnakeSimulationManager manager, GridCoordinate coordinate) {
        return new GridCell<>(coordinate, manager.currentModel().getEntity(coordinate));
    }

    @Test
    void testRemoveSnakeRemovesHeadAndSegmentsWhenSegmentIsSelected() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createConfig());
        SnakeUserAction userAction = new SnakeUserAction();
        GridCoordinate segmentCoordinate = new GridCoordinate(2, 2);
        GridCoordinate headCoordinate = new GridCoordinate(3, 2);

        SnakeHead head = new SnakeHead(
                42,
                SnakeMoveStrategies.strategiesForConfig().getFirst(),
                1,
                0);
        head.move(segmentCoordinate, CompassDirection.E, 0, 0);

        manager.currentModel().setEntity(headCoordinate, head);
        manager.currentModel().setEntity(segmentCoordinate, TerrainConstant.SNAKE_SEGMENT);
        manager.statistics().increaseSnakeHeadCells();
        manager.statistics().increaseLivingSnakeHeadCells();

        userAction.apply(
                manager,
                SnakeUserActionContext.FixedAction.REMOVE_SNAKE,
                selectedCell(manager, segmentCoordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(headCoordinate).isGround()),
                () -> assertTrue(manager.currentModel().getEntity(segmentCoordinate).isGround()),
                () -> assertEquals(0, manager.statistics().getSnakeHeadCells()),
                () -> assertEquals(0, manager.statistics().getLivingSnakeHeadCells())
        );
    }

}
