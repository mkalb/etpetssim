package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_MIN;

/**
 * Immutable configuration of the Sugarscape simulation.
 *
 * <p>The components are grouped into structure, layout, initialization, and rules. Instances are created
 * from unvalidated input; check them with {@link #isValid()} before passing them to the model.
 *
 * @param cellShape             the configured cell shape
 * @param gridEdgeBehavior      the configured grid edge behavior
 * @param gridWidth             the grid width in cells
 * @param gridHeight            the grid height in cells
 * @param cellEdgeLength        the rendered cell edge length in pixels
 * @param cellDisplayMode       the cell display mode used by the UI
 * @param seed                  the random seed used for initialization
 * @param agentPercent          the initial share of agent cells as a fraction of all cells
 * @param sugarPeaks            the number of sugar peaks to generate
 * @param sugarRadiusLimit      the maximum spread radius of a sugar peak
 * @param minSugarAmount        the minimum sugar amount per cell
 * @param maxSugarAmount        the maximum sugar amount per cell
 * @param agentInitialEnergy    the initial energy assigned to new agents
 * @param neighborhoodMode      the neighborhood mode used for movement and search
 * @param sugarRegenerationRate the sugar amount regenerated per step
 * @param agentMetabolismRate   the energy consumed by an agent per step
 * @param agentVisionRange      the agent vision range
 * @param agentMaxAge           the maximum agent age
 */
public record SugarConfig(
        // Structure
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        // Layout
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        // Initialization
        long seed,
        double agentPercent,
        int sugarPeaks,
        int sugarRadiusLimit,
        int minSugarAmount,
        int maxSugarAmount,
        int agentInitialEnergy,
        // Rules
        NeighborhoodMode neighborhoodMode,
        int sugarRegenerationRate,
        int agentMetabolismRate,
        int agentVisionRange,
        int agentMaxAge)
        implements SimulationConfig {

    private boolean hasValidRanges() {
        return isInRangeDouble(agentPercent, AGENT_PERCENT_MIN, AGENT_PERCENT_MAX)
                && isInRangeInt(sugarPeaks, SUGAR_PEAKS_MIN, SUGAR_PEAKS_MAX)
                && isInRangeInt(sugarRadiusLimit, SUGAR_RADIUS_LIMIT_MIN, SUGAR_RADIUS_LIMIT_MAX)
                && isInRangeInt(maxSugarAmount, MAX_SUGAR_AMOUNT_MIN, MAX_SUGAR_AMOUNT_MAX)
                && isInRangeInt(agentInitialEnergy, AGENT_INITIAL_ENERGY_MIN, AGENT_INITIAL_ENERGY_MAX)
                && isInRangeInt(sugarRegenerationRate, SUGAR_REGENERATION_RATE_MIN, SUGAR_REGENERATION_RATE_MAX)
                && isInRangeInt(agentMetabolismRate, AGENT_METABOLISM_RATE_MIN, AGENT_METABOLISM_RATE_MAX)
                && isInRangeInt(agentVisionRange, AGENT_VISION_RANGE_MIN, AGENT_VISION_RANGE_MAX)
                && isInRangeInt(agentMaxAge, AGENT_MAX_AGE_MIN, AGENT_MAX_AGE_MAX);
    }

    private boolean hasValidCombinedRanges() {
        return minSugarAmount <= maxSugarAmount;
    }

    /**
     * Validates the common simulation settings and the Sugarscape-specific selections, ranges, and combined ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && hasExpectedSelection(neighborhoodMode, NEIGHBORHOOD_MODE_DEFAULT)
                && hasExpectedSelection(minSugarAmount, MIN_SUGAR_AMOUNT_DEFAULT)
                && hasValidRanges()
                && hasValidCombinedRanges();
    }

}
