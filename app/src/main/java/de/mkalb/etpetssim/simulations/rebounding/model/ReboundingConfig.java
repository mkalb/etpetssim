package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

import static de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConstraints.*;

/**
 * Immutable configuration for the rebounding-entities simulation.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param verticalWalls the number of generated vertical wall segments
 * @param movingEntityPercent the initial percentage of moving entities
 * @param neighborhoodMode the neighborhood mode used for motion and collisions
 */
public record ReboundingConfig(
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
        int verticalWalls,
        double movingEntityPercent,
        // Rules
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    private boolean hasAllowedSelections() {
        return CELL_SHAPE_VALUES.contains(cellShape)
                && GRID_EDGE_BEHAVIOR_VALUES.contains(gridEdgeBehavior)
                && CELL_DISPLAY_MODE_VALUES.contains(cellDisplayMode)
                && NEIGHBORHOOD_MODE_VALUES.contains(neighborhoodMode);
    }

    private boolean hasValidRanges() {
        return isInRange(verticalWalls, VERTICAL_WALLS_MIN, VERTICAL_WALLS_MAX)
                && isInRange(movingEntityPercent, MOVING_ENTITY_PERCENT_MIN, MOVING_ENTITY_PERCENT_MAX);
    }

    /**
     * Validates the common simulation settings and the rebounding-specific supported selections and ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return SimulationConfig.super.isValid()
                && hasAllowedSelections()
                && hasValidRanges();
    }

}
