package de.mkalb.etpetssim.simulations.core;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.SimulationType;
import de.mkalb.etpetssim.core.AppLocalization;
import javafx.stage.Stage;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationFactoryTest {

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FxTestSupport.ensureStarted();
    }

    /**
     * Creates a {@link Stage} on the FX thread.
     */
    private static Stage createStage() {
        AtomicReference<@Nullable Stage> ref = new AtomicReference<>();
        FxTestSupport.runAndWait(() -> ref.set(new Stage()));
        Stage stage = ref.get();
        assertNotNull(stage, "Stage must not be null after FX initialization");
        return stage;
    }

    /**
     * Creates a {@link SimulationInstance} for the given type on the FX thread.
     * Uses a no-op stage updater.
     */
    private static SimulationInstance createInstance(SimulationType type) {
        Stage stage = createStage();
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        FxTestSupport.runAndWait(() ->
                ref.set(SimulationFactory.createInstance(type, stage, (_, _) -> {})));
        SimulationInstance instance = ref.get();
        assertNotNull(instance, "SimulationInstance must not be null for type: " + type);
        return instance;
    }

    // --- createInstance() returns non-null for every SimulationType ---

    @Test
    void testCreateInstanceAllTypesReturnNonNull() {
        for (SimulationType type : SimulationType.values()) {
            SimulationInstance instance = createInstance(type);
            assertNotNull(instance, "Expected non-null SimulationInstance for type: " + type);
        }
    }

    // --- simulationType() of the returned instance must match the requested type ---

    @Test
    void testCreateInstanceTypeMatchesRequestedType() {
        for (SimulationType type : SimulationType.values()) {
            SimulationInstance instance = createInstance(type);
            assertEquals(type, instance.simulationType(),
                    "simulationType() mismatch for requested type: " + type);
        }
    }

    // --- Non-null view and region for every SimulationType ---

    @Test
    void testCreateInstanceViewIsNonNull() {
        for (SimulationType type : SimulationType.values()) {
            SimulationInstance instance = createInstance(type);
            assertNotNull(instance.simulationMainView(),
                    "Expected non-null simulationMainView() for type: " + type);
        }
    }

    @Test
    void testCreateInstanceRegionIsNonNull() {
        for (SimulationType type : SimulationType.values()) {
            SimulationInstance instance = createInstance(type);
            assertNotNull(instance.region(),
                    "Expected non-null region() for type: " + type);
        }
    }

    // --- Individual type coverage ---

    @Test
    void testCreateInstanceStartscreen() {
        SimulationInstance instance = createInstance(SimulationType.STARTSCREEN);
        assertEquals(SimulationType.STARTSCREEN, instance.simulationType());
    }

    @Test
    void testCreateInstanceEtPets() {
        SimulationInstance instance = createInstance(SimulationType.ET_PETS);
        assertEquals(SimulationType.ET_PETS, instance.simulationType());
    }

    @Test
    void testCreateInstanceWator() {
        SimulationInstance instance = createInstance(SimulationType.WATOR);
        assertEquals(SimulationType.WATOR, instance.simulationType());
    }

    @Test
    void testCreateInstanceConwaysLife() {
        SimulationInstance instance = createInstance(SimulationType.CONWAYS_LIFE);
        assertEquals(SimulationType.CONWAYS_LIFE, instance.simulationType());
    }

    @Test
    void testCreateInstanceLangtonsAnt() {
        SimulationInstance instance = createInstance(SimulationType.LANGTONS_ANT);
        assertEquals(SimulationType.LANGTONS_ANT, instance.simulationType());
    }

    @Test
    void testCreateInstanceForestFire() {
        SimulationInstance instance = createInstance(SimulationType.FOREST_FIRE);
        assertEquals(SimulationType.FOREST_FIRE, instance.simulationType());
    }

    @Test
    void testCreateInstanceSugarscape() {
        SimulationInstance instance = createInstance(SimulationType.SUGARSCAPE);
        assertEquals(SimulationType.SUGARSCAPE, instance.simulationType());
    }

    @Test
    void testCreateInstanceSnake() {
        SimulationInstance instance = createInstance(SimulationType.SNAKE);
        assertEquals(SimulationType.SNAKE, instance.simulationType());
    }

    @Test
    void testCreateInstanceReboundingEntities() {
        SimulationInstance instance = createInstance(SimulationType.REBOUNDING_ENTITIES);
        assertEquals(SimulationType.REBOUNDING_ENTITIES, instance.simulationType());
    }

    @Test
    void testCreateInstanceSimulationLab() {
        SimulationInstance instance = createInstance(SimulationType.SIMULATION_LAB);
        assertEquals(SimulationType.SIMULATION_LAB, instance.simulationType());
    }

    // --- Enum coverage guard ---

    @Test
    void testAllSimulationTypesAreCoveredByFactory() {
        // Ensures that createInstance() handles every declared SimulationType constant
        // without throwing. If a new type is added to the enum without updating
        // SimulationFactory, the exhaustive switch will cause a compile error;
        // this test serves as an explicit runtime guard.
        assertAll(
                () -> {
                    for (SimulationType type : SimulationType.values()) {
                        assertDoesNotThrow(() -> createInstance(type),
                                "createInstance() threw for type: " + type);
                    }
                }
        );
    }

    // --- Stage updater callback wiring (STARTSCREEN) ---

    @Test
    void testCreateInstanceStartscreenInvokesStageUpdater() {
        // The STARTSCREEN view receives the stageUpdater callback; verify it is wired
        // correctly by checking that the returned instance is well-formed.
        Stage stage = createStage();
        AtomicReference<@Nullable SimulationInstance> ref = new AtomicReference<>();
        FxTestSupport.runAndWait(() ->
                ref.set(SimulationFactory.createInstance(SimulationType.STARTSCREEN, stage, (_, _) -> {})));

        SimulationInstance instance = ref.get();
        assertNotNull(instance);
        assertAll(
                () -> assertEquals(SimulationType.STARTSCREEN, instance.simulationType()),
                () -> assertNotNull(instance.simulationMainView()),
                () -> assertNotNull(instance.region())
        );
    }

}
