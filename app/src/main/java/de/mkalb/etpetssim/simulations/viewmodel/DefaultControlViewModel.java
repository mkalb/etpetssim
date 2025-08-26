package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.model.*;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class DefaultControlViewModel
        extends AbstractControlViewModel {

    private static final int STEP_DURATION_INITIAL = 700;
    private static final int STEP_DURATION_MIN = 100;
    private static final int STEP_DURATION_MAX = 2_000;

    private static final int STEP_COUNT_INITIAL = 100;
    private static final int STEP_COUNT_MIN = 1;
    private static final int STEP_COUNT_MAX = 10_000;
    private static final int STEP_COUNT_STEP = 1;

    private final InputEnumProperty<SimulationMode> simulationMode = InputEnumProperty.of(SimulationMode.LIVE,
            SimulationMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputDoublePropertyIntRange stepDuration = InputDoublePropertyIntRange.of(STEP_DURATION_INITIAL,
            STEP_DURATION_MIN, STEP_DURATION_MAX);
    private final InputIntegerProperty stepCount = InputIntegerProperty.of(STEP_COUNT_INITIAL,
            STEP_COUNT_MIN, STEP_COUNT_MAX, STEP_COUNT_STEP);
    private final InputEnumProperty<SimulationStartMode> startMode = InputEnumProperty.of(SimulationStartMode.RUNNING, SimulationStartMode.class, Enum::toString);
    private final InputEnumProperty<SimulationTerminationCheck> terminationCheck = InputEnumProperty.of(SimulationTerminationCheck.CHECKED, SimulationTerminationCheck.class, Enum::toString);
    private final InputEnumProperty<SimulationRestartMode> restartMode = InputEnumProperty.of(SimulationRestartMode.NO_RESTART, SimulationRestartMode.class, Enum::toString);

    private final BooleanProperty actionButtonRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty cancelButtonRequested = new SimpleBooleanProperty(false);

    public DefaultControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

    public BooleanProperty actionButtonRequestedProperty() {
        return actionButtonRequested;
    }

    public BooleanProperty cancelButtonRequestedProperty() {
        return cancelButtonRequested;
    }

    public void requestActionButton() {
        actionButtonRequested.set(true);
    }

    public void requestCancelButton() {
        cancelButtonRequested.set(true);
    }

    public InputEnumProperty<SimulationMode> simulationModeProperty() {
        return simulationMode;
    }

    public InputDoublePropertyIntRange stepDurationProperty() {
        return stepDuration;
    }

    public InputIntegerProperty stepCountProperty() {
        return stepCount;
    }

    public InputEnumProperty<SimulationStartMode> startModeProperty() {
        return startMode;
    }

    public InputEnumProperty<SimulationTerminationCheck> terminationCheckProperty() {
        return terminationCheck;
    }

    public InputEnumProperty<SimulationRestartMode> restartModeProperty() {
        return restartMode;
    }

    public boolean isLiveMode() {
        return simulationMode.getValue() == SimulationMode.LIVE;
    }

    public boolean isBatchMode() {
        return simulationMode.getValue() == SimulationMode.BATCH;
    }

    public boolean isStartPaused() {
        return startMode.getValue() == SimulationStartMode.PAUSED;
    }

    public boolean isTerminationChecked() {
        return terminationCheck.getValue() == SimulationTerminationCheck.CHECKED;
    }

    public boolean isRestartEnabled() {
        return restartMode.getValue() == SimulationRestartMode.RESTART;
    }

}
