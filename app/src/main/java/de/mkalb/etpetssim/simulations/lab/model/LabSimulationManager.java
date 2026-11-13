package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.SynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;

import java.util.Random;

/**
 * Simulation manager of the Simulation Lab.
 */
public final class LabSimulationManager
        extends AbstractTimedSimulationManager<LabEntity, WritableGridModel<LabEntity>, LabConfig, LabStatistics> {

    private final GridStructure structure;
    private final LabStatistics statistics;
    private final TimedSimulationExecutor<LabEntity, WritableGridModel<LabEntity>> executor;

    public LabSimulationManager(LabConfig config) {
        this(config, SimulationInitializationCancellation.none());
    }

    public LabSimulationManager(LabConfig config, SimulationInitializationCancellation cancellation) {
        super(config, LabStatistics.metrics());
        cancellation.checkCanceled();

        structure = config.createGridStructure();
        statistics = new LabStatistics(structure);
        var random = new Random(config.seed());
        var model = new SparseGridModel<>(structure, LabEntity.NORMAL);

        var runner = new SynchronousStepRunner<>(model, new LabStepLogic());
        var terminationCondition = new LabTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentGridModel,
                terminationCondition, statistics));

        initializeGrid(model, random, cancellation);
        cancellation.checkCanceled();
        initializeStatistics(model);
        recordInitialStatisticsSample();
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public LabStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.setStepData(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @Override
    protected TimedSimulationExecutor<LabEntity, WritableGridModel<LabEntity>> executor() {
        return executor;
    }

    private void initializeGrid(WritableGridModel<LabEntity> model,
                                Random random,
                                SimulationInitializationCancellation cancellation) {
        var gridInitializer = GridInitializers.fillRandomPercent(
                config().highlightedPercent(),
                () -> LabEntity.HIGHLIGHTED,
                LabEntity.NORMAL,
                random,
                cancellation::checkCanceled);
        gridInitializer.initialize(model);
    }

    private void initializeStatistics(ReadableGridModel<LabEntity> model) {
        int highlightedCellsInitial = Math.toIntExact(model.countEntities(e -> e == LabEntity.HIGHLIGHTED));
        statistics.setCellCounts(highlightedCellsInitial);
    }

}
