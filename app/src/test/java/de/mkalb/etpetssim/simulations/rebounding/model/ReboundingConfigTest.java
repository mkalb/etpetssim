package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ReboundingConfigTest {

    private static ReboundingConfig createConfig(CellShape cellShape,
                                                 GridEdgeBehavior gridEdgeBehavior,
                                                 CellDisplayMode cellDisplayMode,
                                                 int verticalWalls,
                                                 double movingEntityPercent,
                                                 NeighborhoodMode neighborhoodMode) {
        return new ReboundingConfig(
                cellShape,
                gridEdgeBehavior,
                ReboundingConstraints.GRID_WIDTH_DEFAULT,
                ReboundingConstraints.GRID_HEIGHT_DEFAULT,
                ReboundingConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                1L,
                verticalWalls,
                movingEntityPercent,
                neighborhoodMode
        );
    }

    @Test
    void testIsValidWithDefaultConstraints() {
        ReboundingConfig config = createConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ReboundingConstraints.VERTICAL_WALLS_DEFAULT,
                ReboundingConstraints.MOVING_ENTITY_PERCENT_DEFAULT,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellShape() {
        ReboundingConfig config = createConfig(
                CellShape.TRIANGLE,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ReboundingConstraints.VERTICAL_WALLS_DEFAULT,
                ReboundingConstraints.MOVING_ENTITY_PERCENT_DEFAULT,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        ReboundingConfig config = createConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                GridEdgeBehavior.WRAP_XY,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ReboundingConstraints.VERTICAL_WALLS_DEFAULT,
                ReboundingConstraints.MOVING_ENTITY_PERCENT_DEFAULT,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        ReboundingConfig config = createConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.CIRCLE,
                ReboundingConstraints.VERTICAL_WALLS_DEFAULT,
                ReboundingConstraints.MOVING_ENTITY_PERCENT_DEFAULT,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsVerticalWallsOutsideRange() {
        ReboundingConfig config = createConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ReboundingConstraints.VERTICAL_WALLS_MAX + 1,
                ReboundingConstraints.MOVING_ENTITY_PERCENT_DEFAULT,
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsMovingEntityPercentOutsideRange() {
        ReboundingConfig config = createConfig(
                ReboundingConstraints.CELL_SHAPE_DEFAULT,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ReboundingConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ReboundingConstraints.VERTICAL_WALLS_DEFAULT,
                Math.nextUp(ReboundingConstraints.MOVING_ENTITY_PERCENT_MAX),
                ReboundingConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        ReboundingConfig config = createConfig(
                CellShape.SQUARE,
                ReboundingConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.SHAPE_BORDERED,
                ReboundingConstraints.VERTICAL_WALLS_DEFAULT,
                ReboundingConstraints.MOVING_ENTITY_PERCENT_DEFAULT,
                NeighborhoodMode.EDGES_ONLY);

        assertTrue(config.isValid());
    }

}
