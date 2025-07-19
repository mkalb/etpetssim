package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

public final class ConwayControlViewModel {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
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

}