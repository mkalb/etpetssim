package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SugarConfigTest {

    private static SugarConfig createConfig(CellShape cellShape,
                                            GridEdgeBehavior gridEdgeBehavior,
                                            CellDisplayMode cellDisplayMode,
                                            double agentPercent,
                                            int sugarPeaks,
                                            int sugarRadiusLimit,
                                            int minSugarAmount,
                                            int maxSugarAmount,
                                            int agentInitialEnergy,
                                            int sugarRegenerationRate,
                                            int agentMetabolismRate,
                                            int agentVisionRange,
                                            int agentMaxAge,
                                            NeighborhoodMode neighborhoodMode) {
        return new SugarConfig(
                cellShape,
                gridEdgeBehavior,
                SugarConstraints.GRID_WIDTH_DEFAULT,
                SugarConstraints.GRID_HEIGHT_DEFAULT,
                SugarConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                1L,
                agentPercent,
                sugarPeaks,
                sugarRadiusLimit,
                minSugarAmount,
                maxSugarAmount,
                agentInitialEnergy,
                neighborhoodMode,
                sugarRegenerationRate,
                agentMetabolismRate,
                agentVisionRange,
                agentMaxAge
        );
    }

    @Test
    void testIsValidWithDefaultConstraints() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellShape() {
        SugarConfig config = createConfig(
                CellShape.TRIANGLE,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.SHAPE_BORDERED,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                GridEdgeBehavior.ABSORB_XY,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                NeighborhoodMode.EDGES_AND_VERTICES);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedMinSugarAmount() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT + 1,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsAgentPercentOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                Math.nextUp(SugarConstraints.AGENT_PERCENT_MAX),
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsMaxSugarAmountOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_MAX + 1,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsSugarPeaksOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_MAX + 1,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsSugarRadiusLimitOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_MAX + 1,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsAgentInitialEnergyOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_MAX + 1,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsSugarRegenerationRateOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_MAX + 1,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsAgentMetabolismRateOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_MAX + 1,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsAgentVisionRangeOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_MAX + 1,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsAgentMaxAgeOutsideRange() {
        SugarConfig config = createConfig(
                SugarConstraints.CELL_SHAPE_DEFAULT,
                SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_MAX + 1,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        SugarConfig config = createConfig(
                CellShape.HEXAGON,
                GridEdgeBehavior.BLOCK_X_WRAP_Y,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                SugarConstraints.AGENT_PERCENT_DEFAULT,
                SugarConstraints.SUGAR_PEAKS_DEFAULT,
                SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT,
                SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT,
                SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT,
                SugarConstraints.AGENT_VISION_RANGE_DEFAULT,
                SugarConstraints.AGENT_MAX_AGE_DEFAULT,
                SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

}
