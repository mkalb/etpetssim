package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractObservationViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

public final class LabObservationViewModel
        extends AbstractObservationViewModel<LabStatistics> {

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);

    public LabObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

    public ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

    public @Nullable GridCoordinate getLastClickedCoordinate() {
        return lastClickedCoordinate.get();
    }

}
