package de.mkalb.etpetssim;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.core.*;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class ExtraterrestrialPetsSimulationTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
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
        AtomicReference<@Nullable Region> regionRef = new AtomicReference<>();

        FxTestSupport.runAndWait(() -> {
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
                () -> assertEquals(1, view.termination().awaitCount(), "Termination must be awaited exactly once"),
                () -> assertTrue(region.isDisabled(), "Current simulation region must be disabled during shutdown")
        );
    }

    @Test
    void testCloseRequestShutdownThenStopDoesNotShutDownTwice() {
        CountingMainView view = new CountingMainView();
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            setCurrentSimulationInstance(application,
                    new SimulationInstance(SimulationType.SNAKE, view, region));

            invokeShutdownCurrentSimulation(application);
            application.stop();
        });

        assertEquals(1, view.shutdownCount(), "Close request followed by stop must not shut down twice");
    }

    @Test
    void testStopEscalatesTerminationAfterTimeout() {
        CountingTermination termination = new CountingTermination(false);
        CountingMainView view = new CountingMainView(termination);
        ExtraterrestrialPetsSimulation application = new ExtraterrestrialPetsSimulation();

        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            setCurrentSimulationInstance(application,
                    new SimulationInstance(SimulationType.FOREST_FIRE, view, region));
            application.stop();
        });

        assertAll(
                () -> assertEquals(1, termination.awaitCount()),
                () -> assertEquals(1, termination.shutdownNowCount())
        );
    }

    private static final class CountingMainView implements SimulationMainView {

        private final AtomicInteger shutdownCount = new AtomicInteger();
        private final CountingTermination termination;

        private CountingMainView() {
            this(new CountingTermination(true));
        }

        private CountingMainView(CountingTermination termination) {
            this.termination = termination;
        }

        @Override
        public Region buildMainRegion() {
            return new Region();
        }

        @Override
        public SimulationTermination shutdownSimulation() {
            shutdownCount.incrementAndGet();
            return termination;
        }

        int shutdownCount() {
            return shutdownCount.get();
        }

        CountingTermination termination() {
            return termination;
        }

    }

    private static final class CountingTermination implements SimulationTermination {

        private final AtomicInteger awaitCount = new AtomicInteger();
        private final AtomicInteger shutdownNowCount = new AtomicInteger();
        private final boolean terminates;

        private CountingTermination(boolean terminates) {
            this.terminates = terminates;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            awaitCount.incrementAndGet();
            return terminates;
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

    }

}
