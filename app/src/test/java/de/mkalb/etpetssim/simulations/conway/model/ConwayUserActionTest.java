package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SameParameterValue")
final class ConwayUserActionTest {

    private static ConwayConfig createConfig(double alivePercent) {
        return new ConwayConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                alivePercent,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT
        );
    }

    private static GridCell<ConwayEntity> selectedCell(ConwaySimulationManager manager, int x, int y) {
        GridCoordinate coordinate = new GridCoordinate(x, y);
        return new GridCell<>(coordinate, manager.currentModel().getEntity(coordinate));
    }

    @Test
    void testApplyIgnoresToggleCellWithoutSelection() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConfig(0.0d));
        ConwayUserAction userAction = new ConwayUserAction();

        userAction.apply(manager, ConwayUserActionContext.FixedAction.TOGGLE_CELL, null);

        assertAll(
                () -> assertEquals(0, manager.statistics().getAliveCells()),
                () -> assertEquals(0, manager.statistics().getChangedCells()),
                () -> assertTrue(manager.currentModel().nonDefaultCoordinates().isEmpty())
        );
    }

    @Test
    void testApplyClearGridAsGlobalAction() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConfig(0.0d));
        ConwayUserAction userAction = new ConwayUserAction();

        userAction.apply(manager, ConwayUserActionContext.FixedAction.TOGGLE_CELL, selectedCell(manager, 0, 0));
        userAction.apply(manager, ConwayUserActionContext.FixedAction.TOGGLE_CELL, selectedCell(manager, 1, 0));

        assertAll(
                () -> assertEquals(2, manager.statistics().getAliveCells()),
                () -> assertEquals(2, manager.statistics().getChangedCells())
        );

        userAction.apply(manager, ConwayUserActionContext.FixedAction.CLEAR_GRID, null);

        assertAll(
                () -> assertEquals(0, manager.statistics().getAliveCells()),
                () -> assertEquals(manager.statistics().getTotalCells(), manager.statistics().getDeadCells()),
                () -> assertEquals(4, manager.statistics().getChangedCells()),
                () -> assertTrue(manager.currentModel().nonDefaultCoordinates().isEmpty())
        );
    }

    @Test
    void testPlacePatternAppliesAvailablePatternAtSelectedCell() {
        ConwaySimulationManager manager = new ConwaySimulationManager(createConfig(0.0d));
        ConwayUserAction userAction = new ConwayUserAction();
        ConwayPatternChoice patternChoice = ConwayPatterns.availableChoices(manager.config())
                                                          .getFirst();
        GridCoordinate anchorCoordinate = new GridCoordinate(1, 1);
        int expectedAliveCells = Math.toIntExact(patternChoice.pattern().offsetMap()
                                                              .values()
                                                              .stream()
                                                              .filter(ConwayEntity::isAlive)
                                                              .count());

        userAction.apply(
                manager,
                new ConwayUserActionContext.PlacePattern(patternChoice),
                selectedCell(manager, anchorCoordinate.x(), anchorCoordinate.y()));

        assertAll(
                () -> assertEquals(expectedAliveCells, manager.statistics().getAliveCells()),
                () -> assertEquals(expectedAliveCells, manager.statistics().getChangedCells())
        );
    }

}
