package de.mkalb.etpetssim.engine.executor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class StepExecutionResultTest {

    private static final int STEP_COUNT = 12;
    private static final int EXECUTED_STEPS = 3;
    private static final int NEGATIVE_VALUE = -1;

    @Test
    void testRecordAccessors() {
        StepExecutionResult result = new StepExecutionResult(STEP_COUNT, EXECUTED_STEPS, true, false);

        assertAll(
                () -> assertEquals(STEP_COUNT, result.stepCount()),
                () -> assertEquals(EXECUTED_STEPS, result.executedSteps()),
                () -> assertTrue(result.isFinished()),
                () -> assertFalse(result.isInterrupted())
        );
    }

    @Test
    void testConstructorRejectsNegativeStepCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new StepExecutionResult(NEGATIVE_VALUE, 0, false, false));
    }

    @Test
    void testConstructorRejectsNegativeExecutedSteps() {
        assertThrows(IllegalArgumentException.class,
                () -> new StepExecutionResult(0, NEGATIVE_VALUE, false, false));
    }

    @Test
    void testConstructorRejectsExecutedStepsGreaterThanStepCount() {
        assertThrows(IllegalArgumentException.class, () -> new StepExecutionResult(4, 5, false, false));
    }

}

