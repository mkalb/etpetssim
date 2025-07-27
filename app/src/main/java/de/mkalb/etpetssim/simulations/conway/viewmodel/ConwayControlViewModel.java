package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class ConwayControlViewModel {

    private static final int STEP_DURATION_INITIAL = 700;
    private static final int STEP_DURATION_MIN = 100;
    private static final int STEP_DURATION_MAX = 2_000;

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final InputDoublePropertyIntRange stepDuration = InputDoublePropertyIntRange.of(STEP_DURATION_INITIAL,
            STEP_DURATION_MIN, STEP_DURATION_MAX);
    private final BooleanProperty actionButtonRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty cancelButtonRequested = new SimpleBooleanProperty(false);

    public ConwayControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
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

    public InputDoublePropertyIntRange stepDurationProperty() {
        return stepDuration;
    }

}