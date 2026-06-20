package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.forest.model.*;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class NoUserActionTest {

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
    void testApplyDoesNotMutateStateWithSelectedCell() {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());
        NoUserAction<ForestEntity, WritableGridModel<ForestEntity>, ForestConfig, ForestStatistics, ForestSimulationManager> userAction =
                new NoUserAction<>();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentModel().setEntity(coordinate, ForestEntity.TREE);

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, selectedCell(manager, coordinate));

        assertAll(
                () -> assertSame(ForestEntity.TREE, manager.currentModel().getEntity(coordinate)),
                () -> assertEquals(0, manager.statistics().getTreeCells()),
                () -> assertEquals(0, manager.statistics().getBurningCells())
        );
    }

    @Test
    void testApplyDoesNotMutateStateWithoutSelectedCell() {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());
        NoUserAction<ForestEntity, WritableGridModel<ForestEntity>, ForestConfig, ForestStatistics, ForestSimulationManager> userAction =
                new NoUserAction<>();

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, null);

        assertAll(
                () -> assertTrue(manager.currentModel().nonDefaultCoordinates().isEmpty()),
                () -> assertEquals(manager.statistics().getTotalCells(), manager.statistics().getEmptyCells())
        );
    }

}
