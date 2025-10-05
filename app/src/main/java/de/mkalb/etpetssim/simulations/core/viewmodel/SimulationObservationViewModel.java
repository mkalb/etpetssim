package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public interface SimulationObservationViewModel<STA extends SimulationStatistics> {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    ReadOnlyObjectProperty<STA> statisticsProperty();

    Optional<STA> getStatistics();

    void setStatistics(STA stats);

}
