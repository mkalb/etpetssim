package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.viewmodel.ControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationExecutionMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationState;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class ControlViewTest {

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static Fixture createFixture() {
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.READY);
        var viewModel = new ControlViewModel(simulationState);
        var view = new ControlView(viewModel);
        Region controlRegion = view.buildControlRegion();
        HBox controlHBox = assertInstanceOf(HBox.class,
                assertInstanceOf(ScrollPane.class, controlRegion).getContent());
        return new Fixture(simulationState, viewModel, view, controlHBox);
    }

    private static Node child(Node parent, int index) {
        return assertInstanceOf(Parent.class, parent).getChildrenUnmodifiable().get(index);
    }

    @Test
    void testBuildControlRegion() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();
            HBox controlHBox = fixture.controlHBox();
            Label stepTitleLabel = assertInstanceOf(Label.class, child(fixture.observationBox(), 0));

            assertAll(
                    () -> assertEquals(4, controlHBox.getChildren().size()),
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.CONTROL_START),
                            fixture.actionButton().getText()),
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.CONTROL_CANCEL),
                            fixture.cancelButton().getText()),
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.CONTROL_STEP_TITLE),
                            stepTitleLabel.getText()),
                    () -> assertEquals(AppLocalization.getFormattedText(
                                    SimulationLocalizationKeys.CONTROL_STEP_DURATION_TOOLTIP,
                                    fixture.viewModel().stepDurationProperty().min(),
                                    fixture.viewModel().stepDurationProperty().max()),
                            fixture.stepDurationLabel().getTooltip().getText()),
                    () -> assertEquals(AppLocalization.getFormattedText(
                                    SimulationLocalizationKeys.CONTROL_STEP_COUNT_TOOLTIP,
                                    fixture.viewModel().stepCountProperty().min(),
                                    fixture.viewModel().stepCountProperty().max()),
                            fixture.stepCountLabel().getTooltip().getText())
            );
        });
    }

    @Test
    void testBuildControlRegionShowsStepDurationOnlyInTimedMode() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();
            Node stepDurationBox = child(fixture.stepConfigPane(), 0);
            Node stepCountBox = child(fixture.stepConfigPane(), 1);

            fixture.viewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.TIMED);
            boolean durationVisibleWhenTimed = stepDurationBox.isVisible() && stepDurationBox.isManaged();
            boolean countVisibleWhenTimed = stepCountBox.isVisible() || stepCountBox.isManaged();
            fixture.viewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.BATCH_SINGLE);

            assertAll(
                    () -> assertTrue(durationVisibleWhenTimed),
                    () -> assertFalse(countVisibleWhenTimed),
                    () -> assertFalse(stepDurationBox.isVisible()),
                    () -> assertFalse(stepDurationBox.isManaged()),
                    () -> assertTrue(stepCountBox.isVisible()),
                    () -> assertTrue(stepCountBox.isManaged())
            );
        });
    }

    @ParameterizedTest
    @CsvSource({
            "READY, control.start",
            "CANCELED, control.start",
            "RUNNING_TIMED, control.pause",
            "RUNNING_BATCH, control.pause",
            "PAUSED, control.resume"
    })
    void testBuildControlRegionBindsActionButtonText(SimulationState state, String expectedKey) {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();

            fixture.simulationState().set(state);

            assertEquals(AppLocalization.getText(expectedKey), fixture.actionButton().getText());
        });
    }

    @Test
    void testBuildControlRegionShowsPlaceholderTextWhileTransitioning() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();

            fixture.simulationState().set(SimulationState.INITIALIZING);

            assertEquals("...", fixture.actionButton().getText());
        });
    }

    @ParameterizedTest
    @CsvSource({
            "READY,            false, true,  false, false",
            "INITIALIZING,     true,  true,  true,  true",
            "RUNNING_TIMED,    false, false, true,  true",
            "RUNNING_BATCH,    false, false, true,  true",
            "PAUSING_BATCH,    true,  true,  true,  true",
            "PAUSED,           false, false, false, true",
            "CANCELLING_BATCH, true,  true,  true,  true",
            "CANCELED,         false, true,  false, false",
            "FINISHED,         false, true,  false, false",
            "ERROR,            false, true,  false, false",
            "SHUTTING_DOWN,    true,  true,  true,  true"
    })
    void testBuildControlRegionBindsDisabledStates(SimulationState state,
                                                   boolean actionDisabled,
                                                   boolean cancelDisabled,
                                                   boolean configDisabled,
                                                   boolean startModeDisabled) {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();

            fixture.simulationState().set(state);

            Node pauseBox = fixture.pauseBox();
            assertAll(
                    () -> assertEquals(actionDisabled, fixture.actionButton().isDisabled()),
                    () -> assertEquals(cancelDisabled, fixture.cancelButton().isDisabled()),
                    () -> assertEquals(configDisabled, child(fixture.executionModeBox(), 1).isDisabled()),
                    () -> assertEquals(configDisabled, child(child(fixture.stepConfigPane(), 0), 1).isDisabled()),
                    () -> assertEquals(configDisabled, child(child(fixture.stepConfigPane(), 1), 1).isDisabled()),
                    () -> assertEquals(configDisabled, child(pauseBox, 3).isDisabled()),
                    () -> assertEquals(startModeDisabled, child(pauseBox, 1).isDisabled())
            );
        });
    }

    @Test
    void testBuildControlRegionForwardsButtonActions() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();
            fixture.simulationState().set(SimulationState.PAUSED);

            fixture.actionButton().fire();
            fixture.cancelButton().fire();

            assertAll(
                    () -> assertTrue(fixture.viewModel().actionButtonRequestedProperty().get()),
                    () -> assertTrue(fixture.viewModel().cancelButtonRequestedProperty().get())
            );
        });
    }

    @Test
    void testSetStepCount() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();
            Label stepNumberLabel = assertInstanceOf(Label.class, child(fixture.observationBox(), 1));

            fixture.view().setStepCount(1_234);

            assertEquals(AppLocalization.getFormattedText(SimulationLocalizationKeys.CONTROL_STEP_NUMBER, 1_234),
                    stepNumberLabel.getText());
        });
    }

    private record Fixture(
            ObjectProperty<SimulationState> simulationState,
            ControlViewModel viewModel,
            ControlView view,
            HBox controlHBox) {

        Button actionButton() {
            return assertInstanceOf(Button.class, child(controlHBox.getChildren().getFirst(), 0));
        }

        Button cancelButton() {
            return assertInstanceOf(Button.class, child(controlHBox.getChildren().getFirst(), 1));
        }

        VBox executionModeBox() {
            return assertInstanceOf(VBox.class, child(controlHBox.getChildren().get(1), 0));
        }

        StackPane stepConfigPane() {
            return assertInstanceOf(StackPane.class, child(controlHBox.getChildren().get(1), 1));
        }

        VBox pauseBox() {
            return assertInstanceOf(VBox.class, child(controlHBox.getChildren().get(1), 2));
        }

        Label stepDurationLabel() {
            return assertInstanceOf(Label.class, child(child(stepConfigPane(), 0), 0));
        }

        Label stepCountLabel() {
            return assertInstanceOf(Label.class, child(child(stepConfigPane(), 1), 0));
        }

        VBox observationBox() {
            return assertInstanceOf(VBox.class, controlHBox.getChildren().get(3));
        }

    }

}
