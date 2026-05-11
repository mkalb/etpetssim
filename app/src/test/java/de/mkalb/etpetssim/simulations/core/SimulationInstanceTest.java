package de.mkalb.etpetssim.simulations.core;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.SimulationType;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationInstanceTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
    }

    @Test
    void testRecordAccessorSimulationType() {
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            ref.set(new SimulationInstance(SimulationType.CONWAYS_LIFE, new StubMainView(region), region));
        });
        assertEquals(SimulationType.CONWAYS_LIFE, ref.get().simulationType());
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
        assertSame(viewRef.get(), ref.get().simulationMainView());
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
        assertSame(regionRef.get(), ref.get().region());
    }

    @Test
    void testOfFactoryAssignsType() {
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            ref.set(SimulationInstance.of(SimulationType.ET_PETS, new StubMainView(region)));
        });
        assertEquals(SimulationType.ET_PETS, ref.get().simulationType());
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
        assertSame(viewRef.get(), ref.get().simulationMainView());
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
        assertSame(builtRegionRef.get(), ref.get().region());
    }

    @Test
    void testOfFactoryRegionIsNonNull() {
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            ref.set(SimulationInstance.of(SimulationType.SIMULATION_LAB, new StubMainView(region)));
        });
        assertNotNull(ref.get().region());
    }

    @Test
    void testToDisplayStringFormat() {
        AtomicReference<@Nullable String> result = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.CONWAYS_LIFE, new StubMainView(region));
            result.set(instance.toDisplayString());
        });
        assertEquals("[CONWAYS_LIFE, StubMainView]", result.get());
    }

    // --- toDisplayString() tests ---

    @Test
    void testToDisplayStringContainsTypeName() {
        AtomicReference<@Nullable String> result = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.LANGTONS_ANT, new StubMainView(region));
            result.set(instance.toDisplayString());
        });
        assertTrue(result.get().contains("LANGTONS_ANT"), "Expected type name in display string: " + result.get());
    }

    @Test
    void testToDisplayStringContainsViewClassName() {
        AtomicReference<@Nullable String> result = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.SUGARSCAPE, new StubMainView(region));
            result.set(instance.toDisplayString());
        });
        assertTrue(result.get().contains("StubMainView"), "Expected view class name in display string: " + result.get());
    }

    @Test
    void testToDisplayStringIsNonNull() {
        AtomicReference<@Nullable String> result = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> {
            Region region = new Region();
            SimulationInstance instance = SimulationInstance.of(SimulationType.WATOR, new StubMainView(region));
            result.set(instance.toDisplayString());
        });
        assertNotNull(result.get());
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
        assertAll(
                () -> assertEquals(ref1.get(), ref2.get()),
                () -> assertEquals(ref1.get().hashCode(), ref2.get().hashCode())
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

    /**
     * Minimal stub view that returns a fixed region and does nothing on shutdown.
     * Named so that {@code getSimpleName()} yields a predictable value in {@code toDisplayString()} tests.
     */
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
