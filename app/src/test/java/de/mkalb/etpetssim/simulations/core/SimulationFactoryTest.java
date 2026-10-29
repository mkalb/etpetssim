package de.mkalb.etpetssim.simulations.core;

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
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        FXTestSupport.ensureStarted();
    }

    private static Stage createStage() {
        return FXTestSupport.supplyAndWaitNonNull(Stage::new);
    }

    private static SimulationInstance createInstance(SimulationType type) {
        Stage stage = createStage();
        return FXTestSupport.supplyAndWaitNonNull(() ->
                SimulationFactory.createInstance(type, stage, (_, _) -> {}));
    }

    @Test
    void testCreateInstance() {
        assertEquals(EnumSet.allOf(SimulationType.class), EXPECTED_MAIN_VIEW_TYPES.keySet());

        for (SimulationType type : SimulationType.values()) {
            SimulationInstance instance = createInstance(type);
            assertAll(
                    type.name(),
                    () -> assertEquals(type, instance.simulationType()),
                    () -> assertInstanceOf(EXPECTED_MAIN_VIEW_TYPES.get(type), instance.simulationMainView())
            );
        }
    }

    @Test
    void testCreateInstanceInvokesStageUpdaterForStartscreen() {
        FXTestSupport.runAndWait(() -> {
            Stage stage = new Stage();
            AtomicReference<Stage> updatedStage = new AtomicReference<>();
            AtomicReference<SimulationType> updatedType = new AtomicReference<>();
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
