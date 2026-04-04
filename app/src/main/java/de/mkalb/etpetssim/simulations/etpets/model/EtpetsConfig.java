package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

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
        int petCount,
        int plantPercent,
        int insectPercent,
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

