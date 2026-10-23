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
                LangtonMovementRules.parse((cellShape == CellShape.TRIANGLE) ? "URR" : LangtonConstraints.RULE_DEFAULT)
        );
    }

    private static LangtonCell selectedCell(LangtonSimulationManager manager, GridCoordinate coordinate) {
        return LangtonCell.from(manager.currentGridModel(), coordinate);
    }

    @Test
    void testApplyAddAntUsesSelectedDirectionOnUnvisitedCell() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig());
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = manager.currentGridModel()
                                           .terrainModel()
                                           .filteredCoordinates(e -> e == TerrainConstant.UNVISITED)
                                           .stream()
                                           .filter(c -> manager.currentGridModel().antModel().getEntity(c) instanceof NoAgent)
                                           .findFirst()
                                           .orElseThrow();
        int antCellsBefore = manager.statistics().antCells();
        int visitedCellsBefore = manager.statistics().visitedCells();

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.W),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertInstanceOf(Ant.class, manager.currentGridModel().antModel().getEntity(coordinate)),
                () -> assertEquals(CompassDirection.W, ((Ant) manager.currentGridModel().antModel().getEntity(coordinate)).direction()),
                () -> assertEquals(antCellsBefore + 1, manager.statistics().antCells()),
                () -> assertEquals(visitedCellsBefore + 1, manager.statistics().visitedCells())
        );
    }

    @Test
    void testApplyAddAntUsesValidDirectionForTriangleCellPointingDown() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig(CellShape.TRIANGLE));
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.N),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(coordinate.isTriangleCellPointingDown()),
                () -> assertInstanceOf(Ant.class, manager.currentGridModel().antModel().getEntity(coordinate)),
                () -> assertEquals(CompassDirection.N, ((Ant) manager.currentGridModel().antModel().getEntity(coordinate)).direction())
        );
    }

    @Test
    void testApplyAddAntIgnoresInvalidDirectionForTriangleCellPointingDown() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig(CellShape.TRIANGLE));
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        int antCellsBefore = manager.statistics().antCells();
        int visitedCellsBefore = manager.statistics().visitedCells();

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.S),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(coordinate.isTriangleCellPointingDown()),
                () -> assertInstanceOf(NoAgent.class, manager.currentGridModel().antModel().getEntity(coordinate)),
                () -> assertEquals(antCellsBefore, manager.statistics().antCells()),
                () -> assertEquals(visitedCellsBefore, manager.statistics().visitedCells())
        );
    }

    @Test
    void testApplyAddAntUsesValidDirectionForTriangleCellPointingUp() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig(CellShape.TRIANGLE));
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = new GridCoordinate(1, 0);

        userAction.apply(
                manager,
                new LangtonUserActionContext.AddAnt(CompassDirection.S),
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertFalse(coordinate.isTriangleCellPointingDown()),
                () -> assertInstanceOf(Ant.class, manager.currentGridModel().antModel().getEntity(coordinate)),
                () -> assertEquals(CompassDirection.S, ((Ant) manager.currentGridModel().antModel().getEntity(coordinate)).direction())
        );
    }

    @Test
    void testApplyRemoveAntRemovesSelectedAntCell() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig());
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = manager.currentGridModel()
                                           .antModel()
                                           .filteredCoordinates(LangtonEntity::isAgent)
                                           .stream()
                                           .findFirst()
                                           .orElseThrow();
        int antCellsBefore = manager.statistics().antCells();
        int visitedCellsBefore = manager.statistics().visitedCells();

        userAction.apply(
                manager,
                LangtonUserActionContext.FixedAction.REMOVE_ANT,
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertInstanceOf(NoAgent.class, manager.currentGridModel().antModel().getEntity(coordinate)),
                () -> assertEquals(antCellsBefore - 1, manager.statistics().antCells()),
                () -> assertEquals(visitedCellsBefore, manager.statistics().visitedCells())
        );
    }

    @Test
    void testApplyRemoveAntOnCellWithoutAntDoesNothing() {
        LangtonSimulationManager manager = new LangtonSimulationManager(createConfig());
        LangtonUserAction userAction = new LangtonUserAction();
        GridCoordinate coordinate = manager.currentGridModel()
                                           .terrainModel()
                                           .filteredCoordinates(TerrainConstant::isUnvisited)
                                           .stream()
                                           .findFirst()
                                           .orElseThrow();
        int antCellsBefore = manager.statistics().antCells();
        int visitedCellsBefore = manager.statistics().visitedCells();

        userAction.apply(
                manager,
                LangtonUserActionContext.FixedAction.REMOVE_ANT,
                selectedCell(manager, coordinate));

        assertAll(
                () -> assertInstanceOf(NoAgent.class, manager.currentGridModel().antModel().getEntity(coordinate)),
                () -> assertEquals(antCellsBefore, manager.statistics().antCells()),
                () -> assertEquals(visitedCellsBefore, manager.statistics().visitedCells())
        );
    }

}
