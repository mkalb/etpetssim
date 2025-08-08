package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.model.*;

public abstract class AbstractTimedSimulationManager<ENT extends GridEntity, CON extends SimulationConfig, STA extends TimedSimulationStatistics>
        implements SimulationManager<ENT, CON, STA> {

    private final CON config;

    private long timeoutMillis = Long.MAX_VALUE;
    private Runnable onTimeout = () -> {};

    protected AbstractTimedSimulationManager(CON config) {
        this.config = config;
    }

    protected abstract void updateStatistics();

    protected abstract TimedSimulationExecutor<ENT> executor();

    @Override
    public final CON config() {
        return config;
    }

    @Override
    public final void executeStep() {
        executor().executeStep();
        updateStatistics();

        // Check for timeout
        if (executor().currentStepMillis() > timeoutMillis) {
            onTimeout.run();
        }
    }

    @Override
    public final void executeSteps(int count, Runnable onStep) {
        executor().executeSteps(count, () -> {
            updateStatistics();
            onStep.run();
        });
    }

    @Override
    public final boolean isRunning() {
        return executor().isRunning();
    }

    @Override
    public final int stepCount() {
        return executor().stepCount();
    }

    @Override
    public final ReadableGridModel<ENT> currentModel() {
        return executor().currentModel();
    }

    public final void configureStepTimeout(long newTimeoutMillis, Runnable newOnTimeout) {
        timeoutMillis = newTimeoutMillis;
        onTimeout = newOnTimeout;
        updateStatistics();
    }

    public final long timeoutMillis() {
        return timeoutMillis;
    }

    public final StepTimingStatistics stepTimingStatistics() {
        return executor().stepTimingStatistics();
    }

}
