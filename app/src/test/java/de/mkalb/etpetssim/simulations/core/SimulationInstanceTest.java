package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.layout.Region;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class SimulationInstanceTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static <T> T requireValue(AtomicReference<T> reference) {
        return Objects.requireNonNull(reference.get(), "Expected test fixture value");
    }

    @Test
    void testRecord() {
        AtomicReference<SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<SimulationMainView> viewRef = new AtomicReference<>();
        AtomicReference<Region> regionRef = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            regionRef.set(region);
            viewRef.set(view);
            ref.set(new SimulationInstance(SimulationType.CONWAYS_LIFE, view, region));
        });
        SimulationInstance instance = requireValue(ref);

        assertAll(
                () -> assertEquals(SimulationType.CONWAYS_LIFE, instance.simulationType()),
                () -> assertSame(viewRef.get(), instance.simulationMainView()),
                () -> assertSame(regionRef.get(), instance.region())
        );
    }

    @Test
    void testOf() {
        AtomicReference<SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<SimulationMainView> viewRef = new AtomicReference<>();
        AtomicReference<Region> builtRegionRef = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region builtRegion = new Region();
            StubMainView view = new StubMainView(builtRegion);
            builtRegionRef.set(builtRegion);
            viewRef.set(view);
            ref.set(SimulationInstance.of(SimulationType.ET_PETS, view));
        });
        SimulationInstance instance = requireValue(ref);

        assertAll(
                () -> assertEquals(SimulationType.ET_PETS, instance.simulationType()),
                () -> assertSame(viewRef.get(), instance.simulationMainView()),
                () -> assertSame(builtRegionRef.get(), instance.region())
        );
    }

    @Test
    void testToDisplayString() {
        String result = FXTestSupport.supplyAndWaitNonNull(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.CONWAYS_LIFE, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertEquals("[CONWAYS_LIFE, StubMainView]", result);
    }

    @Test
    void testEqualsReturnsTrueForSameComponents() {
        AtomicReference<SimulationInstance> ref1 = new AtomicReference<>();
        AtomicReference<SimulationInstance> ref2 = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            ref1.set(new SimulationInstance(SimulationType.SNAKE, view, region));
            ref2.set(new SimulationInstance(SimulationType.SNAKE, view, region));
        });
        SimulationInstance instance1 = requireValue(ref1);
        SimulationInstance instance2 = requireValue(ref2);
        assertAll(
                () -> assertEquals(instance1, instance2),
                () -> assertEquals(instance1.hashCode(), instance2.hashCode())
        );
    }

    @Test
    void testEqualsReturnsFalseForDifferentType() {
        AtomicReference<SimulationInstance> ref1 = new AtomicReference<>();
        AtomicReference<SimulationInstance> ref2 = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            ref1.set(new SimulationInstance(SimulationType.WATOR, view, region));
            ref2.set(new SimulationInstance(SimulationType.SNAKE, view, region));
        });
        assertNotEquals(ref1.get(), ref2.get());
    }

    @Test
    void testEqualsReturnsFalseForDifferentRegion() {
        AtomicReference<SimulationInstance> ref1 = new AtomicReference<>();
        AtomicReference<SimulationInstance> ref2 = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            StubMainView view = new StubMainView(new Region());
            ref1.set(new SimulationInstance(SimulationType.WATOR, view, new Region()));
            ref2.set(new SimulationInstance(SimulationType.WATOR, view, new Region()));
        });
        assertNotEquals(ref1.get(), ref2.get());
    }

    private record StubMainView(Region region) implements SimulationMainView {

        @NonNull
        @Override
        public Region buildMainRegion() {
            return region;
        }

        @NonNull
        @Override
        public SimulationTerminationHandle shutdownSimulation() {
            return SimulationTerminationHandle.completed();
        }

    }

}
