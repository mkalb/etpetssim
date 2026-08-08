package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Default implementation of observation-related view-model state.
 */
public final class DefaultObservationViewModel<
        ENT extends GridEntity,
        GC extends GridCellView<ENT>,
        STA extends SimulationStatistics>
        implements SimulationObservationViewModel<STA> {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ReadOnlyObjectWrapper<@Nullable STA> statistics;
    private final ObjectProperty<@Nullable GC> selectedGridCell = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>();
    private StatisticExtrema statisticsExtrema;
    private List<StatisticSample> statisticsHistory;

    /**
     * Creates observation state bound to a shared simulation-state property.
     *
     * @param simulationState shared simulation state property
     */
    public DefaultObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
        statistics = new ReadOnlyObjectWrapper<>();
        statisticsExtrema = StatisticExtrema.empty();
        statisticsHistory = List.of();
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
     * Returns the current statistic extrema snapshot.
     *
     * @return the latest extrema, or an empty snapshot before any step executes
     */
    public StatisticExtrema getStatisticsExtrema() {
        return statisticsExtrema;
    }

    /**
     * Updates the extrema snapshot exposed to observation views.
     *
     * @param extrema the latest extrema snapshot from the simulation manager
     */
    public void setStatisticsExtrema(StatisticExtrema extrema) {
        statisticsExtrema = extrema;
    }

    /**
     * Returns the current statistics history snapshot.
     *
     * @return immutable ordered list of samples, oldest first; empty before any step executes
     */
    public List<StatisticSample> getStatisticsHistory() {
        return statisticsHistory;
    }

    /**
     * Updates the history snapshot exposed to observation views.
     *
     * <p>The list must already be an immutable defensive copy captured on the producing thread.
     *
     * @param history immutable ordered list of samples from the simulation manager
     */
    public void setStatisticsHistory(List<StatisticSample> history) {
        statisticsHistory = history;
    }

    /**
     * Binds the selected-cell property from the main view model.
     *
     * @param property source property to bind from
     */
    public void bindSelectedGridCellProperty(ObjectProperty<@Nullable GC> property) {
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
    public ObjectProperty<@Nullable GC> selectedGridCellProperty() {
        return selectedGridCell;
    }

    @Override
    public ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

}
