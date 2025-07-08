package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * Provides logic for determining the arrangement and neighbor relationships of cells
 * in a two-dimensional grid, based on cell shape, neighborhood mode, and cell position.
 * <p>
 * This class centralizes the calculation of neighbor directions for different grid types
 * (triangular, square, hexagonal) and supports both edge-only and edge-and-vertex neighborhood modes.
 * <p>
 * Results are cached for efficiency. All returned direction lists are immutable.
 *
 * @see CellShape
 * @see NeighborhoodMode
 * @see GridDirection
 * @see GridCoordinate
 */
public final class GridArrangement {

    /**
     * Internal cache for computed direction sets, keyed by a string representing the arrangement parameters.
     */
    private static final Map<String, List<GridDirection>> CACHE = HashMap.newHashMap(16);

    /**
     * Private constructor to prevent instantiation.
     */
    private GridArrangement() {
    }

    /**
     * Returns the list of neighbor directions for a cell, based on its shape, neighborhood mode, and coordinate.
     * <p>
     * The returned list is immutable and may be shared between calls with identical parameters.
     *
     * @param cellShape        the shape of the cell (TRIANGLE, SQUARE, HEXAGON)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param coordinate       the grid coordinate of the cell (may affect direction set for some shapes)
     * @return an immutable list of {@link GridDirection} objects representing neighbor offsets
     */
    public static List<GridDirection> directionsFor(CellShape cellShape, NeighborhoodMode neighborhoodMode, GridCoordinate coordinate) {
        return CACHE.computeIfAbsent(buildCacheKey(cellShape, neighborhoodMode, coordinate), _ -> computeDirections(cellShape, neighborhoodMode, coordinate));
    }

    /**
     * Builds a unique cache key for the given arrangement parameters.
     *
     * @param cellShape        the shape of the cell
     * @param neighborhoodMode the neighborhood mode
     * @param coordinate       the grid coordinate of the cell
     * @return a string key representing the arrangement parameters
     */
    static String buildCacheKey(CellShape cellShape, NeighborhoodMode neighborhoodMode, GridCoordinate coordinate) {
        return switch (cellShape) {
            case TRIANGLE ->
                    cellShape + "::" + neighborhoodMode + "::" + coordinate.isTriangleCellPointingDown() + "::" + coordinate.hasTriangleCellXOffset();
            case SQUARE -> cellShape + "::" + neighborhoodMode;
            case HEXAGON -> cellShape + "::" + coordinate.hasHexagonCellYOffset();
        };
    }

    /**
     * Computes the list of neighbor directions for the given arrangement parameters.
     *
     * @param cellShape        the shape of the cell
     * @param neighborhoodMode the neighborhood mode
     * @param coordinate       the grid coordinate of the cell
     * @return an immutable list of {@link GridDirection} objects representing neighbor offsets
     */
    static List<GridDirection> computeDirections(CellShape cellShape, NeighborhoodMode neighborhoodMode, GridCoordinate coordinate) {
        return switch (cellShape) {
            case TRIANGLE ->
                    computeTriangleDirections(neighborhoodMode, coordinate.isTriangleCellPointingDown(), coordinate.hasTriangleCellXOffset());
            case SQUARE -> computeSquareDirections(neighborhoodMode);
            case HEXAGON -> computeHexagonDirections(coordinate.hasHexagonCellYOffset());
        };
    }

    /**
     * Computes the neighbor directions for a triangle cell, based on neighborhood mode and orientation.
     *
     * @param neighborhoodMode         the neighborhood mode (edges only or edges and vertices)
     * @param isTriangleCellPointingDown whether the triangle cell is pointing downwards
     * @param hasTriangleCellXOffset   whether the triangle cell has an x-offset (affects arrangement)
     * @return an immutable list of {@link GridDirection} objects for the triangle cell
     */
    static List<GridDirection> computeTriangleDirections(
            NeighborhoodMode neighborhoodMode,
            boolean isTriangleCellPointingDown,
            boolean hasTriangleCellXOffset) {

        // Directions for edge neighbors (3 per triangle)
        if (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) {
            if (isTriangleCellPointingDown) {
                if (hasTriangleCellXOffset) { // (2, 2)
                    return List.of(
                            new GridDirection(0, 1), // (2, 3) edge
                            new GridDirection(0, -1), // (2, 1) edge
                            new GridDirection(1, 1) // (3, 3) edge
                    );
                } else { // (2, 4)
                    return List.of(
                            new GridDirection(-1, 1), // (1, 5) edge
                            new GridDirection(0, -1), // (2, 3) edge
                            new GridDirection(0, 1) // (2, 5) edge
                    );
                }
            } else { // pointing up
                if (hasTriangleCellXOffset) { // (2, 5)
                    return List.of(
                            new GridDirection(0, -1), // (2, 4) edge
                            new GridDirection(1, -1), // (3, 4) edge
                            new GridDirection(0, 1) // (2, 6) edge
                    );
                } else { // (2, 3)
                    return List.of(
                            new GridDirection(-1, -1), // (1, 2) edge
                            new GridDirection(0, -1), // (2, 2) edge
                            new GridDirection(0, 1) // (2, 4) edge
                    );
                }
            }
        }

        // Directions for edge + vertex neighbors (12 per triangle)
        if (isTriangleCellPointingDown) {
            if (hasTriangleCellXOffset) { // (2, 2)
                return List.of(
                        new GridDirection(0, 1), // (2, 3) edge
                        new GridDirection(-1, 0), // (1, 2) vertex
                        new GridDirection(-1, -1), // (1, 1) vertex
                        new GridDirection(0, -2), // (2, 0) vertex
                        new GridDirection(0, -1), // (2, 1) edge
                        new GridDirection(1, -2), // (3, 0) vertex
                        new GridDirection(1, -1), // (3, 1) vertex
                        new GridDirection(1, 0), // (3, 2) vertex
                        new GridDirection(1, 1), // (3, 3) edge
                        new GridDirection(1, 2), // (3, 4) vertex
                        new GridDirection(0, 3), // (2, 5) vertex
                        new GridDirection(0, 2) // (2, 4) vertex
                );
            } else { // (2, 4)
                return List.of(
                        new GridDirection(-1, 1), // (1, 5) edge
                        new GridDirection(-1, 0), // (1, 4) vertex
                        new GridDirection(-1, -1), // (1, 3) vertex
                        new GridDirection(-1, -2), // (1, 2) vertex
                        new GridDirection(0, -1), // (2, 3) edge
                        new GridDirection(0, -2), // (2, 2) vertex
                        new GridDirection(1, -1), // (3, 3) vertex
                        new GridDirection(1, 0), // (3, 4) vertex
                        new GridDirection(0, 1), // (2, 5) edge
                        new GridDirection(0, 2), // (2, 6) vertex
                        new GridDirection(0, 3), // (2, 7) vertex
                        new GridDirection(-1, 2) // (1, 6) vertex
                );
            }
        } else { // pointing up
            if (hasTriangleCellXOffset) { // (2, 5)
                return List.of(
                        new GridDirection(0, -1), // (2, 4) edge
                        new GridDirection(0, -2), // (2, 3) vertex
                        new GridDirection(0, -3), // (2, 2) vertex
                        new GridDirection(1, -2), // (3, 3) vertex
                        new GridDirection(1, -1), // (3, 4) edge
                        new GridDirection(1, 0), // (3, 5) vertex
                        new GridDirection(1, 1), // (3, 6) vertex
                        new GridDirection(1, 2), // (3, 7) vertex
                        new GridDirection(0, 1), // (2, 6) edge
                        new GridDirection(0, 2), // (2, 7) vertex
                        new GridDirection(-1, 1), // (1, 6) vertex
                        new GridDirection(-1, 0) // (1, 5) vertex
                );
            } else { // (2, 3)
                return List.of(
                        new GridDirection(-1, -1), // (1, 2) edge
                        new GridDirection(-1, -2), // (1, 1) vertex
                        new GridDirection(0, -3), // (2, 0) vertex
                        new GridDirection(0, -2), // (2, 1) vertex
                        new GridDirection(0, -1), // (2, 2) edge
                        new GridDirection(1, 0), // (3, 3) vertex
                        new GridDirection(1, 1), // (3, 4) vertex
                        new GridDirection(0, 2), // (2, 5) vertex
                        new GridDirection(0, 1), // (2, 4) edge
                        new GridDirection(-1, 2), // (1, 5) vertex
                        new GridDirection(-1, 1), // (1, 4) vertex
                        new GridDirection(-1, 0) // (1, 3) vertex
                );
            }
        }
    }

    /**
     * Computes the neighbor directions for a square cell, based on neighborhood mode.
     *
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @return an immutable list of {@link GridDirection} objects for the square cell
     */
    static List<GridDirection> computeSquareDirections(NeighborhoodMode neighborhoodMode) {
        return switch (neighborhoodMode) {
            case EDGES_ONLY -> List.of(
                    new GridDirection(-1, 0),
                    new GridDirection(1, 0),
                    new GridDirection(0, -1),
                    new GridDirection(0, 1)
            );
            case EDGES_AND_VERTICES -> List.of(
                    new GridDirection(-1, 0),
                    new GridDirection(1, 0),
                    new GridDirection(0, -1),
                    new GridDirection(0, 1),
                    new GridDirection(-1, -1),
                    new GridDirection(-1, 1),
                    new GridDirection(1, -1),
                    new GridDirection(1, 1)
            );
        };
    }

    /**
     * Computes the neighbor directions for a hexagon cell, based on its y-offset.
     *
     * @param hasHexagonCellYOffset whether the hexagon cell has a y-offset (affects arrangement)
     * @return an immutable list of {@link GridDirection} objects for the hexagon cell
     */
    static List<GridDirection> computeHexagonDirections(boolean hasHexagonCellYOffset) {
        if (hasHexagonCellYOffset) {
            return List.of(
                    new GridDirection(-1, 0),
                    new GridDirection(0, -1),
                    new GridDirection(1, 0),
                    new GridDirection(1, 1),
                    new GridDirection(0, 1),
                    new GridDirection(-1, 1)
            );
        } else {
            return List.of(
                    new GridDirection(-1, -1),
                    new GridDirection(0, -1),
                    new GridDirection(1, -1),
                    new GridDirection(1, 0),
                    new GridDirection(0, 1),
                    new GridDirection(-1, 0)
            );
        }
    }

}
