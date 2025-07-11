package de.mkalb.etpetssim.engine;

import java.util.*;
import java.util.stream.*;

/**
 * Provides logic for determining the arrangement and neighbor relationships of cells
 * in a two-dimensional grid, based on cell shape, neighborhood mode, and cell position.
 * <p>
 * This class centralizes the calculation of neighbor directions for different grid types
 * (triangular, square, hexagonal) and supports both edge-only and edge-and-vertex neighborhood modes.
 *
 * @see CellShape
 * @see NeighborhoodMode
 * @see CellNeighbor
 * @see GridOffset
 * @see GridCoordinate
 */
@SuppressWarnings("MagicNumber")
public final class GridArrangement {

    /**
     * The maximum allowed neighborhood radius for neighbor calculations in the grid.
     * <p>
     * This value is used as an upper bound to prevent excessive computation and memory usage
     * when determining neighbors within a given radius. Methods that accept a radius parameter
     * will throw an {@link IllegalArgumentException} if the provided value exceeds this limit.
     */
    public static final int MAX_RADIUS = 100;

    /**
     * Internal cache for computed cell neighbor connections.
     */
    private static final Map<String, List<CellNeighborConnection>> CACHE = HashMap.newHashMap(16);

    /**
     * Private constructor to prevent instantiation.
     */
    private GridArrangement() {
    }

    /**
     * Returns the maximum possible number of direct neighbors for a cell
     * with the given shape and neighborhood mode, ignoring grid boundaries.
     * <p>
     * This value represents the theoretical upper bound for a cell in an infinite grid.
     * For example, a square cell with {@link NeighborhoodMode#EDGES_ONLY} has 4 possible neighbors,
     * while with {@link NeighborhoodMode#EDGES_AND_VERTICES} it has 8.
     *
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @return the maximum possible number of direct neighbors for the specified configuration
     */
    public static int maxNeighborCount(CellShape cellShape, NeighborhoodMode neighborhoodMode) {
        return switch (cellShape) {
            case SQUARE -> (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) ? 4 : 8;
            case HEXAGON -> 6; // Only edge neighbors for hexagons
            case TRIANGLE -> (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) ? 3 : 12;
        };
    }

    /**
     * Returns the maximum possible number of neighbors for a cell with the given shape,
     * neighborhood mode, and radius, ignoring grid boundaries.
     * <p>
     * The result is a theoretical upper bound for the number of cells within the given radius,
     * not the actual count for edge or corner cells in a finite grid.
     *
     * @param cellShape        the geometric shape of the cell (TRIANGLE, SQUARE, HEXAGON)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param radius           the neighborhood radius (&gt; 0)
     * @return the maximum possible number of neighbors within the given radius
     */
    public static int maxNeighborCount(CellShape cellShape, NeighborhoodMode neighborhoodMode, int radius) {
        if (radius <= 0) {
            return 0;
        }

        if (radius == 1) {
            return maxNeighborCount(cellShape, neighborhoodMode);
        }

        return switch (cellShape) {
            case TRIANGLE -> {
                // For triangles: degree = 3 (edges only) or 12 (edges and vertices)
                // Formula: 1 + ((degree * radius * (radius + 1)) / 2)
                int degree = (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) ? 3 : 12;
                yield 1 + ((degree * radius * (radius + 1)) / 2);
            }
            case SQUARE -> {
                // For squares: degree = 4 (edges only) or 8 (edges and vertices)
                // Formula: 1 + ((degree * radius * (radius + 1)) / 2)
                int degree = (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) ? 4 : 8;
                yield 1 + ((degree * radius * (radius + 1)) / 2);
            }
            case HEXAGON ->
                // For hexagons: always 6 neighbors per ring, so degree = 6
                // Formula: 1 + (3 * radius * (radius + 1))
                    1 + (3 * radius * (radius + 1));
        };
    }

    /**
     * Checks if two coordinates are valid neighbors in the grid for the given cell shape and neighborhood mode,
     * ignoring grid boundaries.
     * <p>
     * This method does not check whether the provided {@code from} and {@code to} coordinates are within the valid grid area.
     * It only determines if {@code to} is a direct neighbor of {@code from} according to the specified configuration.
     *
     * @param from             the coordinate of the source cell (not checked for grid validity)
     * @param to               the coordinate of the potential neighbor cell (not checked for grid validity)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @return {@code true} if {@code to} is a neighbor of {@code from} (ignoring grid boundaries), {@code false} otherwise
     */
    public static boolean isCellNeighbor(GridCoordinate from,
                                         GridCoordinate to,
                                         NeighborhoodMode neighborhoodMode,
                                         CellShape cellShape) {
        if (from.equals(to)) {
            return false;
        }
        List<CellNeighbor> neighbors = cellNeighbors(from, neighborhoodMode, cellShape);
        for (CellNeighbor neighbor : neighbors) {
            if (neighbor.neighborCoordinate().equals(to)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a stream of all valid neighbors for a given cell in the grid, considering grid boundaries.
     * <p>
     * This method first checks if the provided {@code startCoordinate} is within the valid bounds of the given
     * {@link GridStructure}. If not, an empty stream is returned. Otherwise, it computes all theoretical neighbors
     * (ignoring boundaries) and filters them to include only those whose coordinates are valid within the grid.
     * <p>
     * The resulting stream contains {@link CellNeighbor} objects describing the direction, connection type,
     * and coordinate of each valid neighbor.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param structure        the grid structure defining size and topology
     * @return a stream of {@link CellNeighbor} objects representing all valid neighbors of the cell (respecting grid boundaries)
     */
    public static Stream<CellNeighbor> validCellNeighborsStream(GridCoordinate startCoordinate,
                                                                NeighborhoodMode neighborhoodMode,
                                                                GridStructure structure) {
        if (!structure.isCoordinateValid(startCoordinate)) {
            return Stream.empty();
        }
        return cellNeighbors(startCoordinate, neighborhoodMode, structure.cellShape())
                .stream()
                .filter(neighbor -> structure.isCoordinateValid(neighbor.neighborCoordinate()));
    }

    /**
     * Returns a stream of all valid neighbor coordinates within the given radius for a cell in the grid, considering grid boundaries.
     * <p>
     * This method first checks if the provided {@code startCoordinate} is within the valid bounds of the given
     * {@link GridStructure}. If not, an empty stream is returned. Otherwise, it computes all theoretical neighbor coordinates
     * (ignoring boundaries) and filters them to include only those that are valid within the grid.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param structure        the grid structure defining size and topology
     * @param radius           the neighborhood radius (&gt; 0) and less than or equal to {@link #MAX_RADIUS}
     * @return a stream of {@link GridCoordinate} objects representing all valid neighbor coordinates of the cell within the given radius (respecting grid boundaries)
     * @throws IllegalArgumentException if the radius is greater than {@link #MAX_RADIUS}
     */
    public static Stream<GridCoordinate> validNeighborCoordinatesStream(GridCoordinate startCoordinate,
                                                                        NeighborhoodMode neighborhoodMode,
                                                                        GridStructure structure,
                                                                        int radius) {
        if (!structure.isCoordinateValid(startCoordinate)) {
            return Stream.empty();
        }
        return coordinatesOfNeighbors(startCoordinate, neighborhoodMode, structure.cellShape(), radius)
                .stream()
                .filter(structure::isCoordinateValid);
    }

    /**
     * Returns a list of all neighbors for a given cell in the grid, based on the specified
     * neighborhood mode and cell shape, ignoring grid boundaries.
     * <p>
     * The returned list contains {@link CellNeighbor} objects, each describing the direction,
     * connection type (edge or vertex), and coordinate of a neighboring cell relative to the
     * given start coordinate. The order and number of neighbors depend on the cell shape and
     * neighborhood mode.
     * <p>
     * <strong>Note:</strong> This method does not check whether the provided {@code startCoordinate}
     * or the resulting neighbor coordinates are within the valid grid area. It only determines
     * the theoretical neighbors as if the grid were infinite.
     * <p>
     * The returned list is unmodifiable.
     *
     * @param startCoordinate   the coordinate of the cell whose neighbors are to be determined (not checked for grid validity)
     * @param neighborhoodMode  the neighborhood mode (edges only or edges and vertices)
     * @param cellShape         the shape of the cell (triangle, square, hexagon)
     * @return an immutable list of {@link CellNeighbor} objects representing all neighbors of the cell (ignoring grid boundaries)
     */
    public static List<CellNeighbor> cellNeighbors(GridCoordinate startCoordinate,
                                                   NeighborhoodMode neighborhoodMode,
                                                   CellShape cellShape) {
        List<CellNeighborConnection> cellNeighborConnections = CACHE.computeIfAbsent(
                buildCacheKey(startCoordinate, neighborhoodMode, cellShape),
                _ -> computeCellNeighborConnections(startCoordinate, neighborhoodMode, cellShape));

        return cellNeighborConnections.stream()
                                      .map(neighborConnection -> new CellNeighbor(
                                              startCoordinate,
                                              neighborConnection.direction(),
                                              neighborConnection.connectionType(),
                                              startCoordinate.offset(neighborConnection.offset())
                                      ))
                                      .toList();
    }

    /**
     * Returns a set of all neighbor coordinates within the given radius for a cell,
     * based on the specified neighborhood mode and cell shape, ignoring grid boundaries.
     * Uses breadth-first search to avoid redundant visits and ensure efficiency.
     *
     * @param startCoordinate   the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode  the neighborhood mode (edges only or edges and vertices)
     * @param cellShape         the shape of the cell (triangle, square, hexagon)
     * @param radius            the neighborhood radius (> 0) and less than or equal to {@link #MAX_RADIUS}
     * @return a set of {@link GridCoordinate} objects representing all neighbor coordinates within the given radius
     * @throws IllegalArgumentException if the radius is greater than {@link #MAX_RADIUS}
     */
    public static Set<GridCoordinate> coordinatesOfNeighbors(GridCoordinate startCoordinate,
                                                             NeighborhoodMode neighborhoodMode,
                                                             CellShape cellShape,
                                                             int radius) {
        if (radius <= 0) {
            return Collections.emptySet();
        }
        if (radius > MAX_RADIUS) {
            throw new IllegalArgumentException("Radius must be less than or equal to " + MAX_RADIUS + ", but was: " + radius);
        }

        Set<GridCoordinate> visited = new HashSet<>();
        Set<GridCoordinate> result = new HashSet<>();
        Queue<GridCoordinate> queue = new ArrayDeque<>();

        visited.add(startCoordinate);
        queue.add(startCoordinate);

        int currentRadius = 0;

        while (!queue.isEmpty() && (currentRadius < radius)) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                GridCoordinate current = Objects.requireNonNull(queue.poll());

                for (CellNeighbor neighbor : cellNeighbors(current, neighborhoodMode, cellShape)) {
                    GridCoordinate neighborCoordinate = neighbor.neighborCoordinate();
                    if (visited.add(neighborCoordinate)) { // Only add if not visited
                        result.add(neighborCoordinate);
                        queue.add(neighborCoordinate);
                    }
                }
            }
            currentRadius++;
        }

        return result;
    }

    static String buildCacheKey(GridCoordinate startCoordinate,
                                NeighborhoodMode neighborhoodMode,
                                CellShape cellShape) {
        return switch (cellShape) {
            case TRIANGLE ->
                    cellShape + "::" + neighborhoodMode + "::" + startCoordinate.isTriangleCellPointingDown() + "::" + startCoordinate.hasTriangleCellXOffset();
            case SQUARE -> cellShape + "::" + neighborhoodMode;
            case HEXAGON -> cellShape + "::" + startCoordinate.hasHexagonCellYOffset();
        };
    }

    static List<CellNeighborConnection> computeCellNeighborConnections(GridCoordinate startCoordinate,
                                                                       NeighborhoodMode neighborhoodMode,
                                                                       CellShape cellShape) {
        return switch (cellShape) {
            case TRIANGLE -> computeTriangleCellNeighborConnections(
                    neighborhoodMode,
                    startCoordinate.isTriangleCellPointingDown(),
                    startCoordinate.hasTriangleCellXOffset());
            case SQUARE -> computeSquareCellNeighborConnections(neighborhoodMode);
            case HEXAGON -> computeHexagonCellNeighborConnections(startCoordinate.hasHexagonCellYOffset());
        };
    }

    static List<CellNeighborConnection> computeTriangleCellNeighborConnections(
            NeighborhoodMode neighborhoodMode,
            boolean isTriangleCellPointingDown,
            boolean hasTriangleCellXOffset) {

        // Directions for edge neighbors (3 per triangle)
        if (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) {
            if (isTriangleCellPointingDown) {
                if (hasTriangleCellXOffset) { // (2, 2)
                    return List.of(
                            new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.SW, CellConnectionType.EDGE), // (2, 3) edge
                            new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE), // (2, 1) edge
                            new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.SE, CellConnectionType.EDGE) // (3, 3) edge
                    );
                } else { // (2, 4)
                    return List.of(
                            new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.SW, CellConnectionType.EDGE), // (1, 5) edge
                            new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE), // (2, 3) edge
                            new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.SE, CellConnectionType.EDGE) // (2, 5) edge
                    );
                }
            } else { // pointing up
                if (hasTriangleCellXOffset) { // (2, 5)
                    return List.of(
                            new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.NW, CellConnectionType.EDGE), // (2, 4) edge
                            new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.NE, CellConnectionType.EDGE), // (3, 4) edge
                            new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE) // (2, 6) edge
                    );
                } else { // (2, 3)
                    return List.of(
                            new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.NW, CellConnectionType.EDGE), // (1, 2) edge
                            new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.NE, CellConnectionType.EDGE), // (2, 2) edge
                            new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE) // (2, 4) edge
                    );
                }
            }
        }

        // TODO Fix CompassDirection for TRIANGLE and NeighborhoodMode.EDGES_AND_VERTICES

        // Directions for edge + vertex neighbors (12 per triangle)
        if (isTriangleCellPointingDown) {
            if (hasTriangleCellXOffset) { // (2, 2)
                return List.of(
                        new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 3) edge
                        new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.N, CellConnectionType.VERTEX), // (1, 2) vertex
                        new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.N, CellConnectionType.VERTEX), // (1, 1) vertex
                        new CellNeighborConnection(new GridOffset(0, -2), CompassDirection.N, CellConnectionType.VERTEX), // (2, 0) vertex
                        new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 1) edge
                        new CellNeighborConnection(new GridOffset(1, -2), CompassDirection.N, CellConnectionType.VERTEX), // (3, 0) vertex
                        new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.N, CellConnectionType.VERTEX), // (3, 1) vertex
                        new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.N, CellConnectionType.VERTEX), // (3, 2) vertex
                        new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.N, CellConnectionType.VERTEX), // (3, 3) edge
                        new CellNeighborConnection(new GridOffset(1, 2), CompassDirection.N, CellConnectionType.VERTEX), // (3, 4) vertex
                        new CellNeighborConnection(new GridOffset(0, 3), CompassDirection.N, CellConnectionType.VERTEX), // (2, 5) vertex
                        new CellNeighborConnection(new GridOffset(0, 2), CompassDirection.N, CellConnectionType.VERTEX) // (2, 4) vertex
                );
            } else { // (2, 4)
                return List.of(
                        new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.N, CellConnectionType.VERTEX), // (1, 5) edge
                        new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.N, CellConnectionType.VERTEX), // (1, 4) vertex
                        new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.N, CellConnectionType.VERTEX), // (1, 3) vertex
                        new CellNeighborConnection(new GridOffset(-1, -2), CompassDirection.N, CellConnectionType.VERTEX), // (1, 2) vertex
                        new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 3) edge
                        new CellNeighborConnection(new GridOffset(0, -2), CompassDirection.N, CellConnectionType.VERTEX), // (2, 2) vertex
                        new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.N, CellConnectionType.VERTEX), // (3, 3) vertex
                        new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.N, CellConnectionType.VERTEX), // (3, 4) vertex
                        new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 5) edge
                        new CellNeighborConnection(new GridOffset(0, 2), CompassDirection.N, CellConnectionType.VERTEX), // (2, 6) vertex
                        new CellNeighborConnection(new GridOffset(0, 3), CompassDirection.N, CellConnectionType.VERTEX), // (2, 7) vertex
                        new CellNeighborConnection(new GridOffset(-1, 2), CompassDirection.N, CellConnectionType.VERTEX) // (1, 6) vertex
                );
            }
        } else { // pointing up
            if (hasTriangleCellXOffset) { // (2, 5)
                return List.of(
                        new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 4) edge
                        new CellNeighborConnection(new GridOffset(0, -2), CompassDirection.N, CellConnectionType.VERTEX), // (2, 3) vertex
                        new CellNeighborConnection(new GridOffset(0, -3), CompassDirection.N, CellConnectionType.VERTEX), // (2, 2) vertex
                        new CellNeighborConnection(new GridOffset(1, -2), CompassDirection.N, CellConnectionType.VERTEX), // (3, 3) vertex
                        new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.N, CellConnectionType.VERTEX), // (3, 4) edge
                        new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.N, CellConnectionType.VERTEX), // (3, 5) vertex
                        new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.N, CellConnectionType.VERTEX), // (3, 6) vertex
                        new CellNeighborConnection(new GridOffset(1, 2), CompassDirection.N, CellConnectionType.VERTEX), // (3, 7) vertex
                        new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 6) edge
                        new CellNeighborConnection(new GridOffset(0, 2), CompassDirection.N, CellConnectionType.VERTEX), // (2, 7) vertex
                        new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.N, CellConnectionType.VERTEX), // (1, 6) vertex
                        new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.N, CellConnectionType.VERTEX) // (1, 5) vertex
                );
            } else { // (2, 3)
                return List.of(
                        new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.N, CellConnectionType.VERTEX), // (1, 2) edge
                        new CellNeighborConnection(new GridOffset(-1, -2), CompassDirection.N, CellConnectionType.VERTEX), // (1, 1) vertex
                        new CellNeighborConnection(new GridOffset(0, -3), CompassDirection.N, CellConnectionType.VERTEX), // (2, 0) vertex
                        new CellNeighborConnection(new GridOffset(0, -2), CompassDirection.N, CellConnectionType.VERTEX), // (2, 1) vertex
                        new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 2) edge
                        new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.N, CellConnectionType.VERTEX), // (3, 3) vertex
                        new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.N, CellConnectionType.VERTEX), // (3, 4) vertex
                        new CellNeighborConnection(new GridOffset(0, 2), CompassDirection.N, CellConnectionType.VERTEX), // (2, 5) vertex
                        new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.N, CellConnectionType.VERTEX), // (2, 4) edge
                        new CellNeighborConnection(new GridOffset(-1, 2), CompassDirection.N, CellConnectionType.VERTEX), // (1, 5) vertex
                        new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.N, CellConnectionType.VERTEX), // (1, 4) vertex
                        new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.N, CellConnectionType.VERTEX) // (1, 3) vertex
                );
            }
        }
    }

    static List<CellNeighborConnection> computeSquareCellNeighborConnections(NeighborhoodMode neighborhoodMode) {
        return switch (neighborhoodMode) {
            case EDGES_ONLY -> List.of(
                    new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.E, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.W, CellConnectionType.EDGE)
            );
            case EDGES_AND_VERTICES -> List.of(
                    new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.NE, CellConnectionType.VERTEX),
                    new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.E, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.SE, CellConnectionType.VERTEX),
                    new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.SW, CellConnectionType.VERTEX),
                    new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.W, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.NW, CellConnectionType.VERTEX)
            );
        };
    }

    static List<CellNeighborConnection> computeHexagonCellNeighborConnections(boolean hasHexagonCellYOffset) {
        if (hasHexagonCellYOffset) {
            return List.of(
                    new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.NE, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.SE, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.SW, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.NW, CellConnectionType.EDGE)
            );
        } else {
            return List.of(
                    new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.NE, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.SE, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.SW, CellConnectionType.EDGE),
                    new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.NW, CellConnectionType.EDGE)
            );
        }
    }

    /**
     * Internal record representing a single neighbor connection for calculation and caching purposes.
     * <p>
     * Encapsulates the relative offset, compass direction, and connection type (edge or vertex)
     * between a cell and one of its neighbors.
     *
     * @param offset         the relative offset to the neighbor cell
     * @param direction      the compass direction to the neighbor
     * @param connectionType the type of connection (edge or vertex)
     */
    record CellNeighborConnection(GridOffset offset, CompassDirection direction, CellConnectionType connectionType) {}

}
