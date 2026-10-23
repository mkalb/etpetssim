package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;
import org.junit.jupiter.api.Test;

import java.util.function.*;

import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.*;
import static org.junit.jupiter.api.Assertions.*;

final class LangtonConfigTest {

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
            v.cellShape = CellShape.HEXAGON;
            v.gridEdgeBehavior = GridEdgeBehavior.ABSORB_XY;
            v.cellDisplayMode = CellDisplayMode.SHAPE_BORDERED;
        }));
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        assertFalse(isValidWith(v -> v.gridEdgeBehavior = GridEdgeBehavior.BLOCK_XY));
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        assertFalse(isValidWith(v -> v.cellDisplayMode = CellDisplayMode.CIRCLE));
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        assertFalse(isValidWith(v -> v.neighborhoodMode = NeighborhoodMode.EDGES_AND_VERTICES));
    }

    @Test
    void testIsValidAcceptsMovementRulesValidForTriangle() {
        assertTrue(isValidWith(v -> {
            v.cellShape = CellShape.TRIANGLE;
            v.movementRules = LangtonMovementRules.parse("URR");
        }));
    }

    @Test
    void testIsValidRejectsMovementRulesInvalidForTriangle() {
        assertFalse(isValidWith(v -> {
            v.cellShape = CellShape.TRIANGLE;
            v.movementRules = LangtonMovementRules.parse("L2R");
        }));
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;
        LangtonMovementRules movementRules = LangtonMovementRules.parse(RULE_DEFAULT);

        LangtonConfig toConfig() {
            return new LangtonConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, neighborhoodMode, movementRules);
        }

    }

}
