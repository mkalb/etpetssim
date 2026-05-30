package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.SynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;

import java.util.*;

public final class ConwaySimulationManager
        extends AbstractTimedSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
        ConwayStatistics> {

    private final GridStructure structure;
    private final ConwayStatistics statistics;
    private final TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor;

    public ConwaySimulationManager(ConwayConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new ConwayStatistics(structure);
        var random = new Random(config.seed());
        var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        var runner = new SynchronousStepRunner<>(model, new ConwayUpdateStrategy(structure, config));
        var terminationCondition = new ConwayTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentModel, terminationCondition, statistics));

        initializeGrid(config, model, random);

        initializeStatistics(model);
    }

    private void initializeGrid(ConwayConfig config, WritableGridModel<ConwayEntity> model, Random random) {
        var gridInitializer = GridInitializers.fillRandomPercent(
                () -> ConwayEntity.ALIVE,
                config.alivePercent(),
                ConwayEntity.DEAD,
                random);
        gridInitializer.initialize(model);
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public ConwayStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    private void initializeStatistics(ReadableGridModel<ConwayEntity> model) {
        int aliveEntities = Math.toIntExact(model
                .countEntities(ConwayEntity::isAlive));
        statistics.updateCells(aliveEntities, aliveEntities);
    }

    @Override
    protected TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor() {
        return executor;
    }

}
