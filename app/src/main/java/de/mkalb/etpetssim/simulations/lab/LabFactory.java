package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.view.*;
import de.mkalb.etpetssim.simulations.lab.viewmodel.*;
import javafx.beans.property.*;

public final class LabFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private LabFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(LabEntity.values());

        // ViewModel
        var configViewModel = new LabConfigViewModel(readOnlySimulationState);
        var controlViewModel = new LabControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<LabEntity, GridCell<LabEntity>, LabStatistics>(readOnlySimulationState);
        var viewModel =
                new LabMainViewModel(simulationState, configViewModel, controlViewModel, observationViewModel);

        // View
        var configView = new LabConfigView(configViewModel);
        var controlView = new LabControlView(controlViewModel);
        var observationView = new LabObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new LabMainView(viewModel, configView, controlView, observationView, entityDescriptorRegistry);

        // Return the main view
        return view;
    }

}
