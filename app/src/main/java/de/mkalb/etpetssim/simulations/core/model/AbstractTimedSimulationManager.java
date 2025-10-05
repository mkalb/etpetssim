package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.model.*;

public abstract class AbstractTimedSimulationManager<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics>
        implements SimulationManager<ENT, GM, CON, STA> {

    private final CON config;

    protected AbstractTimedSimulationManager(CON config) {
        this.config = config;
    }

    protected abstract void updateStatistics();

    protected abstract TimedSimulationExecutor<ENT, GM> executor();

    @Override
    public final CON config() {
        return config;
    }

    @Override
    public final void executeStep() {
        executor().executeStep();
        updateStatistics();
        afterStepExecuted();
    }

    @Override
    public final SimulationExecutor.ExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep) {
        var result = executor().executeSteps(count, checkTermination, () -> {
            updateStatistics();
            onStep.run();
        });
        afterStepsExecuted(result);
        return result;
    }

    @SuppressWarnings({"EmptyMethod", "NoopMethodInAbstractClass"})
    protected void afterStepExecuted() {
        // Do nothing. Can be overridden by subclasses.
    }

    @SuppressWarnings({"EmptyMethod", "NoopMethodInAbstractClass", "unused"})
    protected void afterStepsExecuted(SimulationExecutor.ExecutionResult result) {
        // Do nothing. Can be overridden by subclasses.
    }

    @Override
    public final boolean isFinished() {
        return executor().isFinished();
    }

    @Override
    public final boolean isExecutorFinished() {
        return executor().isExecutorFinished();
    }

    @Override
    public final int stepCount() {
        return executor().stepCount();
    }

    @Override
    public final GM currentModel() {
        return executor().currentModel();
    }

    public final StepTimingStatistics stepTimingStatistics() {
        return executor().stepTimingStatistics();
    }

}
