package de.mkalb.etpetssim.simulations.core;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.SimulationType;
import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.conway.view.ConwayMainView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsMainView;
import de.mkalb.etpetssim.simulations.forest.view.ForestMainView;
import de.mkalb.etpetssim.simulations.lab.view.LabMainView;
import de.mkalb.etpetssim.simulations.langton.view.LangtonMainView;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingMainView;
import de.mkalb.etpetssim.simulations.snake.view.SnakeMainView;
import de.mkalb.etpetssim.simulations.start.StartMainView;
import de.mkalb.etpetssim.simulations.sugar.view.SugarMainView;
import de.mkalb.etpetssim.simulations.wator.view.WatorMainView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class SimulationFactoryTest {

    private static final Map<SimulationType, Class<?>> EXPECTED_MAIN_VIEW_TYPES = Map.ofEntries(
            Map.entry(SimulationType.STARTSCREEN, StartMainView.class),
            Map.entry(SimulationType.ET_PETS, EtpetsMainView.class),
            Map.entry(SimulationType.WATOR, WatorMainView.class),
            Map.entry(SimulationType.CONWAYS_LIFE, ConwayMainView.class),
            Map.entry(SimulationType.LANGTONS_ANT, LangtonMainView.class),
            Map.entry(SimulationType.FOREST_FIRE, ForestMainView.class),
            Map.entry(SimulationType.SUGARSCAPE, SugarMainView.class),
            Map.entry(SimulationType.SNAKE, SnakeMainView.class),
            Map.entry(SimulationType.REBOUNDING_ENTITIES, ReboundingMainView.class),
            Map.entry(SimulationType.SIMULATION_LAB, LabMainView.class));

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FxTestSupport.ensureStarted();
    }

    private static Stage createStage() {
        return FxTestSupport.supplyAndWaitNonNull(Stage::new);
    }

    private static SimulationInstance createInstance(SimulationType type) {
        Stage stage = createStage();
        return FxTestSupport.supplyAndWaitNonNull(() ->
                SimulationFactory.createInstance(type, stage, (_, _) -> {}));
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

    // --- Concrete main view mapping ---

    @Test
    void testCreateInstanceUsesExpectedMainViewForEverySimulationType() {
        assertEquals(EnumSet.allOf(SimulationType.class), EXPECTED_MAIN_VIEW_TYPES.keySet());

        for (SimulationType type : SimulationType.values()) {
            Class<?> expectedMainViewType = EXPECTED_MAIN_VIEW_TYPES.get(type);
            SimulationInstance instance = createInstance(type);
            assertInstanceOf(expectedMainViewType, instance.simulationMainView());
        }
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
        FxTestSupport.runAndWait(() -> {
            Stage stage = new Stage();
            AtomicReference<@Nullable Stage> updatedStage = new AtomicReference<>();
            AtomicReference<@Nullable SimulationType> updatedType = new AtomicReference<>();
            SimulationType expectedType = Arrays.stream(SimulationType.values())
                                                .filter(type -> type.isShownOnStartScreen() && type.isImplemented())
                                                .findFirst()
                                                .orElseThrow();

            SimulationInstance instance = SimulationFactory.createInstance(
                    SimulationType.STARTSCREEN,
                    stage,
                    (nextStage, nextType) -> {
                        updatedStage.set(nextStage);
                        updatedType.set(nextType);
                    });
            VBox startScreen = assertInstanceOf(VBox.class, instance.region());
            Button button = startScreen.getChildren()
                                       .stream()
                                       .filter(Button.class::isInstance)
                                       .map(Button.class::cast)
                                       .filter(candidate -> candidate.getText().equals(expectedType.title()))
                                       .findFirst()
                                       .orElseThrow();

            button.fire();

            assertAll(
                    () -> assertEquals(SimulationType.STARTSCREEN, instance.simulationType()),
                    () -> assertSame(stage, updatedStage.get()),
                    () -> assertEquals(expectedType, updatedType.get())
            );
        });
    }

}
