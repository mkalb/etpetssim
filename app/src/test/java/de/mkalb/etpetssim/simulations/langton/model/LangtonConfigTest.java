package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class LangtonConfigTest {

    private static LangtonConfig createConfig(CellShape cellShape,
                                              GridEdgeBehavior gridEdgeBehavior,
                                              CellDisplayMode cellDisplayMode,
                                              NeighborhoodMode neighborhoodMode,
                                              LangtonMovementRules langtonMovementRules) {
        return new LangtonConfig(
                cellShape,
                gridEdgeBehavior,
                LangtonConstraints.GRID_WIDTH_DEFAULT,
                LangtonConstraints.GRID_HEIGHT_DEFAULT,
                LangtonConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                1L,
                neighborhoodMode,
                langtonMovementRules
        );
    }

    @Test
    void testIsValidAcceptsDefaultConstraints() {
        LangtonConfig config = createConfig(
                LangtonConstraints.CELL_SHAPE_DEFAULT,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.parse("RL"));

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        LangtonConfig config = createConfig(
                LangtonConstraints.CELL_SHAPE_DEFAULT,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                NeighborhoodMode.EDGES_AND_VERTICES,
                LangtonMovementRules.parse("RL"));

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        LangtonConfig config = createConfig(
                LangtonConstraints.CELL_SHAPE_DEFAULT,
                GridEdgeBehavior.BLOCK_XY,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.parse("RL"));

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        LangtonConfig config = createConfig(
                LangtonConstraints.CELL_SHAPE_DEFAULT,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.CIRCLE,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.parse("RL"));

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsMovementRulesInvalidForTriangle() {
        LangtonConfig config = createConfig(
                CellShape.TRIANGLE,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.parse("L2R"));

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsMovementRulesValidForTriangle() {
        LangtonConfig config = createConfig(
                CellShape.TRIANGLE,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.parse("URR"));

        assertTrue(config.isValid());
    }

}
