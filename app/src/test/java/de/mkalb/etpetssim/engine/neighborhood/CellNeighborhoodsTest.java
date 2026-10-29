package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods.CellNeighborConnection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class CellNeighborhoodsTest {

    private static final List<GridCoordinate> INSIDE_COORDINATES =
            List.of(coordinate(0, 0), coordinate(5, 5), coordinate(19, 9));
    private static final List<GridCoordinate> OUTSIDE_COORDINATES = List.of(
            coordinate(-1, -1), coordinate(-1, 5), coordinate(5, -1),
            coordinate(20, 10), coordinate(20, 9), coordinate(19, 10));

    private static GridCoordinate coordinate(int x, int y) {
        return new GridCoordinate(x, y);
    }

    private static GridStructure createStructure(CellShape cellShape, GridEdgeBehavior edgeBehavior) {
        return createStructure(cellShape, edgeBehavior, 20, 10);
    }

    private static GridStructure createStructure(CellShape cellShape,
                                                 GridEdgeBehavior edgeBehavior,
                                                 int width,
                                                 int height) {
        return new GridStructure(new GridTopology(cellShape, edgeBehavior), new GridSize(width, height));
    }

    private static List<CompassDirection> neighborDirections(GridCoordinate coordinate,
                                                             NeighborhoodMode neighborhoodMode,
                                                             CellShape cellShape) {
        return CellNeighborhoods.cellNeighborsIgnoringEdgeBehavior(coordinate, neighborhoodMode, cellShape)
                .map(CellNeighbor::direction)
                .toList();
    }

    private static void assertValidWithEdgeBehavior(GridEdgeBehavior edgeBehavior,
                                                    Set<GridCoordinate> validOutsideCoordinates) {
        for (CellShape cellShape : CellShape.values()) {
            GridStructure structure = createStructure(cellShape, edgeBehavior);
            for (GridCoordinate coordinate : INSIDE_COORDINATES) {
                assertTrue(CellNeighborhoods.isValidWithEdgeBehavior(coordinate, structure),
                        coordinate.toDisplayString());
            }
            for (GridCoordinate coordinate : OUTSIDE_COORDINATES) {
                assertEquals(validOutsideCoordinates.contains(coordinate),
                        CellNeighborhoods.isValidWithEdgeBehavior(coordinate, structure),
                        coordinate.toDisplayString());
            }
        }
    }

    private static void assertEdgeBehaviorResult(GridStructure structure,
                                                 GridCoordinate original,
                                                 GridCoordinate mapped,
                                                 EdgeBehaviorAction action) {
        assertEquals(new EdgeBehaviorResult(original, mapped, action),
                CellNeighborhoods.applyEdgeBehaviorToCoordinate(original, structure));
    }

    private static void assertEdgeBehaviorAction(GridStructure structure,
                                                 EdgeBehaviorAction expectedAction,
                                                 int x,
                                                 int y) {
        assertEquals(expectedAction, CellNeighborhoods.edgeBehaviorActionForCoordinate(coordinate(x, y), structure),
                coordinate(x, y).toDisplayString());
    }

    private static void tryPutOuterRadiusRing(
            SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings) {
        radiusRings.put(1, new TreeMap<>());
    }

    private static void tryPutInnerRadiusRing(SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>> ring,
                                              GridCoordinate coordinate,
                                              RadiusRingCell<GridCoordinate> ringCell) {
        ring.put(coordinate, ringCell);
    }

    private static void tryAddReachedFromPreviousRing(SortedSet<GridCoordinate> reachedFromPreviousRing,
                                                      GridCoordinate coordinate) {
        reachedFromPreviousRing.add(coordinate);
    }

    @Test
    void testDirectionRingsMatchNeighborOrder() {
        GridCoordinate evenColumn = coordinate(2, 2);
        GridCoordinate oddColumn = coordinate(3, 2);

        assertAll(
                () -> assertEquals(CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING,
                        neighborDirections(evenColumn, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE)),
                () -> assertEquals(CellNeighborhoods.SQUARE_EDGES_AND_VERTICES_DIRECTION_RING,
                        neighborDirections(evenColumn, NeighborhoodMode.EDGES_AND_VERTICES, CellShape.SQUARE)),
                () -> assertEquals(CellNeighborhoods.HEXAGON_DIRECTION_RING,
                        neighborDirections(evenColumn, NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON)),
                () -> assertEquals(CellNeighborhoods.HEXAGON_DIRECTION_RING,
                        neighborDirections(oddColumn, NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON))
        );
    }

    @Test
    void testMaxNeighborCount() {
        assertAll(
                () -> assertEquals(3,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE)),
                () -> assertEquals(12,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE)),
                () -> assertEquals(4,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE)),
                () -> assertEquals(8,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.SQUARE)),
                () -> assertEquals(6,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON)),
                () -> assertEquals(6,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.HEXAGON))
        );
    }

    @Test
    void testMaxNeighborCountWithRadius() {
        for (CellShape cellShape : CellShape.values()) {
            for (NeighborhoodMode neighborhoodMode : NeighborhoodMode.values()) {
                assertAll(
                        () -> assertEquals(0, CellNeighborhoods.maxNeighborCount(neighborhoodMode, cellShape, 0)),
                        () -> assertEquals(0, CellNeighborhoods.maxNeighborCount(neighborhoodMode, cellShape, -1)),
                        () -> assertEquals(CellNeighborhoods.maxNeighborCount(neighborhoodMode, cellShape),
                                CellNeighborhoods.maxNeighborCount(neighborhoodMode, cellShape, 1))
                );
            }
        }
        assertAll(
                () -> assertEquals(10,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_ONLY, CellShape.TRIANGLE, 2)),
                () -> assertEquals(37,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.TRIANGLE, 2)),
                () -> assertEquals(13,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE, 2)),
                () -> assertEquals(25,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.SQUARE, 2)),
                () -> assertEquals(19,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON, 2)),
                () -> assertEquals(19,
                        CellNeighborhoods.maxNeighborCount(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.HEXAGON, 2)),
                () -> assertEquals(1 + ((4 * 100 * 101) / 2), CellNeighborhoods.maxNeighborCount(
                        NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE, CellNeighborhoods.MAX_RADIUS))
        );
    }

    @Test
    void testDirectionRing() {
        assertAll(
                () -> assertEquals(CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING,
                        CellNeighborhoods.directionRing(NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE)),
                () -> assertEquals(CellNeighborhoods.SQUARE_EDGES_AND_VERTICES_DIRECTION_RING,
                        CellNeighborhoods.directionRing(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.SQUARE)),
                () -> assertEquals(CellNeighborhoods.HEXAGON_DIRECTION_RING,
                        CellNeighborhoods.directionRing(NeighborhoodMode.EDGES_ONLY, CellShape.HEXAGON)),
                () -> assertEquals(CellNeighborhoods.HEXAGON_DIRECTION_RING,
                        CellNeighborhoods.directionRing(NeighborhoodMode.EDGES_AND_VERTICES, CellShape.HEXAGON))
        );
    }

    @Test
    void testDirectionRingRejectsTriangle() {
        for (NeighborhoodMode neighborhoodMode : NeighborhoodMode.values()) {
            assertThrows(IllegalArgumentException.class,
                    () -> CellNeighborhoods.directionRing(neighborhoodMode, CellShape.TRIANGLE));
        }
    }

    @Test
    void testIsCellNeighbor() {
        GridCoordinate coordinate0 = coordinate(2, 2);
        GridCoordinate coordinate1 = coordinate(2, 1);
        GridCoordinate coordinate2 = coordinate(1, 2);
        NeighborhoodMode edges = NeighborhoodMode.EDGES_ONLY;
        NeighborhoodMode vertices = NeighborhoodMode.EDGES_AND_VERTICES;

        assertAll(
                () -> assertFalse(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate0, edges, CellShape.SQUARE)),
                () -> assertFalse(
                        CellNeighborhoods.isCellNeighbor(coordinate1, coordinate1, vertices, CellShape.TRIANGLE)),
                () -> assertFalse(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate2, edges, CellShape.HEXAGON)),

                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate1, edges, CellShape.TRIANGLE)),
                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate1, edges, CellShape.SQUARE)),
                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate0, coordinate1, edges, CellShape.HEXAGON)),

                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate1, coordinate0, edges, CellShape.TRIANGLE)),
                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate1, coordinate0, edges, CellShape.SQUARE)),
                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate1, coordinate0, edges, CellShape.HEXAGON)),

                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate0, edges, CellShape.TRIANGLE)),
                () -> assertFalse(
                        CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, edges, CellShape.TRIANGLE)),
                () -> assertTrue(
                        CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, vertices, CellShape.TRIANGLE)),

                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate0, edges, CellShape.SQUARE)),
                () -> assertFalse(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, edges, CellShape.SQUARE)),
                () -> assertTrue(
                        CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, vertices, CellShape.SQUARE)),

                () -> assertTrue(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate0, edges, CellShape.HEXAGON)),
                () -> assertFalse(CellNeighborhoods.isCellNeighbor(coordinate2, coordinate1, edges, CellShape.HEXAGON))
        );
    }

    @Test
    void testIsValidWithEdgeBehavior() {
        assertAll(
                () -> assertValidWithEdgeBehavior(GridEdgeBehavior.BLOCK_XY, Set.of()),
                () -> assertValidWithEdgeBehavior(GridEdgeBehavior.ABSORB_XY, Set.of()),
                () -> assertValidWithEdgeBehavior(GridEdgeBehavior.WRAP_XY, Set.copyOf(OUTSIDE_COORDINATES)),
                () -> assertValidWithEdgeBehavior(GridEdgeBehavior.WRAP_X_BLOCK_Y,
                        Set.of(coordinate(-1, 5), coordinate(20, 9))),
                () -> assertValidWithEdgeBehavior(GridEdgeBehavior.BLOCK_X_WRAP_Y,
                        Set.of(coordinate(5, -1), coordinate(19, 10)))
        );
    }

    @Test
    void testApplyEdgeBehaviorToCoordinate() {
        for (CellShape cellShape : CellShape.values()) {
            for (GridEdgeBehavior gridEdgeBehavior : GridEdgeBehavior.values()) {
                GridStructure structure = createStructure(cellShape, gridEdgeBehavior);
                for (GridCoordinate coordinate : INSIDE_COORDINATES) {
                    assertEdgeBehaviorResult(structure, coordinate, coordinate, EdgeBehaviorAction.VALID);
                }
            }

            for (GridEdgeBehavior gridEdgeBehavior : List.of(GridEdgeBehavior.ABSORB_XY, GridEdgeBehavior.BLOCK_XY)) {
                GridStructure structure = createStructure(cellShape, gridEdgeBehavior);
                EdgeBehaviorAction action = (gridEdgeBehavior == GridEdgeBehavior.ABSORB_XY)
                        ? EdgeBehaviorAction.ABSORBED
                        : EdgeBehaviorAction.BLOCKED;
                for (GridCoordinate coordinate : List.of(coordinate(-1, -1), coordinate(-3, -2), coordinate(0, -2),
                        coordinate(-3, 1), coordinate(20, 5), coordinate(5, 15))) {
                    assertEdgeBehaviorResult(structure, coordinate, coordinate, action);
                }
            }

            GridStructure wrapXBlockY = createStructure(cellShape, GridEdgeBehavior.WRAP_X_BLOCK_Y);
            GridStructure blockXWrapY = createStructure(cellShape, GridEdgeBehavior.BLOCK_X_WRAP_Y);
            GridStructure wrapXY = createStructure(cellShape, GridEdgeBehavior.WRAP_XY);
            assertAll(
                    () -> assertEdgeBehaviorResult(wrapXBlockY,
                            coordinate(-1, -1), coordinate(-1, -1), EdgeBehaviorAction.BLOCKED),
                    () -> assertEdgeBehaviorResult(wrapXBlockY,
                            coordinate(-3, -2), coordinate(-3, -2), EdgeBehaviorAction.BLOCKED),
                    () -> assertEdgeBehaviorResult(wrapXBlockY,
                            coordinate(0, -2), coordinate(0, -2), EdgeBehaviorAction.BLOCKED),
                    () -> assertEdgeBehaviorResult(wrapXBlockY,
                            coordinate(-3, 1), coordinate(17, 1), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(wrapXBlockY,
                            coordinate(20, 5), coordinate(0, 5), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(wrapXBlockY,
                            coordinate(5, 15), coordinate(5, 15), EdgeBehaviorAction.BLOCKED),

                    () -> assertEdgeBehaviorResult(blockXWrapY,
                            coordinate(-1, -1), coordinate(-1, -1), EdgeBehaviorAction.BLOCKED),
                    () -> assertEdgeBehaviorResult(blockXWrapY,
                            coordinate(-3, -2), coordinate(-3, -2), EdgeBehaviorAction.BLOCKED),
                    () -> assertEdgeBehaviorResult(blockXWrapY,
                            coordinate(0, -2), coordinate(0, 8), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(blockXWrapY,
                            coordinate(-3, 1), coordinate(-3, 1), EdgeBehaviorAction.BLOCKED),
                    () -> assertEdgeBehaviorResult(blockXWrapY,
                            coordinate(20, 5), coordinate(20, 5), EdgeBehaviorAction.BLOCKED),
                    () -> assertEdgeBehaviorResult(blockXWrapY,
                            coordinate(5, 15), coordinate(5, 5), EdgeBehaviorAction.WRAPPED),

                    () -> assertEdgeBehaviorResult(wrapXY,
                            coordinate(-1, -1), coordinate(19, 9), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(wrapXY,
                            coordinate(-3, -2), coordinate(17, 8), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(wrapXY,
                            coordinate(0, -2), coordinate(0, 8), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(wrapXY,
                            coordinate(-3, 1), coordinate(17, 1), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(wrapXY,
                            coordinate(20, 5), coordinate(0, 5), EdgeBehaviorAction.WRAPPED),
                    () -> assertEdgeBehaviorResult(wrapXY,
                            coordinate(5, 15), coordinate(5, 5), EdgeBehaviorAction.WRAPPED)
            );
        }
    }

    @Test
    void testNeighborEdgeBehaviorResults() {
        GridStructure blocked = createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, 8, 8);
        GridStructure wrapped = createStructure(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY, 8, 8);
        GridCoordinate origin = coordinate(0, 0);

        Collection<EdgeBehaviorResult> blockedResults =
                CellNeighborhoods.neighborEdgeBehaviorResults(origin, NeighborhoodMode.EDGES_ONLY, blocked);
        Collection<EdgeBehaviorResult> wrappedResults =
                CellNeighborhoods.neighborEdgeBehaviorResults(origin, NeighborhoodMode.EDGES_ONLY, wrapped);

        assertAll(
                () -> assertEquals(4, blockedResults.size()),
                () -> assertEquals(2,
                        blockedResults.stream().filter(r -> r.action() == EdgeBehaviorAction.VALID).count()),
                () -> assertEquals(2,
                        blockedResults.stream().filter(r -> r.action() == EdgeBehaviorAction.BLOCKED).count()),
                () -> assertEquals(Set.of(coordinate(1, 0), coordinate(0, 1), coordinate(7, 0), coordinate(0, 7)),
                        Set.copyOf(wrappedResults.stream().map(EdgeBehaviorResult::mapped).toList())),
                () -> assertEquals(2,
                        wrappedResults.stream().filter(r -> r.action() == EdgeBehaviorAction.WRAPPED).count()),
                () -> assertTrue(CellNeighborhoods.neighborEdgeBehaviorResults(
                        coordinate(-1, 0), NeighborhoodMode.EDGES_ONLY, blocked).isEmpty())
        );
    }

    @Test
    void testCellNeighborsIgnoringEdgeBehavior() {
        GridCoordinate origin = coordinate(0, 0);

        List<CellNeighbor> neighbors = CellNeighborhoods.cellNeighborsIgnoringEdgeBehavior(
                origin, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE).toList();

        assertAll(
                () -> assertEquals(List.of(coordinate(0, -1), coordinate(1, 0), coordinate(0, 1), coordinate(-1, 0)),
                        neighbors.stream().map(CellNeighbor::neighborCoordinate).toList()),
                () -> assertTrue(neighbors.stream().allMatch(n -> n.startCoordinate().equals(origin))),
                () -> assertTrue(neighbors.stream().allMatch(n -> n.connection() == CellConnectionType.EDGE))
        );
    }

    @Test
    void testCellNeighborWithEdgeBehavior() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY, 8, 8);
        GridCoordinate origin = coordinate(0, 0);

        Optional<CellNeighborWithEdgeBehavior> west = CellNeighborhoods.cellNeighborWithEdgeBehavior(
                origin, NeighborhoodMode.EDGES_ONLY, structure, CompassDirection.W);

        assertAll(
                () -> assertEquals(Optional.of(new CellNeighborWithEdgeBehavior(origin, CompassDirection.W,
                        CellConnectionType.EDGE, coordinate(-1, 0), coordinate(7, 0),
                        EdgeBehaviorAction.WRAPPED)), west),
                () -> assertTrue(CellNeighborhoods.cellNeighborWithEdgeBehavior(
                        origin, NeighborhoodMode.EDGES_ONLY, structure, CompassDirection.NE).isEmpty())
        );
    }

    @Test
    void testCellNeighborsWithEdgeBehavior() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY, 8, 8);

        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> neighbors =
                CellNeighborhoods.cellNeighborsWithEdgeBehavior(coordinate(0, 0), NeighborhoodMode.EDGES_ONLY,
                        structure);

        assertAll(
                () -> assertEquals(Set.of(coordinate(1, 0), coordinate(0, 1), coordinate(7, 0), coordinate(0, 7)),
                        neighbors.keySet()),
                () -> assertTrue(neighbors.values().stream().allMatch(list -> list.size() == 1)),
                () -> assertEquals(EdgeBehaviorAction.WRAPPED,
                        neighbors.get(coordinate(7, 0)).getFirst().edgeBehaviorAction()),
                () -> assertThrows(UnsupportedOperationException.class, neighbors::clear),
                () -> assertTrue(CellNeighborhoods.cellNeighborsWithEdgeBehavior(
                        coordinate(8, 0), NeighborhoodMode.EDGES_ONLY, structure).isEmpty())
        );
    }

    @Test
    void testCellsByRadiusRingsReturnsUnmodifiableResultForRadiusZero() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY,
                GridSize.MIN_SIZE, GridSize.MIN_SIZE);
        GridCoordinate startCoordinate = coordinate(1, 1);

        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings =
                CellNeighborhoods.cellsByRadiusRings(
                        startCoordinate,
                        NeighborhoodMode.EDGES_ONLY,
                        structure,
                        0,
                        Function.identity());
        RadiusRingCell<GridCoordinate> ringCell = radiusRings.get(0).get(startCoordinate);

        assertAll(
                () -> assertEquals(Set.of(0), radiusRings.keySet()),
                () -> assertEquals(Set.of(startCoordinate), radiusRings.get(0).keySet()),
                () -> assertEquals(0, ringCell.ring()),
                () -> assertEquals(startCoordinate, ringCell.coordinate()),
                () -> assertEquals(startCoordinate, ringCell.cell()),
                () -> assertTrue(ringCell.reachedFromPreviousRing().isEmpty()),
                () -> assertThrows(UnsupportedOperationException.class, () -> tryPutOuterRadiusRing(radiusRings)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> tryPutInnerRadiusRing(radiusRings.get(0), startCoordinate, ringCell)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> tryAddReachedFromPreviousRing(ringCell.reachedFromPreviousRing(), startCoordinate))
        );
    }

    @Test
    void testCellsByRadiusRingsRejectsInvalidRadiusOrCoordinate() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY,
                GridSize.MIN_SIZE, GridSize.MIN_SIZE);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods.cellsByRadiusRings(
                        coordinate(1, 1), NeighborhoodMode.EDGES_ONLY, structure, -1, Function.identity())),
                () -> assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods.cellsByRadiusRings(
                        coordinate(1, 1), NeighborhoodMode.EDGES_ONLY, structure, CellNeighborhoods.MAX_RADIUS + 1,
                        Function.identity())),
                () -> assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods.cellsByRadiusRings(
                        coordinate(8, 1), NeighborhoodMode.EDGES_ONLY, structure, 1, Function.identity()))
        );
    }

    @Test
    void testCellsByRadiusRingsWrapsAndReturnsUniqueCells() {
        GridStructure structure = createStructure(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY,
                GridSize.MIN_SIZE, GridSize.MIN_SIZE);
        GridCoordinate startCoordinate = coordinate(0, 0);

        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings =
                CellNeighborhoods.cellsByRadiusRings(
                        startCoordinate,
                        NeighborhoodMode.EDGES_ONLY,
                        structure,
                        2,
                        Function.identity());
        SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>> ring2 = radiusRings.get(2);
        List<GridCoordinate> allCoordinates = new ArrayList<>();
        for (SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>> ring : radiusRings.values()) {
            allCoordinates.addAll(ring.keySet());
        }

        assertAll(
                () -> assertEquals(Set.of(0, 1, 2), radiusRings.keySet()),
                () -> assertEquals(Set.of(startCoordinate), radiusRings.get(0).keySet()),
                () -> assertEquals(Set.of(coordinate(0, 7), coordinate(1, 0), coordinate(0, 1), coordinate(7, 0)),
                        radiusRings.get(1).keySet()),
                () -> assertEquals(Set.of(
                                coordinate(0, 6), coordinate(1, 7), coordinate(7, 7), coordinate(2, 0),
                                coordinate(1, 1), coordinate(0, 2), coordinate(7, 1), coordinate(6, 0)),
                        ring2.keySet()),
                () -> assertTrue(radiusRings.get(1).entrySet().stream().allMatch(entry ->
                        (entry.getValue().ring() == 1)
                                && entry.getKey().equals(entry.getValue().coordinate())
                                && entry.getKey().equals(entry.getValue().cell())
                                && entry.getValue().reachedFromPreviousRing().equals(Set.of(startCoordinate)))),
                () -> assertEquals(Set.of(coordinate(0, 7)),
                        ring2.get(coordinate(0, 6)).reachedFromPreviousRing()),
                () -> assertEquals(Set.of(coordinate(0, 7), coordinate(1, 0)),
                        ring2.get(coordinate(1, 7)).reachedFromPreviousRing()),
                () -> assertEquals(Set.of(coordinate(0, 7), coordinate(7, 0)),
                        ring2.get(coordinate(7, 7)).reachedFromPreviousRing()),
                () -> assertEquals(Set.of(coordinate(1, 0)),
                        ring2.get(coordinate(2, 0)).reachedFromPreviousRing()),
                () -> assertEquals(Set.of(coordinate(1, 0), coordinate(0, 1)),
                        ring2.get(coordinate(1, 1)).reachedFromPreviousRing()),
                () -> assertEquals(Set.of(coordinate(0, 1)),
                        ring2.get(coordinate(0, 2)).reachedFromPreviousRing()),
                () -> assertEquals(Set.of(coordinate(0, 1), coordinate(7, 0)),
                        ring2.get(coordinate(7, 1)).reachedFromPreviousRing()),
                () -> assertEquals(Set.of(coordinate(7, 0)),
                        ring2.get(coordinate(6, 0)).reachedFromPreviousRing()),
                () -> assertEquals(13, allCoordinates.size()),
                () -> assertEquals(13, new HashSet<>(allCoordinates).size())
        );
    }

    @Test
    void testCellsByRadiusRingsFiltersBlockedAndAbsorbedNeighbors() {
        GridCoordinate startCoordinate = coordinate(0, 0);

        for (GridEdgeBehavior edgeBehavior : List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.ABSORB_XY)) {
            GridStructure structure = createStructure(CellShape.SQUARE, edgeBehavior,
                    GridSize.MIN_SIZE, GridSize.MIN_SIZE);

            SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCoordinate>>> radiusRings =
                    CellNeighborhoods.cellsByRadiusRings(
                            startCoordinate,
                            NeighborhoodMode.EDGES_ONLY,
                            structure,
                            1,
                            Function.identity());

            assertEquals(Set.of(coordinate(1, 0), coordinate(0, 1)), radiusRings.get(1).keySet());
        }
    }

    @Test
    void testNeighborCoordinatesWithinRadius() {
        GridCoordinate origin = coordinate(0, 0);

        Set<GridCoordinate> radiusTwo = CellNeighborhoods.neighborCoordinatesWithinRadius(
                origin, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE, 2);

        assertAll(
                () -> assertEquals(12, radiusTwo.size()),
                () -> assertFalse(radiusTwo.contains(origin)),
                () -> assertTrue(radiusTwo.contains(coordinate(-2, 0))),
                () -> assertTrue(radiusTwo.contains(coordinate(1, 1))),
                () -> assertFalse(radiusTwo.contains(coordinate(2, 1))),
                () -> assertTrue(CellNeighborhoods.neighborCoordinatesWithinRadius(
                        origin, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE, 0).isEmpty())
        );
    }

    @Test
    void testNeighborCoordinatesWithinRadiusRejectsInvalidRadius() {
        GridCoordinate origin = coordinate(0, 0);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods
                        .neighborCoordinatesWithinRadius(origin, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE, -1)),
                () -> assertThrows(IllegalArgumentException.class, () -> CellNeighborhoods
                        .neighborCoordinatesWithinRadius(origin, NeighborhoodMode.EDGES_ONLY, CellShape.SQUARE,
                                CellNeighborhoods.MAX_RADIUS + 1))
        );
    }

    @Test
    void testCellNeighborDirections() {
        assertAll(
                () -> assertEquals(
                        EnumSet.of(CompassDirection.N, CompassDirection.E, CompassDirection.S, CompassDirection.W),
                        CellNeighborhoods.cellNeighborDirections(coordinate(2, 2), NeighborhoodMode.EDGES_ONLY,
                                CellShape.SQUARE)),
                () -> assertEquals(EnumSet.of(CompassDirection.N, CompassDirection.SE, CompassDirection.SW),
                        CellNeighborhoods.cellNeighborDirections(coordinate(2, 2), NeighborhoodMode.EDGES_ONLY,
                                CellShape.TRIANGLE)),
                () -> assertEquals(EnumSet.of(CompassDirection.NE, CompassDirection.S, CompassDirection.NW),
                        CellNeighborhoods.cellNeighborDirections(coordinate(3, 2), NeighborhoodMode.EDGES_ONLY,
                                CellShape.TRIANGLE))
        );
    }

    @Test
    void testEdgeBehaviorActionForCoordinate() {
        Map<GridEdgeBehavior, EdgeBehaviorAction> actionMap = Map.of(
                GridEdgeBehavior.BLOCK_XY, EdgeBehaviorAction.BLOCKED,
                GridEdgeBehavior.ABSORB_XY, EdgeBehaviorAction.ABSORBED,
                GridEdgeBehavior.WRAP_XY, EdgeBehaviorAction.WRAPPED
        );
        for (CellShape cellShape : CellShape.values()) {
            for (GridEdgeBehavior edgeBehavior : GridEdgeBehavior.values()) {
                GridStructure structure = createStructure(cellShape, edgeBehavior);
                assertAll(
                        () -> assertEdgeBehaviorAction(structure, EdgeBehaviorAction.VALID, 0, 0),
                        () -> assertEdgeBehaviorAction(structure, EdgeBehaviorAction.VALID, 19, 0),
                        () -> assertEdgeBehaviorAction(structure, EdgeBehaviorAction.VALID, 0, 9),
                        () -> assertEdgeBehaviorAction(structure, EdgeBehaviorAction.VALID, 19, 9),
                        () -> assertEdgeBehaviorAction(structure, EdgeBehaviorAction.VALID, 5, 5)
                );
            }

            for (Map.Entry<GridEdgeBehavior, EdgeBehaviorAction> entry : actionMap.entrySet()) {
                GridStructure structure = createStructure(cellShape, entry.getKey());
                EdgeBehaviorAction expectedAction = entry.getValue();
                assertAll(
                        () -> assertEdgeBehaviorAction(structure, expectedAction, -1, -1),
                        () -> assertEdgeBehaviorAction(structure, expectedAction, -1, 5),
                        () -> assertEdgeBehaviorAction(structure, expectedAction, 20, 5),
                        () -> assertEdgeBehaviorAction(structure, expectedAction, 5, -1),
                        () -> assertEdgeBehaviorAction(structure, expectedAction, 5, 10),
                        () -> assertEdgeBehaviorAction(structure, expectedAction, 20, 10),
                        () -> assertEdgeBehaviorAction(structure, expectedAction, 25, 15)
                );
            }

            GridStructure blockXWrapY = createStructure(cellShape, GridEdgeBehavior.BLOCK_X_WRAP_Y);
            GridStructure wrapXBlockY = createStructure(cellShape, GridEdgeBehavior.WRAP_X_BLOCK_Y);
            assertAll(
                    () -> assertEdgeBehaviorAction(blockXWrapY, EdgeBehaviorAction.BLOCKED, -1, -1),
                    () -> assertEdgeBehaviorAction(blockXWrapY, EdgeBehaviorAction.BLOCKED, -1, 5),
                    () -> assertEdgeBehaviorAction(blockXWrapY, EdgeBehaviorAction.BLOCKED, 20, 5),
                    () -> assertEdgeBehaviorAction(blockXWrapY, EdgeBehaviorAction.WRAPPED, 5, -1),
                    () -> assertEdgeBehaviorAction(blockXWrapY, EdgeBehaviorAction.WRAPPED, 5, 10),
                    () -> assertEdgeBehaviorAction(blockXWrapY, EdgeBehaviorAction.BLOCKED, 20, 10),
                    () -> assertEdgeBehaviorAction(blockXWrapY, EdgeBehaviorAction.BLOCKED, 25, 15),

                    () -> assertEdgeBehaviorAction(wrapXBlockY, EdgeBehaviorAction.BLOCKED, -1, -1),
                    () -> assertEdgeBehaviorAction(wrapXBlockY, EdgeBehaviorAction.WRAPPED, -1, 5),
                    () -> assertEdgeBehaviorAction(wrapXBlockY, EdgeBehaviorAction.WRAPPED, 20, 5),
                    () -> assertEdgeBehaviorAction(wrapXBlockY, EdgeBehaviorAction.BLOCKED, 5, -1),
                    () -> assertEdgeBehaviorAction(wrapXBlockY, EdgeBehaviorAction.BLOCKED, 5, 10),
                    () -> assertEdgeBehaviorAction(wrapXBlockY, EdgeBehaviorAction.BLOCKED, 20, 10),
                    () -> assertEdgeBehaviorAction(wrapXBlockY, EdgeBehaviorAction.BLOCKED, 25, 15)
            );
        }
    }

    @Test
    void testGetOrComputeCellNeighborConnections() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                GridCoordinate coordinate = coordinate(x, y);
                for (NeighborhoodMode neighborhoodMode : NeighborhoodMode.values()) {
                    for (CellShape cellShape : CellShape.values()) {
                        assertEquals(
                                CellNeighborhoods.computeCellNeighborConnections(
                                        coordinate, neighborhoodMode, cellShape),
                                CellNeighborhoods.getOrComputeCellNeighborConnections(
                                        coordinate, neighborhoodMode, cellShape));
                    }
                }
            }
        }
    }

    @Test
    void testComputeCacheKey() {
        NeighborhoodMode edges = NeighborhoodMode.EDGES_ONLY;
        NeighborhoodMode vertices = NeighborhoodMode.EDGES_AND_VERTICES;

        assertAll(
                () -> assertEquals("SQUARE::EDGES_ONLY",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 2), edges, CellShape.SQUARE)),
                () -> assertEquals("SQUARE::EDGES_AND_VERTICES",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 5), vertices, CellShape.SQUARE)),

                () -> assertEquals("HEXAGON::false",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 2), edges, CellShape.HEXAGON)),
                () -> assertEquals("HEXAGON::false",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 2), vertices, CellShape.HEXAGON)),
                () -> assertEquals("HEXAGON::true",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 2), edges, CellShape.HEXAGON)),
                () -> assertEquals("HEXAGON::true",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 2), vertices, CellShape.HEXAGON)),
                () -> assertEquals("HEXAGON::false",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 5), edges, CellShape.HEXAGON)),
                () -> assertEquals("HEXAGON::false",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 5), vertices, CellShape.HEXAGON)),
                () -> assertEquals("HEXAGON::true",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 5), edges, CellShape.HEXAGON)),
                () -> assertEquals("HEXAGON::true",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 5), vertices, CellShape.HEXAGON)),

                () -> assertEquals("TRIANGLE::EDGES_ONLY::true",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 2), edges, CellShape.TRIANGLE)),
                () -> assertEquals("TRIANGLE::EDGES_AND_VERTICES::true",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 2), vertices, CellShape.TRIANGLE)),
                () -> assertEquals("TRIANGLE::EDGES_ONLY::true",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 5), edges, CellShape.TRIANGLE)),
                () -> assertEquals("TRIANGLE::EDGES_AND_VERTICES::true",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 5), vertices, CellShape.TRIANGLE)),

                () -> assertEquals("TRIANGLE::EDGES_ONLY::false",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 3), edges, CellShape.TRIANGLE)),
                () -> assertEquals("TRIANGLE::EDGES_AND_VERTICES::false",
                        CellNeighborhoods.computeCacheKey(coordinate(2, 3), vertices, CellShape.TRIANGLE)),
                () -> assertEquals("TRIANGLE::EDGES_ONLY::false",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 4), edges, CellShape.TRIANGLE)),
                () -> assertEquals("TRIANGLE::EDGES_AND_VERTICES::false",
                        CellNeighborhoods.computeCacheKey(coordinate(3, 4), vertices, CellShape.TRIANGLE))
        );
    }

    @Test
    void testComputeCellNeighborConnections() {
        NeighborhoodMode edges = NeighborhoodMode.EDGES_ONLY;
        NeighborhoodMode vertices = NeighborhoodMode.EDGES_AND_VERTICES;

        assertAll(
                () -> assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(edges, true),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(2, 2), edges, CellShape.TRIANGLE)),
                () -> assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(vertices, true),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(2, 2), vertices,
                                CellShape.TRIANGLE)),
                () -> assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(edges, false),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(3, 2), edges, CellShape.TRIANGLE)),
                () -> assertEquals(CellNeighborhoods.computeTriangleCellNeighborConnections(vertices, false),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(3, 2), vertices,
                                CellShape.TRIANGLE)),

                () -> assertEquals(CellNeighborhoods.computeSquareCellNeighborConnections(edges),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(2, 2), edges, CellShape.SQUARE)),
                () -> assertEquals(CellNeighborhoods.computeSquareCellNeighborConnections(vertices),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(2, 2), vertices, CellShape.SQUARE)),

                () -> assertEquals(CellNeighborhoods.computeHexagonCellNeighborConnections(false),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(2, 2), edges, CellShape.HEXAGON)),
                () -> assertEquals(CellNeighborhoods.computeHexagonCellNeighborConnections(true),
                        CellNeighborhoods.computeCellNeighborConnections(coordinate(3, 2), edges, CellShape.HEXAGON))
        );
    }

    @Test
    void testComputeTriangleCellNeighborConnections() {
        List<CellNeighborConnection> edgesPointingDown =
                CellNeighborhoods.computeTriangleCellNeighborConnections(NeighborhoodMode.EDGES_ONLY, true);
        List<CellNeighborConnection> verticesPointingDown =
                CellNeighborhoods.computeTriangleCellNeighborConnections(NeighborhoodMode.EDGES_AND_VERTICES, true);
        List<CellNeighborConnection> verticesPointingUp =
                CellNeighborhoods.computeTriangleCellNeighborConnections(NeighborhoodMode.EDGES_AND_VERTICES, false);

        assertAll(
                () -> assertEquals(List.of(
                                CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                                CellNeighborConnection.ofEdge(1, 0, CompassDirection.SE),
                                CellNeighborConnection.ofEdge(-1, 0, CompassDirection.SW)),
                        edgesPointingDown),
                () -> assertEquals(12, verticesPointingDown.size()),
                () -> assertEquals(12, verticesPointingUp.size()),
                () -> assertEquals(3, verticesPointingDown.stream()
                        .filter(c -> c.connection() == CellConnectionType.EDGE)
                        .count()),
                () -> assertEquals(3, verticesPointingUp.stream()
                        .filter(c -> c.connection() == CellConnectionType.EDGE)
                        .count())
        );
    }

    @Test
    void testComputeSquareCellNeighborConnections() {
        assertAll(
                () -> assertEquals(List.of(
                                CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                                CellNeighborConnection.ofEdge(1, 0, CompassDirection.E),
                                CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                                CellNeighborConnection.ofEdge(-1, 0, CompassDirection.W)),
                        CellNeighborhoods.computeSquareCellNeighborConnections(NeighborhoodMode.EDGES_ONLY)),
                () -> assertEquals(List.of(
                                CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                                CellNeighborConnection.ofVertex(1, -1, CompassDirection.NE),
                                CellNeighborConnection.ofEdge(1, 0, CompassDirection.E),
                                CellNeighborConnection.ofVertex(1, 1, CompassDirection.SE),
                                CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                                CellNeighborConnection.ofVertex(-1, 1, CompassDirection.SW),
                                CellNeighborConnection.ofEdge(-1, 0, CompassDirection.W),
                                CellNeighborConnection.ofVertex(-1, -1, CompassDirection.NW)),
                        CellNeighborhoods.computeSquareCellNeighborConnections(NeighborhoodMode.EDGES_AND_VERTICES))
        );
    }

    @Test
    void testComputeHexagonCellNeighborConnections() {
        assertAll(
                () -> assertEquals(List.of(
                                CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                                CellNeighborConnection.ofEdge(1, 0, CompassDirection.NE),
                                CellNeighborConnection.ofEdge(1, 1, CompassDirection.SE),
                                CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                                CellNeighborConnection.ofEdge(-1, 1, CompassDirection.SW),
                                CellNeighborConnection.ofEdge(-1, 0, CompassDirection.NW)),
                        CellNeighborhoods.computeHexagonCellNeighborConnections(true)),
                () -> assertEquals(List.of(
                                CellNeighborConnection.ofEdge(0, -1, CompassDirection.N),
                                CellNeighborConnection.ofEdge(1, -1, CompassDirection.NE),
                                CellNeighborConnection.ofEdge(1, 0, CompassDirection.SE),
                                CellNeighborConnection.ofEdge(0, 1, CompassDirection.S),
                                CellNeighborConnection.ofEdge(-1, 0, CompassDirection.SW),
                                CellNeighborConnection.ofEdge(-1, -1, CompassDirection.NW)),
                        CellNeighborhoods.computeHexagonCellNeighborConnections(false))
        );
    }

}
