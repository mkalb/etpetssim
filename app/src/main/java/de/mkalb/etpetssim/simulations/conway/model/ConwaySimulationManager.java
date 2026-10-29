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
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;

import java.util.Random;

public final class ConwaySimulationManager
        extends AbstractTimedSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
        ConwayStatistics> {

    private final GridStructure structure;
    private final ConwayStatistics statistics;
    private final TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor;

    public ConwaySimulationManager(ConwayConfig config) {
        this(config, SimulationInitializationCancellation.none());
    }

    public ConwaySimulationManager(ConwayConfig config, SimulationInitializationCancellation cancellation) {
        super(config, ConwayStatistics.metrics());
        cancellation.checkCanceled();

        structure = config.createGridStructure();
        statistics = new ConwayStatistics(structure);
        var random = new Random(config.seed());
        var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        var runner = new SynchronousStepRunner<>(model, new ConwayStepLogic(structure, config));
        var terminationCondition = new ConwayTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentGridModel, terminationCondition, statistics));

        initializeGrid(config, model, random, cancellation);
        cancellation.checkCanceled();
        initializeStatistics(model);
        recordInitialStatisticsSample();
    }

    private void initializeGrid(ConwayConfig config,
                                WritableGridModel<ConwayEntity> model,
                                Random random,
                                SimulationInitializationCancellation cancellation) {
        var gridInitializer = GridInitializers.fillRandomPercent(
                config.alivePercent(),
                () -> ConwayEntity.ALIVE,
                ConwayEntity.DEAD,
                random,
                cancellation::checkCanceled);
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
        statistics.setStepData(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    private void initializeStatistics(ReadableGridModel<ConwayEntity> model) {
        int aliveCellsInitial = Math.toIntExact(model.countEntities(ConwayEntity::isAlive));
        statistics.initializeStartupCellCounts(aliveCellsInitial);
    }

    @Override
    protected TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor() {
        return executor;
    }

}
