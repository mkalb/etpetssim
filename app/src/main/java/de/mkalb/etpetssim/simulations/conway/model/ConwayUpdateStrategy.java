package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SynchronousStepLogic;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;

import java.util.*;

public final class ConwayUpdateStrategy implements SynchronousStepLogic<ConwayEntity, ConwayStatistics> {

    private final GridStructure structure;
    private final ConwayConfig config;

    public ConwayUpdateStrategy(GridStructure structure, ConwayConfig config) {
        this.structure = structure;
        this.config = config;
    }

    @Override
    public void performSynchronousStep(ReadableGridModel<ConwayEntity> currentModel,
                                       GridModel<ConwayEntity> nextModel,
                                       int stepIndex,
                                       ConwayStatistics statistics) {
        ConwayTransitionRules transitionRules = config.transitionRules();

        // TODO check if updating ConwayStatistics would be helpful here

        // long start = System.currentTimeMillis();
        updateGrid(currentModel, nextModel, transitionRules, statistics);
        // long duration = System.currentTimeMillis() - start;
        // AppLogger.info("Duration perform: " + duration);
    }

    private void updateGrid(ReadableGridModel<ConwayEntity> currentModel, GridModel<ConwayEntity> nextModel,
                            ConwayTransitionRules transitionRules, ConwayStatistics statistics) {
        Map<GridCoordinate, Integer> deadNeighborCounts = new HashMap<>();
        Set<GridCoordinate> alive = currentModel.nonDefaultCoordinates();

        int aliveCells = 0;

        for (GridCoordinate coordinate : alive) {
            int aliveNeighbors = 0;
            for (EdgeBehaviorResult result : CellNeighborhoods.neighborEdgeResults(coordinate, config.neighborhoodMode(), structure)) {
                if ((result.action() == EdgeBehaviorAction.VALID) || (result.action() == EdgeBehaviorAction.WRAPPED)) {
                    if (alive.contains(result.mapped())) {
                        aliveNeighbors++;
                    } else {
                        deadNeighborCounts.merge(result.mapped(), 1, Integer::sum);
                    }
                }
            }
            if (transitionRules.shouldSurvive(aliveNeighbors)) {
                nextModel.setEntity(coordinate, ConwayEntity.ALIVE);
                aliveCells++;
            }
        }

        for (Map.Entry<GridCoordinate, Integer> entry : deadNeighborCounts.entrySet()) {
            if (transitionRules.shouldBeBorn(entry.getValue())) {
                nextModel.setEntity(entry.getKey(), ConwayEntity.ALIVE);
                aliveCells++;
            }
        }

        statistics.updateAliveCells(aliveCells);
    }

}