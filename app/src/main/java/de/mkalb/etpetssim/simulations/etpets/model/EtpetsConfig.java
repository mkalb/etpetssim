package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.COMBINED_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.PET_COUNT_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.PET_COUNT_MIN;

/**
 * Immutable configuration of the ET Pets simulation.
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
 * @param rockPercent      the initial share of rock terrain cells as a fraction of all cells
 * @param waterPercent     the initial share of water terrain cells as a fraction of all cells
 * @param plantPercent     the initial share of plant resource cells as a fraction of all cells
 * @param insectPercent    the initial share of insect resource cells as a fraction of all cells
 * @param petCount         the number of pets to spawn initially
 * @param neighborhoodMode the neighborhood mode used for movement and interaction
 */
public record EtpetsConfig(
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
        double rockPercent,
        double waterPercent,
        double plantPercent,
        double insectPercent,
        int petCount,
        // Rules
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    private boolean hasValidRanges() {
        return isInRangeDouble(rockPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRangeDouble(waterPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRangeDouble(plantPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRangeDouble(insectPercent, PERCENT_MIN, PERCENT_MAX)
                && isInRangeInt(petCount, PET_COUNT_MIN, PET_COUNT_MAX);
    }

    private boolean hasValidCombinedRanges() {
        return (rockPercent + waterPercent + plantPercent + insectPercent) <= COMBINED_PERCENT_MAX;
    }

    /**
     * Validates the common simulation settings and the ET Pets-specific selections, ranges, and combined ranges.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        return isBaseValid()
                && hasAllowedCoreSelections(CELL_SHAPE_VALUES, GRID_EDGE_BEHAVIOR_VALUES, CELL_DISPLAY_MODE_VALUES)
                && hasExpectedSelection(neighborhoodMode, NEIGHBORHOOD_MODE_DEFAULT)
                && hasValidRanges()
                && hasValidCombinedRanges();
    }

}
