package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.SynchronousStepLogic;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighbor;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Synchronous step logic of Conway's Game of Life that applies the survive and birth rules to all cells.
 */
public final class ConwayStepLogic implements SynchronousStepLogic<ConwayEntity, ConwayStatistics> {

    private final GridStructure structure;
    private final ConwayConfig config;

    public ConwayStepLogic(GridStructure structure, ConwayConfig config) {
        this.structure = structure;
        this.config = config;
    }

    @Override
    public void performSynchronousStep(ReadableGridModel<ConwayEntity> currentGridModel,
                                       WritableGridModel<ConwayEntity> nextGridModel,
                                       int stepIndex,
                                       ConwayStatistics statistics) {
        // Counter for ConwayStatistics
        int aliveCells = 0;
        int changedCells = 0;

        Set<GridCoordinate> alive = currentGridModel.nonDefaultCoordinates();
        int[] aliveNeighborCounts = new int[structure.cellCount()];
        List<GridCoordinate> neighborCoordinates = countAliveNeighbors(alive, aliveNeighborCounts);

        for (var coordinate : alive) {
            if (config.transitionRules().shouldSurvive(aliveNeighborCounts[cellIndex(coordinate)])) {
                nextGridModel.setEntity(coordinate, ConwayEntity.ALIVE);
                aliveCells++;
            } else {
                changedCells++;
            }
        }

        for (var coordinate : neighborCoordinates) {
            if (!alive.contains(coordinate)
                    && config.transitionRules().shouldBeBorn(aliveNeighborCounts[cellIndex(coordinate)])) {
                nextGridModel.setEntity(coordinate, ConwayEntity.ALIVE);
                aliveCells++;
                changedCells++;
            }
        }

        statistics.setCellCounts(aliveCells, changedCells);
    }

    /**
     * Counts the living neighbors of all cells that have at least one living neighbor.
     *
     * <p>Starting from the living cells avoids a neighbor search for every cell; this relies on the symmetric
     * neighbor relation of all cell shapes and edge behaviors. It also relies on the minimum grid size, which ensures
     * that the neighbors of a cell map to different cells, also when the grid wraps.
     *
     * @param alive               the coordinates of the living cells
     * @param aliveNeighborCounts the counts per cell index, all {@code 0} on entry; filled by this method
     * @return the coordinates within the grid that have at least one living neighbor, each once
     */
    private List<GridCoordinate> countAliveNeighbors(Set<GridCoordinate> alive, int[] aliveNeighborCounts) {
        List<GridCoordinate> neighborCoordinates = new ArrayList<>();
        for (var coordinate : alive) {
            for (CellNeighbor neighbor : CellNeighborhoods.cellNeighborsIgnoringEdgeBehavior(coordinate,
                    config.neighborhoodMode(), structure.cellShape()).toList()) {
                EdgeBehaviorResult result = CellNeighborhoods.applyEdgeBehaviorToCoordinate(
                        neighbor.neighborCoordinate(), structure);
                if (result.action().isWithinGrid()) {
                    int index = cellIndex(result.mapped());
                    if (aliveNeighborCounts[index] == 0) {
                        neighborCoordinates.add(result.mapped());
                    }
                    aliveNeighborCounts[index]++;
                }
            }
        }
        return neighborCoordinates;
    }

    private int cellIndex(GridCoordinate coordinate) {
        return (coordinate.y() * structure.size().width()) + coordinate.x();
    }

}
