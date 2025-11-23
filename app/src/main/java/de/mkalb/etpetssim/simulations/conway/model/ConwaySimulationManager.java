package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
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
        statistics = new ConwayStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        // Executor with runner and terminationCondition
        var runner = new SynchronousStepRunner<>(model, new ConwayUpdateStrategy(structure, config));
        var terminationCondition = new ConwayTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentModel, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    private void initializeGrid(ConwayConfig config, WritableGridModel<ConwayEntity> model, Random random) {
        GridInitializer<ConwayEntity> gridInitializer =
                GridInitializers.placeRandomPercentWithConstants(
                        config.alivePercent(),
                        ConwayEntity.ALIVE,
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

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void updateInitialStatistics(ReadableGridModel<ConwayEntity> model) {
        int aliveEntities = (int) model.countEntities(ConwayEntity::isAlive);
        statistics.updateCells(aliveEntities, aliveEntities);
    }

    @Override
    protected TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor() {
        return executor;
    }

}
