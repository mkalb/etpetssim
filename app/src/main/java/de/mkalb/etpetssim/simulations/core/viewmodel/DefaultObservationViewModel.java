package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import javafx.beans.property.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Default implementation of observation-related view-model state.
 */
public final class DefaultObservationViewModel<
        ENT extends GridEntity,
        STA extends SimulationStatistics>
        implements SimulationObservationViewModel<STA> {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ReadOnlyObjectWrapper<@Nullable STA> statistics;

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
    public ReadOnlyObjectProperty<@Nullable STA> statisticsProperty() {
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
     * Binds the selected-cell property from the main view model.
     *
     * @param property source property to bind from
     */
    public void bindSelectedGridCellProperty(ObjectProperty<@Nullable GridCell<ENT>> property) {
        if (selectedGridCell.isBound()) {
            selectedGridCell.unbind();
        }
        selectedGridCell.bind(property);
    }

    /**
     * Exposes the currently selected grid cell.
     *
     * @return selected grid cell property
     */
    public ObjectProperty<@Nullable GridCell<ENT>> selectedGridCellProperty() {
        return selectedGridCell;
    }

    /**
     * Exposes the last clicked grid coordinate.
     *
     * @return last clicked coordinate property
     */
    @Override
    public ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

}
