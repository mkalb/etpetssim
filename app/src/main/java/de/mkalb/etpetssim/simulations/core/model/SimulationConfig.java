package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.*;

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
     * Calculates the total number of cells based on configured width and height.
     *
     * @return cell count of the configured grid
     */
    default int calculateCellCount() {
        return gridWidth() * gridHeight();
    }

    /**
     * Validates whether this configuration can be used to construct a simulation grid.
     * <p>
     * Implementations should override this method when additional simulation-specific
     * validation is required. Such overrides should call {@code SimulationConfig.super.isValid()}
     * first and then apply their own constraints.
     * </p>
     *
     * @return {@code true} if all configuration values are valid
     */
    default boolean isValid() {
        return !GridSize.isInvalidSize(gridWidth())
                && !GridSize.isInvalidSize(gridHeight())
                && GridStructure.isValid(createGridTopology(), createGridSize())
                && (cellEdgeLength() > 0);
    }

}
