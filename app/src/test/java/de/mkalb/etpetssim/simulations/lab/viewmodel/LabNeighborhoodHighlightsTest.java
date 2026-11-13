package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellConnectionType;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.engine.neighborhood.RadiusRingCell;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LabNeighborhoodHighlightsTest {

    private static RadiusRingCell<GridCell<LabEntity>> createRingCell(int ring, GridCoordinate coordinate) {
        return new RadiusRingCell<>(ring, coordinate, new GridCell<>(coordinate, LabEntity.NORMAL), new TreeSet<>());
    }

    private static CellNeighborWithEdgeBehavior createNeighbor(GridCoordinate start, GridCoordinate neighbor) {
        return new CellNeighborWithEdgeBehavior(start, CompassDirection.E, CellConnectionType.EDGE,
                neighbor, neighbor, EdgeBehaviorAction.VALID);
    }

    @Test
    void testRecord() {
        GridCoordinate center = new GridCoordinate(1, 1);
        GridCoordinate east = new GridCoordinate(2, 1);
        GridCoordinate west = new GridCoordinate(0, 1);

        SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>> ring1 = new TreeMap<>();
        ring1.put(east, createRingCell(1, east));
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadius =
                new TreeMap<>();
        ringCellsByRadius.put(1, ring1);
        ringCellsByRadius.put(0, new TreeMap<>(Map.of(center, createRingCell(0, center))));

        List<GridCoordinate> validNeighborCoordinates = new ArrayList<>(List.of(east, west));

        List<CellNeighborWithEdgeBehavior> eastNeighbors = new ArrayList<>(List.of(createNeighbor(center, east)));
        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> validNeighborsWithEdgeBehavior = new LinkedHashMap<>();
        validNeighborsWithEdgeBehavior.put(east, eastNeighbors);
        validNeighborsWithEdgeBehavior.put(west, List.of(createNeighbor(center, west)));

        List<EdgeBehaviorResult> neighborEdgeBehaviorResults =
                new ArrayList<>(List.of(new EdgeBehaviorResult(east, east, EdgeBehaviorAction.VALID)));

        LabNeighborhoodHighlights highlights = new LabNeighborhoodHighlights(
                ringCellsByRadius,
                validNeighborCoordinates,
                validNeighborsWithEdgeBehavior,
                neighborEdgeBehaviorResults);

        ring1.put(west, createRingCell(1, west));
        ringCellsByRadius.put(2, new TreeMap<>());
        validNeighborCoordinates.add(center);
        eastNeighbors.add(createNeighbor(center, west));
        validNeighborsWithEdgeBehavior.put(center, List.of());
        neighborEdgeBehaviorResults.add(new EdgeBehaviorResult(west, west, EdgeBehaviorAction.VALID));

        assertAll(
                () -> assertEquals(List.of(0, 1), new ArrayList<>(highlights.ringCellsByRadius().keySet())),
                () -> assertEquals(Set.of(east), highlights.ringCellsByRadius().get(1).keySet()),
                () -> assertEquals(List.of(east, west), highlights.validNeighborCoordinates()),
                () -> assertEquals(List.of(east, west),
                        new ArrayList<>(highlights.validNeighborsWithEdgeBehavior().keySet())),
                () -> assertEquals(List.of(createNeighbor(center, east)),
                        highlights.validNeighborsWithEdgeBehavior().get(east)),
                () -> assertEquals(List.of(new EdgeBehaviorResult(east, east, EdgeBehaviorAction.VALID)),
                        highlights.neighborEdgeBehaviorResults()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.ringCellsByRadius().put(2, new TreeMap<>())),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.ringCellsByRadius().get(1).put(west, createRingCell(1, west))),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.validNeighborCoordinates().add(center)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.validNeighborsWithEdgeBehavior().put(center, List.of())),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.validNeighborsWithEdgeBehavior().get(east).add(createNeighbor(center, west))),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.neighborEdgeBehaviorResults().add(new EdgeBehaviorResult(west, west,
                                EdgeBehaviorAction.VALID)))
        );
    }

    @Test
    void testFrom() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(8, 8));
        var model = new SparseGridModel<>(structure, LabEntity.NORMAL);
        GridCoordinate center = new GridCoordinate(0, 0);
        GridCoordinate east = new GridCoordinate(1, 0);
        GridCoordinate south = new GridCoordinate(0, 1);
        model.setEntity(east, LabEntity.HIGHLIGHTED);

        LabNeighborhoodHighlights highlights = LabNeighborhoodHighlights.from(center, NeighborhoodMode.EDGES_ONLY,
                structure, model);

        assertAll(
                () -> assertEquals(Set.of(center), highlights.ringCellsByRadius().get(0).keySet()),
                () -> assertEquals(LabNeighborhoodHighlights.RING_MAX_RADIUS,
                        highlights.ringCellsByRadius().lastKey()),
                () -> assertEquals(LabEntity.HIGHLIGHTED,
                        highlights.ringCellsByRadius().get(1).get(east).cell().entity()),
                () -> assertTrue(highlights.validNeighborCoordinates().stream().allMatch(structure::isCoordinateValid)),
                () -> assertTrue(highlights.validNeighborCoordinates().containsAll(List.of(east, south,
                        new GridCoordinate(2, 0), new GridCoordinate(1, 1)))),
                () -> assertEquals(Set.of(east, south), highlights.validNeighborsWithEdgeBehavior().keySet()),
                () -> assertEquals(4, highlights.neighborEdgeBehaviorResults().size())
        );
    }

}
