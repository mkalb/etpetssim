package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabSimulationManager;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.LabUserAction;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.view.LabConfigView;
import de.mkalb.etpetssim.simulations.lab.view.LabMainView;
import de.mkalb.etpetssim.simulations.lab.view.LabObservationView;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabConfigViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Factory for the Simulation Lab main view.
 */
public final class LabFactory {

    private LabFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(LabEntity.values());

        // ViewModel
        var configViewModel = new LabConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<LabEntity, GridCell<LabEntity>, LabStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        LabSimulationManager::new, ReadableGridModel::getGridCell, new LabUserAction());

        // View
        var configView = new LabConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new LabObservationView(observationViewModel, entityDescriptorRegistry);
        return new LabMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
