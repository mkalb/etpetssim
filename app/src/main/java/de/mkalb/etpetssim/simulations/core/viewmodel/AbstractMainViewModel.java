package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Base class for main view models that coordinate config, observation and selection state.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GM>  grid model type exposed by the simulation
 * @param <CON> config type provided by the config view model
 * @param <STA> statistics type exposed through observation state
 */
public abstract class AbstractMainViewModel<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends SimulationStatistics>
        implements SimulationMainViewModel {

    protected final SimulationConfigViewModel<CON> configViewModel;
    protected final SimulationObservationViewModel<STA> observationViewModel;
    private final ObjectProperty<SimulationState> simulationState;
    private final ObjectProperty<SimulationNotificationType> notificationTypeProperty =
            new SimpleObjectProperty<>(SimulationNotificationType.NONE);

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable GridCoordinate> previousClickedCoordinate = new SimpleObjectProperty<>();

    protected AbstractMainViewModel(ObjectProperty<SimulationState> simulationState,
                                    SimulationConfigViewModel<CON> configViewModel,
                                    SimulationObservationViewModel<STA> observationViewModel) {
        this.simulationState = simulationState;
        this.configViewModel = configViewModel;
        this.observationViewModel = observationViewModel;

        observationViewModel.lastClickedCoordinateProperty().bind(lastClickedCoordinateProperty());
    }

    @Override
    public final ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public final SimulationState getSimulationState() {
        return simulationState.get();
    }

    /**
     * Updates the current simulation state.
     *
     * @param state next simulation state
     */
    protected final void setSimulationState(SimulationState state) {
        simulationState.set(state);
    }

    @Override
    public final ObjectProperty<SimulationNotificationType> notificationTypeProperty() {
        return notificationTypeProperty;
    }

    @Override
    public final void setNotificationType(SimulationNotificationType notificationType) {
        notificationTypeProperty.set(notificationType);
    }

    /**
     * Returns the currently active configuration object.
     *
     * @return current immutable config
     */
    protected abstract CON getCurrentConfig();

    /**
     * Checks whether a simulation manager is currently available.
     *
     * @return {@code true} when a manager exists
     */
    public abstract boolean hasSimulationManager();

    /**
     * Returns the current simulation model.
     *
     * @return current model snapshot
     */
    public abstract GM getCurrentModel();

    /**
     * Exposes the most recently clicked coordinate.
     *
     * @return mutable coordinate property
     */
    public final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

    /**
     * Exposes the coordinate that was clicked before the last click.
     *
     * @return mutable coordinate property
     */
    public final ObjectProperty<@Nullable GridCoordinate> previousClickedCoordinateProperty() {
        return previousClickedCoordinate;
    }

    /**
     * Returns the most recently clicked coordinate.
     *
     * @return optional coordinate
     */
    public Optional<GridCoordinate> getLastClickedCoordinate() {
        return Optional.ofNullable(lastClickedCoordinate.get());
    }

    /**
     * Returns the coordinate clicked before the most recent click.
     *
     * @return optional coordinate
     */
    public Optional<GridCoordinate> getPreviousClickedCoordinate() {
        return Optional.ofNullable(previousClickedCoordinate.get());
    }

    /**
     * Shifts click history and stores a new last-clicked coordinate.
     *
     * @param coordinate new coordinate, or {@code null} to clear the current click
     */
    public final void updateClickedCoordinateProperties(@Nullable GridCoordinate coordinate) {
        previousClickedCoordinate.set(lastClickedCoordinate.get());
        lastClickedCoordinate.set(coordinate);
    }

    /**
     * Clears current and previous click coordinates.
     */
    public final void resetClickedCoordinateProperties() {
        previousClickedCoordinate.set(null);
        lastClickedCoordinate.set(null);
    }

}
