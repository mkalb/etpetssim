package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.shared.SimulationMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStartMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.shared.SimulationTerminationCheckMode;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import de.mkalb.etpetssim.ui.InputDoubleIntRangeProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Default implementation of control view-model state and user requests.
 */
public final class DefaultControlViewModel
        implements SimulationControlViewModel {

    private static final int STEP_DURATION_INITIAL = 700;
    private static final int STEP_DURATION_MIN = 50;
    private static final int STEP_DURATION_MAX = 2_000;
    private static final int STEP_COUNT_INITIAL = 100;
    private static final int STEP_COUNT_MIN = 1;
    private static final int STEP_COUNT_MAX = 10_000;
    private static final int STEP_COUNT_STEP = 1;

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final InputChoiceProperty<SimulationMode> simulationMode = InputChoiceProperty.ofEnum(SimulationMode.TIMED,
            SimulationMode.class,
            e -> AppLocalization.getText(e.resourceKey()));
    private final InputDoubleIntRangeProperty stepDuration = InputDoubleIntRangeProperty.of(STEP_DURATION_INITIAL,
            STEP_DURATION_MIN, STEP_DURATION_MAX);
    private final InputIntegerProperty stepCount = InputIntegerProperty.of(STEP_COUNT_INITIAL,
            STEP_COUNT_MIN, STEP_COUNT_MAX, STEP_COUNT_STEP);
    private final InputChoiceProperty<SimulationStartMode> startMode = InputChoiceProperty.ofEnum(SimulationStartMode.START_IMMEDIATELY, SimulationStartMode.class, Enum::toString);
    private final InputChoiceProperty<SimulationTerminationCheckMode> terminationCheckMode = InputChoiceProperty.ofEnum(SimulationTerminationCheckMode.CHECKED, SimulationTerminationCheckMode.class, Enum::toString);

    private final BooleanProperty actionButtonRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty cancelButtonRequested = new SimpleBooleanProperty(false);

    public DefaultControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    /**
     * Creates a control view model preconfigured with the minimum timed-step duration.
     *
     * @param simulationState shared simulation state
     * @return configured control view model
     */
    public static DefaultControlViewModel createWithMinStepDuration(ReadOnlyObjectProperty<SimulationState> simulationState) {
        DefaultControlViewModel controlViewModel = new DefaultControlViewModel(simulationState);
        controlViewModel.stepDuration.setValue(STEP_DURATION_MIN);
        return controlViewModel;
    }

    @Override
    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    /**
     * Exposes the action-button trigger flag.
     *
     * @return action trigger property
     */
    public BooleanProperty actionButtonRequestedProperty() {
        return actionButtonRequested;
    }

    /**
     * Exposes the cancel-button trigger flag.
     *
     * @return cancel trigger property
     */
    public BooleanProperty cancelButtonRequestedProperty() {
        return cancelButtonRequested;
    }

    /**
     * Triggers an action-button request (start, pause, or resume depending on state).
     */
    public void requestActionButton() {
        actionButtonRequested.set(true);
    }

    /**
     * Triggers a cancel-button action request.
     */
    public void requestCancelButton() {
        cancelButtonRequested.set(true);
    }

    public InputChoiceProperty<SimulationMode> simulationModeProperty() {
        return simulationMode;
    }

    /**
     * Exposes the timed-step duration input in milliseconds.
     *
     * @return step-duration input property wrapper
     */
    public InputDoubleIntRangeProperty stepDurationProperty() {
        return stepDuration;
    }

    /**
     * Exposes the batch step-count input.
     *
     * @return step-count input property wrapper
     */
    public InputIntegerProperty stepCountProperty() {
        return stepCount;
    }

    public InputChoiceProperty<SimulationStartMode> startModeProperty() {
        return startMode;
    }

    public InputChoiceProperty<SimulationTerminationCheckMode> terminationCheckProperty() {
        return terminationCheckMode;
    }

    /**
     * Returns whether configuration controls should be disabled for the current simulation state.
     *
     * @return {@code true} if configuration controls are disabled
     */
    public boolean isControlConfigDisabled() {
        return switch (getSimulationState()) {
            case READY, PAUSED, CANCELED, FINISHED, ERROR -> false;
            case INITIALIZING, RUNNING_TIMED, RUNNING_BATCH, PAUSING_BATCH, CANCELLING_BATCH, SHUTTING_DOWN -> true;
        };
    }

    public boolean isModeTimed() {
        return simulationMode.getValue() == SimulationMode.TIMED;
    }

    /**
     * Returns whether any batch execution mode is selected.
     *
     * @return {@code true} if a batch mode is selected
     */
    public boolean isModeBatch() {
        return isModeBatchSingle() || isModeBatchContinuous();
    }

    public boolean isModeBatchSingle() {
        return simulationMode.getValue() == SimulationMode.BATCH_SINGLE;
    }

    public boolean isModeBatchContinuous() {
        return simulationMode.getValue() == SimulationMode.BATCH_CONTINUOUS;
    }

    /**
     * Returns whether simulation startup should end in paused state.
     *
     * @return {@code true} if start-paused mode is selected
     */
    public boolean isStartPaused() {
        return startMode.getValue() == SimulationStartMode.START_PAUSED;
    }

    public boolean isTerminationChecked() {
        return terminationCheckMode.getValue() == SimulationTerminationCheckMode.CHECKED;
    }

}
