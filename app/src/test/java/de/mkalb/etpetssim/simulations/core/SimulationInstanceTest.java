package de.mkalb.etpetssim.simulations.core;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.SimulationType;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class SimulationInstanceTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
    }

    @Test
    void testRecordAccessorSimulationType() {
        SimulationInstance instance = FxTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            return new SimulationInstance(SimulationType.CONWAYS_LIFE, new StubMainView(region), region);
        });
        assertEquals(SimulationType.CONWAYS_LIFE, instance.simulationType());
    }

    // --- Direct constructor / record accessor tests ---

    @Test
    void testRecordAccessorSimulationMainView() {
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<@Nullable SimulationMainView> viewRef = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            viewRef.set(view);
            ref.set(new SimulationInstance(SimulationType.WATOR, view, region));
        });
        SimulationInstance instance = ref.get();
        assertNotNull(instance);
        assertSame(viewRef.get(), instance.simulationMainView());
    }

    @Test
    void testRecordAccessorRegion() {
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<@Nullable Region> regionRef = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            regionRef.set(region);
            ref.set(new SimulationInstance(SimulationType.SNAKE, new StubMainView(region), region));
        });
        SimulationInstance instance = ref.get();
        assertNotNull(instance);
        assertSame(regionRef.get(), instance.region());
    }

    @Test
    void testOfFactoryAssignsType() {
        SimulationInstance instance = FxTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            return SimulationInstance.of(SimulationType.ET_PETS, new StubMainView(region));
        });
        assertEquals(SimulationType.ET_PETS, instance.simulationType());
    }

    // --- SimulationInstance.of() factory tests ---

    @Test
    void testOfFactoryAssignsView() {
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<@Nullable SimulationMainView> viewRef = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            viewRef.set(view);
            ref.set(SimulationInstance.of(SimulationType.ET_PETS, view));
        });
        SimulationInstance instance = ref.get();
        assertNotNull(instance);
        assertSame(viewRef.get(), instance.simulationMainView());
    }

    @Test
    void testOfFactoryDelegatesToBuildMainRegion() {
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        AtomicReference<@Nullable Region> builtRegionRef = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region builtRegion = new Region();
            builtRegionRef.set(builtRegion);
            ref.set(SimulationInstance.of(SimulationType.FOREST_FIRE, new StubMainView(builtRegion)));
        });
        // The region stored in the instance must be exactly the one returned by buildMainRegion()
        SimulationInstance instance = ref.get();
        assertNotNull(instance);
        assertSame(builtRegionRef.get(), instance.region());
    }

    @Test
    void testOfFactoryRegionIsNonNull() {
        SimulationInstance instance = FxTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            return SimulationInstance.of(SimulationType.SIMULATION_LAB, new StubMainView(region));
        });
        assertNotNull(instance.region());
    }

    @Test
    void testToDisplayStringFormat() {
        String result = FxTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.CONWAYS_LIFE, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertEquals("[CONWAYS_LIFE, StubMainView]", result);
    }

    // --- toDisplayString() tests ---

    @Test
    void testToDisplayStringContainsTypeName() {
        String result = FxTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.LANGTONS_ANT, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertTrue(result.contains("LANGTONS_ANT"), "Expected type name in display string: " + result);
    }

    @Test
    void testToDisplayStringContainsViewClassName() {
        String result = FxTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.SUGARSCAPE, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertTrue(result.contains("StubMainView"), "Expected view class name in display string: " + result);
    }

    @Test
    void testToDisplayStringIsNonNull() {
        String result = FxTestSupport.supplyAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.WATOR, new StubMainView(region));
            return instance.toDisplayString();
        });
        assertNotNull(result);
    }

    @Test
    void testRecordEqualityForSameComponents() {
        AtomicReference<@Nullable SimulationInstance> ref1 = new AtomicReference<>();
        AtomicReference<@Nullable SimulationInstance> ref2 = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            ref1.set(new SimulationInstance(SimulationType.SNAKE, view, region));
            ref2.set(new SimulationInstance(SimulationType.SNAKE, view, region));
        });
        SimulationInstance instance1 = ref1.get();
        SimulationInstance instance2 = ref2.get();
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertAll(
                () -> assertEquals(instance1, instance2),
                () -> assertEquals(instance1.hashCode(), instance2.hashCode())
        );
    }

    // --- Record equality and hashCode ---

    @Test
    void testRecordInequalityForDifferentType() {
        AtomicReference<@Nullable SimulationInstance> ref1 = new AtomicReference<>();
        AtomicReference<@Nullable SimulationInstance> ref2 = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            StubMainView view = new StubMainView(region);
            ref1.set(new SimulationInstance(SimulationType.WATOR, view, region));
            ref2.set(new SimulationInstance(SimulationType.SNAKE, view, region));
        });
        assertNotEquals(ref1.get(), ref2.get());
    }

    @Test
    void testRecordInequalityForDifferentRegion() {
        AtomicReference<@Nullable SimulationInstance> ref1 = new AtomicReference<>();
        AtomicReference<@Nullable SimulationInstance> ref2 = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            StubMainView view = new StubMainView(new Region());
            ref1.set(new SimulationInstance(SimulationType.WATOR, view, new Region()));
            ref2.set(new SimulationInstance(SimulationType.WATOR, view, new Region()));
        });
        assertNotEquals(ref1.get(), ref2.get());
    }

    private record StubMainView(Region region) implements SimulationMainView {

        @Override
        public Region buildMainRegion() {
            return region;
        }

        @Override
        public void shutdownSimulation() {
        }

    }

}
