package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class ReboundingUserActionTest {

    private static ReboundingConfig createConfig(int verticalWalls, double movingEntityPercent) {
        return new ReboundingConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ReboundingConstraints.GRID_WIDTH_DEFAULT,
                ReboundingConstraints.GRID_HEIGHT_DEFAULT,
                ReboundingConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                verticalWalls,
                movingEntityPercent,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT
        );
    }

    private static GridCell<ReboundingEntity> selectedCell(ReboundingSimulationManager manager, GridCoordinate coordinate) {
        return new GridCell<>(coordinate, manager.currentModel().getEntity(coordinate));
    }

    @Test
    void testAddWallAddsWallToSelectedGroundCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        int wallCellsBefore = manager.statistics().getWallCells();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.ADD_WALL,
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isWall()),
                () -> assertEquals(wallCellsBefore + 1, manager.statistics().getWallCells())
        );
    }

    @Test
    void testRemoveWallRemovesSelectedWallCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(1, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        var wallCell = manager.currentModel()
                              .filteredCells(ReboundingEntity::isWall)
                              .stream()
                              .findFirst()
                              .orElseThrow();
        int wallCellsBefore = manager.statistics().getWallCells();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.REMOVE_WALL,
                selectedCell(manager, wallCell.coordinate()));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(wallCell.coordinate()).isGround()),
                () -> assertEquals(wallCellsBefore - 1, manager.statistics().getWallCells())
        );
    }

    @Test
    void testRemoveRebounderRemovesSelectedRebounderCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.02d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        var rebounderCell = manager.currentModel()
                                   .filteredCells(ReboundingEntity::isRebounder)
                                   .stream()
                                   .findFirst()
                                   .orElseThrow();
        int movingEntityCellsBefore = manager.statistics().getMovingEntityCells();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.REMOVE_REBOUNDER,
                selectedCell(manager, rebounderCell.coordinate()));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(rebounderCell.coordinate()).isGround()),
                () -> assertEquals(movingEntityCellsBefore - 1, manager.statistics().getMovingEntityCells())
        );
    }

    @Test
    void testAddRebounderAddsSelectedDirectionToGroundCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        int movingEntityCellsBefore = manager.statistics().getMovingEntityCells();

        userAction.apply(
                manager,
                new ReboundingUserActionContext.AddRebounder(CompassDirection.SE),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isRebounder()),
                () -> assertEquals(movingEntityCellsBefore + 1, manager.statistics().getMovingEntityCells())
        );
    }

    @Test
    void testFillWallsFillsAllGroundCells() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        int totalCells = manager.currentModel().allCells().size();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.FILL_WALLS,
                null);

        assertAll(
                () -> assertEquals(totalCells, manager.currentModel().countEntities(ReboundingEntity::isWall)),
                () -> assertEquals(totalCells, manager.statistics().getWallCells())
        );
    }

    @Test
    void testFillWallsKeepsExistingRebounderCells() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        GridCoordinate rebounderCoordinate = new GridCoordinate(0, 0);
        manager.currentModel().setEntity(rebounderCoordinate, new Rebounder(CompassDirection.N));
        manager.statistics().increaseMovingEntityCells();
        int totalCells = manager.currentModel().allCells().size();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.FILL_WALLS,
                null);

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(rebounderCoordinate).isRebounder()),
                () -> assertEquals(totalCells - 1L, manager.currentModel().countEntities(ReboundingEntity::isWall)),
                () -> assertEquals(totalCells - 1, manager.statistics().getWallCells()),
                () -> assertEquals(1, manager.statistics().getMovingEntityCells())
        );
    }

}
