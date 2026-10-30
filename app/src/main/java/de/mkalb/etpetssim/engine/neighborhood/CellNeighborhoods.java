package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.EdgeBehavior;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Computes the neighbors of grid cells by cell shape, neighborhood mode, and cell position, and applies the edge
 * behavior of a grid structure to neighbor coordinates.
 *
 * <p>Supports triangle, square, and hexagon cells and both edge-only and edge-and-vertex neighborhood modes.
 *
 * <p>The parameters follow the order start coordinate (or coordinates), neighborhood mode, cell shape or grid
 * structure, further parameters (e.g., a direction or a radius).
 */
@SuppressWarnings("MagicNumber")
public final class CellNeighborhoods {

    /**
     * Maximum radius of {@link #cellsByRadiusRings} and {@link #neighborCoordinatesWithinRadius}; both reject a
     * larger radius with an {@link IllegalArgumentException} to bound computation and memory usage.
     */
    public static final int MAX_RADIUS = 100;

    /**
     * Direction ring of square cells with {@link NeighborhoodMode#EDGES_ONLY}: N, E, S, W.
     */
    public static final List<CompassDirection> SQUARE_EDGES_DIRECTION_RING =
            List.of(CompassDirection.N, CompassDirection.E, CompassDirection.S, CompassDirection.W);

    /**
     * Direction ring of square cells with {@link NeighborhoodMode#EDGES_AND_VERTICES}: N, NE, E, SE, S, SW, W, NW.
     */
    public static final List<CompassDirection> SQUARE_EDGES_AND_VERTICES_DIRECTION_RING =
            List.of(
                    CompassDirection.N, CompassDirection.NE, CompassDirection.E, CompassDirection.SE,
                    CompassDirection.S, CompassDirection.SW, CompassDirection.W, CompassDirection.NW
            );

    /**
     * Direction ring of hexagon cells in both neighborhood modes: N, NE, SE, S, SW, NW.
     */
    public static final List<CompassDirection> HEXAGON_DIRECTION_RING =
            List.of(
                    CompassDirection.N, CompassDirection.NE, CompassDirection.SE,
                    CompassDirection.S, CompassDirection.SW, CompassDirection.NW
            );

    private static final int INITIAL_CACHE_CAPACITY = 32;

    private static final Map<String, List<CellNeighborConnection>> CACHE =
            new ConcurrentHashMap<>(INITIAL_CACHE_CAPACITY);

    private CellNeighborhoods() {
    }

    /**
     * Returns the maximum possible number of direct neighbors for a cell
     * with the given shape and neighborhood mode, ignoring grid boundaries.
     *
     * <p>This value represents the theoretical upper bound for a cell in an infinite grid.
     * For example, a square cell with {@link NeighborhoodMode#EDGES_ONLY} has 4 possible neighbors,
     * while with {@link NeighborhoodMode#EDGES_AND_VERTICES} it has 8.
     *
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @return the maximum possible number of direct neighbors for the specified configuration
     */
    public static int maxNeighborCount(NeighborhoodMode neighborhoodMode, CellShape cellShape) {
        return switch (cellShape) {
            case SQUARE -> (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) ? 4 : 8;
            case HEXAGON -> 6; // Only edge neighbors exist for hexagons
            case TRIANGLE -> (neighborhoodMode == NeighborhoodMode.EDGES_ONLY) ? 3 : 12;
        };
    }

    /**
     * Returns the maximum possible number of neighbors for a cell with the given shape,
     * neighborhood mode, and radius, ignoring grid boundaries.
     *
     * <p>The result is used as an upper bound for sizing internal collections and does not
     * represent an exact neighbor count for all configurations.
     * For {@code radius == 1}, the returned value equals the direct neighbor count from
     * {@link #maxNeighborCount(NeighborhoodMode, CellShape)}.
     * For larger radii, this method returns {@code 1 + ((degree * radius * (radius + 1)) / 2)} with the direct
     * neighbor count as {@code degree}, a conservative value that also covers the start cell.
     *
     * <p>If {@code radius <= 0}, this method returns {@code 0}.
     *
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the geometric shape of the cell (TRIANGLE, SQUARE, HEXAGON)
     * @param radius           the neighborhood radius
     * @return the maximum possible number of neighbors within the given radius (or {@code 0} if {@code radius <= 0})
     */
    public static int maxNeighborCount(NeighborhoodMode neighborhoodMode, CellShape cellShape, int radius) {
        if (radius <= 0) {
            return 0;
        }
        int degree = maxNeighborCount(neighborhoodMode, cellShape);
        if (radius == 1) {
            return degree;
        }
        return 1 + ((degree * radius * (radius + 1)) / 2);
    }

    /**
     * Returns the clockwise direction ring of the direct neighbors of a cell with the given shape and neighborhood
     * mode, for use with {@link CompassDirection#distanceOnRing(CompassDirection, CompassDirection, List)}.
     *
     * <p>The ring lists the directions in the order of {@link #cellNeighborsIgnoringEdgeBehavior}. Hexagon cells have
     * the same ring in both neighborhood modes; triangle cells have no ring, because their neighbor directions depend
     * on the orientation of the cell.
     *
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @return {@link #SQUARE_EDGES_DIRECTION_RING}, {@link #SQUARE_EDGES_AND_VERTICES_DIRECTION_RING}, or
     * {@link #HEXAGON_DIRECTION_RING}
     * @throws IllegalArgumentException if the cell shape is {@link CellShape#TRIANGLE}
     */
    public static List<CompassDirection> directionRing(NeighborhoodMode neighborhoodMode, CellShape cellShape) {
        return switch (cellShape) {
            case TRIANGLE -> throw new IllegalArgumentException("cellShape must be SQUARE or HEXAGON, but was: "
                    + cellShape);
            case SQUARE -> (neighborhoodMode == NeighborhoodMode.EDGES_ONLY)
                    ? SQUARE_EDGES_DIRECTION_RING
                    : SQUARE_EDGES_AND_VERTICES_DIRECTION_RING;
            case HEXAGON -> HEXAGON_DIRECTION_RING;
        };
    }

    /**
     * Checks if two coordinates are valid neighbors in the grid for the given cell shape and neighborhood mode,
     * ignoring grid boundaries.
     *
     * <p>This method does not check whether the provided {@code from} and {@code to} coordinates are within the
     * valid grid area. It only determines if {@code to} is a direct neighbor of {@code from} according to the
     * specified configuration.
     *
     * @param from             the coordinate of the source cell (not checked for grid validity)
     * @param to               the coordinate of the potential neighbor cell (not checked for grid validity)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @return {@code true} if {@code to} is a neighbor of {@code from} (ignoring grid boundaries), {@code false}
     * otherwise
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
     * Determines whether the given coordinate is valid within the grid after applying edge behavior.
     *
     * <p>A coordinate is valid if its edge behavior action lies within the grid
     * ({@link EdgeBehaviorAction#isWithinGrid()}): {@link EdgeBehaviorAction#VALID} or
     * {@link EdgeBehaviorAction#WRAPPED}. Coordinates resulting in {@link EdgeBehaviorAction#BLOCKED} or
     * {@link EdgeBehaviorAction#ABSORBED} are not valid.
     *
     * @param coordinate the grid coordinate to check
     * @param structure  the grid structure defining size and edge behavior
     * @return {@code true} if the coordinate is valid after edge behavior, {@code false} otherwise
     */
    public static boolean isValidWithEdgeBehavior(GridCoordinate coordinate, GridStructure structure) {
        return edgeBehaviorActionForCoordinate(coordinate, structure).isWithinGrid();
    }

    /**
     * Applies the configured edge behavior to a given grid coordinate within the specified grid structure.
     *
     * <p>Depending on the edge behavior and whether the coordinate is out of bounds, this method
     * returns a mapped coordinate and the resulting {@link EdgeBehaviorAction}:
     * <ul>
     *   <li>{@code VALID}, {@code BLOCKED}, {@code ABSORBED}: returns the original coordinate unchanged.</li>
     *   <li>{@code WRAPPED}: wraps the coordinate to the opposite edge using modular arithmetic.</li>
     * </ul>
     *
     * @param original  the original grid coordinate to process
     * @param structure the grid structure defining bounds and edge behaviors
     * @return an {@link EdgeBehaviorResult} containing the original coordinate, the mapped coordinate, and the
     * action taken
     */
    public static EdgeBehaviorResult applyEdgeBehaviorToCoordinate(GridCoordinate original, GridStructure structure) {
        EdgeBehaviorAction action = edgeBehaviorActionForCoordinate(original, structure);
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
                },
                action);
    }

    /**
     * Returns a collection of {@link EdgeBehaviorResult} for all theoretical neighbors of a given cell,
     * applying the grid's edge behavior to each neighbor coordinate and ensuring only one result per mapped
     * coordinate, always preferring results with {@link EdgeBehaviorAction#VALID} if present.
     *
     * <p>This method first checks if the provided {@code startCoordinate} is within the valid bounds of the given
     * {@link GridStructure}. If not, an empty collection is returned. Otherwise, it computes all theoretical
     * neighbors (ignoring boundaries), applies edge behavior to each, and collects the results such that for each
     * mapped coordinate, only the best result (preferably with action {@code VALID}) is included.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param structure        the grid structure defining size and topology
     * @return an unmodifiable collection of {@link EdgeBehaviorResult} for all unique mapped neighbor coordinates
     */
    public static Collection<EdgeBehaviorResult> neighborEdgeBehaviorResults(
            GridCoordinate startCoordinate,
            NeighborhoodMode neighborhoodMode,
            GridStructure structure) {
        if (!structure.isCoordinateValid(startCoordinate)) {
            return Collections.emptyList();
        }
        List<CellNeighbor> neighbors =
                cellNeighborsIgnoringEdgeBehavior(startCoordinate, neighborhoodMode, structure.cellShape()).toList();
        Map<GridCoordinate, EdgeBehaviorResult> bestResults = new HashMap<>();
        for (CellNeighbor neighbor : neighbors) {
            EdgeBehaviorResult result = applyEdgeBehaviorToCoordinate(neighbor.neighborCoordinate(), structure);
            bestResults.merge(
                    result.mapped(),
                    result,
                    (existing, candidate) -> (candidate.action() == EdgeBehaviorAction.VALID) ? candidate : existing
            );
        }
        return Collections.unmodifiableCollection(bestResults.values());
    }

    /**
     * Returns all theoretical neighbors of a cell, <b>ignoring grid boundaries and edge behavior</b>.
     *
     * <p>Each {@link CellNeighbor} describes the direction, the connection type (edge or vertex), and the coordinate
     * of a neighbor relative to the start coordinate, in clockwise order. The calculation assumes an infinite grid
     * and does not check whether the start or neighbor coordinates are valid within any grid structure.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbors are to be determined (not checked for grid
     *                         validity)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @return a stream of all theoretical neighbors of the cell
     */
    public static Stream<CellNeighbor> cellNeighborsIgnoringEdgeBehavior(GridCoordinate startCoordinate,
                                                                         NeighborhoodMode neighborhoodMode,
                                                                         CellShape cellShape) {
        List<CellNeighborConnection> cellNeighborConnections =
                getOrComputeCellNeighborConnections(startCoordinate, neighborhoodMode, cellShape);

        return cellNeighborConnections.stream()
                .map(neighborConnection -> new CellNeighbor(
                        startCoordinate,
                        neighborConnection.direction(),
                        neighborConnection.connection(),
                        startCoordinate.offset(neighborConnection.offset())
                ));
    }

    /**
     * Returns an {@link Optional} containing a {@link CellNeighborWithEdgeBehavior} for the neighbor of a cell
     * in the specified compass direction, applying the grid's edge behavior to the neighbor coordinate.
     *
     * <p>Determines the theoretical neighbor in the given direction (based on cell shape and neighborhood mode),
     * applies the grid's edge behavior to the neighbor coordinate, and returns a record describing the relationship
     * and edge behavior outcome. If no neighbor exists in the specified direction, an empty {@link Optional} is
     * returned.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbor is to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param structure        the grid structure defining size, boundaries, and edge behavior
     * @param direction        the compass direction of the desired neighbor
     * @return an {@link Optional} containing the {@link CellNeighborWithEdgeBehavior} for the neighbor in the given
     * direction, or an empty {@link Optional} if no such neighbor exists
     */
    public static Optional<CellNeighborWithEdgeBehavior> cellNeighborWithEdgeBehavior(GridCoordinate startCoordinate,
                                                                                      NeighborhoodMode neighborhoodMode,
                                                                                      GridStructure structure,
                                                                                      CompassDirection direction) {
        List<CellNeighborConnection> cellNeighborConnections =
                getOrComputeCellNeighborConnections(startCoordinate, neighborhoodMode, structure.cellShape());

        for (CellNeighborConnection neighborConnection : cellNeighborConnections) {
            if (neighborConnection.direction() == direction) {
                GridCoordinate neighborCoordinate = startCoordinate.offset(neighborConnection.offset());
                EdgeBehaviorResult edgeResult = applyEdgeBehaviorToCoordinate(neighborCoordinate, structure);

                return Optional.of(new CellNeighborWithEdgeBehavior(
                        startCoordinate,
                        neighborConnection.direction(),
                        neighborConnection.connection(),
                        neighborCoordinate,
                        edgeResult.mapped(),
                        edgeResult.action()
                ));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all direct neighbors of a cell with the grid's edge behavior applied, grouped by the mapped
     * (post-edge-behavior) neighbor coordinate.
     *
     * <p>For each theoretical neighbor (see {@link #cellNeighborsIgnoringEdgeBehavior}), the grid's edge behavior is
     * applied. The resulting {@link CellNeighborWithEdgeBehavior} records contain the original and the mapped
     * neighbor coordinate and the edge behavior action. All actions are included, also {@code BLOCKED} and
     * {@code ABSORBED}, so that the simulation can distinguish between the edge behavior outcomes; several neighbors
     * can share a mapped coordinate, e.g., when a small grid wraps.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param structure        the grid structure defining size, boundaries, and edge behavior
     * @return an unmodifiable map from the mapped neighbor coordinates to their {@link CellNeighborWithEdgeBehavior}
     * records, or an empty map if the start coordinate is not valid within the grid structure
     */
    public static Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> cellNeighborsWithEdgeBehavior(
            GridCoordinate startCoordinate,
            NeighborhoodMode neighborhoodMode,
            GridStructure structure) {
        if (!structure.isCoordinateValid(startCoordinate)) {
            return Collections.emptyMap();
        }
        return cellNeighborsIgnoringEdgeBehavior(startCoordinate, neighborhoodMode, structure.cellShape())
                .map(neighbor -> CellNeighborWithEdgeBehavior.of(neighbor,
                        applyEdgeBehaviorToCoordinate(neighbor.neighborCoordinate(), structure)))
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(CellNeighborWithEdgeBehavior::mappedNeighborCoordinate),
                        Collections::unmodifiableMap));
    }

    /**
     * Returns all cells grouped by radius rings around a start coordinate, applying the grid's edge behavior
     * during each ring expansion and storing the result in unmodifiable sorted maps.
     *
     * <p>Ring {@code 0} always contains the start coordinate itself. Each following ring contains only coordinates
     * that are first reached in that exact radius, so no coordinate appears in more than one ring.
     * For each cell in ring {@code r > 0}, the returned {@link RadiusRingCell} stores the sorted set of
     * coordinates from ring {@code r - 1} through which it was reached.
     *
     * <p>Only neighbors whose edge behavior action lies within the grid ({@link EdgeBehaviorAction#isWithinGrid()})
     * are included and used for subsequent ring expansion.
     *
     * <p>The returned outer map always contains all radius keys from {@code 0} up to and including {@code radius},
     * even if some higher rings are empty.
     *
     * @param <C>              the generic cell value type created for each coordinate
     * @param startCoordinate  the coordinate of the center cell whose radius rings are to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param structure        the grid structure defining size, cell shape, and edge behavior
     * @param radius           the maximum radius ring to compute (non-negative and less than or equal to
     *                         {@link #MAX_RADIUS})
     * @param cellFactory      function creating the generic cell value for a coordinate
     * @return an unmodifiable sorted map from radius ring to an unmodifiable sorted map of coordinates and their
     * cell metadata
     * @throws IllegalArgumentException if the radius is negative, greater than {@link #MAX_RADIUS},
     *                                  or if the start coordinate is not valid within the grid structure
     */
    public static <C> SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<C>>> cellsByRadiusRings(
            GridCoordinate startCoordinate,
            NeighborhoodMode neighborhoodMode,
            GridStructure structure,
            int radius,
            Function<GridCoordinate, C> cellFactory) {
        checkRadius(radius);
        if (!structure.isCoordinateValid(startCoordinate)) {
            throw new IllegalArgumentException("startCoordinate must be valid for structure " + structure
                    + ", but was: " + startCoordinate);
        }

        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<C>>> radiusRings = new TreeMap<>();
        Set<GridCoordinate> visitedCoordinates =
                HashSet.newHashSet(1 + maxNeighborCount(neighborhoodMode, structure.cellShape(), radius));
        Set<GridCoordinate> currentRingCoordinates = HashSet.newHashSet(1);

        visitedCoordinates.add(startCoordinate);
        currentRingCoordinates.add(startCoordinate);

        SortedMap<GridCoordinate, RadiusRingCell<C>> ring0 = new TreeMap<>();
        ring0.put(startCoordinate,
                new RadiusRingCell<>(0, startCoordinate, cellFactory.apply(startCoordinate), new TreeSet<>()));
        radiusRings.put(0, Collections.unmodifiableSortedMap(ring0));

        for (int currentRadius = 1; currentRadius <= radius; currentRadius++) {
            SortedMap<GridCoordinate, SortedSet<GridCoordinate>> nextRingAccumulator = new TreeMap<>();

            for (GridCoordinate currentCoordinate : currentRingCoordinates) {
                Collection<EdgeBehaviorResult> neighborResults =
                        neighborEdgeBehaviorResults(currentCoordinate, neighborhoodMode, structure);
                for (EdgeBehaviorResult edgeBehaviorResult : neighborResults) {
                    if (!edgeBehaviorResult.action().isWithinGrid()) {
                        continue;
                    }

                    GridCoordinate mappedCoordinate = edgeBehaviorResult.mapped();
                    if (visitedCoordinates.contains(mappedCoordinate)) {
                        continue;
                    }

                    nextRingAccumulator
                            .computeIfAbsent(mappedCoordinate, _ -> new TreeSet<>())
                            .add(currentCoordinate);
                }
            }

            SortedMap<GridCoordinate, RadiusRingCell<C>> currentRing = new TreeMap<>();
            for (Map.Entry<GridCoordinate, SortedSet<GridCoordinate>> entry : nextRingAccumulator.entrySet()) {
                GridCoordinate ringCoordinate = entry.getKey();
                currentRing.put(ringCoordinate, new RadiusRingCell<>(
                        currentRadius, ringCoordinate,
                        cellFactory.apply(ringCoordinate),
                        entry.getValue()));
            }

            visitedCoordinates.addAll(currentRing.keySet());
            currentRingCoordinates = HashSet.newHashSet(currentRing.size());
            currentRingCoordinates.addAll(currentRing.keySet());

            radiusRings.put(currentRadius, Collections.unmodifiableSortedMap(currentRing));
        }

        return Collections.unmodifiableSortedMap(radiusRings);
    }

    /**
     * Returns a set of all neighbor coordinates within the given radius for a cell,
     * based on the specified neighborhood mode and cell shape, ignoring grid boundaries.
     * The start coordinate itself is not included.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbors are to be determined
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @param radius           the neighborhood radius (non-negative and less than or equal to {@link #MAX_RADIUS})
     * @return a new modifiable set of all neighbor coordinates within the given radius, empty for radius {@code 0}
     * @throws IllegalArgumentException if the radius is negative or greater than {@link #MAX_RADIUS}
     */
    public static Set<GridCoordinate> neighborCoordinatesWithinRadius(GridCoordinate startCoordinate,
                                                                      NeighborhoodMode neighborhoodMode,
                                                                      CellShape cellShape,
                                                                      int radius) {
        checkRadius(radius);

        int maxNeighbors = maxNeighborCount(neighborhoodMode, cellShape, radius);
        Set<GridCoordinate> visited = HashSet.newHashSet(1 + maxNeighbors);
        Set<GridCoordinate> result = HashSet.newHashSet(maxNeighbors);
        Queue<GridCoordinate> queue = new ArrayDeque<>();

        visited.add(startCoordinate);
        queue.add(startCoordinate);

        int currentRadius = 0;

        while (!queue.isEmpty() && (currentRadius < radius)) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                GridCoordinate current = Objects.requireNonNull(queue.poll());

                List<CellNeighbor> neighbors =
                        cellNeighborsIgnoringEdgeBehavior(current, neighborhoodMode, cellShape).toList();
                for (CellNeighbor neighbor : neighbors) {
                    GridCoordinate neighborCoordinate = neighbor.neighborCoordinate();
                    if (visited.add(neighborCoordinate)) {
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
     * <p>This method does not perform any boundary checks or apply edge behavior. The returned
     * directions represent neighbors in an infinite grid. Note that {@code startCoordinate} is
     * relevant for triangle parity and hexagon Y-offsets but is ignored for square cells.
     *
     * @param startCoordinate  the coordinate of the cell whose neighbor directions are requested (not validated)
     * @param neighborhoodMode the neighborhood mode (edges only or edges and vertices)
     * @param cellShape        the shape of the cell (triangle, square, hexagon)
     * @return a modifiable {@link EnumSet} of {@link CompassDirection} containing the
     * directions of all theoretical direct neighbors for the given configuration
     */
    public static Set<CompassDirection> cellNeighborDirections(GridCoordinate startCoordinate,
                                                               NeighborhoodMode neighborhoodMode,
                                                               CellShape cellShape) {
        return getOrComputeCellNeighborConnections(startCoordinate, neighborhoodMode, cellShape)
                .stream()
                .map(CellNeighborConnection::direction)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(CompassDirection.class)));
    }

    /**
     * Determines the {@link EdgeBehaviorAction} for a given coordinate in a grid structure.
     *
     * <p>Returns {@code VALID} if the coordinate is within bounds. Otherwise, the edge behaviors of the axes that the
     * coordinate exceeds decide the action, in this priority:
     * <ol>
     *   <li>{@code BLOCKED} if one of them is {@link EdgeBehavior#BLOCK}</li>
     *   <li>{@code ABSORBED} if one of them is {@link EdgeBehavior#ABSORB}</li>
     *   <li>{@code WRAPPED} otherwise, because all of them are {@link EdgeBehavior#WRAP}</li>
     * </ol>
     *
     * @param coordinate the grid coordinate to check
     * @param structure  the grid structure defining bounds and edge behaviors
     * @return the resulting {@link EdgeBehaviorAction} for the coordinate
     */
    static EdgeBehaviorAction edgeBehaviorActionForCoordinate(GridCoordinate coordinate, GridStructure structure) {
        if (structure.isCoordinateValid(coordinate)) {
            return EdgeBehaviorAction.VALID;
        }
        GridCoordinate min = structure.minCoordinateInclusive();
        GridCoordinate max = structure.maxCoordinateExclusive();

        boolean outX = (coordinate.x() < min.x()) || (coordinate.x() >= max.x());
        boolean outY = (coordinate.y() < min.y()) || (coordinate.y() >= max.y());

        EdgeBehavior edgeBehaviorX = structure.edgeBehaviorX();
        EdgeBehavior edgeBehaviorY = structure.edgeBehaviorY();

        if ((outX && (edgeBehaviorX == EdgeBehavior.BLOCK)) || (outY && (edgeBehaviorY == EdgeBehavior.BLOCK))) {
            return EdgeBehaviorAction.BLOCKED;
        }
        if ((outX && (edgeBehaviorX == EdgeBehavior.ABSORB)) || (outY && (edgeBehaviorY == EdgeBehavior.ABSORB))) {
            return EdgeBehaviorAction.ABSORBED;
        }
        return EdgeBehaviorAction.WRAPPED;
    }

    /**
     * Returns cached neighbor connection definitions for the specified configuration.
     *
     * <p>The cache key is generated by {@link #computeCacheKey(GridCoordinate, NeighborhoodMode, CellShape)}
     * and includes only parity/offset information that affects the geometry.
     *
     * @param startCoordinate  the source coordinate (used for parity/offset-dependent shapes)
     * @param neighborhoodMode the neighborhood mode to evaluate
     * @param cellShape        the cell shape to evaluate
     * @return an immutable list of relative neighbor connections for the configuration
     */
    static List<CellNeighborConnection> getOrComputeCellNeighborConnections(GridCoordinate startCoordinate,
                                                                            NeighborhoodMode neighborhoodMode,
                                                                            CellShape cellShape) {
        return CACHE.computeIfAbsent(
                computeCacheKey(startCoordinate, neighborhoodMode, cellShape),
                _ -> computeCellNeighborConnections(startCoordinate, neighborhoodMode, cellShape));
    }

    /**
     * Generates the cache key for a neighbor-connection configuration.
     *
     * <p>The key contains only shape-specific properties that influence adjacency:
     * triangle orientation for triangles, no coordinate component for squares,
     * and column-based vertical offset for hexagons.
     *
     * @param startCoordinate  the source coordinate (used only where relevant)
     * @param neighborhoodMode the neighborhood mode
     * @param cellShape        the cell shape
     * @return a deterministic cache key for the given configuration
     */
    static String computeCacheKey(GridCoordinate startCoordinate,
                                  NeighborhoodMode neighborhoodMode,
                                  CellShape cellShape) {
        return switch (cellShape) {
            case TRIANGLE -> cellShape + "::" + neighborhoodMode + "::" + startCoordinate.isTriangleCellPointingDown();
            case SQUARE -> cellShape + "::" + neighborhoodMode;
            case HEXAGON -> cellShape + "::" + startCoordinate.hasHexagonCellYOffset();
        };
    }

    /**
     * Computes the relative neighbor connections for the given configuration.
     *
     * <p>Performs shape-specific dispatch and returns only geometric neighbor
     * relations (without boundary checks or edge behavior mapping).
     *
     * @param startCoordinate  the source coordinate (used for parity/offset-dependent shapes)
     * @param neighborhoodMode the neighborhood mode
     * @param cellShape        the cell shape
     * @return a list of relative neighbor connections
     */
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

    /**
     * Computes relative neighbor connections for a triangle cell.
     *
     * <p>The result depends on both the neighborhood mode and the triangle orientation
     * (pointing down vs. pointing up). The comments name the neighbors of the example cells (2, 2) (pointing down)
     * and (3, 2) (pointing up).
     *
     * @param neighborhoodMode           the neighborhood mode
     * @param isTriangleCellPointingDown {@code true} if the triangle points down, {@code false} otherwise
     * @return a list of relative neighbor connections in clockwise order
     */
    static List<CellNeighborConnection> computeTriangleCellNeighborConnections(
            NeighborhoodMode neighborhoodMode,
            boolean isTriangleCellPointingDown) {
        return switch (neighborhoodMode) {
            case EDGES_ONLY -> {
                if (isTriangleCellPointingDown) {
                    yield List.of(
                            CellNeighborConnection.ofEdge(0, -1, CompassDirection.N), // neighbor (2, 1)
                            CellNeighborConnection.ofEdge(1, 0, CompassDirection.SE), // neighbor (3, 2)
                            CellNeighborConnection.ofEdge(-1, 0, CompassDirection.SW) // neighbor (1, 2)
                    );
                } else {
                    yield List.of(
                            CellNeighborConnection.ofEdge(1, 0, CompassDirection.NE), // neighbor (4, 2)
                            CellNeighborConnection.ofEdge(0, 1, CompassDirection.S), // neighbor (3, 3)
                            CellNeighborConnection.ofEdge(-1, 0, CompassDirection.NW) // neighbor (2, 2)
                    );
                }
            }
            case EDGES_AND_VERTICES -> {
                if (isTriangleCellPointingDown) {
                    yield List.of(
                            CellNeighborConnection.ofEdge(0, -1, CompassDirection.N), // neighbor (2, 1)
                            CellNeighborConnection.ofVertex(1, -1, CompassDirection.NNE), // neighbor (3, 1)
                            CellNeighborConnection.ofVertex(2, -1, CompassDirection.NE), // neighbor (4, 1)
                            CellNeighborConnection.ofVertex(2, 0, CompassDirection.E), // neighbor (4, 2)
                            CellNeighborConnection.ofEdge(1, 0, CompassDirection.SE), // neighbor (3, 2)
                            CellNeighborConnection.ofVertex(1, 1, CompassDirection.SSE), // neighbor (3, 3)
                            CellNeighborConnection.ofVertex(0, 1, CompassDirection.S), // neighbor (2, 3)
                            CellNeighborConnection.ofVertex(-1, 1, CompassDirection.SSW), // neighbor (1, 3)
                            CellNeighborConnection.ofEdge(-1, 0, CompassDirection.SW), // neighbor (1, 2)
                            CellNeighborConnection.ofVertex(-2, 0, CompassDirection.W), // neighbor (0, 2)
                            CellNeighborConnection.ofVertex(-2, -1, CompassDirection.NW), // neighbor (0, 1)
                            CellNeighborConnection.ofVertex(-1, -1, CompassDirection.NNW) // neighbor (1, 1)
                    );
                } else {
                    yield List.of(
                            CellNeighborConnection.ofVertex(0, -1, CompassDirection.N), // neighbor (3, 1)
                            CellNeighborConnection.ofVertex(1, -1, CompassDirection.NNE), // neighbor (4, 1)
                            CellNeighborConnection.ofEdge(1, 0, CompassDirection.NE), // neighbor (4, 2)
                            CellNeighborConnection.ofVertex(2, 0, CompassDirection.E), // neighbor (5, 2)
                            CellNeighborConnection.ofVertex(2, 1, CompassDirection.SE), // neighbor (5, 3)
                            CellNeighborConnection.ofVertex(1, 1, CompassDirection.SSE), // neighbor (4, 3)
                            CellNeighborConnection.ofEdge(0, 1, CompassDirection.S), // neighbor (3, 3)
                            CellNeighborConnection.ofVertex(-1, 1, CompassDirection.SSW), // neighbor (2, 3)
                            CellNeighborConnection.ofVertex(-2, 1, CompassDirection.SW), // neighbor (1, 3)
                            CellNeighborConnection.ofVertex(-2, 0, CompassDirection.W), // neighbor (1, 2)
                            CellNeighborConnection.ofEdge(-1, 0, CompassDirection.NW), // neighbor (2, 2)
                            CellNeighborConnection.ofVertex(-1, -1, CompassDirection.NNW) // neighbor (2, 1)
                    );
                }
            }
        };
    }

    /**
     * Computes relative neighbor connections for a square cell.
     *
     * @param neighborhoodMode the neighborhood mode
     * @return a list of relative neighbor connections in clockwise order
     */
    static List<CellNeighborConnection> computeSquareCellNeighborConnections(NeighborhoodMode neighborhoodMode) {
        return switch (neighborhoodMode) {
            case EDGES_ONLY -> List.of(
                    CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                    CellNeighborConnection.ofEdge(1, 0, CompassDirection.E),
                    CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                    CellNeighborConnection.ofEdge(-1, 0, CompassDirection.W)
            );
            case EDGES_AND_VERTICES -> List.of(
                    CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                    CellNeighborConnection.ofVertex(1, -1, CompassDirection.NE),
                    CellNeighborConnection.ofEdge(1, 0, CompassDirection.E),
                    CellNeighborConnection.ofVertex(1, 1, CompassDirection.SE),
                    CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                    CellNeighborConnection.ofVertex(-1, 1, CompassDirection.SW),
                    CellNeighborConnection.ofEdge(-1, 0, CompassDirection.W),
                    CellNeighborConnection.ofVertex(-1, -1, CompassDirection.NW)
            );
        };
    }

    /**
     * Computes relative neighbor connections for a hexagon cell.
     *
     * <p>In the staggered layout, the returned offsets depend on whether the source
     * column uses a vertical Y-offset.
     *
     * @param hasHexagonCellYOffset {@code true} if the source hexagon is in an offset column
     * @return a list of six edge-neighbor connections in clockwise order
     */
    static List<CellNeighborConnection> computeHexagonCellNeighborConnections(boolean hasHexagonCellYOffset) {
        if (hasHexagonCellYOffset) {
            return List.of(
                    CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                    CellNeighborConnection.ofEdge(1, 0, CompassDirection.NE),
                    CellNeighborConnection.ofEdge(1, 1, CompassDirection.SE),
                    CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                    CellNeighborConnection.ofEdge(-1, 1, CompassDirection.SW),
                    CellNeighborConnection.ofEdge(-1, 0, CompassDirection.NW)
            );
        } else {
            return List.of(
                    CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                    CellNeighborConnection.ofEdge(1, -1, CompassDirection.NE),
                    CellNeighborConnection.ofEdge(1, 0, CompassDirection.SE),
                    CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                    CellNeighborConnection.ofEdge(-1, 0, CompassDirection.SW),
                    CellNeighborConnection.ofEdge(-1, -1, CompassDirection.NW)
            );
        }
    }

    private static void checkRadius(int radius) {
        if ((radius < 0) || (radius > MAX_RADIUS)) {
            throw new IllegalArgumentException("radius must be in [0, " + MAX_RADIUS + "], but was: " + radius);
        }
    }

    /**
     * Relative offset, compass direction, and connection type of one neighbor of a cell, cached per configuration.
     *
     * @param offset     the relative offset to the neighbor cell
     * @param direction  the compass direction to the neighbor
     * @param connection the type of connection (edge or vertex)
     */
    record CellNeighborConnection(GridOffset offset, CompassDirection direction, CellConnectionType connection) {

        static CellNeighborConnection ofEdge(int dx, int dy, CompassDirection direction) {
            return new CellNeighborConnection(new GridOffset(dx, dy), direction, CellConnectionType.EDGE);
        }

        static CellNeighborConnection ofVertex(int dx, int dy, CompassDirection direction) {
            return new CellNeighborConnection(new GridOffset(dx, dy), direction, CellConnectionType.VERTEX);
        }

    }

}
