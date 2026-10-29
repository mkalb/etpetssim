package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class SimulationTimerTest {

    private static final double STEP_INTERVAL_MILLIS = 20.0d;

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    @Test
    void testStartRejectsInvalidDuration() {
        SimulationTimer timer = new SimulationTimer(() -> {
        });

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> timer.start(Duration.ZERO)),
                () -> assertThrows(IllegalArgumentException.class, () -> timer.start(Duration.millis(-1))),
                () -> assertThrows(IllegalArgumentException.class, () -> timer.start(Duration.INDEFINITE)),
                () -> assertThrows(IllegalArgumentException.class, () -> timer.start(Duration.UNKNOWN))
        );
    }

    @Test
    void testStopWithoutStartKeepsStoppedState() {
        SimulationTimer timer = new SimulationTimer(() -> {
        });

        FXTestSupport.runAndWait(timer::stop);

        assertFalse(timer.isRunning());
    }

    @Test
    void testIsRunning() {
        SimulationTimer timer = new SimulationTimer(() -> {
        });
        AtomicBoolean runningAfterStart = new AtomicBoolean(false);
        AtomicBoolean runningAfterStop = new AtomicBoolean(true);

        FXTestSupport.runAndWait(() -> {
            timer.start(Duration.millis(10));
            runningAfterStart.set(timer.isRunning());
            timer.stop();
            runningAfterStop.set(timer.isRunning());
        });

        assertAll(
                () -> assertTrue(runningAfterStart.get()),
                () -> assertFalse(runningAfterStop.get())
        );
    }

    @Test
    void testStartExecutesSimulationStep() throws InterruptedException {
        CountDownLatch stepLatch = new CountDownLatch(2);
        AtomicInteger stepCount = new AtomicInteger();
        SimulationTimer timer = new SimulationTimer(() -> {
            stepCount.incrementAndGet();
            stepLatch.countDown();
        });

        try {
            FXTestSupport.runAndWait(() -> timer.start(Duration.millis(STEP_INTERVAL_MILLIS)));
            assertTrue(stepLatch.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } finally {
            FXTestSupport.runAndWait(timer::stop);
        }

        assertAll(
                () -> assertTrue(stepCount.get() >= 2),
                () -> assertFalse(timer.isRunning())
        );
    }

}
