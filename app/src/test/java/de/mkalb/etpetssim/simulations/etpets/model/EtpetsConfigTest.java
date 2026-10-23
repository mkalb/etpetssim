package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.function.*;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class EtpetsConfigTest {

    private static boolean isValidWith(Consumer<ConfigValues> change) {
        ConfigValues values = new ConfigValues();
        change.accept(values);
        return values.toConfig().isValid();
    }

    private static void clearPercents(ConfigValues values) {
        values.rockPercent = PERCENT_MIN;
        values.waterPercent = PERCENT_MIN;
        values.plantPercent = PERCENT_MIN;
        values.insectPercent = PERCENT_MIN;
    }

    @Test
    void testIsValidAcceptsDefaultConstraints() {
        assertTrue(isValidWith(_ -> {}));
    }

    @Test
    void testIsValidAcceptsBoundaryValues() {
        assertAll(
                () -> assertTrue(isValidWith(v -> v.rockPercent = PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> {
                    clearPercents(v);
                    v.rockPercent = PERCENT_MAX;
                })),
                () -> assertTrue(isValidWith(v -> v.waterPercent = PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> {
                    clearPercents(v);
                    v.waterPercent = PERCENT_MAX;
                })),
                () -> assertTrue(isValidWith(v -> v.plantPercent = PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> {
                    clearPercents(v);
                    v.plantPercent = PERCENT_MAX;
                })),
                () -> assertTrue(isValidWith(v -> v.insectPercent = PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> {
                    clearPercents(v);
                    v.insectPercent = PERCENT_MAX;
                })),
                () -> assertTrue(isValidWith(v -> v.petCount = PET_COUNT_MIN)),
                () -> assertTrue(isValidWith(v -> v.petCount = PET_COUNT_MAX))
        );
    }

    @Test
    void testIsValidRejectsUnsupportedCellShape() {
        assertFalse(isValidWith(v -> v.cellShape = CellShape.SQUARE));
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
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        assertFalse(isValidWith(v -> v.neighborhoodMode = NeighborhoodMode.EDGES_AND_VERTICES));
    }

    @Test
    void testIsValidRejectsRockPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.rockPercent = Math.nextDown(PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> {
                    clearPercents(v);
                    v.rockPercent = Math.nextUp(PERCENT_MAX);
                }))
        );
    }

    @Test
    void testIsValidRejectsWaterPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.waterPercent = Math.nextDown(PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> {
                    clearPercents(v);
                    v.waterPercent = Math.nextUp(PERCENT_MAX);
                }))
        );
    }

    @Test
    void testIsValidRejectsPlantPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.plantPercent = Math.nextDown(PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> {
                    clearPercents(v);
                    v.plantPercent = Math.nextUp(PERCENT_MAX);
                }))
        );
    }

    @Test
    void testIsValidRejectsInsectPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.insectPercent = Math.nextDown(PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> {
                    clearPercents(v);
                    v.insectPercent = Math.nextUp(PERCENT_MAX);
                }))
        );
    }

    @Test
    void testIsValidRejectsPetCountOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.petCount = PET_COUNT_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.petCount = PET_COUNT_MAX + 1))
        );
    }

    @Test
    void testIsValidAcceptsTotalPercentAtLimit() {
        assertTrue(isValidWith(v -> {
            v.rockPercent = 0.20d;
            v.waterPercent = 0.20d;
            v.plantPercent = 0.05d;
            v.insectPercent = 0.05d;
        }));
    }

    @Test
    void testIsValidRejectsTotalPercentAboveLimit() {
        assertFalse(isValidWith(v -> {
            v.rockPercent = 0.20d;
            v.waterPercent = 0.20d;
            v.plantPercent = 0.10d;
            v.insectPercent = 0.01d;
        }));
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        double rockPercent = ROCK_PERCENT_DEFAULT;
        double waterPercent = WATER_PERCENT_DEFAULT;
        double plantPercent = PLANT_PERCENT_DEFAULT;
        double insectPercent = INSECT_PERCENT_DEFAULT;
        int petCount = PET_COUNT_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;

        EtpetsConfig toConfig() {
            return new EtpetsConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, rockPercent, waterPercent, plantPercent,
                    insectPercent, petCount, neighborhoodMode);
        }

    }

}
