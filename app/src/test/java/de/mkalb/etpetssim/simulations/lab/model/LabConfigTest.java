package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import org.junit.jupiter.api.Test;

import java.util.function.*;

import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.*;
import static org.junit.jupiter.api.Assertions.*;

final class LabConfigTest {

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
            v.cellShape = CellShape.SQUARE;
            v.gridEdgeBehavior = GridEdgeBehavior.BLOCK_X_WRAP_Y;
            v.cellDisplayMode = CellDisplayMode.CIRCLE;
            v.colorMode = LabColorMode.GRAYSCALE;
            v.neighborhoodMode = NeighborhoodMode.EDGES_ONLY;
        }));
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        assertFalse(isValidWith(v -> v.cellDisplayMode = CellDisplayMode.EMOJI));
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        LabColorMode colorMode = COLOR_MODE_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;

        LabConfig toConfig() {
            return new LabConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, colorMode, 1L, neighborhoodMode);
        }

    }

}
