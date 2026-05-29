package de.mkalb.etpetssim.simulations.lab.shared;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.engine.neighborhood.RadiusRingCell;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;

import java.util.*;

/**
 * Computed neighborhood data for a selected cell in the simulation lab,
 * used to drive the visual highlight overlay on the canvas.
 *
 * @param ringCellsByRadius           cells grouped by radius ring, used for ring highlight rendering
 * @param validNeighborCoordinates    direct neighbor coordinates that lie within the grid
 * @param validNeighborsWithEdgeBehavior neighbors with edge-behavior details, keyed by coordinate
 * @param neighborEdgeResults         edge-behavior results for all neighbors of the selected cell
 */
public record LabNeighborhoodHighlights(
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadius,
        List<GridCoordinate> validNeighborCoordinates,
        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> validNeighborsWithEdgeBehavior,
        List<EdgeBehaviorResult> neighborEdgeResults) {

    public LabNeighborhoodHighlights {
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadiusCopy = new TreeMap<>();
        ringCellsByRadius.forEach((radius, cellsByCoordinate) ->
                ringCellsByRadiusCopy.put(radius, Collections.unmodifiableSortedMap(new TreeMap<>(cellsByCoordinate))));
        ringCellsByRadius = Collections.unmodifiableSortedMap(ringCellsByRadiusCopy);

        validNeighborCoordinates = List.copyOf(validNeighborCoordinates);

        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> neighborsWithEdgeBehaviorCopy = new LinkedHashMap<>();
        validNeighborsWithEdgeBehavior.forEach((coordinate, neighborCells) ->
                neighborsWithEdgeBehaviorCopy.put(coordinate, List.copyOf(neighborCells)));
        validNeighborsWithEdgeBehavior = Collections.unmodifiableMap(neighborsWithEdgeBehaviorCopy);

        neighborEdgeResults = List.copyOf(neighborEdgeResults);
    }

}

