package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface BaseMainViewModel {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

}