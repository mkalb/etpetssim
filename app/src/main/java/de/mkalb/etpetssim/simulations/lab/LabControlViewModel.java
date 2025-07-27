package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.*;

public final class LabControlViewModel {

    private final ObjectProperty<SimulationState> simulationState;
    private final BooleanProperty drawRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty drawModelRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty drawTestRequested = new SimpleBooleanProperty(false);

    public LabControlViewModel(SimpleObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public BooleanProperty drawRequestedProperty() {
        return drawRequested;
    }

    public BooleanProperty drawModelRequestedProperty() {
        return drawModelRequested;
    }

    public BooleanProperty drawTestRequestedProperty() {
        return drawTestRequested;
    }

    public void requestDraw() {
        drawRequested.set(true);
    }

    public void requestDrawModel() {
        drawModelRequested.set(true);
    }

    public void requestDrawTest() {
        drawTestRequested.set(true);
    }

}
