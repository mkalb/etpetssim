package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.model.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface SimulationMainViewModel {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    ObjectProperty<SimulationNotificationType> notificationTypeProperty();

    void setNotificationType(SimulationNotificationType notificationType);

    GridStructure getStructure();

    double getCellEdgeLength();

    void shutdownSimulation();

}
