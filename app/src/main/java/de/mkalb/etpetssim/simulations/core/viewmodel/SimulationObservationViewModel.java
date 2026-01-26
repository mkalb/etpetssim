package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

public interface SimulationObservationViewModel<STA extends SimulationStatistics> {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    ReadOnlyObjectProperty<STA> statisticsProperty();

    Optional<STA> getStatistics();

    void setStatistics(STA stats);

    ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty();

}
