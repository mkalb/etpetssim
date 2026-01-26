package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import javafx.beans.property.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class DefaultObservationViewModel<
        ENT extends GridEntity,
        STA extends SimulationStatistics>
        implements SimulationObservationViewModel<STA> {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ReadOnlyObjectWrapper<STA> statistics;

    private final ObjectProperty<@Nullable GridCell<ENT>> selectedGridCell = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>();

    public DefaultObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
        statistics = new ReadOnlyObjectWrapper<>();
    }

    @Override
    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    @Override
    public ReadOnlyObjectProperty<STA> statisticsProperty() {
        return statistics.getReadOnlyProperty();
    }

    @Override
    public Optional<STA> getStatistics() {
        return Optional.ofNullable(statistics.get());
    }

    @Override
    public void setStatistics(STA stats) {
        statistics.set(stats);
    }

    /**
     * Bind property from MainViewModel to observation ViewModel
     * @param property the property to bind to
     */
    public void bindSelectedGridCellProperty(ObjectProperty<@Nullable GridCell<ENT>> property) {
        selectedGridCell.bind(property);
    }

    /**
     * Getter used by Observation View.
     * @return the selected grid cell property
     */
    public ObjectProperty<@Nullable GridCell<ENT>> selectedGridCellProperty() {
        return selectedGridCell;
    }

    /**
     * Getter used by Observation View.
     * @return the last clicked coordinate property
     */
    @Override
    public ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

}
