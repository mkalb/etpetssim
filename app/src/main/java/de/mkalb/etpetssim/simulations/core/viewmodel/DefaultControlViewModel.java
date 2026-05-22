package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Default implementation of control view-model state and user requests.
 */
public final class DefaultControlViewModel
        extends AbstractControlViewModel {

    private static final int STEP_DURATION_INITIAL = 700;
    private static final int STEP_DURATION_MIN = 50;
    private static final int STEP_DURATION_MAX = 2_000;

    private static final int STEP_COUNT_INITIAL = 100;
    private static final int STEP_COUNT_MIN = 1;
    private static final int STEP_COUNT_MAX = 10_000;
    private static final int STEP_COUNT_STEP = 1;

    private final InputEnumProperty<SimulationMode> simulationMode = InputEnumProperty.of(SimulationMode.TIMED,
            SimulationMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputDoublePropertyIntRange stepDuration = InputDoublePropertyIntRange.of(STEP_DURATION_INITIAL,
            STEP_DURATION_MIN, STEP_DURATION_MAX);
    private final InputIntegerProperty stepCount = InputIntegerProperty.of(STEP_COUNT_INITIAL,
            STEP_COUNT_MIN, STEP_COUNT_MAX, STEP_COUNT_STEP);
    private final InputEnumProperty<SimulationStartMode> startMode = InputEnumProperty.of(SimulationStartMode.START_IMMEDIATELY, SimulationStartMode.class, Enum::toString);
    private final InputEnumProperty<SimulationTerminationCheck> terminationCheck = InputEnumProperty.of(SimulationTerminationCheck.CHECKED, SimulationTerminationCheck.class, Enum::toString);

    private final BooleanProperty actionButtonRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty cancelButtonRequested = new SimpleBooleanProperty(false);

    public DefaultControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

    /**
     * Creates a control view model preconfigured with the minimum timed-step duration.
     *
     * @param simulationState shared simulation state
     * @return configured control view model
     */
    public static DefaultControlViewModel withMinStepDuration(ReadOnlyObjectProperty<SimulationState> simulationState) {
        DefaultControlViewModel controlViewModel = new DefaultControlViewModel(simulationState);
        controlViewModel.stepDuration.setValue(STEP_DURATION_MIN);
        return controlViewModel;
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

    /**
     * Exposes the selected simulation mode input.
     *
     * @return simulation mode input property wrapper
     */
    public InputEnumProperty<SimulationMode> simulationModeProperty() {
        return simulationMode;
    }

    /**
     * Exposes the timed-step duration input.
     *
     * @return step-duration input property wrapper
     */
    public InputDoublePropertyIntRange stepDurationProperty() {
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

    /**
     * Exposes the simulation start-mode input.
     *
     * @return start-mode input property wrapper
     */
    public InputEnumProperty<SimulationStartMode> startModeProperty() {
        return startMode;
    }

    /**
     * Exposes the termination-check mode input.
     *
     * @return termination-check input property wrapper
     */
    public InputEnumProperty<SimulationTerminationCheck> terminationCheckProperty() {
        return terminationCheck;
    }

    /**
     * Returns whether the timed execution mode is selected.
     *
     * @return {@code true} if timed mode is selected
     */
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

    /**
     * Returns whether single-batch execution mode is selected.
     *
     * @return {@code true} if single-batch mode is selected
     */
    public boolean isModeBatchSingle() {
        return simulationMode.getValue() == SimulationMode.BATCH_SINGLE;
    }

    /**
     * Returns whether continuous-batch execution mode is selected.
     *
     * @return {@code true} if continuous-batch mode is selected
     */
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

    /**
     * Returns whether termination checks are enabled.
     *
     * @return {@code true} if termination checks are enabled
     */
    public boolean isTerminationChecked() {
        return terminationCheck.getValue() == SimulationTerminationCheck.CHECKED;
    }

}
