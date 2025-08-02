package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface BaseConfigViewModel<T> {

    T getConfig();

    void setConfig(T config);

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

}