package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.shared.*;
import javafx.beans.property.*;

/**
 * Contract for the main simulation orchestration view model.
 */
public interface SimulationMainViewModel {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    /**
     * Exposes the notification type shown by the main view; {@link SimulationNotificationType#NONE} hides the
     * notification.
     *
     * @return mutable notification type property
     */
    ObjectProperty<SimulationNotificationType> notificationTypeProperty();

    void setNotificationType(SimulationNotificationType notificationType);

    /**
     * Returns the grid structure of the active simulation.
     *
     * @return simulation structure
     */
    GridStructure getStructure();

    /**
     * Returns the configured visual edge length of one cell of the active simulation.
     *
     * @return edge length in pixels
     */
    double getCellEdgeLength();

    /**
     * Immediately stops the simulation and releases resources on the JavaFX Application Thread.
     *
     * <p>This operation is idempotent and must not wait for background work to terminate.
     *
     * @return termination handle for application-exit bookkeeping
     */
    SimulationTerminationHandle shutdownSimulation();

}
