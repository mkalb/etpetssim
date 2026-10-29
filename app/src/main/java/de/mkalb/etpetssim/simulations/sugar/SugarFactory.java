package de.mkalb.etpetssim.simulations.sugar;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.sugar.model.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import de.mkalb.etpetssim.simulations.sugar.view.*;
import de.mkalb.etpetssim.simulations.sugar.viewmodel.*;
import javafx.beans.property.*;

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
