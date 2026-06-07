package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ForestConfigTest {

    private static ForestConfig createConfig(CellShape cellShape,
                                             GridEdgeBehavior gridEdgeBehavior,
                                             CellDisplayMode cellDisplayMode,
                                             double treeDensity,
                                             double treeGrowthProbability,
                                             double lightningIgnitionProbability,
                                             NeighborhoodMode neighborhoodMode) {
        return new ForestConfig(
                cellShape,
                gridEdgeBehavior,
                ForestConstraints.GRID_WIDTH_DEFAULT,
                ForestConstraints.GRID_HEIGHT_DEFAULT,
                ForestConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                1L,
                treeDensity,
                neighborhoodMode,
                treeGrowthProbability,
                lightningIgnitionProbability
        );
    }

    @Test
    void testIsValidWithDefaultConstraints() {
        ForestConfig config = createConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                ForestConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ForestConstraints.TREE_DENSITY_DEFAULT,
                ForestConstraints.TREE_GROWTH_PROBABILITY_DEFAULT,
                ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_DEFAULT,
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        ForestConfig config = createConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                GridEdgeBehavior.ABSORB_XY,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ForestConstraints.TREE_DENSITY_DEFAULT,
                ForestConstraints.TREE_GROWTH_PROBABILITY_DEFAULT,
                ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_DEFAULT,
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsTreeDensityOutsideRange() {
        ForestConfig config = createConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                ForestConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                Math.nextUp(ForestConstraints.TREE_DENSITY_MAX),
                ForestConstraints.TREE_GROWTH_PROBABILITY_DEFAULT,
                ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_DEFAULT,
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsTreeGrowthProbabilityOutsideRange() {
        ForestConfig config = createConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                ForestConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ForestConstraints.TREE_DENSITY_DEFAULT,
                Math.nextUp(ForestConstraints.TREE_GROWTH_PROBABILITY_MAX),
                ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_DEFAULT,
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsLightningIgnitionProbabilityOutsideRange() {
        ForestConfig config = createConfig(
                ForestConstraints.CELL_SHAPE_DEFAULT,
                ForestConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ForestConstraints.TREE_DENSITY_DEFAULT,
                ForestConstraints.TREE_GROWTH_PROBABILITY_DEFAULT,
                Math.nextUp(ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_MAX),
                ForestConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        ForestConfig config = createConfig(
                CellShape.TRIANGLE,
                GridEdgeBehavior.WRAP_XY,
                CellDisplayMode.EMOJI,
                ForestConstraints.TREE_DENSITY_DEFAULT,
                ForestConstraints.TREE_GROWTH_PROBABILITY_DEFAULT,
                ForestConstraints.LIGHTNING_IGNITION_PROBABILITY_DEFAULT,
                NeighborhoodMode.EDGES_AND_VERTICES);

        assertTrue(config.isValid());
    }

}
