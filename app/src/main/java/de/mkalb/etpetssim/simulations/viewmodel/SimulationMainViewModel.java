package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface SimulationMainViewModel {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    BooleanProperty simulationTimeoutProperty();

    GridStructure getStructure();

    double getCellEdgeLength();

    void shutdownSimulation();

}
