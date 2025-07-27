package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class LabControlViewModel {

    private final ObjectProperty<SimulationState> simulationState;
    private Runnable onDrawButtonListener = () -> {};
    private Runnable onDrawModelButtonListener = () -> {};
    private Runnable onDrawTestButtonListener = () -> {};

    public LabControlViewModel(SimpleObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public void setOnDrawButtonListener(Runnable listener) {
        onDrawButtonListener = listener;
    }

    public void setOnDrawModelButtonListener(Runnable listener) {
        onDrawModelButtonListener = listener;
    }

    public void setOnDrawTestButtonListener(Runnable listener) {
        onDrawTestButtonListener = listener;
    }

    public void onDrawButtonClicked() {
        onDrawButtonListener.run();
    }

    public void onDrawModelButtonClicked() {
        onDrawModelButtonListener.run();
    }

    public void onDrawTestButtonClicked() {
        onDrawTestButtonListener.run();
    }

}
