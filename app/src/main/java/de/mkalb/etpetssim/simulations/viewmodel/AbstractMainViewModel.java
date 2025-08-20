package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.model.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class AbstractMainViewModel<
        ENT extends GridEntity,
        CON extends SimulationConfig,
        STA extends SimulationStatistics>
        implements SimulationMainViewModel {

    protected final SimulationConfigViewModel<CON> configViewModel;
    protected final SimulationObservationViewModel<STA> observationViewModel;
    private final ObjectProperty<SimulationState> simulationState;
    private final ObjectProperty<SimulationNotificationType> notificationTypeProperty =
            new SimpleObjectProperty<>(SimulationNotificationType.NONE);

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

    public abstract ReadableGridModel<ENT> getCurrentModel();

}
