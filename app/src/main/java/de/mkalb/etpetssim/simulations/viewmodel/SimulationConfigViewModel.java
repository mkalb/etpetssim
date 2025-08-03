package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface SimulationConfigViewModel<C> {

    C getConfig();

    void setConfig(C config);

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

}