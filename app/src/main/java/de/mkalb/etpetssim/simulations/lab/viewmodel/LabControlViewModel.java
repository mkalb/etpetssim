package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractControlViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class LabControlViewModel
        extends AbstractControlViewModel {

    private final BooleanProperty drawRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty drawModelRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty drawTestRequested = new SimpleBooleanProperty(false);

    public LabControlViewModel(SimpleObjectProperty<SimulationState> simulationState) {
        super(simulationState);
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
