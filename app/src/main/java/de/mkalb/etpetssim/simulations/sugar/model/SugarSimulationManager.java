package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

import java.util.*;

public final class SugarSimulationManager
        extends AbstractTimedSimulationManager<SugarEntity, SugarGridModel, SugarConfig,
        SugarStatistics> {

    private final GridStructure structure;
    private final SugarStatistics statistics;
    private final TimedSimulationExecutor<SugarEntity, SugarGridModel> executor;

    public SugarSimulationManager(SugarConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new SugarStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new SugarGridModel(structure,
                new SparseGridModel<>(structure, SugarResourceNone.NONE),
                new SparseGridModel<>(structure, SugarAgentNone.NONE));

        // Executor with runner and terminationCondition
        var runner = new SugarStepRunner(config, random, model);
        var terminationCondition = new SugarTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    private void initializeGrid(SugarConfig config, SugarGridModel model, Random random) {
        GridInitializer<SugarResourceEntity> resourceGridInitializer =
                GridInitializers.fillRandomPercent(
                        () -> new SugarResourceSugar(config.maxSugarAmount(), config.maxSugarAmount()),
                        config.sugarPercent(), SugarResourceNone.NONE,
                        random);
        resourceGridInitializer.initialize(model.resourceModel());

        GridInitializer<SugarAgentEntity> agentGridInitializer =
                GridInitializers.fillRandomPercent(
                        () -> new SugarAgent(config.agentInitialEnergy()),
                        config.agentPercent(),
                        SugarAgentNone.NONE,
                        random);
        agentGridInitializer.initialize(model.agentModel());
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public SugarStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @SuppressWarnings({"NumericCastThatLosesPrecision"})
    private void updateInitialStatistics(SugarGridModel model) {
        int resourceCellsInitial = (int) model.resourceModel().countEntities(e -> !e.isNone());
        int agentCellsInitial = (int) model.agentModel().countEntities(e -> !e.isNone());
        statistics.updateInitialCells(resourceCellsInitial, agentCellsInitial);
    }

    @Override
    protected TimedSimulationExecutor<SugarEntity, SugarGridModel> executor() {
        return executor;
    }

}
