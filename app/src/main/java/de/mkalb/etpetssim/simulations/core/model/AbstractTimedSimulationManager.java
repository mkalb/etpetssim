package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.*;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Base implementation for managers backed by a timed simulation executor.
 *
 * <p>Subclasses must supply the concrete {@link TimedSimulationExecutor} via {@link #executor()}
 * and update the statistics snapshot after each step via {@link #updateStatistics()}.
 * Optional hook methods allow subclasses to react after step execution without overriding
 * the final step-dispatch methods.
 *
 * @param <ENT> entity type used by the simulation model
 * @param <GM>  model type used for simulation execution
 * @param <CON> immutable configuration type
 * @param <STA> timing-aware statistics snapshot type
 */
public abstract class AbstractTimedSimulationManager<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics>
        implements SimulationManager<ENT, GM, CON, STA> {

    private final CON config;

    /**
     * Initializes the base manager with the given immutable configuration.
     *
     * @param config the immutable simulation configuration
     */
    protected AbstractTimedSimulationManager(CON config) {
        this.config = config;
    }

    /**
     * Updates the statistics snapshot after each simulation step.
     * Called by {@link #executeStep()} and within the per-step callback of {@link #executeSteps}.
     */
    protected abstract void updateStatistics();

    /**
     * Returns the timed simulation executor backing this manager.
     *
     * @return the executor used for simulation step execution
     */
    protected abstract TimedSimulationExecutor<ENT, GM> executor();

    @Override
    public final CON config() {
        return config;
    }

    @Override
    public final void executeStep() {
        var timedExecutor = executor();
        timedExecutor.executeStep();
        updateStatistics();
        afterStepExecuted();
    }

    @Override
    public final StepExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep) {
        var timedExecutor = executor();
        var result = timedExecutor.executeSteps(count, checkTermination, () -> {
            updateStatistics();
            onStep.run();
        });
        // Keep snapshots synchronized even when no per-step callback is triggered.
        updateStatistics();
        afterStepsExecuted(result);
        return result;
    }

    /**
     * Hook invoked after a single step has been executed and statistics updated.
     *
     * <p>The default implementation does nothing. Subclasses may override this method
     * to perform additional processing after each individual step.
     */
    @SuppressWarnings({"EmptyMethod", "NoopMethodInAbstractClass"})
    protected void afterStepExecuted() {
    }

    /**
     * Hook invoked after a multistep batch has been executed.
     *
     * <p>The default implementation does nothing. Subclasses may override this method
     * to react to the aggregated {@link StepExecutionResult} of the completed batch.
     *
     * @param result the aggregated execution result of the completed batch
     */
    @SuppressWarnings({"EmptyMethod", "NoopMethodInAbstractClass", "unused"})
    protected void afterStepsExecuted(StepExecutionResult result) {
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

    /**
     * Returns the current step timing statistics from the executor.
     *
     * @return timing statistics for the most recently executed step or batch
     */
    public final StepTimingStatistics stepTimingStatistics() {
        return executor().stepTimingStatistics();
    }

}
