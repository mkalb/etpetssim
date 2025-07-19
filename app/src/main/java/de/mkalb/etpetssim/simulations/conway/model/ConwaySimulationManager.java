package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;

public final class ConwaySimulationManager {

    private final ConwayConfig config;

    private final GridStructure structure;
    private final SimulationExecutor<ConwayEntity> executor;
    private final ConwayStatistics statistics;

    public ConwaySimulationManager(ConwayConfig config) {
        this.config = config;

        structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(config.gridWidth(), config.gridHeight())
        );
        GridModel<ConwayEntity> model = new SparseGridModel<>(structure, ConwayEntity.DEAD);
        GridInitializers.placeRandomPercent(() -> ConwayEntity.ALIVE, config.alivePercent(), new java.util.Random()).initialize(model);
        SynchronousStepRunner<ConwayEntity> runner = new SynchronousStepRunner<>(model, new ConwayUpdateStrategy(structure));
        executor = new DefaultSimulationExecutor<>(runner, runner::currentModel, new ConwayTerminationCondition());
        statistics = new ConwayStatistics(structure.cellCount());
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