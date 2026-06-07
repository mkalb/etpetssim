package de.mkalb.etpetssim.ui;

import de.mkalb.FxTestSupport;
import javafx.util.Duration;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationTimerTest {

    private static final double STEP_INTERVAL_MILLIS = 20.0d;

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
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

        FxTestSupport.runAndWait(timer::stop);

        assertFalse(timer.isRunning());
    }

    @Test
    void testStartAndStopUpdatesRunningState() {
        SimulationTimer timer = new SimulationTimer(() -> {
        });
        AtomicBoolean runningAfterStart = new AtomicBoolean(false);
        AtomicBoolean runningAfterStop = new AtomicBoolean(true);

        FxTestSupport.runAndWait(() -> {
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
            FxTestSupport.runAndWait(() -> timer.start(Duration.millis(STEP_INTERVAL_MILLIS)));
            assertTrue(stepLatch.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } finally {
            FxTestSupport.runAndWait(timer::stop);
        }

        assertAll(
                () -> assertTrue(stepCount.get() >= 2),
                () -> assertFalse(timer.isRunning())
        );
    }

}

