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

    private void initializeGrid(ForestConfig config, WritableGridModel<ForestEntity> model, Random random) {
        GridInitializer<ForestEntity> gridInitializer =
                GridInitializers.placeRandomPercentWithConstants(
                        config.treeDensity(),
                        ForestEntity.TREE,
                        ForestEntity.EMPTY,
                        random);
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
