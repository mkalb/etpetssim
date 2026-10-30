package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class TimedSimulationExecutorTest {

    private static final long MIN_STEP_NANOS = 1_000L;

    @Test
    void testCurrentGridModel() {
        StubExecutor delegate = new StubExecutor(0);

        assertSame(delegate.currentGridModel(), new TimedSimulationExecutor<>(delegate).currentGridModel());
    }

    @Test
    void testStepCount() {
        StubExecutor delegate = new StubExecutor(0);
        TimedSimulationExecutor<TestEntity, SparseGridModel<TestEntity>> executor =
                new TimedSimulationExecutor<>(delegate);

        delegate.stepCount = 7;

        assertEquals(7, executor.stepCount());
    }

    @Test
    void testIsFinished() {
        StubExecutor delegate = new StubExecutor(0);
        TimedSimulationExecutor<TestEntity, SparseGridModel<TestEntity>> executor =
                new TimedSimulationExecutor<>(delegate);
        boolean finishedBefore = executor.isFinished();

        delegate.finished = true;

        assertAll(
                () -> assertFalse(finishedBefore),
                () -> assertTrue(executor.isFinished())
        );
    }

    @Test
    void testIsExecutorFinished() {
        StubExecutor delegate = new StubExecutor(0);
        TimedSimulationExecutor<TestEntity, SparseGridModel<TestEntity>> executor =
                new TimedSimulationExecutor<>(delegate);
        boolean executorFinishedBefore = executor.isExecutorFinished();

        delegate.stepCount = Integer.MAX_VALUE;

        assertAll(
                () -> assertFalse(executorFinishedBefore),
                () -> assertTrue(executor.isExecutorFinished())
        );
    }

    @Test
    void testExecuteStep() {
        StubExecutor delegate = new StubExecutor(0);
        TimedSimulationExecutor<TestEntity, SparseGridModel<TestEntity>> executor =
                new TimedSimulationExecutor<>(delegate);

        executor.executeStep();
        executor.executeStep();

        assertEquals(2, delegate.stepCount);
    }

    @Test
    void testStepTimingStatistics() {
        TimedSimulationExecutor<TestEntity, SparseGridModel<TestEntity>> executor =
                new TimedSimulationExecutor<>(new StubExecutor(0));

        assertEquals(StepTimingStatistics.empty(), executor.stepTimingStatistics());
    }

    @Test
    void testStepTimingStatisticsReturnsDurationsAfterSteps() {
        TimedSimulationExecutor<TestEntity, SparseGridModel<TestEntity>> executor =
                new TimedSimulationExecutor<>(new StubExecutor(0));

        executor.executeStep();
        executor.executeStep();
        executor.executeStep();
        StepTimingStatistics statistics = executor.stepTimingStatistics();

        assertAll(
                () -> assertTrue(statistics.minNanos() >= MIN_STEP_NANOS),
                () -> assertTrue(statistics.sumNanos() >= (3 * statistics.minNanos())),
                () -> assertTrue(statistics.sumNanos() >= statistics.maxNanos()),
                () -> assertEquals(statistics.sumNanos() / 3, statistics.avgNanos())
        );
    }

    @Test
    void testStepTimingStatisticsThrowsWhenDelegateStepsWereNotTimed() {
        TimedSimulationExecutor<TestEntity, SparseGridModel<TestEntity>> executor =
                new TimedSimulationExecutor<>(new StubExecutor(1));

        assertThrows(IllegalArgumentException.class, executor::stepTimingStatistics);
    }

    private enum TestEntity implements GridEntity {
        EMPTY;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private static final class StubExecutor implements SimulationExecutor<TestEntity, SparseGridModel<TestEntity>> {

        private final SparseGridModel<TestEntity> model = new SparseGridModel<>(
                new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                        GridSize.square(GridSize.MIN_SIZE)),
                TestEntity.EMPTY);
        private int stepCount;
        private boolean finished;

        private StubExecutor(int initialStepCount) {
            stepCount = initialStepCount;
        }

        @NonNull
        @Override
        public SparseGridModel<TestEntity> currentGridModel() {
            return model;
        }

        @Override
        public int stepCount() {
            return stepCount;
        }

        @Override
        public boolean isFinished() {
            return finished;
        }

        @Override
        public void executeStep() {
            long startNanos = System.nanoTime();
            while ((System.nanoTime() - startNanos) < MIN_STEP_NANOS) {
                Thread.onSpinWait();
            }
            stepCount++;
        }

    }

}
