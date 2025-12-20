package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;

import java.util.*;

@SuppressWarnings("ClassCanBeRecord")
public final class ForestUpdateStrategy implements SynchronousStepLogic<ForestEntity, ForestStatistics> {

    private final GridStructure structure;
    private final ForestConfig config;
    private final Random random;

    public ForestUpdateStrategy(GridStructure structure, ForestConfig config, Random random) {
        this.structure = structure;
        this.config = config;
        this.random = random;
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    @Override
    public void performSynchronousStep(ReadableGridModel<ForestEntity> currentModel,
                                       WritableGridModel<ForestEntity> nextModel,
                                       int stepIndex,
                                       ForestStatistics statistics) {
        currentModel.cells()
                    .map(c -> processCell(c, currentModel))
                    .filter(c -> !c.entity().isEmpty())
                    .forEach(nextModel::setEntity);
        int treeCells = (int) nextModel.countEntities(ForestEntity::isTree);
        int burningCells = (int) nextModel.countEntities(ForestEntity::isBurning);

        statistics.updateCells(treeCells, burningCells);
    }

    private GridCell<ForestEntity> processCell(GridCell<ForestEntity> cell,
                                               ReadableGridModel<ForestEntity> model) {
        return switch (cell.entity()) {
            case EMPTY -> {
                if (random.nextDouble() < config.treeGrowthProbability()) {
                    yield new GridCell<>(cell.coordinate(), ForestEntity.TREE);
                }
                yield cell;
            }
            case TREE -> {
                if (hasBurningNeighbor(cell, model)
                        || (random.nextDouble() < config.lightningIgnitionProbability())) {
                    yield new GridCell<>(cell.coordinate(), ForestEntity.BURNING);
                }
                yield cell;
            }
            case BURNING -> new GridCell<>(cell.coordinate(), ForestEntity.EMPTY);
        };
    }

    private boolean hasBurningNeighbor(GridCell<ForestEntity> cell, ReadableGridModel<ForestEntity> model) {
        return CellNeighborhoods.cellNeighborsIgnoringEdgeBehavior(cell.coordinate(), config.neighborhoodMode(), structure.cellShape())
                                .map(neighbor -> CellNeighborhoods.applyEdgeBehaviorToCoordinate(neighbor.neighborCoordinate(), structure))
                                .filter(r -> (r.action() == EdgeBehaviorAction.VALID) || (r.action() == EdgeBehaviorAction.WRAPPED))
                                .map(EdgeBehaviorResult::mapped)
                                .map(model::getEntity)
                                .anyMatch(ForestEntity::isBurning);
    }

}