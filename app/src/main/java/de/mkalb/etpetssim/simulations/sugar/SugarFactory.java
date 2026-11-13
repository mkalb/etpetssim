package de.mkalb.etpetssim.simulations.sugar;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.ControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.ControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.ObservationViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarCell;
import de.mkalb.etpetssim.simulations.sugar.model.SugarSimulationManager;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.SugarUserAction;
import de.mkalb.etpetssim.simulations.sugar.model.entity.EntityDescriptors;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;
import de.mkalb.etpetssim.simulations.sugar.view.SugarConfigView;
import de.mkalb.etpetssim.simulations.sugar.view.SugarMainView;
import de.mkalb.etpetssim.simulations.sugar.view.SugarObservationView;
import de.mkalb.etpetssim.simulations.sugar.viewmodel.SugarConfigViewModel;
import de.mkalb.etpetssim.simulations.sugar.viewmodel.SugarEditToolBarViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Factory for the Sugarscape main view.
 */
public final class SugarFactory {

    private SugarFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new SugarConfigViewModel(readOnlySimulationState);
        var editToolBarViewModel = new SugarEditToolBarViewModel();
        var controlViewModel = new ControlViewModel(readOnlySimulationState);
        var observationViewModel = new ObservationViewModel<SugarEntity, SugarCell, SugarStatistics>();
        var viewModel =
                new MainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        SugarSimulationManager::new, SugarCell::from, new SugarUserAction());

        // View
        var configView = new SugarConfigView(configViewModel);
        var controlView = new ControlView(controlViewModel);
        var observationView = new SugarObservationView(observationViewModel, entityDescriptorRegistry);
        return new SugarMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView,
                observationView);
    }

}
