package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.*;

/**
 * Immutable configuration for the simulation lab view.
 *
 * @param cellShape        the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth        the grid width in cells
 * @param gridHeight       the grid height in cells
 * @param cellEdgeLength   the rendered cell edge length in pixels
 * @param cellDisplayMode  the cell display mode used by the UI
 * @param colorMode        the color rendering mode
 * @param seed             the random seed used for initialization
 * @param neighborhoodMode the neighborhood mode used for highlighting and inspection
 */
public record LabConfig(
        // Structure
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        // Layout
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        ColorMode colorMode,
        // Initialization
        long seed,
        // Rules
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    private boolean hasAllowedSelections() {
        return CELL_SHAPE_VALUES.contains(cellShape)
                && GRID_EDGE_BEHAVIOR_VALUES.contains(gridEdgeBehavior)
                && CELL_DISPLAY_MODE_VALUES.contains(cellDisplayMode)
                && COLOR_MODE_VALUES.contains(colorMode)
                && NEIGHBORHOOD_MODE_VALUES.contains(neighborhoodMode);
    }

    /**
     * Validates the common simulation settings and the lab-specific selectable inspection settings.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return SimulationConfig.super.isValid()
                && hasAllowedSelections();
    }

    public enum ColorMode {
        COLOR, GRAYSCALE
    }

}
