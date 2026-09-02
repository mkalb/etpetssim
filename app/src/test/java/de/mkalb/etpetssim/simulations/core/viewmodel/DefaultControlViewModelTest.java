package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.simulations.core.shared.*;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class DefaultControlViewModelTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
    }

    @Test
    void testControlConfigurationDisabledStates() {
        FxTestSupport.runAndWait(() -> {
            var state = new SimpleObjectProperty<>(SimulationState.READY);
            var viewModel = new DefaultControlViewModel(state);

            for (SimulationState simulationState : EnumSet.of(
                    SimulationState.INITIALIZING,
                    SimulationState.RUNNING_TIMED,
                    SimulationState.RUNNING_BATCH,
                    SimulationState.PAUSING_BATCH,
                    SimulationState.CANCELLING_BATCH,
                    SimulationState.SHUTTING_DOWN)) {
                state.set(simulationState);
                assertTrue(viewModel.isControlConfigDisabled(), simulationState::name);
            }
        });
    }

    @Test
    void testControlConfigurationEnabledStates() {
        FxTestSupport.runAndWait(() -> {
            var state = new SimpleObjectProperty<>(SimulationState.INITIALIZING);
            var viewModel = new DefaultControlViewModel(state);

            for (SimulationState simulationState : EnumSet.of(
                    SimulationState.READY,
                    SimulationState.PAUSED,
                    SimulationState.CANCELED,
                    SimulationState.FINISHED,
                    SimulationState.ERROR)) {
                state.set(simulationState);
                assertFalse(viewModel.isControlConfigDisabled(), simulationState::name);
            }
        });
    }

    @Test
    void testDefaultInputValues() {
        FxTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            assertAll(
                    () -> assertEquals(SimulationMode.TIMED, viewModel.simulationModeProperty().getValue()),
                    () -> assertEquals(700.0d, viewModel.stepDurationProperty().getValue()),
                    () -> assertEquals(100, viewModel.stepCountProperty().getValue()),
                    () -> assertEquals(SimulationStartMode.START_IMMEDIATELY, viewModel.startModeProperty().getValue()),
                    () -> assertEquals(SimulationTerminationCheck.CHECKED, viewModel.terminationCheckProperty().getValue())
            );
        });
    }

    @Test
    void testWithMinStepDuration() {
        FxTestSupport.runAndWait(() -> {
            var viewModel = DefaultControlViewModel.withMinStepDuration(
                    new SimpleObjectProperty<>(SimulationState.READY));

            assertEquals(viewModel.stepDurationProperty().min(), viewModel.stepDurationProperty().getValue());
        });
    }

    @Test
    void testRequestButtonsSetRequestFlags() {
        FxTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            assertAll(
                    () -> assertFalse(viewModel.actionButtonRequestedProperty().get()),
                    () -> assertFalse(viewModel.cancelButtonRequestedProperty().get())
            );
            viewModel.requestActionButton();
            viewModel.requestCancelButton();

            assertAll(
                    () -> assertTrue(viewModel.actionButtonRequestedProperty().get()),
                    () -> assertTrue(viewModel.cancelButtonRequestedProperty().get())
            );
        });
    }

    @Test
    void testSimulationModePredicates() {
        FxTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationMode simulationMode : SimulationMode.values()) {
                viewModel.simulationModeProperty().setValue(simulationMode);
                assertAll(
                        simulationMode.name(),
                        () -> assertEquals(simulationMode == SimulationMode.TIMED, viewModel.isModeTimed()),
                        () -> assertEquals(simulationMode != SimulationMode.TIMED, viewModel.isModeBatch()),
                        () -> assertEquals(simulationMode == SimulationMode.BATCH_SINGLE, viewModel.isModeBatchSingle()),
                        () -> assertEquals(simulationMode == SimulationMode.BATCH_CONTINUOUS, viewModel.isModeBatchContinuous())
                );
            }
        });
    }

    @Test
    void testStartPausedPredicate() {
        FxTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationStartMode startMode : SimulationStartMode.values()) {
                viewModel.startModeProperty().setValue(startMode);
                assertEquals(startMode == SimulationStartMode.START_PAUSED, viewModel.isStartPaused());
            }
        });
    }

    @Test
    void testTerminationCheckedPredicate() {
        FxTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationTerminationCheck terminationCheck : SimulationTerminationCheck.values()) {
                viewModel.terminationCheckProperty().setValue(terminationCheck);
                assertEquals(terminationCheck == SimulationTerminationCheck.CHECKED, viewModel.isTerminationChecked());
            }
        });
    }

    @Test
    void testSimulationStatePropertyIsForwarded() {
        FxTestSupport.runAndWait(() -> {
            var state = new SimpleObjectProperty<>(SimulationState.READY);
            var viewModel = new DefaultControlViewModel(state);

            assertSame(state, viewModel.simulationStateProperty());
            state.set(SimulationState.PAUSED);

            assertEquals(SimulationState.PAUSED, viewModel.getSimulationState());
        });
    }

}
