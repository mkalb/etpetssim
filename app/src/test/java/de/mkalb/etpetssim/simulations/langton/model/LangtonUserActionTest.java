package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;
import de.mkalb.etpetssim.simulations.langton.shared.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class LangtonUserActionTest {

    private static LangtonConfig createConfig() {
        return createConfig(LangtonConstraints.CELL_SHAPE_DEFAULT);
    }

    private static LangtonConfig createConfig(CellShape cellShape) {
        return new LangtonConfig(
                cellShape,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LangtonConstraints.GRID_WIDTH_DEFAULT,
                LangtonConstraints.GRID_HEIGHT_DEFAULT,
                LangtonConstraints.CELL_EDGE_LENGTH_DEFAULT,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.fromString((cellShape == CellShape.TRIANGLE) ? "URR" : LangtonConstraints.RULE_DEFAULT)
        );
    }

    private static LangtonCell selectedCell(LangtonSimulationManager manager, GridCoordinate coordinate) {
        return LangtonCell.of(manager.currentModel(), coordinate);
    }

    @Test
    void testAddAntUsesSelectedDirectionOnUnvisitedCell() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig());
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = manager.currentModel()
                                           .groundModel()
                                           .filteredCoordinates(e -> e == TerrainConstant.UNVISITED)
                                           .stream()
                                           .filter(c -> manager.currentModel().antModel().getEntity(c) instanceof NoAgent)
                                           .findFirst()
                                           .orElseThrow();
        int antCellsBefore = manager.statistics().getAntCells();
        int visitedCellsBefore = manager.statistics().getVisitedCells();

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.W),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertInstanceOf(Ant.class, manager.currentModel().antModel().getEntity(coordinate)),
                () -> assertEquals(CompassDirection.W, ((Ant) manager.currentModel().antModel().getEntity(coordinate)).direction()),
                () -> assertEquals(antCellsBefore + 1, manager.statistics().getAntCells()),
                () -> assertEquals(visitedCellsBefore + 1, manager.statistics().getVisitedCells())
        );
    }

    @Test
    void testAddAntUsesValidDirectionForTriangleCellPointingDown() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig(CellShape.TRIANGLE));
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.N),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(coordinate.isTriangleCellPointingDown()),
                () -> assertInstanceOf(Ant.class, manager.currentModel().antModel().getEntity(coordinate)),
                () -> assertEquals(CompassDirection.N, ((Ant) manager.currentModel().antModel().getEntity(coordinate)).direction())
        );
    }

    @Test
    void testAddAntIgnoresInvalidDirectionForTriangleCellPointingDown() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig(CellShape.TRIANGLE));
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        int antCellsBefore = manager.statistics().getAntCells();
        int visitedCellsBefore = manager.statistics().getVisitedCells();

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.S),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(coordinate.isTriangleCellPointingDown()),
                () -> assertInstanceOf(NoAgent.class, manager.currentModel().antModel().getEntity(coordinate)),
                () -> assertEquals(antCellsBefore, manager.statistics().getAntCells()),
                () -> assertEquals(visitedCellsBefore, manager.statistics().getVisitedCells())
        );
    }

    @Test
    void testAddAntUsesValidDirectionForTriangleCellPointingUp() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig(CellShape.TRIANGLE));
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = new GridCoordinate(1, 0);

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.S),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertFalse(coordinate.isTriangleCellPointingDown()),
                () -> assertInstanceOf(Ant.class, manager.currentModel().antModel().getEntity(coordinate)),
                () -> assertEquals(CompassDirection.S, ((Ant) manager.currentModel().antModel().getEntity(coordinate)).direction())
        );
    }

}
