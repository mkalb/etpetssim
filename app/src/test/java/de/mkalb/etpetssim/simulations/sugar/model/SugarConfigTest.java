package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_DISPLAY_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_EDGE_LENGTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_SHAPE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_HEIGHT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_WIDTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_MIN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SugarConfigTest {

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
            v.gridEdgeBehavior = GridEdgeBehavior.BLOCK_X_WRAP_Y;
        }));
    }

    @Test
    void testIsValidAcceptsBoundaryValues() {
        assertAll(
                () -> assertTrue(isValidWith(v -> v.agentPercent = AGENT_PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> v.agentPercent = AGENT_PERCENT_MAX)),
                () -> assertTrue(isValidWith(v -> v.sugarPeaks = SUGAR_PEAKS_MIN)),
                () -> assertTrue(isValidWith(v -> v.sugarPeaks = SUGAR_PEAKS_MAX)),
                () -> assertTrue(isValidWith(v -> v.sugarRadiusLimit = SUGAR_RADIUS_LIMIT_MIN)),
                () -> assertTrue(isValidWith(v -> v.sugarRadiusLimit = SUGAR_RADIUS_LIMIT_MAX)),
                () -> assertTrue(isValidWith(v -> v.maxSugarAmount = MAX_SUGAR_AMOUNT_MIN)),
                () -> assertTrue(isValidWith(v -> v.maxSugarAmount = MAX_SUGAR_AMOUNT_MAX)),
                () -> assertTrue(isValidWith(v -> v.agentInitialEnergy = AGENT_INITIAL_ENERGY_MIN)),
                () -> assertTrue(isValidWith(v -> v.agentInitialEnergy = AGENT_INITIAL_ENERGY_MAX)),
                () -> assertTrue(isValidWith(v -> v.sugarRegenerationRate = SUGAR_REGENERATION_RATE_MIN)),
                () -> assertTrue(isValidWith(v -> v.sugarRegenerationRate = SUGAR_REGENERATION_RATE_MAX)),
                () -> assertTrue(isValidWith(v -> v.agentMetabolismRate = AGENT_METABOLISM_RATE_MIN)),
                () -> assertTrue(isValidWith(v -> v.agentMetabolismRate = AGENT_METABOLISM_RATE_MAX)),
                () -> assertTrue(isValidWith(v -> v.agentVisionRange = AGENT_VISION_RANGE_MIN)),
                () -> assertTrue(isValidWith(v -> v.agentVisionRange = AGENT_VISION_RANGE_MAX)),
                () -> assertTrue(isValidWith(v -> v.agentMaxAge = AGENT_MAX_AGE_MIN)),
                () -> assertTrue(isValidWith(v -> v.agentMaxAge = AGENT_MAX_AGE_MAX))
        );
    }

    @Test
    void testIsValidRejectsUnsupportedCellShape() {
        assertFalse(isValidWith(v -> v.cellShape = CellShape.TRIANGLE));
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        assertFalse(isValidWith(v -> v.gridEdgeBehavior = GridEdgeBehavior.ABSORB_XY));
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        assertFalse(isValidWith(v -> v.cellDisplayMode = CellDisplayMode.SHAPE_BORDERED));
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        assertFalse(isValidWith(v -> v.neighborhoodMode = NeighborhoodMode.EDGES_AND_VERTICES));
    }

    @Test
    void testIsValidRejectsUnexpectedMinSugarAmount() {
        assertFalse(isValidWith(v -> v.minSugarAmount = MIN_SUGAR_AMOUNT_DEFAULT + 1));
    }

    @Test
    void testIsValidRejectsAgentPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.agentPercent = Math.nextDown(AGENT_PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> v.agentPercent = Math.nextUp(AGENT_PERCENT_MAX)))
        );
    }

    @Test
    void testIsValidRejectsSugarPeaksOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sugarPeaks = SUGAR_PEAKS_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sugarPeaks = SUGAR_PEAKS_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSugarRadiusLimitOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sugarRadiusLimit = SUGAR_RADIUS_LIMIT_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sugarRadiusLimit = SUGAR_RADIUS_LIMIT_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsMaxSugarAmountOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.maxSugarAmount = MAX_SUGAR_AMOUNT_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.maxSugarAmount = MAX_SUGAR_AMOUNT_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsAgentInitialEnergyOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.agentInitialEnergy = AGENT_INITIAL_ENERGY_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.agentInitialEnergy = AGENT_INITIAL_ENERGY_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSugarRegenerationRateOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sugarRegenerationRate = SUGAR_REGENERATION_RATE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sugarRegenerationRate = SUGAR_REGENERATION_RATE_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsAgentMetabolismRateOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.agentMetabolismRate = AGENT_METABOLISM_RATE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.agentMetabolismRate = AGENT_METABOLISM_RATE_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsAgentVisionRangeOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.agentVisionRange = AGENT_VISION_RANGE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.agentVisionRange = AGENT_VISION_RANGE_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsAgentMaxAgeOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.agentMaxAge = AGENT_MAX_AGE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.agentMaxAge = AGENT_MAX_AGE_MAX + 1))
        );
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        double agentPercent = AGENT_PERCENT_DEFAULT;
        int sugarPeaks = SUGAR_PEAKS_DEFAULT;
        int sugarRadiusLimit = SUGAR_RADIUS_LIMIT_DEFAULT;
        int minSugarAmount = MIN_SUGAR_AMOUNT_DEFAULT;
        int maxSugarAmount = MAX_SUGAR_AMOUNT_DEFAULT;
        int agentInitialEnergy = AGENT_INITIAL_ENERGY_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;
        int sugarRegenerationRate = SUGAR_REGENERATION_RATE_DEFAULT;
        int agentMetabolismRate = AGENT_METABOLISM_RATE_DEFAULT;
        int agentVisionRange = AGENT_VISION_RANGE_DEFAULT;
        int agentMaxAge = AGENT_MAX_AGE_DEFAULT;

        SugarConfig toConfig() {
            return new SugarConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, agentPercent, sugarPeaks, sugarRadiusLimit,
                    minSugarAmount, maxSugarAmount, agentInitialEnergy, neighborhoodMode, sugarRegenerationRate,
                    agentMetabolismRate, agentVisionRange, agentMaxAge);
        }

    }

}
