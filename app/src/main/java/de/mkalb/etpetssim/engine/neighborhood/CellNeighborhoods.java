package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.*;

import java.util.*;
import java.util.stream.*;

/**
 * Provides static utility methods for determining the arrangement and neighbor relationships of cells
 * in a two-dimensional grid, based on cell shape, neighborhood mode, and cell position.
 * <p>
 * This class centralizes the calculation of neighbor directions for different grid types
 * (triangular, square, hexagonal) and supports both edge-only and edge-and-vertex neighborhood modes.
 *
 * @see de.mkalb.etpetssim.engine.CellShape
 * @see NeighborhoodMode
 * @see de.mkalb.etpetssim.engine.neighborhood.CellNeighbor
 * @see de.mkalb.etpetssim.engine.GridOffset
 * @see de.mkalb.etpetssim.engine.GridCoordinate
 */
@SuppressWarnings("MagicNumber")
public final class CellNeighborhoods {

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
    private CellNeighborhoods() {
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
            case HEXAGON -> 6; // Only edge neighbors exist for hexagons
            case TRIANGLE -> (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) ? 3 : 12;
        };
    }

    /**
     * Returns the maximum possible number of neighbors for a cell with the given shape,
     * neighborhood mode, and radius, ignoring grid boundaries.
     * <p>
     * The result is a theoretical upper bound for the number of cells within the given radius,
     * not the actual count for edge or corner cells in a finite grid.
     * <p>
     * If {@code radius <= 0}, this method returns {@code 0}.
     *
     * @param cellShape        the geometric shape of the cell (TRIANGLE, SQUARE, HEXAGON)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param radius           the neighborhood radius (non-negative)
     * @return the maximum possible number of neighbors within the given radius (or {@code 0} if {@code radius <= 0})
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
        return cellNeighborsIgnoringEdgeBehavior(from, neighborhoodMode, cellShape)
                .anyMatch(neighbor -> neighbor.neighborCoordinate().equals(to));
    }

    /**
     * Determines the {@link EdgeBehaviorAction} for a given coordinate in a grid structure.
     * <p>
     * Returns {@code VALID} if the coordinate is within bounds. If out of bounds,
     * applies the configured edge behavior for each axis (X and Y) and prioritizes actions:
     * <ol>
     *   <li>{@code BLOCKED} if either axis uses {@link de.mkalb.etpetssim.engine.EdgeBehavior#BLOCK}</li>
     *   <li>{@code ABSORBED} if either axis uses {@link de.mkalb.etpetssim.engine.EdgeBehavior#ABSORB}</li>
     *   <li>{@code REFLECTED} if either axis uses {@link de.mkalb.etpetssim.engine.EdgeBehavior#REFLECT}</li>
     *   <li>{@code WRAPPED} if either axis uses {@link de.mkalb.etpetssim.engine.EdgeBehavior#WRAP}</li>
     * </ol>
     *
     * @param coordinate the grid coordinate to check
     * @param structure the grid structure defining bounds and edge behaviors
     * @return the resulting {@link EdgeBehaviorAction} for the coordinate
     * @throws IllegalArgumentException if an unknown edge behavior is encountered or if WRAP and REFLECT are combined
     */
    public static EdgeBehaviorAction edgeActionForCoordinate(GridCoordinate coordinate, GridStructure structure) {
        if (structure.isCoordinateValid(coordinate)) {
            return EdgeBehaviorAction.VALID;
        }
        GridCoordinate min = structure.minCoordinateInclusive();
        GridCoordinate max = structure.maxCoordinateExclusive();

        boolean outX = (coordinate.x() < min.x()) || (coordinate.x() >= max.x());
        boolean outY = (coordinate.y() < min.y()) || (coordinate.y() >= max.y());

        if (!outX && !outY) {
            // This should not happen; fall back to VALID.
            return EdgeBehaviorAction.VALID;
        }

        EdgeBehavior edgeBehaviorX = structure.edgeBehaviorX();
        EdgeBehavior edgeBehaviorY = structure.edgeBehaviorY();

        // Check for incompatible edge behaviors
        if (((edgeBehaviorX == EdgeBehavior.WRAP) && (edgeBehaviorY == EdgeBehavior.REFLECT))
                || ((edgeBehaviorX == EdgeBehavior.REFLECT) && (edgeBehaviorY == EdgeBehavior.WRAP))) {
            throw new IllegalArgumentException("WRAP and REFLECT edge behaviors cannot be combined for X and Y edges in a GridEdgeBehavior.");
        }

        // Prioritize in order: BLOCK, ABSORB, REFLECT, WRAP.
        if ((outX && (edgeBehaviorX == EdgeBehavior.BLOCK)) || (outY && (edgeBehaviorY == EdgeBehavior.BLOCK))) {
            return EdgeBehaviorAction.BLOCKED;
        }
        if ((outX && (edgeBehaviorX == EdgeBehavior.ABSORB)) || (outY && (edgeBehaviorY == EdgeBehavior.ABSORB))) {
            return EdgeBehaviorAction.ABSORBED;
        }
        if ((outX && (edgeBehaviorX == EdgeBehavior.REFLECT)) || (outY && (edgeBehaviorY == EdgeBehavior.REFLECT))) {
            return EdgeBehaviorAction.REFLECTED;
        }
        if ((outX && (edgeBehaviorX == EdgeBehavior.WRAP)) || (outY && (edgeBehaviorY == EdgeBehavior.WRAP))) {
            return EdgeBehaviorAction.WRAPPED;
        }

        throw new IllegalArgumentException("Unknown EdgeBehavior for out-of-bounds coordinate: " +
                "edgeBehaviorX=" + edgeBehaviorX + ", edgeBehaviorY=" + edgeBehaviorY);
    }

    /**
     * Determines whether the given coordinate is valid within the grid after applying edge behavior.
     * <p>
     * A coordinate is considered valid if the edge behavior action is {@link EdgeBehaviorAction#VALID},
     * {@link EdgeBehaviorAction#WRAPPED}, or {@link EdgeBehaviorAction#REFLECTED}.
     * Coordinates resulting in {@link EdgeBehaviorAction#BLOCKED} or {@link EdgeBehaviorAction#ABSORBED}
     * are not considered valid for further simulation steps.
     *
     * @param coordinate the grid coordinate to check
     * @param structure  the grid structure defining size and edge behavior
     * @return {@code true} if the coordinate is valid after edge behavior, {@code false} otherwise
     */
    public static boolean isValidEdgeCoordinate(GridCoordinate coordinate, GridStructure structure) {
        EdgeBehaviorAction action = edgeActionForCoordinate(coordinate, structure);
        return (action == EdgeBehaviorAction.VALID)
                || (action == EdgeBehaviorAction.WRAPPED)
                || (action == EdgeBehaviorAction.REFLECTED);
    }

    /**
     * Applies the configured edge behavior to a given grid coordinate within the specified grid structure.
     * <p>
     * Depending on the edge behavior and whether the coordinate is out of bounds, this method
     * returns a mapped coordinate and the resulting {@link EdgeBehaviorAction}:
     * <ul>
     *   <li>{@code VALID}, {@code BLOCKED}, {@code ABSORBED}: returns the original coordinate unchanged.</li>
     *   <li>{@code WRAPPED}: wraps the coordinate to the opposite edge using modular arithmetic.</li>
     *   <li>{@code REFLECTED}: reflects the coordinate at the grid boundary, simulating a bounce effect.</li>
     * </ul>
     *
     * @param original the original grid coordinate to process
     * @param structure the grid structure defining bounds and edge behaviors
     * @return an {@link EdgeBehaviorResult} containing the original coordinate, the mapped coordinate, and the action taken
     */
    public static EdgeBehaviorResult applyEdgeBehaviorToCoordinate(GridCoordinate original, GridStructure structure) {
        EdgeBehaviorAction action = edgeActionForCoordinate(original, structure);
        return new EdgeBehaviorResult(
                original,
                switch (action) {
                    case VALID, BLOCKED, ABSORBED -> original;
                    case WRAPPED -> {
                        GridCoordinate min = structure.minCoordinateInclusive();
                        int width = structure.size().width();
                        int height = structure.size().height();

                        int wrappedX = ((((original.x() - min.x()) % width) + width) % width) + min.x();
                        int wrappedY = ((((original.y() - min.y()) % height) + height) % height) + min.y();

                        yield new GridCoordinate(wrappedX, wrappedY);
                    }
                    case REFLECTED -> {
                        GridCoordinate min = structure.minCoordinateInclusive();
                        GridCoordinate max = structure.maxCoordinateExclusive();
                        int x = original.x();
                        int y = original.y();

                        // Reflect X if it is out of bounds.
                        if (x < min.x()) {
                            x = min.x() + (min.x() - x - 1);
                        } else if (x >= max.x()) {
                            x = max.x() - ((x - max.x()) + 1);
                        }

                        // Reflect Y if it is out of bounds.
                        if (y < min.y()) {
                            y = min.y() + (min.y() - y - 1);
                        } else if (y >= max.y()) {
                            y = max.y() - ((y - max.y()) + 1);
                        }

                        yield new GridCoordinate(x, y);
                    }
                },
                action);
    }

    /**
     * Returns a collection of {@link EdgeBehaviorResult} for all theoretical neighbors of a given cell,
     * applying the grid's edge behavior to each neighbor coordinate and ensuring only one result per mapped coordinate,
     * always preferring results with {@link EdgeBehaviorAction#VALID} if present.
     * <p>
     * This method first checks if the provided {@code startCoordinate} is within the valid bounds of the given
     * {@link GridStructure}. If not, an empty collection is returned. Otherwise, it computes all theoretical neighbors
     * (ignoring boundaries), applies edge behavior to each, and collects the results such that for each mapped coordinate,
     * only the best result (preferably with action {@code VALID}) is included.
     * <p>
     * The resulting collection contains {@link EdgeBehaviorResult} objects describing the mapping and action for each unique
     * mapped neighbor coordinate.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param structure        the grid structure defining size and topology
     * @return an unmodifiable collection of {@link EdgeBehaviorResult} for all unique mapped neighbor coordinates (with edge behavior applied)
     */
    public static Collection<EdgeBehaviorResult> neighborEdgeResults(
            GridCoordinate startCoordinate,
            NeighborhoodMode neighborhoodMode,
            GridStructure structure) {
        if (!structure.isCoordinateValid(startCoordinate)) {
            return Collections.emptyList();
        }
        Map<GridCoordinate, EdgeBehaviorResult> bestResults = new HashMap<>();
        for (CellNeighbor neighbor : cellNeighborsIgnoringEdgeBehavior(startCoordinate, neighborhoodMode, structure.cellShape()).toList()) {
            EdgeBehaviorResult result = applyEdgeBehaviorToCoordinate(neighbor.neighborCoordinate(), structure);
            bestResults.merge(
                    result.mapped(),
                    result,
                    (existing, candidate) -> (existing.action() == EdgeBehaviorAction.VALID) ? existing :
                            ((candidate.action() == EdgeBehaviorAction.VALID) ? candidate : existing)
            );
        }
        return Collections.unmodifiableCollection(bestResults.values());
    }

    /**
     * Returns a stream of all theoretical neighbors for a given cell, based on the specified
     * neighborhood mode and cell shape, <b>ignoring grid boundaries and edge behavior</b>.
     * <p>
     * The returned stream contains {@link CellNeighbor} objects, each describing the direction,
     * connection type (edge or vertex), and coordinate of a neighboring cell relative to the
     * given start coordinate. The calculation assumes an infinite grid and does not check
     * whether the start or neighbor coordinates are valid within any grid structure.
     * <p>
     * <b>Note:</b> This method does <b>not</b> apply any edge behavior or boundary checks.
     * It is intended for use cases where only the geometric neighbor relationships are needed.
     * The caller can collect the stream into a list if needed.
     *
     * @param startCoordinate   the coordinate of the cell whose neighbors are to be determined (not checked for grid validity)
     * @param neighborhoodMode  the neighborhood mode (edges only or edges and vertices)
     * @param cellShape         the shape of the cell (triangle, square, hexagon)
     * @return a stream of {@link CellNeighbor} objects representing all theoretical neighbors of the cell (ignoring grid boundaries and edge behavior)
     */
    public static Stream<CellNeighbor> cellNeighborsIgnoringEdgeBehavior(GridCoordinate startCoordinate,
                                                                         NeighborhoodMode neighborhoodMode,
                                                                         CellShape cellShape) {
        List<CellNeighborConnection> cellNeighborConnections =
                getCellNeighborConnections(startCoordinate, neighborhoodMode, cellShape);

        return cellNeighborConnections.stream()
                                      .map(neighborConnection -> new CellNeighbor(
                                              startCoordinate,
                                              neighborConnection.direction(),
                                              neighborConnection.connectionType(),
                                              startCoordinate.offset(neighborConnection.offset())
                                      ));
    }

    /**
     * Returns an {@link Optional} containing a {@link CellNeighborWithEdgeBehavior} for the neighbor of a cell
     * in the specified compass direction, applying the grid's edge behavior to the neighbor coordinate.
     * <p>
     * This method determines the theoretical neighbor in the given direction (based on cell shape and neighborhood mode),
     * applies the grid's edge behavior to the neighbor coordinate, and returns a record describing the relationship
     * and edge behavior outcome. If no neighbor exists in the specified direction, an empty {@link Optional} is returned.
     *
     * @param startCoordinate   the coordinate of the cell whose neighbor is to be determined
     * @param neighborhoodMode  the neighborhood mode (edges only or edges and vertices)
     * @param direction         the compass direction of the desired neighbor
     * @param structure         the grid structure defining size, boundaries, and edge behavior
     * @return an {@link Optional} containing the {@link CellNeighborWithEdgeBehavior} for the neighbor in the given direction,
     *         or an empty {@link Optional} if no such neighbor exists
     */
    public static Optional<CellNeighborWithEdgeBehavior> cellNeighborWithEdgeBehavior(GridCoordinate startCoordinate,
                                                                                      NeighborhoodMode neighborhoodMode,
                                                                                      CompassDirection direction,
                                                                                      GridStructure structure) {
        List<CellNeighborConnection> cellNeighborConnections =
                getCellNeighborConnections(startCoordinate, neighborhoodMode, structure.cellShape());

        for (CellNeighborConnection neighborConnection : cellNeighborConnections) {
            if (neighborConnection.direction() == direction) {
                GridCoordinate neighborCoordinate = startCoordinate.offset(neighborConnection.offset());
                EdgeBehaviorResult edgeResult = applyEdgeBehaviorToCoordinate(neighborCoordinate, structure);

                return Optional.of(new CellNeighborWithEdgeBehavior(
                        startCoordinate,
                        neighborConnection.direction(),
                        neighborConnection.connectionType(),
                        neighborCoordinate,
                        edgeResult.mapped(),
                        edgeResult.action()
                ));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a map of all direct neighbors for a given cell, applying the grid's edge behavior
     * to each neighbor coordinate and grouping the results by the mapped (post-edge-behavior) coordinate.
     * <p>
     * For each theoretical neighbor (as determined by cell shape and neighborhood mode, ignoring boundaries),
     * the grid's edge behavior is applied. The resulting {@link de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior} records
     * contain both the original (theoretical) and mapped neighbor coordinates, as well as the edge behavior action
     * (e.g., VALID, WRAPPED, BLOCKED, ABSORBED, REFLECTED).
     * <p>
     * The returned map groups all neighbor relationships by the mapped coordinate, allowing the simulation
     * to distinguish between different edge behavior outcomes (including absorbed or blocked neighbors).
     * <b>Note:</b> This method does <b>not</b> filter out any edge behavior actions; all are included.
     *
     * @param startCoordinate   the coordinate of the cell whose neighbors are to be determined (must be valid in the grid)
     * @param neighborhoodMode  the neighborhood mode (edges only or edges and vertices)
     * @param structure         the grid structure defining size, boundaries, and edge behavior
     * @return a map from mapped neighbor coordinates to lists of {@link de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior} records,
     *         each describing the relationship and edge behavior outcome for that neighbor
     */
    public static Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> cellNeighborsWithEdgeBehavior(
            GridCoordinate startCoordinate,
            NeighborhoodMode neighborhoodMode,
            GridStructure structure) {
        if (!structure.isCoordinateValid(startCoordinate)) {
            return Collections.emptyMap();
        }
        return cellNeighborsIgnoringEdgeBehavior(startCoordinate, neighborhoodMode, structure.cellShape())
                .map(neighbor -> {
                    EdgeBehaviorResult result = applyEdgeBehaviorToCoordinate(neighbor.neighborCoordinate(), structure);
                    return CellNeighborWithEdgeBehavior.of(neighbor, result);
                })
                .collect(Collectors.groupingBy(CellNeighborWithEdgeBehavior::mappedNeighborCoordinate));
    }

    /**
     * Returns a set of all neighbor coordinates within the given radius for a cell,
     * based on the specified neighborhood mode and cell shape, ignoring grid boundaries.
     * Uses breadth-first search to avoid redundant visits and ensure efficiency.
     * <p>
     * If {@code radius <= 0}, this method returns an empty set. If {@code radius > MAX_RADIUS}, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param startCoordinate   the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode  the neighborhood mode (edges only or edges and vertices)
     * @param cellShape         the shape of the cell (triangle, square, hexagon)
     * @param radius            the neighborhood radius (non-negative) and less than or equal to {@link #MAX_RADIUS}
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

        int maxNeighbors = maxNeighborCount(cellShape, neighborhoodMode, radius);
        Set<GridCoordinate> visited = HashSet.newHashSet(1 + maxNeighbors); // startCoordinate plus maxNeighbors
        Set<GridCoordinate> result = HashSet.newHashSet(maxNeighbors);
        Queue<GridCoordinate> queue = new ArrayDeque<>();

        visited.add(startCoordinate);
        queue.add(startCoordinate);

        int currentRadius = 0;

        while (!queue.isEmpty() && (currentRadius < radius)) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                GridCoordinate current = Objects.requireNonNull(queue.poll());

                for (CellNeighbor neighbor : cellNeighborsIgnoringEdgeBehavior(current, neighborhoodMode, cellShape).toList()) {
                    GridCoordinate neighborCoordinate = neighbor.neighborCoordinate();
                    if (visited.add(neighborCoordinate)) { // Add only if not visited.
                        result.add(neighborCoordinate);
                        queue.add(neighborCoordinate);
                    }
                }
            }
            currentRadius++;
        }

        return result;
    }

    /**
     * Returns the set of compass directions for all theoretical direct neighbors of the cell at
     * the given {@link GridCoordinate}, according to the specified {@link NeighborhoodMode} and
     * {@link CellShape}.
     *
     * <p>This method extracts the {@link CompassDirection} values from the internal neighbor
     * connection list (via {@link #getCellNeighborConnections(GridCoordinate, NeighborhoodMode, CellShape)})
     * and therefore does not perform any boundary checks or apply edge behavior. The returned
     * directions represent neighbors in an infinite grid. Note that {@code startCoordinate} is
     * relevant for triangle parity and hexagon Y-offsets but is effectively ignored for square cells
     * (consistent with the cache key strategy).
     *
     * @param startCoordinate  the coordinate of the cell whose neighbor directions are requested (not validated)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @return a modifiable {@link java.util.EnumSet} of {@link CompassDirection} containing the
     *         directions of all theoretical direct neighbors for the given configuration
     */
    public static Set<CompassDirection> cellNeighborDirections(GridCoordinate startCoordinate,
                                                               NeighborhoodMode neighborhoodMode,
                                                               CellShape cellShape) {
        return getCellNeighborConnections(startCoordinate, neighborhoodMode, cellShape)
                .stream()
                .map(CellNeighborConnection::direction)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(CompassDirection.class)));
    }

    static List<CellNeighborConnection> getCellNeighborConnections(GridCoordinate startCoordinate,
                                                                   NeighborhoodMode neighborhoodMode,
                                                                   CellShape cellShape) {
        return CACHE.computeIfAbsent(
                generateCacheKey(startCoordinate, neighborhoodMode, cellShape),
                _ -> computeCellNeighborConnections(startCoordinate, neighborhoodMode, cellShape));
    }

    static String generateCacheKey(GridCoordinate startCoordinate,
                                   NeighborhoodMode neighborhoodMode,
                                   CellShape cellShape) {
        return switch (cellShape) {
            case TRIANGLE -> cellShape + "::" + neighborhoodMode + "::" + startCoordinate.isTriangleCellPointingDown();
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
                    startCoordinate.isTriangleCellPointingDown());
            case SQUARE -> computeSquareCellNeighborConnections(neighborhoodMode);
            case HEXAGON -> computeHexagonCellNeighborConnections(startCoordinate.hasHexagonCellYOffset());
        };
    }

    static List<CellNeighborConnection> computeTriangleCellNeighborConnections(
            NeighborhoodMode neighborhoodMode,
            boolean isTriangleCellPointingDown) {

        // Directions for edge neighbors (3 per triangle)
        return switch (neighborhoodMode) {
            case NeighborhoodMode.EDGES_ONLY -> {
                if (isTriangleCellPointingDown) { // example: (2, 2)
                    yield List.of(
                            new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE), // neighbor (2, 1): edge connection
                            new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.SE, CellConnectionType.EDGE), // neighbor (3, 2): edge connection
                            new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.SW, CellConnectionType.EDGE) // neighbor (1, 2): edge connection
                    );
                } else { // example: (3, 2)
                    yield List.of(
                            new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.NE, CellConnectionType.EDGE), // neighbor (4, 2): edge connection
                            new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE), // neighbor (3, 3): edge connection
                            new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.NW, CellConnectionType.EDGE) // neighbor (2, 2): edge connection
                    );
                }
            }
            case NeighborhoodMode.EDGES_AND_VERTICES -> {
                if (isTriangleCellPointingDown) { // example: (2, 2)
                    yield List.of(
                            new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.EDGE), // neighbor (2, 1): edge connection
                            new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.NNE, CellConnectionType.VERTEX), // neighbor (3, 1): vertex connection
                            new CellNeighborConnection(new GridOffset(2, -1), CompassDirection.NE, CellConnectionType.VERTEX), // neighbor (4, 1): vertex connection
                            new CellNeighborConnection(new GridOffset(2, 0), CompassDirection.E, CellConnectionType.VERTEX), // neighbor (4, 2): vertex connection
                            new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.SE, CellConnectionType.EDGE), // neighbor (3, 2): edge connection
                            new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.SSE, CellConnectionType.VERTEX), // neighbor (3, 3): vertex connection
                            new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.VERTEX), // neighbor (2, 3): vertex connection
                            new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.SSW, CellConnectionType.VERTEX), // neighbor (1, 3): vertex connection
                            new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.SW, CellConnectionType.EDGE), // neighbor (1, 2): edge connection
                            new CellNeighborConnection(new GridOffset(-2, 0), CompassDirection.W, CellConnectionType.VERTEX), // neighbor (0, 2): vertex connection
                            new CellNeighborConnection(new GridOffset(-2, -1), CompassDirection.NW, CellConnectionType.VERTEX), // neighbor (0, 1): vertex connection
                            new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.NNW, CellConnectionType.VERTEX) // neighbor (1, 1): vertex connection
                    );
                } else { // example: (3, 2)
                    yield List.of(
                            new CellNeighborConnection(new GridOffset(0, -1), CompassDirection.N, CellConnectionType.VERTEX), // neighbor (3, 1): vertex connection
                            new CellNeighborConnection(new GridOffset(1, -1), CompassDirection.NNE, CellConnectionType.VERTEX), // neighbor (4, 1): vertex connection
                            new CellNeighborConnection(new GridOffset(1, 0), CompassDirection.NE, CellConnectionType.EDGE), // neighbor (4, 2): edge connection
                            new CellNeighborConnection(new GridOffset(2, 0), CompassDirection.E, CellConnectionType.VERTEX), // neighbor (5, 2): vertex connection
                            new CellNeighborConnection(new GridOffset(2, 1), CompassDirection.SE, CellConnectionType.VERTEX), // neighbor (5, 3): vertex connection
                            new CellNeighborConnection(new GridOffset(1, 1), CompassDirection.SSE, CellConnectionType.VERTEX), // neighbor (4, 3): vertex connection
                            new CellNeighborConnection(new GridOffset(0, 1), CompassDirection.S, CellConnectionType.EDGE), // neighbor (3, 3): edge connection
                            new CellNeighborConnection(new GridOffset(-1, 1), CompassDirection.SSW, CellConnectionType.VERTEX), // neighbor (2, 3): vertex connection
                            new CellNeighborConnection(new GridOffset(-2, 1), CompassDirection.SW, CellConnectionType.VERTEX), // neighbor (1, 3): vertex connection
                            new CellNeighborConnection(new GridOffset(-2, 0), CompassDirection.W, CellConnectionType.VERTEX), // neighbor (1, 2): vertex connection
                            new CellNeighborConnection(new GridOffset(-1, 0), CompassDirection.NW, CellConnectionType.EDGE), // neighbor (2, 2): edge connection
                            new CellNeighborConnection(new GridOffset(-1, -1), CompassDirection.NNW, CellConnectionType.VERTEX) // neighbor (2, 1): vertex connection
                    );
                }
            }
        };
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
