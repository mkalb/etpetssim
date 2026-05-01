package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

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

    private static final int PERCENT_MIN = 0;
    private static final int PERCENT_MAX = 100;
    private static final int OBSTACLE_PERCENT_MAX = 50;
    private static final int PET_COUNT_MIN = 0;
    private static final int PET_COUNT_MAX = 20;

    private static boolean isOutOfRange(int value, int min, int max) {
        return (value < min) || (value > max);
    }

    /**
     * Validates the common simulation settings and the ET-Pets-specific placement constraints.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        if (!SimulationConfig.super.isValid()) {
            return false;
        }
        if (cellShape != CellShape.HEXAGON) {
            return false;
        }
        if (gridEdgeBehavior != GridEdgeBehavior.BLOCK_XY) {
            return false;
        }
        if (neighborhoodMode != NeighborhoodMode.EDGES_ONLY) {
            return false;
        }
        if (isOutOfRange(rockPercent, PERCENT_MIN, PERCENT_MAX)
                || isOutOfRange(waterPercent, PERCENT_MIN, PERCENT_MAX)
                || isOutOfRange(plantPercent, PERCENT_MIN, PERCENT_MAX)
                || isOutOfRange(insectPercent, PERCENT_MIN, PERCENT_MAX)
                || isOutOfRange(petCount, PET_COUNT_MIN, PET_COUNT_MAX)) {
            return false;
        }
        // Spec: rockPercent + waterPercent MUST NOT exceed 50%.
        return ((rockPercent + waterPercent) <= OBSTACLE_PERCENT_MAX)
                && ((plantPercent + insectPercent) <= PERCENT_MAX);
    }

}

