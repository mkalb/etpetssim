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
        int lightningStrikes = 0;
        int burnedTrees = 0;
        for (GridCell<ForestEntity> cell : currentGridModel.allCells()) {
            GridCoordinate coordinate = cell.coordinate();
            switch (cell.entity()) {
                case EMPTY -> {
                    if (random.nextDouble() < config.treeGrowthProbability()) {
                        nextGridModel.setEntity(coordinate, ForestEntity.TREE);
                        treeCells++;
                    }
                }
                case TREE -> {
                    // A tree with a burning neighbor ignites without a random draw for lightning.
                    if (burningNeighborCoordinates.contains(coordinate)) {
                        nextGridModel.setEntity(coordinate, ForestEntity.BURNING);
                        burningCells++;
                    } else if (random.nextDouble() < config.lightningIgnitionProbability()) {
                        nextGridModel.setEntity(coordinate, ForestEntity.BURNING);
                        burningCells++;
                        lightningStrikes++;
                    } else {
                        nextGridModel.setEntity(coordinate, ForestEntity.TREE);
                        treeCells++;
                    }
                }
                // The next grid model starts empty, so a burned-down tree needs no write.
                case BURNING -> burnedTrees++;
            }
        }

        statistics.updateStepCounts(treeCells, burningCells, lightningStrikes, burnedTrees);
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

}
