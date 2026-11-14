package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.layout.Region;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@Execution(ExecutionMode.SAME_THREAD)
final class SimulationScreenTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    @Test
    void testOf() {
        StubMainView view = FXTestSupport.supplyAndWaitNonNull(() -> new StubMainView(new Region()));

        SimulationScreen screen = SimulationScreen.of(SimulationType.ET_PETS, view);

        assertAll(
                () -> assertEquals(SimulationType.ET_PETS, screen.simulationType()),
                () -> assertSame(view, screen.simulationMainView()),
                () -> assertSame(view.region(), screen.region())
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
        public SimulationShutdownHandle shutdownSimulation() {
            return SimulationShutdownHandle.completed();
        }

    }

}
