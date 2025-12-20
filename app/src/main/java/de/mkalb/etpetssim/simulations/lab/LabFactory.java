package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.view.*;
import de.mkalb.etpetssim.simulations.lab.viewmodel.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class LabFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private LabFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(LabEntity.values());

        // ViewModel
        var configViewModel = new LabConfigViewModel(readOnlySimulationState);
        var controlViewModel = new LabControlViewModel(readOnlySimulationState);
        var observationViewModel = new LabObservationViewModel(readOnlySimulationState);
        var viewModel = new LabMainViewModel(simulationState, configViewModel, controlViewModel, observationViewModel);

        // View
        var configView = new LabConfigView(configViewModel);
        var controlView = new LabControlView(controlViewModel);
        var observationView = new LabObservationView(observationViewModel);
        var view = new LabMainView(viewModel, configView, controlView, observationView, entityDescriptorRegistry);

        // Return the main view
        return view;
    }

}
