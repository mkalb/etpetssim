package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.executor.*;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;

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
    private final List<StatisticMetric<STA>> metrics;
    private final StatisticHistory statisticsHistory;
    private final StatisticExtremaTracker statisticsExtremaTracker;

    /**
     * Initializes the base manager with the given immutable configuration.
     *
     * @param config the immutable simulation configuration
     */
    protected AbstractTimedSimulationManager(CON config) {
        this(config, List.of());
    }

    /**
     * Initializes the base manager with configuration and metric descriptors used for
     * immutable sample history and generic extrema tracking.
     *
     * @param config  the immutable simulation configuration
     * @param metrics metric descriptors sampled after each executed step
     */
    protected AbstractTimedSimulationManager(CON config,
                                             List<StatisticMetric<STA>> metrics) {
        this.config = config;
        this.metrics = List.copyOf(metrics);
        statisticsHistory = new StatisticHistory();
        statisticsExtremaTracker = new StatisticExtremaTracker(metrics);
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
        recordStatisticsSample();
        afterStepExecuted();
    }

    @Override
    public final StepExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep) {
        var timedExecutor = executor();
        var result = timedExecutor.executeSteps(count, checkTermination, () -> {
            updateStatistics();
            recordStatisticsSample();
            onStep.run();
        });
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

    @Override
    public final List<StatisticSample> statisticsHistory() {
        return statisticsHistory.asList();
    }

    @Override
    public final StatisticExtrema statisticsExtrema() {
        return statisticsExtremaTracker.snapshot();
    }

    /**
     * Returns the current step timing statistics from the executor.
     *
     * @return timing statistics for the most recently executed step or batch
     */
    public final StepTimingStatistics stepTimingStatistics() {
        return executor().stepTimingStatistics();
    }

    /**
     * Records the initial step-0 sample after simulation-specific startup counters were initialized.
     *
     * <p>Subclasses should call this once at the end of their constructor.
     */
    protected final void recordInitialStatisticsSample() {
        updateStatistics();
        recordStatisticsSample();
    }

    private void recordStatisticsSample() {
        STA liveStatistics = statistics();
        Map<String, Double> values = new LinkedHashMap<>(metrics.size());
        for (var metric : metrics) {
            double value = metric.extractor().applyAsDouble(liveStatistics);
            if (!Double.isFinite(value)) {
                AppLogger.errorf("AbstractTimedSimulationManager: non-finite metric value replaced with NaN: key=%s, value=%f",
                        metric.key(), value);
                value = Double.NaN;
            }
            values.put(metric.key(), value);
        }

        var sample = new StatisticSample(stepCount(), stepTimingStatistics(), values);
        statisticsHistory.add(sample);
        statisticsExtremaTracker.update(sample.values(), sample.stepCount());
    }

}
