package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;

import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT;

/**
 * Immutable configuration of the Langton's Ant simulation.
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
 * @param seed             the random seed used for initialization
 * @param neighborhoodMode the neighborhood mode used for movement
 * @param movementRules    the cyclic movement rule set for visited states
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
        LangtonMovementRules movementRules)
        implements SimulationConfig {

    private boolean hasValidMovementRules() {
        return movementRules.isValidForCellShape(cellShape);
    }

    /**
     * Validates the common simulation settings and the Langton-specific selections and movement rules.
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
