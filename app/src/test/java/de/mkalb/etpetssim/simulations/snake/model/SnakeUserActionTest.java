package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import de.mkalb.etpetssim.simulations.snake.shared.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
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
    void testApplyIgnoresMissingSelection() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createConfig());
        SnakeUserAction userAction = new SnakeUserAction();

        userAction.apply(manager, SnakeUserActionContext.FixedAction.ADD_WALL, null);

        assertAll(
                () -> assertEquals(0, manager.statistics().getWallCells()),
                () -> assertTrue(manager.currentModel().nonDefaultCoordinates().isEmpty())
        );
    }

    @Test
    void testAddWallAddsWallToSelectedGroundCell() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createConfig());
        SnakeUserAction userAction = new SnakeUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(manager, SnakeUserActionContext.FixedAction.ADD_WALL, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isWall()),
                () -> assertEquals(1, manager.statistics().getWallCells())
        );
    }

    @Test
    void testRemoveWallRemovesSelectedWallCell() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createConfig());
        SnakeUserAction userAction = new SnakeUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentModel().setEntity(coordinate, TerrainConstant.WALL);
        manager.statistics().adjustWallCells(1);

        userAction.apply(manager, SnakeUserActionContext.FixedAction.REMOVE_WALL, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isGround()),
                () -> assertEquals(0, manager.statistics().getWallCells())
        );
    }

    @Test
    void testAddFoodAddsFoodToSelectedGroundCell() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createConfig());
        SnakeUserAction userAction = new SnakeUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(manager, SnakeUserActionContext.FixedAction.ADD_FOOD, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isFood()),
                () -> assertEquals(1, manager.statistics().getFoodCells())
        );
    }

    @Test
    void testRemoveFoodRemovesSelectedFoodCell() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createConfig());
        SnakeUserAction userAction = new SnakeUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentModel().setEntity(coordinate, TerrainConstant.GROWTH_FOOD);
        manager.statistics().adjustFoodCells(1);

        userAction.apply(manager, SnakeUserActionContext.FixedAction.REMOVE_FOOD, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isGround()),
                () -> assertEquals(0, manager.statistics().getFoodCells())
        );
    }

    @Test
    void testAddSnakeAddsSnakeHeadToSelectedGroundCell() {
        SnakeSimulationManager manager = new SnakeSimulationManager(createConfig());
        SnakeUserAction userAction = new SnakeUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(
                manager,
                new SnakeUserActionContext.AddSnake(SnakeMoveStrategies.MOMENTUM),
                selectedCell(manager, coordinate));

        SnakeEntity entity = manager.currentModel().getEntity(coordinate);
        assertAll(
                () -> assertInstanceOf(SnakeHead.class, entity),
                () -> assertEquals(1, manager.statistics().getSnakeHeadCells()),
                () -> assertEquals(1, manager.statistics().getLivingSnakeHeadCells())
        );
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
