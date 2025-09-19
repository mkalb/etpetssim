package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationManager;

import java.util.*;

public final class LangtonSimulationManager
        extends AbstractTimedSimulationManager<LangtonEntity, CompositeGridModel<LangtonEntity>, LangtonConfig,
        LangtonStatistics> {

    private final GridStructure structure;
    private final LangtonStatistics statistics;
    private final TimedSimulationExecutor<LangtonEntity, CompositeGridModel<LangtonEntity>> executor;

    public LangtonSimulationManager(LangtonConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new LangtonStatistics(structure.cellCount());
        WritableGridModel<LangtonEntity> groundModel = new SparseGridModel<>(structure, LangtonGroundEntity.UNVISITED);
        WritableGridModel<LangtonEntity> antModel = new SparseGridModel<>(structure, LangtonAntNone.NONE);
        CompositeGridModel<LangtonEntity> compositeGridModel = new CompositeGridModel<>(List.of(groundModel, antModel));

        // Executor with runner and terminationCondition
        var runner = new LangtonStepRunner(compositeGridModel);
        var terminationCondition = new LangtonTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::compositeGridModel, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, compositeGridModel);

        updateInitialStatistics(config, compositeGridModel);
    }

    private void initializeGrid(LangtonConfig config, CompositeGridModel<LangtonEntity> compositeGridModel) {
        // TODO implement initializeGrid
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public LangtonStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    private void updateInitialStatistics(LangtonConfig config, CompositeGridModel<LangtonEntity> compositeGridModel) {
        // TODO implement updateInitialStatistics
    }

    @Override
    protected TimedSimulationExecutor<LangtonEntity, CompositeGridModel<LangtonEntity>> executor() {
        return executor;
    }

}
