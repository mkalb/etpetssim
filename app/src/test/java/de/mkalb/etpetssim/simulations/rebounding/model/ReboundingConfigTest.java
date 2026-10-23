package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.function.*;

import static de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConstraints.*;
import static org.junit.jupiter.api.Assertions.*;

final class ReboundingConfigTest {

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
            v.cellDisplayMode = CellDisplayMode.SHAPE_BORDERED;
            v.neighborhoodMode = NeighborhoodMode.EDGES_ONLY;
        }));
    }

    @Test
    void testIsValidAcceptsBoundaryValues() {
        assertAll(
                () -> assertTrue(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MIN)),
                () -> assertTrue(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MAX)),
                () -> assertTrue(isValidWith(v -> v.movingEntityPercent = MOVING_ENTITY_PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> v.movingEntityPercent = MOVING_ENTITY_PERCENT_MAX))
        );
    }

    @Test
    void testIsValidRejectsUnsupportedCellShape() {
        assertFalse(isValidWith(v -> v.cellShape = CellShape.TRIANGLE));
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        assertFalse(isValidWith(v -> v.gridEdgeBehavior = GridEdgeBehavior.WRAP_XY));
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        assertFalse(isValidWith(v -> v.cellDisplayMode = CellDisplayMode.CIRCLE));
    }

    @Test
    void testIsValidRejectsVerticalWallsOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.verticalWalls = VERTICAL_WALLS_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsMovingEntityPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.movingEntityPercent = Math.nextDown(MOVING_ENTITY_PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> v.movingEntityPercent = Math.nextUp(MOVING_ENTITY_PERCENT_MAX)))
        );
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        int verticalWalls = VERTICAL_WALLS_DEFAULT;
        double movingEntityPercent = MOVING_ENTITY_PERCENT_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;

        ReboundingConfig toConfig() {
            return new ReboundingConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, verticalWalls, movingEntityPercent,
                    neighborhoodMode);
        }

    }

}
