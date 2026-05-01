package de.mkalb.etpetssim.ui;

import de.mkalb.FxTestSupport;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationTimerTest {

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

}

