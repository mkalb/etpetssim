package de.mkalb.etpetssim.simulations.sugar;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
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
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<SugarEntity, SugarCell, SugarStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        SugarSimulationManager::new, SugarCell::from, new SugarUserAction());

        // View
        var configView = new SugarConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new SugarObservationView(observationViewModel, entityDescriptorRegistry);
        return new SugarMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
