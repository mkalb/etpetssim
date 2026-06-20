package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
                ReboundingUserActionContext.REMOVE_WALL,
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
                ReboundingUserActionContext.REMOVE_REBOUNDER,
                selectedCell(manager, rebounderCell.coordinate()));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(rebounderCell.coordinate()).isGround()),
                () -> assertEquals(movingEntityCellsBefore - 1, manager.statistics().getMovingEntityCells())
        );
    }

}
