package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;

import java.util.*;

public final class ForestSimulationManager
        extends AbstractTimedSimulationManager<ForestEntity, WritableGridModel<ForestEntity>, ForestConfig,
        ForestStatistics> {

    private final GridStructure structure;
    private final ForestStatistics statistics;
    private final TimedSimulationExecutor<ForestEntity, WritableGridModel<ForestEntity>> executor;

    public ForestSimulationManager(ForestConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new ForestStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new ArrayGridModel<>(structure, ForestEntity.EMPTY);

        // Executor with runner and terminationCondition
        var runner = new SynchronousStepRunner<>(model, new ForestUpdateStrategy(structure, config, random));
        var terminationCondition = new ForestTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentModel, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    @SuppressWarnings("MagicNumber")
    private void initializeGrid(ForestConfig config, WritableGridModel<ForestEntity> model, Random random) {
        double treePercent = config.treeDensity();
        double emptyPercent = 1.0d - treePercent;

        GridInitializer<ForestEntity> gridInitializer;
        if ((treePercent > 0) && (treePercent <= 0.75d)) {
            gridInitializer = GridInitializers.placeRandomPercent(
                    () -> ForestEntity.TREE,
                    ForestEntity::isEmpty,
                    treePercent, random);
        } else if (emptyPercent < 1.0d) {
            gridInitializer = GridInitializers.constant(ForestEntity.TREE)
                                              .andThen(GridInitializers.placeRandomPercent(
                                                      () -> ForestEntity.EMPTY,
                                                      ForestEntity::isTree,
                                                      emptyPercent, random));
        } else {
            gridInitializer = GridInitializers.identity();
        }
        gridInitializer.initialize(model);
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public ForestStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void updateInitialStatistics(ReadableGridModel<ForestEntity> model) {
        int treeEntities = (int) model.countEntities(ForestEntity::isTree);
        statistics.updateCells(treeEntities, 0);
    }

    @Override
    protected TimedSimulationExecutor<ForestEntity, WritableGridModel<ForestEntity>> executor() {
        return executor;
    }

}
