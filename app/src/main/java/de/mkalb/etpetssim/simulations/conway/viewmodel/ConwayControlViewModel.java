package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.ui.ExtendedDoublePropertyIntRange;
import javafx.beans.property.ReadOnlyObjectProperty;

public final class ConwayControlViewModel {

    private static final int STEP_DURATION_INITIAL = 1_000;
    private static final int STEP_DURATION_MIN = 100;
    private static final int STEP_DURATION_MAX = 2_000;

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ExtendedDoublePropertyIntRange stepDuration = ExtendedDoublePropertyIntRange.of(STEP_DURATION_INITIAL,
            STEP_DURATION_MIN, STEP_DURATION_MAX);
    private Runnable onActionButtonListener = () -> {};
    private Runnable onCancelButtonListener = () -> {};

    public ConwayControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public void setOnActionButtonListener(Runnable listener) {
        onActionButtonListener = listener;
    }

    public void setOnCancelButtonListener(Runnable listener) {
        onCancelButtonListener = listener;
    }

    public void onActionButtonClicked() {
        onActionButtonListener.run();
    }

    public void onCancelButtonClicked() {
        onCancelButtonListener.run();
    }

    public ExtendedDoublePropertyIntRange stepDurationProperty() {
        return stepDuration;
    }

}