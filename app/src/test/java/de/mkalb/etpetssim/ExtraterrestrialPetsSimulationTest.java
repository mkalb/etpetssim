package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.core.SimulationInstance;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
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
final class ExtraterrestrialPetsSimulationTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static SimulationInstance createInstance(SimulationType simulationType, SimulationMainView view) {
        return new SimulationInstance(simulationType, view, new Region());
    }

    @BeforeEach
    void setUpBeforeEach() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testStopShutsDownCurrentSimulationOnce() {
        CountingMainView view = new CountingMainView();
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            SimulationInstance instance = createInstance(SimulationType.WATOR, view);
            application.setCurrentSimulationInstance(instance);

            application.stop();
            application.stop();

            assertAll(
                    () -> assertEquals(1, view.shutdownCount()),
                    () -> assertEquals(1, view.terminationHandle().awaitCount()),
                    () -> assertTrue(instance.region().isDisabled())
            );
        });
    }

    @Test
    void testStopDoesNotShutDownTwiceAfterCloseRequest() {
        CountingMainView view = new CountingMainView();
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            application.setCurrentSimulationInstance(createInstance(SimulationType.SNAKE, view));

            application.shutdownCurrentSimulation();
            application.stop();
        });

        assertEquals(1, view.shutdownCount());
    }

    @Test
    void testStopEscalatesWhenTerminationDoesNotComplete() {
        CountingTerminationHandle terminationHandle =
                new CountingTerminationHandle(TerminationAwaitOutcome.INCOMPLETE);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            application.setCurrentSimulationInstance(
                    createInstance(SimulationType.FOREST_FIRE, new CountingMainView(terminationHandle)));
            application.stop();
        });

        assertAll(
                () -> assertEquals(1, terminationHandle.awaitCount()),
                () -> assertEquals(1, terminationHandle.shutdownNowCount())
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testStopInterruptsTerminationWaitAndRestoresInterruptStatus() {
        CountingTerminationHandle terminationHandle =
                new CountingTerminationHandle(TerminationAwaitOutcome.INTERRUPTED);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();
        AtomicBoolean interrupted = new AtomicBoolean();

        FXTestSupport.runAndWait(() -> {
            try {
                application.setCurrentSimulationInstance(
                        createInstance(SimulationType.SUGARSCAPE, new CountingMainView(terminationHandle)));
                application.stop();
                interrupted.set(Thread.currentThread().isInterrupted());
            } finally {
                Thread.interrupted();
            }
        });

        assertAll(
                () -> assertEquals(1, terminationHandle.awaitCount()),
                () -> assertEquals(1, terminationHandle.shutdownNowCount()),
                () -> assertTrue(interrupted.get())
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testStopInterruptionEscalatesEveryPendingTermination() {
        CountingTerminationHandle interruptedTerminationHandle =
                new CountingTerminationHandle(TerminationAwaitOutcome.INTERRUPTED);
        CountingTerminationHandle pendingTerminationHandle =
                new CountingTerminationHandle(TerminationAwaitOutcome.TERMINATED);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            try {
                application.setCurrentSimulationInstance(
                        createInstance(SimulationType.SUGARSCAPE, new CountingMainView(interruptedTerminationHandle)));
                application.shutdownCurrentSimulation();
                application.setCurrentSimulationInstance(
                        createInstance(SimulationType.WATOR, new CountingMainView(pendingTerminationHandle)));
                application.stop();
            } finally {
                Thread.interrupted();
            }
        });

        assertAll(
                () -> assertEquals(1, interruptedTerminationHandle.awaitCount()),
                () -> assertEquals(1, interruptedTerminationHandle.shutdownNowCount()),
                () -> assertEquals(0, pendingTerminationHandle.awaitCount()),
                () -> assertEquals(1, pendingTerminationHandle.shutdownNowCount())
        );
    }

    @Test
    void testStopSharesTerminationTimeoutBudgetAcrossPendingTerminations() {
        CountingTerminationHandle firstTerminationHandle = new CountingTerminationHandle(
                TerminationAwaitOutcome.TERMINATED, TimeUnit.MILLISECONDS.toNanos(10));
        CountingTerminationHandle secondTerminationHandle =
                new CountingTerminationHandle(TerminationAwaitOutcome.TERMINATED);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            application.setCurrentSimulationInstance(
                    createInstance(SimulationType.CONWAYS_LIFE, new CountingMainView(firstTerminationHandle)));
            application.shutdownCurrentSimulation();
            application.setCurrentSimulationInstance(
                    createInstance(SimulationType.WATOR, new CountingMainView(secondTerminationHandle)));
            application.stop();
        });

        assertAll(
                () -> assertEquals(1, firstTerminationHandle.awaitCount()),
                () -> assertEquals(1, secondTerminationHandle.awaitCount()),
                () -> assertTrue(
                        secondTerminationHandle.awaitedTimeoutNanos() < firstTerminationHandle.awaitedTimeoutNanos())
        );
    }

    private enum TerminationAwaitOutcome {
        TERMINATED,
        INCOMPLETE,
        INTERRUPTED
    }

    private static final class CountingMainView implements SimulationMainView {

        private final AtomicInteger shutdownCount = new AtomicInteger();
        private final CountingTerminationHandle terminationHandle;

        private CountingMainView() {
            this(new CountingTerminationHandle(TerminationAwaitOutcome.TERMINATED));
        }

        private CountingMainView(CountingTerminationHandle terminationHandle) {
            this.terminationHandle = terminationHandle;
        }

        @NonNull
        @Override
        public Region buildMainRegion() {
            return new Region();
        }

        @NonNull
        @Override
        public SimulationTerminationHandle shutdownSimulation() {
            shutdownCount.incrementAndGet();
            return terminationHandle;
        }

        int shutdownCount() {
            return shutdownCount.get();
        }

        CountingTerminationHandle terminationHandle() {
            return terminationHandle;
        }

    }

    private static final class CountingTerminationHandle implements SimulationTerminationHandle {

        private final AtomicInteger awaitCount = new AtomicInteger();
        private final AtomicInteger shutdownNowCount = new AtomicInteger();
        private final AtomicLong awaitedTimeoutNanos = new AtomicLong(-1L);
        private final TerminationAwaitOutcome awaitOutcome;
        private final long awaitDelayNanos;

        private CountingTerminationHandle(TerminationAwaitOutcome awaitOutcome) {
            this(awaitOutcome, 0L);
        }

        private CountingTerminationHandle(TerminationAwaitOutcome awaitOutcome, long awaitDelayNanos) {
            this.awaitOutcome = awaitOutcome;
            this.awaitDelayNanos = awaitDelayNanos;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
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
