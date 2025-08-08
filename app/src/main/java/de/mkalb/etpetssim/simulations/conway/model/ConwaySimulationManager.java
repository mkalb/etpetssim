package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationManager;

public final class ConwaySimulationManager
        extends AbstractTimedSimulationManager<ConwayEntity, ConwayConfig, ConwayStatistics> {

    private final GridStructure structure;
    private final ConwayStatistics statistics;
    private final TimedSimulationExecutor<ConwayEntity> executor;

    public ConwaySimulationManager(ConwayConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new ConwayStatistics(structure.cellCount());
        var random = new java.util.Random();
        var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        // Executor with runner and terminationCondition
        SynchronousStepRunner<ConwayEntity, ConwayStatistics> runner = new SynchronousStepRunner<>(model,
                new ConwayUpdateStrategy(structure));
        var terminationCondition = new ConwayTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentModel, terminationCondition, statistics));

        GridInitializers.placeRandomPercent(() -> ConwayEntity.ALIVE, config.alivePercent(), random).initialize(model);

        updateStatistics();
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
                executor.currentModel().count(cell -> cell.entity().isAlive()),
                timeoutMillis(),
                executor.stepTimingStatistics());
        // TODO implement method countNonDefault
    }

    @Override
    protected TimedSimulationExecutor<ConwayEntity> executor() {
        return executor;
    }

}
