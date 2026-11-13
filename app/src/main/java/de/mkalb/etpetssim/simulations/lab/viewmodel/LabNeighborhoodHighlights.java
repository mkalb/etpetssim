package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.engine.neighborhood.RadiusRingCell;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Computed neighborhood data for a selected cell in the Simulation Lab, used to draw its neighborhood highlights on
 * the canvas.
 *
 * @param ringCellsByRadius              cells grouped by radius ring, used for ring highlight rendering
 * @param validNeighborCoordinates       neighbor coordinates within the highlight radius that lie within the grid
 * @param validNeighborsWithEdgeBehavior neighbors with edge-behavior details, keyed by coordinate
 * @param neighborEdgeBehaviorResults    edge-behavior results for all neighbors of the selected cell
 */
public record LabNeighborhoodHighlights(
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadius,
        List<GridCoordinate> validNeighborCoordinates,
        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> validNeighborsWithEdgeBehavior,
        List<EdgeBehaviorResult> neighborEdgeBehaviorResults) {

    public static final int RING_MAX_RADIUS = 5;
    public static final int NEIGHBOR_COORDINATE_RADIUS = 2;

    public LabNeighborhoodHighlights {
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadiusCopy =
                new TreeMap<>();
        ringCellsByRadius.forEach((radius, cellsByCoordinate) ->
                ringCellsByRadiusCopy.put(radius, Collections.unmodifiableSortedMap(new TreeMap<>(cellsByCoordinate))));
        ringCellsByRadius = Collections.unmodifiableSortedMap(ringCellsByRadiusCopy);

        validNeighborCoordinates = List.copyOf(validNeighborCoordinates);

        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> neighborsWithEdgeBehaviorCopy = new LinkedHashMap<>();
        validNeighborsWithEdgeBehavior.forEach((coordinate, neighborCells) ->
                neighborsWithEdgeBehaviorCopy.put(coordinate, List.copyOf(neighborCells)));
        validNeighborsWithEdgeBehavior = Collections.unmodifiableMap(neighborsWithEdgeBehaviorCopy);

        neighborEdgeBehaviorResults = List.copyOf(neighborEdgeBehaviorResults);
    }

    /**
     * Computes the neighborhood highlights of a cell from the current grid model.
     *
     * <p>The radius rings reach up to radius {@value #RING_MAX_RADIUS}, the neighbor coordinates up to radius
     * {@value #NEIGHBOR_COORDINATE_RADIUS}; neighbors outside the grid are left out.
     *
     * @param center           the coordinate of the selected cell
     * @param neighborhoodMode the neighborhood mode of the configuration
     * @param structure        the grid structure of {@code model}
     * @param model            the grid model that provides the entities of the ring cells
     * @return the computed neighborhood highlights
     */
    public static LabNeighborhoodHighlights from(GridCoordinate center,
                                                 NeighborhoodMode neighborhoodMode,
                                                 GridStructure structure,
                                                 ReadableGridModel<LabEntity> model) {
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadius =
                CellNeighborhoods.cellsByRadiusRings(center,
                        neighborhoodMode,
                        structure,
                        RING_MAX_RADIUS,
                        coordinate -> new GridCell<>(coordinate, model.getEntity(coordinate)));

        List<GridCoordinate> validNeighborCoordinates = CellNeighborhoods.neighborCoordinatesWithinRadius(
                        center,
                        neighborhoodMode,
                        structure.cellShape(),
                        NEIGHBOR_COORDINATE_RADIUS)
                .stream()
                .filter(structure::isCoordinateValid)
                .toList();

        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> validNeighborsWithEdgeBehavior = new LinkedHashMap<>();
        CellNeighborhoods.cellNeighborsWithEdgeBehavior(center, neighborhoodMode, structure)
                .forEach((neighborCoordinate, neighborCells) -> {
                    if (structure.isCoordinateValid(neighborCoordinate)) {
                        validNeighborsWithEdgeBehavior.put(neighborCoordinate, neighborCells);
                    }
                });

        List<EdgeBehaviorResult> neighborEdgeBehaviorResults = List.copyOf(
                CellNeighborhoods.neighborEdgeBehaviorResults(center, neighborhoodMode, structure));

        return new LabNeighborhoodHighlights(
                ringCellsByRadius,
                validNeighborCoordinates,
                validNeighborsWithEdgeBehavior,
                neighborEdgeBehaviorResults);
    }

}
