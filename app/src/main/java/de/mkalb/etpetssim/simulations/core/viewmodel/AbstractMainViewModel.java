package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.simulations.core.model.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

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
    }

    @Override
    public final ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public final SimulationState getSimulationState() {
        return simulationState.get();
    }

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

    protected abstract CON getCurrentConfig();

    public abstract boolean hasSimulationManager();

    public abstract GM getCurrentModel();

    public final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

    public final ObjectProperty<@Nullable GridCoordinate> previousClickedCoordinateProperty() {
        return previousClickedCoordinate;
    }

    public Optional<GridCoordinate> getLastClickedCoordinate() {
        return Optional.ofNullable(lastClickedCoordinate.get());
    }

    public Optional<GridCoordinate> getPreviousClickedCoordinate() {
        return Optional.ofNullable(previousClickedCoordinate.get());
    }

    public final void updateClickedCoordinateProperties(@Nullable GridCoordinate coordinate) {
        previousClickedCoordinate.set(lastClickedCoordinate.get());
        lastClickedCoordinate.set(coordinate);
    }

    public final void resetClickedCoordinateProperties() {
        previousClickedCoordinate.set(null);
        lastClickedCoordinate.set(null);
    }

}
