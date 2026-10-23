package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class LabNeighborhoodHighlightsTest {

    private static RadiusRingCell<GridCell<LabEntity>> ringCell(int ring, GridCoordinate coordinate) {
        return new RadiusRingCell<>(ring, coordinate, new GridCell<>(coordinate, LabEntity.NORMAL), new TreeSet<>());
    }

    private static CellNeighborWithEdgeBehavior neighbor(GridCoordinate start, GridCoordinate neighbor) {
        return new CellNeighborWithEdgeBehavior(start, CompassDirection.E, CellConnectionType.EDGE,
                neighbor, neighbor, EdgeBehaviorAction.VALID);
    }

    @Test
    void testRecord() {
        GridCoordinate center = new GridCoordinate(1, 1);
        GridCoordinate east = new GridCoordinate(2, 1);
        GridCoordinate west = new GridCoordinate(0, 1);

        SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>> ring1 = new TreeMap<>();
        ring1.put(east, ringCell(1, east));
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadius = new TreeMap<>();
        ringCellsByRadius.put(1, ring1);
        ringCellsByRadius.put(0, new TreeMap<>(Map.of(center, ringCell(0, center))));

        List<GridCoordinate> validNeighborCoordinates = new ArrayList<>(List.of(east, west));

        List<CellNeighborWithEdgeBehavior> eastNeighbors = new ArrayList<>(List.of(neighbor(center, east)));
        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> validNeighborsWithEdgeBehavior = new LinkedHashMap<>();
        validNeighborsWithEdgeBehavior.put(east, eastNeighbors);
        validNeighborsWithEdgeBehavior.put(west, List.of(neighbor(center, west)));

        List<EdgeBehaviorResult> neighborEdgeBehaviorResults =
                new ArrayList<>(List.of(new EdgeBehaviorResult(east, east, EdgeBehaviorAction.VALID)));

        LabNeighborhoodHighlights highlights = new LabNeighborhoodHighlights(
                ringCellsByRadius,
                validNeighborCoordinates,
                validNeighborsWithEdgeBehavior,
                neighborEdgeBehaviorResults);

        ring1.put(west, ringCell(1, west));
        ringCellsByRadius.put(2, new TreeMap<>());
        validNeighborCoordinates.add(center);
        eastNeighbors.add(neighbor(center, west));
        validNeighborsWithEdgeBehavior.put(center, List.of());
        neighborEdgeBehaviorResults.add(new EdgeBehaviorResult(west, west, EdgeBehaviorAction.VALID));

        assertAll(
                () -> assertEquals(List.of(0, 1), new ArrayList<>(highlights.ringCellsByRadius().keySet())),
                () -> assertEquals(Set.of(east), highlights.ringCellsByRadius().get(1).keySet()),
                () -> assertEquals(List.of(east, west), highlights.validNeighborCoordinates()),
                () -> assertEquals(List.of(east, west), new ArrayList<>(highlights.validNeighborsWithEdgeBehavior().keySet())),
                () -> assertEquals(List.of(neighbor(center, east)), highlights.validNeighborsWithEdgeBehavior().get(east)),
                () -> assertEquals(List.of(new EdgeBehaviorResult(east, east, EdgeBehaviorAction.VALID)),
                        highlights.neighborEdgeBehaviorResults()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.ringCellsByRadius().put(2, new TreeMap<>())),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.ringCellsByRadius().get(1).put(west, ringCell(1, west))),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.validNeighborCoordinates().add(center)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.validNeighborsWithEdgeBehavior().put(center, List.of())),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.validNeighborsWithEdgeBehavior().get(east).add(neighbor(center, west))),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> highlights.neighborEdgeBehaviorResults().add(new EdgeBehaviorResult(west, west, EdgeBehaviorAction.VALID)))
        );
    }

}
