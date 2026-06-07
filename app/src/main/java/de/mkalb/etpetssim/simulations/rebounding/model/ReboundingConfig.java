package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import static de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConstraints.*;

/**
 * Immutable configuration for a simulation.
 *
 * @param cellShape           the configured cell shape
 * @param gridEdgeBehavior    the configured grid edge behavior
 * @param gridWidth           the grid width in cells
 * @param gridHeight          the grid height in cells
 * @param cellEdgeLength      the rendered cell edge length in pixels
 * @param cellDisplayMode     the cell display mode used by the UI
 * @param seed                the random seed used for initialization
 * @param verticalWalls       the number of generated vertical wall segments
 * @param movingEntityPercent the initial percentage of moving entities
 * @param neighborhoodMode    the neighborhood mode used for motion and collisions
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

    private boolean hasValidRanges() {
        return isInRangeInt(verticalWalls, VERTICAL_WALLS_MIN, VERTICAL_WALLS_MAX)
                && isInRangeDouble(movingEntityPercent, MOVING_ENTITY_PERCENT_MIN, MOVING_ENTITY_PERCENT_MAX);
    }

    /**
     * Validates the common simulation settings and the rebounding-specific supported selections and ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && isAllowedSelection(neighborhoodMode, NEIGHBORHOOD_MODE_VALUES)
                && hasValidRanges();
    }

}
