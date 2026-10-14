package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.core.*;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.layout.Region;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class ExtraterrestrialPetsSimulationTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static void setCurrentSimulationInstance(ExtraterrestrialPetsSimulation application,
                                                     SimulationInstance simulationInstance) {
        try {
            Field field = ExtraterrestrialPetsSimulation.class.getDeclaredField("currentSimulationInstance");
            field.setAccessible(true);
            field.set(application, simulationInstance);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to set current simulation instance", e);
        }
    }

    private static void invokeShutdownCurrentSimulation(ExtraterrestrialPetsSimulation application) {
        try {
            Method method = ExtraterrestrialPetsSimulation.class.getDeclaredMethod("shutdownCurrentSimulation");
            method.setAccessible(true);
            method.invoke(application);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to invoke current simulation shutdown", e);
        }
    }

    @BeforeEach
    void setUpBeforeEach() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testStopShutsDownCurrentSimulationOnce() {
        CountingMainView view = new CountingMainView();
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();
        AtomicReference<Region> regionRef = new AtomicReference<>();

        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            regionRef.set(region);
            setCurrentSimulationInstance(application,
                    new SimulationInstance(SimulationType.WATOR, view, region));

            application.stop();
            application.stop();
        });
        Region region = Objects.requireNonNull(regionRef.get(), "Expected simulation region");

        assertAll(
                () -> assertEquals(1, view.shutdownCount(), "Current simulation must be shut down exactly once"),
                () -> assertEquals(1, view.terminationHandle().awaitCount(), "Termination must be awaited exactly once"),
                () -> assertTrue(region.isDisabled(), "Current simulation region must be disabled during shutdown")
        );
    }

    @Test
    void testCloseRequestShutdownThenStopDoesNotShutDownTwice() {
        CountingMainView view = new CountingMainView();
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            setCurrentSimulationInstance(application,
                    new SimulationInstance(SimulationType.SNAKE, view, region));

            invokeShutdownCurrentSimulation(application);
            application.stop();
        });

        assertEquals(1, view.shutdownCount(), "Close request followed by stop must not shut down twice");
    }

    @Test
    void testStopEscalatesWhenTerminationDoesNotComplete() {
        CountingTerminationHandle terminationHandle = new CountingTerminationHandle(TerminationAwaitOutcome.INCOMPLETE);
        CountingMainView view = new CountingMainView(terminationHandle);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            setCurrentSimulationInstance(application,
                    new SimulationInstance(SimulationType.FOREST_FIRE, view, region));
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
        CountingTerminationHandle terminationHandle = new CountingTerminationHandle(TerminationAwaitOutcome.INTERRUPTED);
        CountingMainView view = new CountingMainView(terminationHandle);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();
        AtomicBoolean interrupted = new AtomicBoolean();

        FXTestSupport.runAndWait(() -> {
            try {
                setCurrentSimulationInstance(application,
                        new SimulationInstance(SimulationType.SUGARSCAPE, view, new Region()));
                application.stop();
                interrupted.set(Thread.currentThread().isInterrupted());
            } finally {
                Thread.interrupted();
            }
        });

        assertAll(
                () -> assertEquals(1, terminationHandle.awaitCount()),
                () -> assertEquals(1, terminationHandle.shutdownNowCount()),
                () -> assertTrue(interrupted.get(), "The interrupted status must be restored after termination waiting")
        );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testStopInterruptionEscalatesEveryPendingTermination() {
        CountingTerminationHandle interruptedTerminationHandle = new CountingTerminationHandle(TerminationAwaitOutcome.INTERRUPTED);
        CountingTerminationHandle pendingTerminationHandle = new CountingTerminationHandle(TerminationAwaitOutcome.TERMINATED);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            try {
                setCurrentSimulationInstance(application,
                        new SimulationInstance(SimulationType.SUGARSCAPE, new CountingMainView(interruptedTerminationHandle), new Region()));
                invokeShutdownCurrentSimulation(application);
                setCurrentSimulationInstance(application,
                        new SimulationInstance(SimulationType.WATOR, new CountingMainView(pendingTerminationHandle), new Region()));
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
        CountingTerminationHandle secondTerminationHandle = new CountingTerminationHandle(TerminationAwaitOutcome.TERMINATED);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FXTestSupport.runAndWait(() -> {
            setCurrentSimulationInstance(application,
                    new SimulationInstance(SimulationType.CONWAYS_LIFE, new CountingMainView(firstTerminationHandle), new Region()));
            invokeShutdownCurrentSimulation(application);
            setCurrentSimulationInstance(application,
                    new SimulationInstance(SimulationType.WATOR, new CountingMainView(secondTerminationHandle), new Region()));
            application.stop();
        });

        assertAll(
                () -> assertEquals(1, firstTerminationHandle.awaitCount()),
                () -> assertEquals(1, secondTerminationHandle.awaitCount()),
                () -> assertTrue(secondTerminationHandle.awaitedTimeoutNanos() < firstTerminationHandle.awaitedTimeoutNanos(),
                        "Each termination must receive the remaining shared timeout budget")
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
