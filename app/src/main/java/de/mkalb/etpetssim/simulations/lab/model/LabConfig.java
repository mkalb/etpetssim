package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabBaseLayer;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabDistribution;
import de.mkalb.etpetssim.simulations.lab.shared.LabPattern;

import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.BASE_LAYER_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.COLOR_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.DISTRIBUTION_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.HIGHLIGHTED_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.HIGHLIGHTED_PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.PATTERN_VALUES;

/**
 * Immutable configuration of the Simulation Lab.
 *
 * <p>The components are grouped into structure, layout, initialization, and rules. Instances are created
 * from unvalidated input; check them with {@link #isValid()} before passing them to the model.
 *
 * @param cellShape          the configured cell shape
 * @param gridEdgeBehavior   the configured grid edge behavior
 * @param gridWidth          the grid width in cells
 * @param gridHeight         the grid height in cells
 * @param cellEdgeLength     the rendered cell edge length in pixels
 * @param cellDisplayMode    the cell display mode used by the UI
 * @param colorMode          the color rendering mode
 * @param baseLayer          the content of the base layer, independent of the model
 * @param seed               the random seed used for initialization
 * @param distribution       the distribution of the highlighted cells at the start
 * @param highlightedPercent the share of highlighted cells as a fraction of all cells, used by the random
 *                           distributions
 * @param pattern            the pattern of highlighted cells, used by {@link LabDistribution#PATTERN}
 * @param neighborhoodMode   the neighborhood mode used for highlighting and inspection
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
        LabBaseLayer baseLayer,
        // Initialization
        long seed,
        LabDistribution distribution,
        double highlightedPercent,
        LabPattern pattern,
        // Rules
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    /**
     * Validates the common simulation settings and the Lab-specific selections and ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && isAllowedSelection(colorMode, COLOR_MODE_VALUES)
                && isAllowedSelection(baseLayer, BASE_LAYER_VALUES)
                && isAllowedSelection(distribution, DISTRIBUTION_VALUES)
                && isAllowedSelection(pattern, PATTERN_VALUES)
                && hasValidRanges();
    }

    private boolean hasValidRanges() {
        return isInRangeDouble(highlightedPercent, HIGHLIGHTED_PERCENT_MIN, HIGHLIGHTED_PERCENT_MAX);
    }

}
