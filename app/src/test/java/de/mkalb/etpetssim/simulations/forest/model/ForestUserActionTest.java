package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ForestUserActionTest {

    private static ForestConfig createConfig() {
        return new ForestConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                ForestConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ForestConstraints.GRID_WIDTH_DEFAULT,
                ForestConstraints.GRID_HEIGHT_DEFAULT,
                ForestConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                0.0d,
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ForestConstraints.TREE_GROWTH_PROBABILITY_DEFAULT,
                ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_DEFAULT
        );
    }

    private static GridCell<ForestEntity> selectedCell(ForestSimulationManager manager, GridCoordinate coordinate) {
        return new GridCell<>(coordinate, manager.currentModel().getEntity(coordinate));
    }

    @Test
    void testApplyIgnoresMissingSelection() {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());
        ForestUserAction userAction = new ForestUserAction();

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, null);

        assertAll(
                () -> assertEquals(manager.statistics().getTotalCells(), manager.statistics().getEmptyCells()),
                () -> assertEquals(0, manager.statistics().getTreeCells()),
                () -> assertEquals(0, manager.statistics().getBurningCells())
        );
    }

    @Test
    void testApplyTurnsEmptyCellIntoTree() {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());
        ForestUserAction userAction = new ForestUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, selectedCell(manager, coordinate));

        assertAll(
                () -> assertSame(ForestEntity.TREE, manager.currentModel().getEntity(coordinate)),
                () -> assertEquals(manager.statistics().getTotalCells() - 1, manager.statistics().getEmptyCells()),
                () -> assertEquals(1, manager.statistics().getTreeCells()),
                () -> assertEquals(0, manager.statistics().getBurningCells())
        );
    }

    @Test
    void testApplyTurnsTreeCellIntoBurningCell() {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());
        ForestUserAction userAction = new ForestUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentModel().setEntity(coordinate, ForestEntity.TREE);
        manager.statistics().updateCellCounts(1, 0);

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, selectedCell(manager, coordinate));

        assertAll(
                () -> assertSame(ForestEntity.BURNING, manager.currentModel().getEntity(coordinate)),
                () -> assertEquals(manager.statistics().getTotalCells() - 1, manager.statistics().getEmptyCells()),
                () -> assertEquals(0, manager.statistics().getTreeCells()),
                () -> assertEquals(1, manager.statistics().getBurningCells())
        );
    }

    @Test
    void testApplyTurnsBurningCellIntoEmptyCell() {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());
        ForestUserAction userAction = new ForestUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentModel().setEntity(coordinate, ForestEntity.BURNING);
        manager.statistics().updateCellCounts(0, 1);

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, selectedCell(manager, coordinate));

        assertAll(
                () -> assertSame(ForestEntity.EMPTY, manager.currentModel().getEntity(coordinate)),
                () -> assertEquals(manager.statistics().getTotalCells(), manager.statistics().getEmptyCells()),
                () -> assertEquals(0, manager.statistics().getTreeCells()),
                () -> assertEquals(0, manager.statistics().getBurningCells())
        );
    }

}
