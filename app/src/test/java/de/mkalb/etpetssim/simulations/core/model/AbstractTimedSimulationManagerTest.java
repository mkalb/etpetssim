package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

@Execution(ExecutionMode.SAME_THREAD)
final class AbstractTimedSimulationManagerTest {

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
            extends AbstractTimedSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
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
            statistics.setStepData(executor.stepCount(), executor.stepTimingStatistics());
        }

        @NonNull
        @Override
        protected TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor() {
            return executor;
        }

    }

}
