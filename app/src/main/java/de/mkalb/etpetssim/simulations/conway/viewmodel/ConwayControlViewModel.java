package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

public final class ConwayControlViewModel {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final DoubleProperty stepDuration = new SimpleDoubleProperty(1_000.0d);
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

    public DoubleProperty stepDurationProperty() {
        return stepDuration;
    }

    public double getStepDuration() {
        return stepDuration.get();
    }

    public void setStepDuration(double value) {
        stepDuration.set(value);
    }

    public Duration getStepDurationAsDuration() {
        return Duration.millis(stepDuration.get());
    }

}