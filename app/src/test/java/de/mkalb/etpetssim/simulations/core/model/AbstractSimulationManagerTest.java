package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.engine.executor.SynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStepLogic;
import de.mkalb.etpetssim.simulations.conway.model.ConwayTerminationCondition;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Execution(ExecutionMode.SAME_THREAD)
final class AbstractSimulationManagerTest {

    private static final String KEY_INFINITE = "infinite";

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static ConwayConfig createConfig() {
        return new ConwayConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT
        );
    }

    private static TestSimulationManager createManager() {
        return new TestSimulationManager(createConfig(), ConwayStatistics.metrics());
    }

    @Test
    void testConfig() {
        ConwayConfig config = createConfig();
        TestSimulationManager manager = new TestSimulationManager(config, ConwayStatistics.metrics());

        assertSame(config, manager.config());
    }

    @Test
    void testExecuteStepRecordsOneSample() {
        TestSimulationManager manager = createManager();

        manager.executeStep();

        var history = manager.statisticsHistory();
        assertAll(
                () -> assertEquals(2, history.size()),
                () -> assertEquals(1, history.getLast().stepCount())
        );
    }

    @Test
    void testExecuteStepsRecordsEveryExecutedStepWithoutDuplicateFinalSample() {
        TestSimulationManager manager = createManager();

        int executeCount = 7;
        manager.executeSteps(executeCount, false, () -> {
        });

        var history = manager.statisticsHistory();
        long finalStepSamples = history.stream()
                .filter(sample -> sample.stepCount() == executeCount)
                .count();
        assertAll(
                () -> assertEquals(executeCount + 1, history.size()),
                () -> assertEquals(executeCount, history.getLast().stepCount()),
                () -> assertEquals(1L, finalStepSamples)
        );
    }

    @Test
    void testExecuteStepsDoesNotRecordSampleForZeroSteps() {
        TestSimulationManager manager = createManager();
        var historyBefore = manager.statisticsHistory();

        var result = manager.executeSteps(0, false, () -> fail("callback must not run"));

        assertAll(
                () -> assertEquals(0, result.stepCount()),
                () -> assertEquals(0, result.executedSteps()),
                () -> assertFalse(result.isFinished()),
                () -> assertFalse(result.isInterrupted()),
                () -> assertEquals(historyBefore, manager.statisticsHistory())
        );
    }

    @Test
    void testExecuteStepsStopsWhenFinishedWithTerminationCheck() {
        TestSimulationManager manager = createManager();
        AtomicInteger callbackCount = new AtomicInteger();

        var result = manager.executeSteps(5, true, callbackCount::incrementAndGet);

        assertAll(
                () -> assertEquals(1, result.stepCount()),
                () -> assertEquals(1, result.executedSteps()),
                () -> assertTrue(result.isFinished()),
                () -> assertFalse(result.isInterrupted()),
                () -> assertEquals(1, callbackCount.get()),
                () -> assertEquals(2, manager.statisticsHistory().size())
        );
    }

    @Test
    void testExecuteStepsRunsCallbackAfterRecordingEachSample() {
        TestSimulationManager manager = createManager();
        List<Integer> sampledStepCounts = new ArrayList<>();

        manager.executeSteps(3, false,
                () -> sampledStepCounts.add(manager.statisticsHistory().getLast().stepCount()));

        assertEquals(List.of(1, 2, 3), sampledStepCounts);
    }

    @Test
    void testIsFinished() {
        TestSimulationManager manager = createManager();

        // The grid of the test manager has no alive cell, so the termination condition of Conway is met.
        assertTrue(manager.isFinished());
    }

    @Test
    void testIsExecutorFinished() {
        TestSimulationManager manager = createManager();

        manager.executeStep();

        assertFalse(manager.isExecutorFinished());
    }

    @Test
    void testStepCount() {
        TestSimulationManager manager = createManager();
        int initialStepCount = manager.stepCount();

        manager.executeStep();
        manager.executeStep();

        assertAll(
                () -> assertEquals(0, initialStepCount),
                () -> assertEquals(2, manager.stepCount())
        );
    }

    @Test
    void testCurrentGridModel() {
        TestSimulationManager manager = createManager();

        manager.executeStep();

        assertAll(
                () -> assertSame(manager.executor().currentGridModel(), manager.currentGridModel()),
                () -> assertSame(manager.structure(), manager.currentGridModel().structure())
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testStatisticsHistory() {
        TestSimulationManager manager = createManager();

        var history = manager.statisticsHistory();

        assertAll(
                () -> assertEquals(1, history.size()),
                () -> assertEquals(0, history.getFirst().stepCount()),
                () -> assertEquals(Set.copyOf(ConwayStatistics.metrics().stream().map(StatisticsMetric::key).toList()),
                        history.getFirst().values().keySet()),
                () -> assertThrows(UnsupportedOperationException.class, () -> history.add(history.getFirst()))
        );
    }

    @Test
    void testStatisticsExtrema() {
        TestSimulationManager manager = createManager();

        manager.executeStep();
        var extrema = manager.statisticsExtrema();

        assertAll(
                () -> assertEquals(Set.of(ConwayStatistics.KEY_ALIVE_CELLS, ConwayStatistics.KEY_DEAD_CELLS),
                        extrema.minimumValues().keySet()),
                () -> assertEquals(Set.of(ConwayStatistics.KEY_ALIVE_CELLS, ConwayStatistics.KEY_DEAD_CELLS,
                        ConwayStatistics.KEY_CHANGED_CELLS), extrema.maximumValues().keySet()),
                () -> assertEquals(new StatisticsExtremum(0.0d, 0),
                        extrema.minimumValues().get(ConwayStatistics.KEY_ALIVE_CELLS))
        );
    }

    @Test
    void testStepTimingStatistics() {
        TestSimulationManager manager = createManager();
        var initialTimingStatistics = manager.stepTimingStatistics();

        manager.executeStep();

        assertAll(
                () -> assertEquals(StepTimingStatistics.empty(), initialTimingStatistics),
                () -> assertEquals(manager.executor().stepTimingStatistics(), manager.stepTimingStatistics()),
                () -> assertEquals(manager.stepTimingStatistics(),
                        manager.statisticsHistory().getLast().stepTimingStatistics())
        );
    }

    @Test
    void testRecordStatisticsSampleReplacesNonFiniteValueWithNaN() {
        var metric = new StatisticsMetric<ConwayStatistics>(KEY_INFINITE, "test.infinite",
                _ -> Double.POSITIVE_INFINITY, StatisticsExtremaMode.NONE);
        TestSimulationManager manager = new TestSimulationManager(createConfig(), List.of(metric));

        manager.executeStep();

        var history = manager.statisticsHistory();
        assertAll(
                () -> assertEquals(Double.NaN, history.getFirst().values().get(KEY_INFINITE)),
                () -> assertEquals(Double.NaN, history.getLast().values().get(KEY_INFINITE))
        );
    }

    private static final class TestSimulationManager
            extends AbstractSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
            ConwayStatistics> {

        private final GridStructure structure;
        private final ConwayStatistics statistics;
        private final TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor;

        TestSimulationManager(ConwayConfig config, List<StatisticsMetric<ConwayStatistics>> metrics) {
            super(config, metrics);
            structure = config.createGridStructure();
            statistics = new ConwayStatistics(structure);
            var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);
            var runner = new SynchronousStepRunner<>(model, new ConwayStepLogic(structure, config));
            executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner,
                    runner::currentGridModel, new ConwayTerminationCondition(), statistics));
            recordInitialStatisticsSample();
        }

        @NonNull
        @Override
        public GridStructure structure() {
            return structure;
        }

        @NonNull
        @Override
        public ConwayStatistics statistics() {
            return statistics;
        }

        @Override
        protected void updateStatistics() {
            statistics.setStepCount(executor.stepCount());
        }

        @NonNull
        @Override
        protected TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor() {
            return executor;
        }

    }

}
