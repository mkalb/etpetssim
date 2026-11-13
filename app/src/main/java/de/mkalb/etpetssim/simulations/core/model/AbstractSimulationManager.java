package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepExecutionResult;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation for managers backed by a timed simulation executor.
 *
 * <p>Subclasses must supply the concrete {@link TimedSimulationExecutor} via {@link #executor()}
 * and update the statistics snapshot after each step via {@link #updateStatistics()}.
 * After each step, the manager records a sample of the given metrics in its history and extrema.
 *
 * @param <ENT> entity type used by the simulation model
 * @param <GM>  model type used for simulation execution
 * @param <CON> immutable configuration type
 * @param <STA> statistics snapshot type
 */
public abstract class AbstractSimulationManager<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends SimulationStatistics> {

    private final CON config;
    private final List<StatisticsMetric<STA>> metrics;
    private final StatisticsHistory statisticsHistory;
    private final StatisticsExtremaTracker statisticsExtremaTracker;

    /**
     * Creates a manager that samples the given metrics after each executed step.
     *
     * @param config  the immutable simulation configuration
     * @param metrics metric descriptors sampled after each executed step
     */
    protected AbstractSimulationManager(CON config, List<StatisticsMetric<STA>> metrics) {
        this.config = config;
        this.metrics = List.copyOf(metrics);
        statisticsHistory = new StatisticsHistory();
        statisticsExtremaTracker = new StatisticsExtremaTracker(metrics);
    }

    public final CON config() {
        return config;
    }

    /**
     * Returns the grid structure currently used by the simulation.
     *
     * @return grid structure
     */
    public abstract GridStructure structure();

    /**
     * Returns the latest statistics snapshot.
     *
     * @return simulation statistics
     */
    public abstract STA statistics();

    public final void executeStep() {
        var timedExecutor = executor();
        timedExecutor.executeStep();
        updateStatistics();
        recordStatisticsSample();
    }

    /**
     * Executes multiple simulation steps with optional early termination.
     *
     * @param count            maximum number of steps to execute
     * @param checkTermination whether logical termination should stop execution early
     * @param onStep           callback invoked after each executed step
     * @return aggregated execution result
     */
    public final StepExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep) {
        var timedExecutor = executor();
        return timedExecutor.executeSteps(count, checkTermination, () -> {
            updateStatistics();
            recordStatisticsSample();
            onStep.run();
        });
    }

    /**
     * Indicates whether the simulation reached its logical termination condition.
     *
     * @return {@code true} if logically finished
     */
    public final boolean isFinished() {
        return executor().isFinished();
    }

    /**
     * Indicates whether the executor reached its technical step limit.
     *
     * @return {@code true} if technically finished
     */
    public final boolean isExecutorFinished() {
        return executor().isExecutorFinished();
    }

    public final int stepCount() {
        return executor().stepCount();
    }

    public final GM currentGridModel() {
        return executor().currentGridModel();
    }

    /**
     * Returns immutable sampled statistics history ordered from oldest to newest.
     *
     * @return sampled statistics history
     */
    public final List<StatisticsSample> statisticsHistory() {
        return statisticsHistory.toList();
    }

    /**
     * Returns tracked minimum and maximum metric values.
     *
     * @return tracked metric extrema
     */
    public final StatisticsExtrema statisticsExtrema() {
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
     * Updates the statistics snapshot after each simulation step.
     * Called by {@link #executeStep()}, within the per-step callback of {@link #executeSteps}, and by
     * {@link #recordInitialStatisticsSample()}.
     */
    protected abstract void updateStatistics();

    /**
     * Returns the timed simulation executor backing this manager.
     *
     * @return the executor used for simulation step execution
     */
    protected abstract TimedSimulationExecutor<ENT, GM> executor();

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
                AppLogger
                        .errorf("AbstractSimulationManager: Replacing non-finite metric value with NaN: key: %s, "
                                        + "value: %f",
                                metric.key(), value);
                value = Double.NaN;
            }
            values.put(metric.key(), value);
        }

        var sample = new StatisticsSample(stepCount(), stepTimingStatistics(), values);
        statisticsHistory.add(sample);
        statisticsExtremaTracker.recordSample(sample.values(), sample.stepCount());
    }

}
