package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.*;

/**
 * Immutable configuration for the Conway simulation.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param alivePercent the initial alive-cell percentage in the range {@code 0.0} to {@code 1.0}
 * @param neighborhoodMode the neighborhood mode used for transition evaluation
 * @param transitionRules the survive/birth rule set
 */
public record ConwayConfig(
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
        double alivePercent,
        // Rules
        NeighborhoodMode neighborhoodMode,
        ConwayTransitionRules transitionRules)
        implements SimulationConfig {

    private boolean hasValidRanges() {
        return isInRangeDouble(alivePercent, ALIVE_PERCENT_MIN, ALIVE_PERCENT_MAX);
    }

    private boolean hasValidTransitionRules() {
        int maxNeighborCount = CellNeighborhoods.maxNeighborCount(cellShape, neighborhoodMode);
        return transitionRules.birthCounts().stream().noneMatch(count -> count < BIRTH_NEIGHBOR_COUNT_MIN)
                && transitionRules.surviveCounts().stream().allMatch(count -> count <= maxNeighborCount)
                && transitionRules.birthCounts().stream().allMatch(count -> count <= maxNeighborCount);
    }

    /**
     * Validates the common simulation settings and the Conway-specific initialization and transition rules.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && hasExpectedSelection(neighborhoodMode, NEIGHBORHOOD_MODE_DEFAULT)
                && hasValidRanges()
                && hasValidTransitionRules();
    }

}
