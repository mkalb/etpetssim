package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.SynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.support.GridInitializer;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.core.model.AbstractSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;

import java.util.Random;

/**
 * Simulation manager of the Simulation Lab.
 */
public final class LabSimulationManager
        extends AbstractSimulationManager<LabEntity, WritableGridModel<LabEntity>, LabConfig, LabStatistics> {

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
        WritableGridModel<LabEntity> model = createGridModel();

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
        statistics.setStepCount(executor.stepCount());
    }

    @Override
    protected TimedSimulationExecutor<LabEntity, WritableGridModel<LabEntity>> executor() {
        return executor;
    }

    private WritableGridModel<LabEntity> createGridModel() {
        return switch (config().gridModelType()) {
            case SPARSE -> new SparseGridModel<>(structure, LabEntity.NORMAL);
            case ARRAY -> new ArrayGridModel<>(structure, LabEntity.NORMAL);
        };
    }

    private void initializeGrid(WritableGridModel<LabEntity> model,
                                Random random,
                                SimulationInitializationCancellation cancellation) {
        createGridInitializer(model.structure().size(), random, cancellation).initialize(model);
    }

    private GridInitializer<LabEntity> createGridInitializer(GridSize size,
                                                             Random random,
                                                             SimulationInitializationCancellation cancellation) {
        double highlightedPercent = config().highlightedPercent();
        return switch (config().distribution()) {
            case RANDOM_PERCENT -> GridInitializers.fillWithPercent(
                    highlightedPercent,
                    () -> LabEntity.HIGHLIGHTED,
                    LabEntity.NORMAL,
                    random,
                    cancellation::checkCanceled);
            case RANDOM_PROBABILITY -> GridInitializers.fillWithProbability(
                    highlightedPercent,
                    () -> LabEntity.HIGHLIGHTED,
                    LabEntity.NORMAL,
                    random,
                    cancellation::checkCanceled);
            // Rule deviation (simulations.md): no cancellation check; the lab exposes initializers without one.
            case BORDER -> GridInitializers.placeBorder(LabEntity.HIGHLIGHTED);
            case PATTERN -> createPatternInitializer(size);
        };
    }

    private GridInitializer<LabEntity> createPatternInitializer(GridSize size) {
        GridPattern<LabEntity> pattern = LabPatterns.createPattern(config().pattern(), size);
        // Rule deviation (simulations.md): no cancellation check; the lab exposes initializers without one.
        return GridInitializers.placePattern(pattern, LabPatterns.computeAnchor(pattern, size));
    }

    private void initializeStatistics(ReadableGridModel<LabEntity> model) {
        int highlightedCellsInitial = Math.toIntExact(model.countEntities(e -> e == LabEntity.HIGHLIGHTED));
        statistics.setCellCounts(highlightedCellsInitial);
    }

}
