package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;
import de.mkalb.etpetssim.simulations.wator.shared.WatorUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class WatorUserActionTest {

    private static WatorConfig createConfig() {
        return new WatorConfig(
                WatorConstraints.CELL_SHAPE_DEFAULT,
                WatorConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                WatorConstraints.GRID_WIDTH_DEFAULT,
                WatorConstraints.GRID_HEIGHT_DEFAULT,
                WatorConstraints.CELL_EDGE_LENGTH_DEFAULT,
                WatorConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                0.0d,
                0.0d,
                WatorConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                WatorConstraints.FISH_MAX_AGE_DEFAULT,
                WatorConstraints.FISH_MIN_REPRODUCTION_AGE_DEFAULT,
                WatorConstraints.FISH_MIN_REPRODUCTION_INTERVAL_DEFAULT,
                WatorConstraints.SHARK_MAX_AGE_DEFAULT,
                WatorConstraints.SHARK_BIRTH_ENERGY_DEFAULT,
                WatorConstraints.SHARK_ENERGY_LOSS_PER_STEP_DEFAULT,
                WatorConstraints.SHARK_ENERGY_GAIN_PER_FISH_DEFAULT,
                WatorConstraints.SHARK_MIN_REPRODUCTION_AGE_DEFAULT,
                WatorConstraints.SHARK_MIN_REPRODUCTION_ENERGY_DEFAULT,
                WatorConstraints.SHARK_MIN_REPRODUCTION_INTERVAL_DEFAULT
        );
    }

    private static GridCell<WatorEntity> selectedCell(WatorSimulationManager manager, GridCoordinate coordinate) {
        return new GridCell<>(coordinate, manager.currentModel().getEntity(coordinate));
    }

    @Test
    void testApplyIgnoresMissingSelection() {
        WatorSimulationManager manager = new WatorSimulationManager(createConfig());
        WatorUserAction userAction = new WatorUserAction();

        userAction.apply(manager, WatorUserActionContext.ADD_FISH, null);

        assertAll(
                () -> assertEquals(0, manager.statistics().getFishCells()),
                () -> assertEquals(0, manager.statistics().getSharkCells()),
                () -> assertTrue(manager.currentModel().nonDefaultCoordinates().isEmpty())
        );
    }

    @Test
    void testAddFishAddsFishToSelectedWaterCell() {
        WatorSimulationManager manager = new WatorSimulationManager(createConfig());
        WatorUserAction userAction = new WatorUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(manager, WatorUserActionContext.ADD_FISH, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isFish()),
                () -> assertEquals(1, manager.statistics().getFishCells()),
                () -> assertEquals(0, manager.statistics().getSharkCells())
        );
    }

    @Test
    void testAddSharkAddsSharkToSelectedWaterCell() {
        WatorSimulationManager manager = new WatorSimulationManager(createConfig());
        WatorUserAction userAction = new WatorUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(manager, WatorUserActionContext.ADD_SHARK, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isShark()),
                () -> assertEquals(0, manager.statistics().getFishCells()),
                () -> assertEquals(1, manager.statistics().getSharkCells())
        );
    }

    @Test
    void testRemoveCreatureRemovesFishFromSelectedCell() {
        WatorSimulationManager manager = new WatorSimulationManager(createConfig());
        WatorUserAction userAction = new WatorUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentModel().setEntity(coordinate, manager.createFish(0));
        manager.statistics().adjustCellCounts(1, 0);

        userAction.apply(manager, WatorUserActionContext.REMOVE_CREATURE, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isWater()),
                () -> assertEquals(0, manager.statistics().getFishCells()),
                () -> assertEquals(0, manager.statistics().getSharkCells())
        );
    }

    @Test
    void testRemoveCreatureRemovesSharkFromSelectedCell() {
        WatorSimulationManager manager = new WatorSimulationManager(createConfig());
        WatorUserAction userAction = new WatorUserAction();
        GridCoordinate coordinate = new GridCoordinate(1, 0);
        manager.currentModel().setEntity(coordinate, manager.createShark(0));
        manager.statistics().adjustCellCounts(0, 1);

        userAction.apply(manager, WatorUserActionContext.REMOVE_CREATURE, selectedCell(manager, coordinate));

        assertAll(
                () -> assertTrue(manager.currentModel().getEntity(coordinate).isWater()),
                () -> assertEquals(0, manager.statistics().getFishCells()),
                () -> assertEquals(0, manager.statistics().getSharkCells())
        );
    }

}
