package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.SynchronousStepLogic;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

        Map<GridCoordinate, Integer> deadNeighborCounts = new HashMap<>();
        Set<GridCoordinate> alive = currentGridModel.nonDefaultCoordinates();

        for (var coordinate : alive) {
            int aliveNeighbors = 0;
            for (var result : CellNeighborhoods.neighborEdgeBehaviorResults(coordinate, config.neighborhoodMode(), structure)) {
                if ((result.action() == EdgeBehaviorAction.VALID) || (result.action() == EdgeBehaviorAction.WRAPPED)) {
                    if (alive.contains(result.mapped())) {
                        aliveNeighbors++;
                    } else {
                        deadNeighborCounts.merge(result.mapped(), 1, Integer::sum);
                    }
                }
            }
            if (config.transitionRules().shouldSurvive(aliveNeighbors)) {
                nextGridModel.setEntity(coordinate, ConwayEntity.ALIVE);
                aliveCells++;
            } else {
                changedCells++;
            }
        }

        for (var entry : deadNeighborCounts.entrySet()) {
            if (config.transitionRules().shouldBeBorn(entry.getValue())) {
                nextGridModel.setEntity(entry.getKey(), ConwayEntity.ALIVE);
                aliveCells++;
                changedCells++;
            }
        }

        statistics.setCellCounts(aliveCells, changedCells);
    }

}
