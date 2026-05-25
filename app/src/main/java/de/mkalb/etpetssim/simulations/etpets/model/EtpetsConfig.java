package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.*;

/**
 * Immutable configuration for the ET Pets simulation.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param rockPercent the initial rock terrain percentage
 * @param waterPercent the initial water terrain percentage
 * @param plantPercent the initial plant resource percentage
 * @param insectPercent the initial insect resource percentage
 * @param petCount the number of pets to spawn initially
 * @param neighborhoodMode the neighborhood mode used for movement and interaction
 */
public record EtpetsConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        // Initialization
        int rockPercent,
        int waterPercent,
        int plantPercent,
        int insectPercent,
        int petCount,
        // Rules
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    private boolean hasExpectedTopologyRules() {
        return (cellShape == CELL_SHAPE_DEFAULT)
                && (gridEdgeBehavior == GRID_EDGE_BEHAVIOR_DEFAULT)
                && (cellDisplayMode == CELL_DISPLAY_MODE_DEFAULT)
                && (neighborhoodMode == NEIGHBORHOOD_MODE_DEFAULT);
    }

    private boolean hasValidRanges() {
        return isInRange(rockPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRange(waterPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRange(plantPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRange(insectPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRange(petCount, PET_COUNT_MIN, PET_COUNT_MAX);
    }

    private boolean hasValidCombinedPercents() {
        return ((rockPercent + waterPercent) <= OBSTACLE_PERCENT_MAX)
                && ((plantPercent + insectPercent) <= PERCENT_MAX);
    }

    /**
     * Validates the common simulation settings and the ET-Pets-specific placement constraints.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return SimulationConfig.super.isValid()
                && hasExpectedTopologyRules()
                && hasValidRanges()
                // Spec: rockPercent + waterPercent MUST NOT exceed 50%.
                && hasValidCombinedPercents();
    }

}

