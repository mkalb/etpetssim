package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Contract for simulation control state shared with control views.
 */
public interface SimulationControlViewModel {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

}
