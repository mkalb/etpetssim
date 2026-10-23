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
