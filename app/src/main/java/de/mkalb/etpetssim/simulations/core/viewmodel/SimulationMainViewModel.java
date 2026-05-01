package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.model.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Contract for the main simulation orchestration view model.
 */
public interface SimulationMainViewModel {

    /**
     * Exposes the current simulation state as read-only JavaFX property.
     *
     * @return read-only simulation state property
     */
    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    /**
     * Returns the current simulation state.
     *
     * @return current simulation state
     */
    SimulationState getSimulationState();

    /**
     * Exposes the notification type used by the main view.
     *
     * @return mutable notification type property
     */
    ObjectProperty<SimulationNotificationType> notificationTypeProperty();

    /**
     * Updates the current notification type.
     *
     * @param notificationType new notification type
     */
    void setNotificationType(SimulationNotificationType notificationType);

    /**
     * Returns the grid structure of the active simulation.
     *
     * @return simulation structure
     */
    GridStructure getStructure();

    /**
     * Returns the configured visual edge length of one cell.
     *
     * @return edge length in pixels
     */
    double getCellEdgeLength();

    /**
     * Stops the simulation and releases resources.
     */
    void shutdownSimulation();

}
