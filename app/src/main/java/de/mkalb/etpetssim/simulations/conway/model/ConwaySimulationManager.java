package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.*;

public final class ConwaySimulationManager {

    private final ConwayConfig config;

    private final GridStructure structure;
    private final ConwayStatistics statistics;
    private final SimulationExecutor<ConwayEntity> executor;

    public ConwaySimulationManager(ConwayConfig config) {
        this.config = config;

        structure = new GridStructure(
                new GridTopology(config.cellShape(), config.gridEdgeBehavior()),
                new GridSize(config.gridWidth(), config.gridHeight())
        );

        statistics = new ConwayStatistics(structure.cellCount());

        GridModel<ConwayEntity> model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        SynchronousStepRunner<ConwayEntity, ConwayStatistics> runner = new SynchronousStepRunner<>(model,
                new ConwayUpdateStrategy(structure));

        executor = new DefaultSimulationExecutor<>(runner, runner::currentModel, new ConwayTerminationCondition(), statistics);

        GridInitializers.placeRandomPercent(() -> ConwayEntity.ALIVE, config.alivePercent(), new java.util.Random()).initialize(model);

        updateStatistics(executor.currentStep(), executor.currentModel());
    }

    public void executeStep() {
        executor.executeStep();
        updateStatistics(executor.currentStep(), executor.currentModel());
    }

    void updateStatistics(long currentStep, GridModel<ConwayEntity> currentModel) {
        statistics.update(
                currentStep,
                currentModel.count(cell -> cell.entity().isAlive()));
    }

    public boolean isRunning() {
        return executor.isRunning();
    }

    public ReadableGridModel<ConwayEntity> currentModel() {
        return executor.currentModel();
    }

    public long currentStep() {
        return executor.currentStep();
    }

    public ConwayStatistics statistics() {
        return statistics;
    }

    public GridStructure structure() {
        return structure;
    }

    public ConwayConfig config() {
        return config;
    }

}
