package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.SimulationMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStartMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.shared.SimulationTerminationCheckMode;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class DefaultControlViewModelTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    @Test
    void testIsControlConfigDisabledReturnsTrueForBusyStates() {
        FXTestSupport.runAndWait(() -> {
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
    void testIsControlConfigDisabledReturnsFalseForIdleStates() {
        FXTestSupport.runAndWait(() -> {
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
    void testConstructorSetsDefaultInputValues() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            assertAll(
                    () -> assertEquals(SimulationMode.TIMED, viewModel.simulationModeProperty().getValue()),
                    () -> assertEquals(700.0d, viewModel.stepDurationProperty().getValue()),
                    () -> assertEquals(100, viewModel.stepCountProperty().getValue()),
                    () -> assertEquals(SimulationStartMode.START_IMMEDIATELY, viewModel.startModeProperty().getValue()),
                    () -> assertEquals(SimulationTerminationCheckMode.CHECKED, viewModel.terminationCheckProperty().getValue())
            );
        });
    }

    @Test
    void testCreateWithMinStepDuration() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = DefaultControlViewModel.createWithMinStepDuration(
                    new SimpleObjectProperty<>(SimulationState.READY));

            assertEquals(viewModel.stepDurationProperty().min(), viewModel.stepDurationProperty().getValue());
        });
    }

    @Test
    void testRequestActionButton() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            assertFalse(viewModel.actionButtonRequestedProperty().get());
            viewModel.requestActionButton();

            assertTrue(viewModel.actionButtonRequestedProperty().get());
        });
    }

    @Test
    void testRequestCancelButton() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            assertFalse(viewModel.cancelButtonRequestedProperty().get());
            viewModel.requestCancelButton();

            assertTrue(viewModel.cancelButtonRequestedProperty().get());
        });
    }

    @Test
    void testIsModeTimed() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationMode simulationMode : SimulationMode.values()) {
                viewModel.simulationModeProperty().setValue(simulationMode);
                assertEquals(simulationMode == SimulationMode.TIMED, viewModel.isModeTimed(), simulationMode.name());
            }
        });
    }

    @Test
    void testIsModeBatch() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationMode simulationMode : SimulationMode.values()) {
                viewModel.simulationModeProperty().setValue(simulationMode);
                assertEquals(simulationMode != SimulationMode.TIMED, viewModel.isModeBatch(), simulationMode.name());
            }
        });
    }

    @Test
    void testIsModeBatchSingle() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationMode simulationMode : SimulationMode.values()) {
                viewModel.simulationModeProperty().setValue(simulationMode);
                assertEquals(simulationMode == SimulationMode.BATCH_SINGLE, viewModel.isModeBatchSingle(), simulationMode.name());
            }
        });
    }

    @Test
    void testIsModeBatchContinuous() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationMode simulationMode : SimulationMode.values()) {
                viewModel.simulationModeProperty().setValue(simulationMode);
                assertEquals(simulationMode == SimulationMode.BATCH_CONTINUOUS, viewModel.isModeBatchContinuous(), simulationMode.name());
            }
        });
    }

    @Test
    void testIsStartPaused() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationStartMode startMode : SimulationStartMode.values()) {
                viewModel.startModeProperty().setValue(startMode);
                assertEquals(startMode == SimulationStartMode.START_PAUSED, viewModel.isStartPaused());
            }
        });
    }

    @Test
    void testIsTerminationChecked() {
        FXTestSupport.runAndWait(() -> {
            var viewModel = new DefaultControlViewModel(new SimpleObjectProperty<>(SimulationState.READY));

            for (SimulationTerminationCheckMode terminationCheckMode : SimulationTerminationCheckMode.values()) {
                viewModel.terminationCheckProperty().setValue(terminationCheckMode);
                assertEquals(terminationCheckMode == SimulationTerminationCheckMode.CHECKED, viewModel.isTerminationChecked());
            }
        });
    }

    @Test
    void testSimulationStatePropertyIsForwarded() {
        FXTestSupport.runAndWait(() -> {
            var state = new SimpleObjectProperty<>(SimulationState.READY);
            var viewModel = new DefaultControlViewModel(state);

            assertSame(state, viewModel.simulationStateProperty());
            state.set(SimulationState.PAUSED);

            assertEquals(SimulationState.PAUSED, viewModel.getSimulationState());
        });
    }

}
