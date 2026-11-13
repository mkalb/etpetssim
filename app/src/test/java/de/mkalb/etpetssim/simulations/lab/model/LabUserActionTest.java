package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final class LabUserActionTest {

    private static LabConfig createConfig() {
        return new LabConfig(
                LabConstraints.CELL_SHAPE_DEFAULT,
                LabConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LabConstraints.GRID_WIDTH_DEFAULT,
                LabConstraints.GRID_HEIGHT_DEFAULT,
                LabConstraints.CELL_EDGE_LENGTH_DEFAULT,
                LabConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LabConstraints.COLOR_MODE_DEFAULT,
                1L,
                0.0d,
                LabConstraints.NEIGHBORHOOD_MODE_DEFAULT
        );
    }

    private static GridCell<LabEntity> createSelectedCell(LabSimulationManager manager, GridCoordinate coordinate) {
        return new GridCell<>(coordinate, manager.currentGridModel().getEntity(coordinate));
    }

    @Test
    void testApplyIgnoresMissingSelection() {
        LabSimulationManager manager = new LabSimulationManager(createConfig());
        LabUserAction userAction = new LabUserAction();

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, null);

        assertAll(
                () -> assertEquals(0, manager.statistics().highlightedCells()),
                () -> assertEquals(manager.statistics().totalCells(), manager.statistics().normalCells())
        );
    }

    @Test
    void testApplyTurnsNormalCellIntoHighlightedCell() {
        LabSimulationManager manager = new LabSimulationManager(createConfig());
        LabUserAction userAction = new LabUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, createSelectedCell(manager, coordinate));

        assertAll(
                () -> assertSame(LabEntity.HIGHLIGHTED, manager.currentGridModel().getEntity(coordinate)),
                () -> assertEquals(1, manager.statistics().highlightedCells()),
                () -> assertEquals(manager.statistics().totalCells() - 1, manager.statistics().normalCells())
        );
    }

    @Test
    void testApplyTurnsHighlightedCellIntoNormalCell() {
        LabSimulationManager manager = new LabSimulationManager(createConfig());
        LabUserAction userAction = new LabUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().setEntity(coordinate, LabEntity.HIGHLIGHTED);
        manager.statistics().setCellCounts(1);

        userAction.apply(manager, NoUserActionContext.NO_CONTEXT, createSelectedCell(manager, coordinate));

        assertAll(
                () -> assertSame(LabEntity.NORMAL, manager.currentGridModel().getEntity(coordinate)),
                () -> assertEquals(0, manager.statistics().highlightedCells()),
                () -> assertEquals(manager.statistics().totalCells(), manager.statistics().normalCells())
        );
    }

}
