package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class LabConfigTest {

    private static LabConfig createConfig(CellShape cellShape,
                                          GridEdgeBehavior gridEdgeBehavior,
                                          CellDisplayMode cellDisplayMode,
                                          LabColorMode colorMode,
                                          NeighborhoodMode neighborhoodMode) {
        return new LabConfig(
                cellShape,
                gridEdgeBehavior,
                LabConstraints.GRID_WIDTH_DEFAULT,
                LabConstraints.GRID_HEIGHT_DEFAULT,
                LabConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                colorMode,
                1L,
                neighborhoodMode
        );
    }

    @Test
    void testIsValidWithDefaultConstraints() {
        LabConfig config = createConfig(
                LabConstraints.CELL_SHAPE_DEFAULT,
                LabConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LabConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LabConstraints.COLOR_MODE_DEFAULT,
                LabConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidAcceptsAlternativeAllowedSelections() {
        LabConfig config = createConfig(
                CellShape.SQUARE,
                GridEdgeBehavior.BLOCK_X_WRAP_Y,
                CellDisplayMode.CIRCLE,
                LabColorMode.GRAYSCALE,
                NeighborhoodMode.EDGES_ONLY);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        LabConfig config = createConfig(
                LabConstraints.CELL_SHAPE_DEFAULT,
                LabConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.EMOJI,
                LabConstraints.COLOR_MODE_DEFAULT,
                LabConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

}
