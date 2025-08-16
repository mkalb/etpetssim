package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationMode;
import de.mkalb.etpetssim.simulations.model.SimulationState;
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
            SimulationMode.class, Enum::toString);
    private final InputDoublePropertyIntRange stepDuration = InputDoublePropertyIntRange.of(STEP_DURATION_INITIAL,
            STEP_DURATION_MIN, STEP_DURATION_MAX);
    private final InputIntegerProperty stepCount = InputIntegerProperty.of(STEP_COUNT_INITIAL,
            STEP_COUNT_MIN, STEP_COUNT_MAX, STEP_COUNT_STEP);
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

    public boolean isLiveMode() {
        return simulationMode.getValue() == SimulationMode.LIVE;
    }

    public boolean isBatchMode() {
        return simulationMode.getValue() == SimulationMode.BATCH;
    }

}
