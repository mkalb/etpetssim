package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ReboundingUserActionTest {

    private static ReboundingConfig createConfig(int verticalWalls, double rebounderPercent) {
        return new ReboundingConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ReboundingConstraints.GRID_WIDTH_DEFAULT,
                ReboundingConstraints.GRID_HEIGHT_DEFAULT,
                ReboundingConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                verticalWalls,
                rebounderPercent,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT
        );
    }

    private static GridCell<ReboundingEntity> createSelectedCell(ReboundingSimulationManager manager,
                                                                 GridCoordinate coordinate) {
        return new GridCell<>(coordinate, manager.currentGridModel().getEntity(coordinate));
    }

    @Test
    void testApplyAddWallAddsWallToSelectedGroundCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        int wallCellsBefore = manager.statistics().wallCells();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.ADD_WALL,
                createSelectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentGridModel().getEntity(coordinate).isWall()),
                () -> assertEquals(wallCellsBefore + 1, manager.statistics().wallCells())
        );
    }

    @Test
    void testApplyRemoveWallRemovesSelectedWallCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(1, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        var wallCell = manager.currentGridModel()
                .filteredCells(ReboundingEntity::isWall)
                .stream()
                .findFirst()
                .orElseThrow();
        int wallCellsBefore = manager.statistics().wallCells();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.REMOVE_WALL,
                createSelectedCell(manager, wallCell.coordinate()));

        assertAll(
                () -> assertTrue(manager.currentGridModel().getEntity(wallCell.coordinate()).isGround()),
                () -> assertEquals(wallCellsBefore - 1, manager.statistics().wallCells())
        );
    }

    @Test
    void testApplyRemoveRebounderRemovesSelectedRebounderCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.02d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        var rebounderCell = manager.currentGridModel()
                .filteredCells(ReboundingEntity::isRebounder)
                .stream()
                .findFirst()
                .orElseThrow();
        int rebounderCellsBefore = manager.statistics().rebounderCells();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.REMOVE_REBOUNDER,
                createSelectedCell(manager, rebounderCell.coordinate()));

        assertAll(
                () -> assertTrue(manager.currentGridModel().getEntity(rebounderCell.coordinate()).isGround()),
                () -> assertEquals(rebounderCellsBefore - 1, manager.statistics().rebounderCells())
        );
    }

    @Test
    void testApplyAddRebounderAddsSelectedDirectionToGroundCell() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        int rebounderCellsBefore = manager.statistics().rebounderCells();

        userAction.apply(
                manager,
                new ReboundingUserActionContext.AddRebounder(CompassDirection.SE),
                createSelectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentGridModel().getEntity(coordinate).isRebounder()),
                () -> assertEquals(rebounderCellsBefore + 1, manager.statistics().rebounderCells())
        );
    }

    @Test
    void testApplyFillWallsFillsAllGroundCells() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        int totalCells = manager.currentGridModel().allCells().size();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.FILL_WALLS,
                null);

        assertAll(
                () -> assertEquals(totalCells, manager.currentGridModel().countEntities(ReboundingEntity::isWall)),
                () -> assertEquals(totalCells, manager.statistics().wallCells())
        );
    }

    @Test
    void testApplyFillWallsKeepsExistingRebounderCells() {
        ReboundingSimulationManager manager = new ReboundingSimulationManager(createConfig(0, 0.0d));
        ReboundingUserAction userAction = new ReboundingUserAction();
        GridCoordinate rebounderCoordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().setEntity(rebounderCoordinate, new Rebounder(CompassDirection.N));
        manager.statistics().incrementRebounderCells();
        int totalCells = manager.currentGridModel().allCells().size();

        userAction.apply(
                manager,
                ReboundingUserActionContext.FixedAction.FILL_WALLS,
                null);

        assertAll(
                () -> assertTrue(manager.currentGridModel().getEntity(rebounderCoordinate).isRebounder()),
                () -> assertEquals(totalCells - 1L, manager.currentGridModel().countEntities(ReboundingEntity::isWall)),
                () -> assertEquals(totalCells - 1, manager.statistics().wallCells()),
                () -> assertEquals(1, manager.statistics().rebounderCells())
        );
    }

}
