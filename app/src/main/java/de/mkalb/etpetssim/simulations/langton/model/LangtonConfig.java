package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;

import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.*;

/**
 * Immutable configuration for a simulation.
 *
 * @param cellShape            the configured cell shape
 * @param gridEdgeBehavior     the configured grid edge behavior
 * @param gridWidth            the grid width in cells
 * @param gridHeight           the grid height in cells
 * @param cellEdgeLength       the rendered cell edge length in pixels
 * @param cellDisplayMode      the cell display mode used by the UI
 * @param seed                 the random seed used for initialization
 * @param neighborhoodMode     the neighborhood mode used for movement
 * @param langtonMovementRules the cyclic movement rule set for visited states
 */
public record LangtonConfig(
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
        // Rules
        NeighborhoodMode neighborhoodMode,
        LangtonMovementRules langtonMovementRules)
        implements SimulationConfig {

    private boolean hasValidMovementRules() {
        return langtonMovementRules.isValidForCellShape(cellShape);
    }

    /**
     * Validates the common simulation settings and the Langton-specific supported selections and movement rules.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && hasExpectedSelection(neighborhoodMode, NEIGHBORHOOD_MODE_DEFAULT)
                && hasValidMovementRules();
    }

}
