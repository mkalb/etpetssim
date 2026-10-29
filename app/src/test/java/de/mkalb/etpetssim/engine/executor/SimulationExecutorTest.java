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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

final class SimulationExecutorTest {

    private static final int NEVER_FINISHED = Integer.MAX_VALUE;

    @Test
    void testIsExecutorFinished() {
        assertAll(
                () -> assertTrue(new TestExecutor(Integer.MAX_VALUE, NEVER_FINISHED).isExecutorFinished()),
                () -> assertFalse(new TestExecutor(Integer.MAX_VALUE - 1, NEVER_FINISHED).isExecutorFinished()),
                () -> assertFalse(new TestExecutor(0, NEVER_FINISHED).isExecutorFinished())
        );
    }

    @Test
    void testExecuteSteps() {
        TestExecutor executor = new TestExecutor(0, NEVER_FINISHED);
        AtomicInteger callbackCalls = new AtomicInteger();

        StepExecutionResult result = executor.executeSteps(5, true, callbackCalls::incrementAndGet);

        assertAll(
                () -> assertEquals(new StepExecutionResult(5, 5, false, false), result),
                () -> assertEquals(5, callbackCalls.get())
        );
    }

    @Test
    void testExecuteStepsReturnsEmptyResultForZeroCount() {
        TestExecutor executor = new TestExecutor(2, NEVER_FINISHED);

        StepExecutionResult result = executor.executeSteps(0, true, () -> fail("callback must not run"));

        assertEquals(new StepExecutionResult(2, 0, false, false), result);
    }

    @Test
    void testExecuteStepsStopsWhenFinishedAndTerminationIsChecked() {
        TestExecutor executor = new TestExecutor(0, 3);
        AtomicInteger callbackCalls = new AtomicInteger();

        StepExecutionResult result = executor.executeSteps(10, true, callbackCalls::incrementAndGet);

        assertAll(
                () -> assertEquals(new StepExecutionResult(3, 3, true, false), result),
                () -> assertEquals(3, callbackCalls.get())
        );
    }

    @Test
    void testExecuteStepsIgnoresFinishedWhenTerminationIsNotChecked() {
        TestExecutor executor = new TestExecutor(0, 3);

        StepExecutionResult result = executor.executeSteps(10, false, () -> {
        });

        assertEquals(new StepExecutionResult(10, 10, false, false), result);
    }

    @Test
    void testExecuteStepsStopsAtTechnicalLimit() {
        TestExecutor executor = new TestExecutor(Integer.MAX_VALUE - 2, NEVER_FINISHED);

        StepExecutionResult result = executor.executeSteps(10, false, () -> {
        });

        assertEquals(new StepExecutionResult(Integer.MAX_VALUE, 2, true, false), result);
    }

    @Test
    void testExecuteStepsReturnsInterruptedBeforeFirstStep() {
        TestExecutor executor = new TestExecutor(0, NEVER_FINISHED);
        Thread.currentThread().interrupt();

        try {
            StepExecutionResult result = executor.executeSteps(5, true, () -> fail("callback must not run"));

            assertEquals(new StepExecutionResult(0, 0, false, true), result);
        } finally {
            assertTrue(Thread.interrupted());
        }
    }

    @Test
    void testExecuteStepsReturnsInterruptedWhenCallbackInterrupts() {
        TestExecutor executor = new TestExecutor(0, 2);
        AtomicInteger callbackCalls = new AtomicInteger();

        try {
            StepExecutionResult result = executor.executeSteps(5, true, () -> {
                if (callbackCalls.incrementAndGet() == 2) {
                    Thread.currentThread().interrupt();
                }
            });

            assertEquals(new StepExecutionResult(2, 2, false, true), result);
        } finally {
            assertTrue(Thread.interrupted());
        }
    }

    private enum TestEntity implements GridEntity {
        EMPTY;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private static final class TestExecutor implements SimulationExecutor<TestEntity, SparseGridModel<TestEntity>> {

        private final SparseGridModel<TestEntity> model = new SparseGridModel<>(
                new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                        GridSize.square(GridSize.MIN_SIZE)),
                TestEntity.EMPTY);
        private final int finishedAtStep;
        private int stepCount;

        private TestExecutor(int initialStepCount, int finishedAtStep) {
            stepCount = initialStepCount;
            this.finishedAtStep = finishedAtStep;
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
            return stepCount >= finishedAtStep;
        }

        @Override
        public void executeStep() {
            stepCount++;
        }

    }

}
