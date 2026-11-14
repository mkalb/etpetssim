package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.core.SimulationScreen;
import de.mkalb.etpetssim.simulations.core.SimulationShutdownHandle;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.layout.Region;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class EtpetssimApplicationTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static SimulationScreen createScreen(SimulationType simulationType, SimulationMainView view) {
        return new SimulationScreen(simulationType, view, new Region());
    }

    @BeforeEach
    void setUpBeforeEach() {
        // Rule deviation (junit.md): stop() shuts down the AppLogger
        AppLogger.initializeForTesting();
    }

    @Test
    void testStopShutsDownCurrentSimulationOnce() {
        CountingMainView view = new CountingMainView();
        EtpetssimApplication application = new EtpetssimApplication();

        FXTestSupport.runAndWait(() -> {
            SimulationScreen screen = createScreen(SimulationType.WATOR, view);
            application.setCurrentSimulationScreen(screen);

            application.stop();
            application.stop();

            assertAll(
                    () -> assertEquals(1, view.shutdownCount()),
                    () -> assertEquals(1, view.shutdownHandle().awaitCount()),
                    () -> assertTrue(screen.region().isDisabled())
            );
        });
    }

    @Test
    void testStopDoesNotShutDownTwiceAfterCloseRequest() {
        CountingMainView view = new CountingMainView();
        EtpetssimApplication application = new EtpetssimApplication();

        FXTestSupport.runAndWait(() -> {
            application.setCurrentSimulationScreen(createScreen(SimulationType.SNAKE, view));

            application.shutdownCurrentSimulation();
            application.stop();
        });

        assertEquals(1, view.shutdownCount());
    }

    @Test
    void testStopEscalatesWhenTerminationDoesNotComplete() {
        CountingShutdownHandle shutdownHandle =
                new CountingShutdownHandle(TerminationAwaitOutcome.INCOMPLETE);
        EtpetssimApplication application = new EtpetssimApplication();

        FXTestSupport.runAndWait(() -> {
            application.setCurrentSimulationScreen(
                    createScreen(SimulationType.FOREST_FIRE, new CountingMainView(shutdownHandle)));
            application.stop();
        });

        assertAll(
                () -> assertEquals(1, shutdownHandle.awaitCount()),
                () -> assertEquals(1, shutdownHandle.shutdownNowCount())
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testStopInterruptsTerminationWaitAndRestoresInterruptStatus() {
        CountingShutdownHandle shutdownHandle =
                new CountingShutdownHandle(TerminationAwaitOutcome.INTERRUPTED);
        EtpetssimApplication application = new EtpetssimApplication();
        AtomicBoolean interrupted = new AtomicBoolean();

        FXTestSupport.runAndWait(() -> {
            try {
                application.setCurrentSimulationScreen(
                        createScreen(SimulationType.SUGARSCAPE, new CountingMainView(shutdownHandle)));
                application.stop();
                interrupted.set(Thread.currentThread().isInterrupted());
            } finally {
                Thread.interrupted();
            }
        });

        assertAll(
                () -> assertEquals(1, shutdownHandle.awaitCount()),
                () -> assertEquals(1, shutdownHandle.shutdownNowCount()),
                () -> assertTrue(interrupted.get())
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testStopEscalatesEveryPendingTerminationWhenInterrupted() {
        CountingShutdownHandle interruptedShutdownHandle =
                new CountingShutdownHandle(TerminationAwaitOutcome.INTERRUPTED);
        CountingShutdownHandle pendingShutdownHandle =
                new CountingShutdownHandle(TerminationAwaitOutcome.TERMINATED);
        EtpetssimApplication application = new EtpetssimApplication();

        FXTestSupport.runAndWait(() -> {
            try {
                application.setCurrentSimulationScreen(
                        createScreen(SimulationType.SUGARSCAPE, new CountingMainView(interruptedShutdownHandle)));
                application.shutdownCurrentSimulation();
                application.setCurrentSimulationScreen(
                        createScreen(SimulationType.WATOR, new CountingMainView(pendingShutdownHandle)));
                application.stop();
            } finally {
                Thread.interrupted();
            }
        });

        assertAll(
                () -> assertEquals(1, interruptedShutdownHandle.awaitCount()),
                () -> assertEquals(1, interruptedShutdownHandle.shutdownNowCount()),
                () -> assertEquals(0, pendingShutdownHandle.awaitCount()),
                () -> assertEquals(1, pendingShutdownHandle.shutdownNowCount())
        );
    }

    @Test
    void testStopSharesTerminationTimeoutBudgetAcrossPendingTerminations() {
        CountingShutdownHandle firstShutdownHandle = new CountingShutdownHandle(
                TerminationAwaitOutcome.TERMINATED, TimeUnit.MILLISECONDS.toNanos(10));
        CountingShutdownHandle secondShutdownHandle =
                new CountingShutdownHandle(TerminationAwaitOutcome.TERMINATED);
        EtpetssimApplication application = new EtpetssimApplication();

        FXTestSupport.runAndWait(() -> {
            application.setCurrentSimulationScreen(
                    createScreen(SimulationType.CONWAYS_LIFE, new CountingMainView(firstShutdownHandle)));
            application.shutdownCurrentSimulation();
            application.setCurrentSimulationScreen(
                    createScreen(SimulationType.WATOR, new CountingMainView(secondShutdownHandle)));
            application.stop();
        });

        assertAll(
                () -> assertEquals(1, firstShutdownHandle.awaitCount()),
                () -> assertEquals(1, secondShutdownHandle.awaitCount()),
                () -> assertTrue(
                        secondShutdownHandle.awaitedTimeoutNanos() < firstShutdownHandle.awaitedTimeoutNanos())
        );
    }

    private enum TerminationAwaitOutcome {
        TERMINATED,
        INCOMPLETE,
        INTERRUPTED
    }

    private static final class CountingMainView implements SimulationMainView {

        private final AtomicInteger shutdownCount = new AtomicInteger();
        private final CountingShutdownHandle shutdownHandle;

        private CountingMainView() {
            this(new CountingShutdownHandle(TerminationAwaitOutcome.TERMINATED));
        }

        private CountingMainView(CountingShutdownHandle shutdownHandle) {
            this.shutdownHandle = shutdownHandle;
        }

        @NonNull
        @Override
        public Region buildMainRegion() {
            return new Region();
        }

        @NonNull
        @Override
        public SimulationShutdownHandle shutdownSimulation() {
            shutdownCount.incrementAndGet();
            return shutdownHandle;
        }

        int shutdownCount() {
            return shutdownCount.get();
        }

        CountingShutdownHandle shutdownHandle() {
            return shutdownHandle;
        }

    }

    private static final class CountingShutdownHandle implements SimulationShutdownHandle {

        private final AtomicInteger awaitCount = new AtomicInteger();
        private final AtomicInteger shutdownNowCount = new AtomicInteger();
        private final AtomicLong awaitedTimeoutNanos = new AtomicLong(-1L);
        private final TerminationAwaitOutcome awaitOutcome;
        private final long awaitDelayNanos;

        private CountingShutdownHandle(TerminationAwaitOutcome awaitOutcome) {
            this(awaitOutcome, 0L);
        }

        private CountingShutdownHandle(TerminationAwaitOutcome awaitOutcome, long awaitDelayNanos) {
            this.awaitOutcome = awaitOutcome;
            this.awaitDelayNanos = awaitDelayNanos;
        }

        @Override
        public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
            awaitCount.incrementAndGet();
            awaitedTimeoutNanos.set(unit.toNanos(timeout));
            if (awaitDelayNanos > 0L) {
                LockSupport.parkNanos(awaitDelayNanos);
            }
            return switch (awaitOutcome) {
                case TERMINATED -> true;
                case INCOMPLETE -> false;
                case INTERRUPTED -> throw new InterruptedException("Test interruption");
            };
        }

        @Override
        public void shutdownNow() {
            shutdownNowCount.incrementAndGet();
        }

        int awaitCount() {
            return awaitCount.get();
        }

        int shutdownNowCount() {
            return shutdownNowCount.get();
        }

        long awaitedTimeoutNanos() {
            return awaitedTimeoutNanos.get();
        }

    }

}
