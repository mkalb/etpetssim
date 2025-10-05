package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface SimulationConfigViewModel<CON extends SimulationConfig> {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    CON getConfig();

}
