package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;

import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.*;

/**
 * Immutable configuration of the Simulation Lab simulation.
 *
 * <p>The components are grouped into structure, layout, initialization, and rules. Instances are created
 * from unvalidated input; check them with {@link #isValid()} before passing them to the model.
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
        LabColorMode colorMode,
        // Initialization
        long seed,
        // Rules
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    /**
     * Validates the common simulation settings and the Lab-specific selections.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && isAllowedSelection(colorMode, COLOR_MODE_VALUES);
    }

}
