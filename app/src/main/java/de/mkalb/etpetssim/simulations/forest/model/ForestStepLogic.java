package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.SynchronousStepLogic;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Synchronous step logic of the Forest-fire model that grows, ignites, and burns down trees.
 */
public final class ForestStepLogic implements SynchronousStepLogic<ForestEntity, ForestStatistics> {

    private final GridStructure structure;
    private final ForestConfig config;
    private final Random random;

    public ForestStepLogic(GridStructure structure, ForestConfig config, Random random) {
        this.structure = structure;
        this.config = config;
        this.random = random;
    }

    @Override
    public void performSynchronousStep(ReadableGridModel<ForestEntity> currentGridModel,
                                       WritableGridModel<ForestEntity> nextGridModel,
                                       int stepIndex,
                                       ForestStatistics statistics) {
        Set<GridCoordinate> burningNeighborCoordinates = collectBurningNeighborCoordinates(currentGridModel);
        int treeCells = 0;
        int burningCells = 0;
        for (GridCell<ForestEntity> cell : currentGridModel.allCells()) {
            ForestEntity nextEntity = computeNextEntity(cell, burningNeighborCoordinates);
            if (nextEntity.isTree()) {
                treeCells++;
            } else if (nextEntity.isBurning()) {
                burningCells++;
            }
            if (!nextEntity.isEmpty()) {
                nextGridModel.setEntity(cell.coordinate(), nextEntity);
            }
        }

        statistics.setCellCounts(treeCells, burningCells);
    }

    /**
     * Collects the coordinates of all cells that have a burning neighbor.
     *
     * <p>Starting from the burning cells, which are usually few, avoids a neighbor search for every tree; this
     * relies on the symmetric neighbor relation of all cell shapes and edge behaviors.
     *
     * @param model the current grid model
     * @return the coordinates within the grid that have at least one burning neighbor
     */
    private Set<GridCoordinate> collectBurningNeighborCoordinates(ReadableGridModel<ForestEntity> model) {
        return model.filteredCoordinates(ForestEntity::isBurning)
                .stream()
                .flatMap(coordinate -> CellNeighborhoods.cellNeighborsIgnoringEdgeBehavior(coordinate,
                        config.neighborhoodMode(), structure.cellShape()))
                .map(neighbor -> CellNeighborhoods.applyEdgeBehaviorToCoordinate(neighbor.neighborCoordinate(),
                        structure))
                .filter(result -> result.action().isWithinGrid())
                .map(EdgeBehaviorResult::mapped)
                .collect(Collectors.toSet());
    }

    private ForestEntity computeNextEntity(GridCell<ForestEntity> cell,
                                           Set<GridCoordinate> burningNeighborCoordinates) {
        return switch (cell.entity()) {
            case EMPTY -> (random.nextDouble() < config.treeGrowthProbability())
                    ? ForestEntity.TREE : ForestEntity.EMPTY;
            // A tree with a burning neighbor ignites without a random draw; keep the order of the conditions,
            // because it determines the random sequence of a seed.
            case TREE -> (burningNeighborCoordinates.contains(cell.coordinate())
                    || (random.nextDouble() < config.lightningIgnitionProbability()))
                    ? ForestEntity.BURNING : ForestEntity.TREE;
            case BURNING -> ForestEntity.EMPTY;
        };
    }

}
