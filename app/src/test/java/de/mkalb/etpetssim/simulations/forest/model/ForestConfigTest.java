package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.function.*;

import static de.mkalb.etpetssim.simulations.forest.model.ForestConstraints.*;
import static org.junit.jupiter.api.Assertions.*;

final class ForestConfigTest {

    private static boolean isValidWith(Consumer<ConfigValues> change) {
        ConfigValues values = new ConfigValues();
        change.accept(values);
        return values.toConfig().isValid();
    }

    @Test
    void testIsValidAcceptsDefaultConstraints() {
        assertTrue(isValidWith(_ -> {}));
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        assertTrue(isValidWith(v -> {
            v.cellShape = CellShape.TRIANGLE;
            v.gridEdgeBehavior = GridEdgeBehavior.WRAP_XY;
            v.cellDisplayMode = CellDisplayMode.EMOJI;
            v.neighborhoodMode = NeighborhoodMode.EDGES_AND_VERTICES;
        }));
    }

    @Test
    void testIsValidAcceptsBoundaryValues() {
        assertAll(
                () -> assertTrue(isValidWith(v -> v.treeDensity = TREE_DENSITY_MIN)),
                () -> assertTrue(isValidWith(v -> v.treeDensity = TREE_DENSITY_MAX)),
                () -> assertTrue(isValidWith(v -> v.treeGrowthProbability = TREE_GROWTH_PROBABILITY_MIN)),
                () -> assertTrue(isValidWith(v -> v.treeGrowthProbability = TREE_GROWTH_PROBABILITY_MAX)),
                () -> assertTrue(isValidWith(v -> v.lightningIgnitionProbability = LIGHTNING_IGNITION_PROBABILITY_MIN)),
                () -> assertTrue(isValidWith(v -> v.lightningIgnitionProbability = LIGHTNING_IGNITION_PROBABILITY_MAX))
        );
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        assertFalse(isValidWith(v -> v.gridEdgeBehavior = GridEdgeBehavior.ABSORB_XY));
    }

    @Test
    void testIsValidRejectsTreeDensityOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.treeDensity = Math.nextDown(TREE_DENSITY_MIN))),
                () -> assertFalse(isValidWith(v -> v.treeDensity = Math.nextUp(TREE_DENSITY_MAX)))
        );
    }

    @Test
    void testIsValidRejectsTreeGrowthProbabilityOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.treeGrowthProbability = Math.nextDown(TREE_GROWTH_PROBABILITY_MIN))),
                () -> assertFalse(isValidWith(v -> v.treeGrowthProbability = Math.nextUp(TREE_GROWTH_PROBABILITY_MAX)))
        );
    }

    @Test
    void testIsValidRejectsLightningIgnitionProbabilityOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(
                        v -> v.lightningIgnitionProbability = Math.nextDown(LIGHTNING_IGNITION_PROBABILITY_MIN))),
                () -> assertFalse(isValidWith(
                        v -> v.lightningIgnitionProbability = Math.nextUp(LIGHTNING_IGNITION_PROBABILITY_MAX)))
        );
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        double treeDensity = TREE_DENSITY_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;
        double treeGrowthProbability = TREE_GROWTH_PROBABILITY_DEFAULT;
        double lightningIgnitionProbability = LIGHTNING_IGNITION_PROBABILITY_DEFAULT;

        ForestConfig toConfig() {
            return new ForestConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, treeDensity, neighborhoodMode,
                    treeGrowthProbability, lightningIgnitionProbability);
        }

    }

}
