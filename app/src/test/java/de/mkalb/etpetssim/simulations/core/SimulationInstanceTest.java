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
    void testSimulationType() {
        SimulationInstance instance = FXTestSupport.supplyAndWaitNonNull(() -> {
            Region region = new Region();
            return new SimulationInstance(SimulationType.CONWAYS_LIFE, new StubMainView(region), region);
        });
        assertEquals(SimulationType.CONWAYS_LIFE, instance.simulationType());
    }

    @Test
    void testSimulationMainView() {
        AtomicReference<SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<SimulationMainView> viewRef = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            viewRef.set(view);
            ref.set(new SimulationInstance(SimulationType.WATOR, view, region));
        });
        SimulationInstance instance = requireValue(ref);
        assertSame(viewRef.get(), instance.simulationMainView());
    }

    @Test
    void testRegion() {
        AtomicReference<SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<Region> regionRef = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            regionRef.set(region);
            ref.set(new SimulationInstance(SimulationType.SNAKE, new StubMainView(region), region));
        });
        SimulationInstance instance = requireValue(ref);
        assertSame(regionRef.get(), instance.region());
    }

    @Test
    void testOfAssignsType() {
        SimulationInstance instance = FXTestSupport.supplyAndWaitNonNull(() -> {
            Region region = new Region();
            return SimulationInstance.of(SimulationType.ET_PETS, new StubMainView(region));
        });
        assertEquals(SimulationType.ET_PETS, instance.simulationType());
    }

    @Test
    void testOfAssignsView() {
        AtomicReference<SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<SimulationMainView> viewRef = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            viewRef.set(view);
            ref.set(SimulationInstance.of(SimulationType.ET_PETS, view));
        });
        SimulationInstance instance = requireValue(ref);
        assertSame(viewRef.get(), instance.simulationMainView());
    }

    @Test
    void testOfDelegatesToBuildMainRegion() {
        AtomicReference<SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<Region> builtRegionRef = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            Region builtRegion = new Region();
            builtRegionRef.set(builtRegion);
            ref.set(SimulationInstance.of(SimulationType.FOREST_FIRE, new StubMainView(builtRegion)));
        });
        SimulationInstance instance = ref.get();
        assertNotNull(instance);
        assertSame(builtRegionRef.get(), instance.region());
    }

    @Test
    void testOfCreatesNonNullRegion() {
        SimulationInstance instance = FXTestSupport.supplyAndWaitNonNull(() -> {
            Region region = new Region();
            return SimulationInstance.of(SimulationType.SIMULATION_LAB, new StubMainView(region));
        });
        assertNotNull(instance.region());
    }

    @Test
    void testToDisplayStringReturnsExpectedFormat() {
        String result = FXTestSupport.supplyAndWaitNonNull(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.CONWAYS_LIFE, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertEquals("[CONWAYS_LIFE, StubMainView]", result);
    }

    @Test
    void testToDisplayStringContainsTypeName() {
        String result = FXTestSupport.supplyAndWaitNonNull(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.LANGTONS_ANT, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertTrue(result.contains("LANGTONS_ANT"), "Expected type name in display string: " + result);
    }

    @Test
    void testToDisplayStringContainsViewClassName() {
        String result = FXTestSupport.supplyAndWaitNonNull(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.SUGARSCAPE, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertTrue(result.contains("StubMainView"), "Expected view class name in display string: " + result);
    }

    @Test
    void testToDisplayStringReturnsNonNull() {
        String result = FXTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.WATOR, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertNotNull(result);
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
