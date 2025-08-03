package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface SimulationConfigViewModel<T> {

    T getConfig();

    void setConfig(T config);

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

}