package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final class ForestUserActionTest {

    private static final GridCoordinate COORDINATE = new GridCoordinate(0, 0);

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

    private static ForestSimulationManager createManagerWithEntity(ForestEntity entity) {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());
        manager.currentGridModel().setEntity(COORDINATE, entity);
        manager.statistics().setCellCounts(entity.isTree() ? 1 : 0, entity.isBurning() ? 1 : 0);
        return manager;
    }

    private static void assertCellCounts(int treeCells, int burningCells, ForestStatistics statistics) {
        assertAll(
                () -> assertEquals(statistics.totalCells() - treeCells - burningCells, statistics.emptyCells()),
                () -> assertEquals(treeCells, statistics.treeCells()),
                () -> assertEquals(burningCells, statistics.burningCells())
        );
    }

    @ParameterizedTest
    @CsvSource({
            "EMPTY,   TREE,    1, 0",
            "TREE,    BURNING, 0, 1",
            "BURNING, EMPTY,   0, 0"
    })
    void testApply(ForestEntity entity, ForestEntity expectedEntity, int expectedTreeCells,
                   int expectedBurningCells) {
        ForestSimulationManager manager = createManagerWithEntity(entity);

        new ForestUserAction().apply(manager, NoUserActionContext.NO_CONTEXT, new GridCell<>(COORDINATE, entity));

        assertAll(
                () -> assertSame(expectedEntity, manager.currentGridModel().getEntity(COORDINATE)),
                () -> assertCellCounts(expectedTreeCells, expectedBurningCells, manager.statistics())
        );
    }

    @Test
    void testApplyIgnoresMissingSelection() {
        ForestSimulationManager manager = new ForestSimulationManager(createConfig());

        new ForestUserAction().apply(manager, NoUserActionContext.NO_CONTEXT, null);

        assertCellCounts(0, 0, manager.statistics());
    }

}
