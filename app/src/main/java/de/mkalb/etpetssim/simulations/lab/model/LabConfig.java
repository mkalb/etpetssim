package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

/**
 * Immutable configuration for the simulation lab view.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param colorMode the color rendering mode
 * @param neighborhoodMode the neighborhood mode used for highlighting and inspection
 */
public record LabConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
        long seed,
        ColorMode colorMode,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    public enum ColorMode {
        COLOR, GRAYSCALE
    }

}
