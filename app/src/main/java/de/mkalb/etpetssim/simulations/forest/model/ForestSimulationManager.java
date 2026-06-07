package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;

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
        statistics = new ForestStatistics(structure);
        var random = new Random(config.seed());
        var model = new ArrayGridModel<>(structure, ForestEntity.EMPTY);

        var runner = new SynchronousStepRunner<>(model, new ForestUpdateStrategy(structure, config, random));
        var terminationCondition = new ForestTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentModel, terminationCondition, statistics));

        initializeGrid(config, model, random);

        initializeStatistics(model);
    }

    private void initializeGrid(ForestConfig config, WritableGridModel<ForestEntity> model, Random random) {
        var gridInitializer = GridInitializers.fillRandomPercent(
                () -> ForestEntity.TREE,
                config.treeDensity(),
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

    private void initializeStatistics(ReadableGridModel<ForestEntity> model) {
        int treeEntities = Math.toIntExact(model
                .countEntities(ForestEntity::isTree));
        statistics.updateCells(treeEntities, 0);
    }

    @Override
    protected TimedSimulationExecutor<ForestEntity, WritableGridModel<ForestEntity>> executor() {
        return executor;
    }

}
