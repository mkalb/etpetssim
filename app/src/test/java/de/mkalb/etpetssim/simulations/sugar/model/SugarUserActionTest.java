package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.sugar.model.entity.NoResource;
import de.mkalb.etpetssim.simulations.sugar.model.entity.ResourceEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;
import de.mkalb.etpetssim.simulations.sugar.shared.SugarAddSugarLevel;
import de.mkalb.etpetssim.simulations.sugar.shared.SugarUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

final class SugarUserActionTest {

    private static SugarConfig createConfig() {
        return new SugarConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.GRID_WIDTH_DEFAULT,
                SugarConstraints.GRID_HEIGHT_DEFAULT,
                SugarConstraints.CELL_EDGE_LENGTH_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                0.0d,
                1,
                0,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT
        );
    }

    @Test
    void testApplyAddSugarAddsResourceWithSelectedLevel() {
        SugarSimulationManager manager = new SugarSimulationManager(createConfig());
        SugarUserAction userAction = new SugarUserAction();
        GridCoordinate coordinate = manager.currentGridModel()
                .resourceModel()
                .filteredCoordinates(ResourceEntity::isEmpty)
                .stream()
                .findFirst()
                .orElseThrow();
        int resourceCellsBefore = manager.statistics().resourceCells();

        userAction.apply(
                manager,
                new SugarUserActionContext.AddSugar(SugarAddSugarLevel.HIGH),
                SugarCell.from(manager.currentGridModel(), coordinate));

        ResourceEntity resourceEntity = manager.currentGridModel().resourceModel().getEntity(coordinate);
        assertAll(
                () -> assertInstanceOf(Sugar.class, resourceEntity),
                () -> assertEquals(
                        SugarAddSugarLevel.HIGH.computeSugarAmount(manager.config().maxSugarAmount()),
                        ((Sugar) resourceEntity).currentAmount()),
                () -> assertEquals(resourceCellsBefore + 1, manager.statistics().resourceCells())
        );
    }

    @Test
    void testApplyRemoveSugarRemovesSelectedResourceCell() {
        SugarSimulationManager manager = new SugarSimulationManager(createConfig());
        SugarUserAction userAction = new SugarUserAction();
        GridCoordinate coordinate = manager.currentGridModel()
                .resourceModel()
                .filteredCoordinates(ResourceEntity::isNotEmpty)
                .stream()
                .findFirst()
                .orElseThrow();
        int resourceCellsBefore = manager.statistics().resourceCells();

        userAction.apply(
                manager,
                SugarUserActionContext.FixedAction.REMOVE_SUGAR,
                SugarCell.from(manager.currentGridModel(), coordinate));

        assertAll(
                () -> assertSame(NoResource.NO_RESOURCE, manager.currentGridModel().resourceModel().getEntity(coordinate)),
                () -> assertEquals(resourceCellsBefore - 1, manager.statistics().resourceCells())
        );
    }

}
