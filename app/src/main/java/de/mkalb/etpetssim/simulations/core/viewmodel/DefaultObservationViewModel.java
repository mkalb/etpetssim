package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema;
import de.mkalb.etpetssim.simulations.core.model.StatisticsSample;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of observation-related view-model state.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GC>  grid cell type used for cell selection
 * @param <STA> statistics type exposed to observation views
 */
public final class DefaultObservationViewModel<
        ENT extends GridEntity,
        GC extends GridCellView<ENT>,
        STA extends SimulationStatistics>
        implements SimulationObservationViewModel<STA> {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;
    private final ObjectProperty<@Nullable STA> statistics = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable GC> selectedGridCell = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>();
    private final ObjectProperty<StatisticsExtrema> statisticsExtrema =
            new SimpleObjectProperty<>(StatisticsExtrema.empty());
    private final ReadOnlyObjectWrapper<List<StatisticsSample>> statisticsHistory =
            new ReadOnlyObjectWrapper<>(List.of());

    public DefaultObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
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
    public Optional<STA> getStatistics() {
        return Optional.ofNullable(statistics.get());
    }

    @Override
    public void setStatistics(STA statistics) {
        this.statistics.set(statistics);
    }

    @Override
    public StatisticsExtrema getStatisticsExtrema() {
        return statisticsExtrema.get();
    }

    /**
     * Updates the extrema snapshot exposed to observation views.
     *
     * @param extrema the latest extrema snapshot from the simulation manager
     */
    public void setStatisticsExtrema(StatisticsExtrema extrema) {
        statisticsExtrema.set(extrema);
    }

    @Override
    public ReadOnlyObjectProperty<List<StatisticsSample>> statisticsHistoryProperty() {
        return statisticsHistory.getReadOnlyProperty();
    }

    /**
     * Updates the history snapshot exposed to observation views.
     *
     * <p>The list must already be an immutable defensive copy captured on the producing thread.
     *
     * @param history immutable ordered list of samples from the simulation manager
     */
    public void setStatisticsHistory(List<StatisticsSample> history) {
        statisticsHistory.set(history);
    }

    /**
     * Resets the statistics snapshot, extrema, and history to their initial empty state.
     *
     * <p>Call this on restart and during shutdown so a stale statistics snapshot (which may keep a
     * {@code GridStructure} of a finished simulation strongly reachable) is released, together with its extrema and
     * history.
     */
    public void resetStatistics() {
        statisticsExtrema.set(StatisticsExtrema.empty());
        statisticsHistory.set(List.of());
        statistics.set(null);
    }

    /**
     * Binds the selected-cell property to the given property of the main view model, replacing any previous
     * binding.
     *
     * @param property source property to bind from
     */
    public void bindSelectedGridCellProperty(ObjectProperty<@Nullable GC> property) {
        if (selectedGridCell.isBound()) {
            selectedGridCell.unbind();
        }
        selectedGridCell.bind(property);
    }

    public ObjectProperty<@Nullable GC> selectedGridCellProperty() {
        return selectedGridCell;
    }

    @Override
    public ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

}
