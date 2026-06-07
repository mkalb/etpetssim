package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import java.util.*;

/**
 * Describes the immutable configuration values required to create a simulation grid.
 * <p>
 * Implementations are typically Java records that provide these shared core settings and
 * add simulation-specific configuration parameters as additional record components.
 * </p>
 */
public interface SimulationConfig {

    /**
     * Returns the configured cell shape.
     *
     * @return cell shape
     */
    CellShape cellShape();

    /**
     * Returns the configured edge behavior of the grid.
     *
     * @return grid edge behavior
     */
    GridEdgeBehavior gridEdgeBehavior();

    /**
     * Returns the configured grid width in cells.
     *
     * @return grid width
     */
    int gridWidth();

    /**
     * Returns the configured grid height in cells.
     *
     * @return grid height
     */
    int gridHeight();

    /**
     * Returns the configured visual edge length of one cell.
     *
     * @return cell edge length
     */
    double cellEdgeLength();

    /**
     * Returns the configured display mode for cells.
     *
     * @return cell display mode
     */
    CellDisplayMode cellDisplayMode();

    /**
     * Returns the random seed used for deterministic simulation setup.
     *
     * @return random seed
     */
    long seed();

    /**
     * Returns the configured neighborhood mode used for movement and interaction rules.
     *
     * @return neighborhood mode used for movement and interaction rules
     */
    NeighborhoodMode neighborhoodMode();

    /**
     * Creates the effective grid topology from the configured shape and edge behavior.
     *
     * @return derived grid topology
     */
    default GridTopology createGridTopology() {
        return new GridTopology(cellShape(), gridEdgeBehavior());
    }

    /**
     * Creates the effective grid size from configured width and height.
     *
     * @return derived grid size
     */
    default GridSize createGridSize() {
        return new GridSize(gridWidth(), gridHeight());
    }

    /**
     * Creates the full grid structure used by simulation models.
     *
     * @return derived grid structure
     */
    default GridStructure createGridStructure() {
        return new GridStructure(createGridTopology(), createGridSize());
    }

    /**
     * Computes the total number of cells based on configured width and height.
     *
     * @return cell count of the configured grid
     */
    default int computeCellCount() {
        return Math.multiplyExact(gridWidth(), gridHeight());
    }

    /**
     * Returns whether the given floating-point value is inside the inclusive range.
     *
     * @param value the value to test
     * @param min   the inclusive minimum
     * @param max   the inclusive maximum
     * @return {@code true} if the value is inside the range, otherwise {@code false}
     */
    default boolean isInRangeDouble(double value, double min, double max) {
        return (value >= min) && (value <= max);
    }

    /**
     * Returns whether the given integer value is inside the inclusive range.
     *
     * @param value the value to test
     * @param min   the inclusive minimum
     * @param max   the inclusive maximum
     * @return {@code true} if the value is inside the range, otherwise {@code false}
     */
    default boolean isInRangeInt(int value, int min, int max) {
        return (value >= min) && (value <= max);
    }

    /**
     * Returns whether the given selection is contained in the allowed values.
     *
     * @param selection         the configured selection
     * @param allowedSelections the allowed values for the selection
     * @param <T>               the selection type
     * @return {@code true} if the selection is allowed, otherwise {@code false}
     */
    default <T> boolean isAllowedSelection(T selection, Collection<T> allowedSelections) {
        return allowedSelections.contains(selection);
    }

    /**
     * Returns whether the configured core selectable settings are all contained in their allowed values.
     *
     * @param allowedCellShapes        the allowed cell-shape values
     * @param allowedGridEdgeBehaviors the allowed grid-edge-behavior values
     * @param allowedCellDisplayModes  the allowed cell-display-mode values
     * @return {@code true} if all configured core selections are allowed, otherwise {@code false}
     */
    default boolean hasAllowedCoreSelections(Collection<CellShape> allowedCellShapes,
                                             Collection<GridEdgeBehavior> allowedGridEdgeBehaviors,
                                             Collection<CellDisplayMode> allowedCellDisplayModes) {
        return isAllowedSelection(cellShape(), allowedCellShapes)
                && isAllowedSelection(gridEdgeBehavior(), allowedGridEdgeBehaviors)
                && isAllowedSelection(cellDisplayMode(), allowedCellDisplayModes);
    }

    /**
     * Returns whether the configured value equals the expected value.
     *
     * @param configuredValue the configured value
     * @param expectedValue   the required value
     * @param <T>             the value type
     * @return {@code true} if the configured value matches the expected value, otherwise {@code false}
     */
    default <T> boolean hasExpectedSelection(T configuredValue, T expectedValue) {
        return Objects.equals(configuredValue, expectedValue);
    }

    /**
     * Validates the shared core simulation settings required to construct a simulation grid.
     *
     * @return {@code true} if all shared configuration values are valid
     */
    default boolean isBaseValid() {
        return !GridSize.isInvalidSize(gridWidth())
                && !GridSize.isInvalidSize(gridHeight())
                && GridStructure.isValid(createGridTopology(), createGridSize())
                && (cellEdgeLength() > 0.0d);
    }

    /**
     * Validates whether this configuration can be used to construct a simulation grid.
     * <p>
     * Implementations should override this method when additional simulation-specific
     * validation is required. Such overrides should call {@code isBaseValid()}
     * first and then apply their own constraints.
     * </p>
     *
     * @return {@code true} if all configuration values are valid
     */
    default boolean isValid() {
        return isBaseValid();
    }

}
