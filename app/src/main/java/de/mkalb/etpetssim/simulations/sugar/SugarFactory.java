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

public final class SugarFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SugarFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new SugarConfigViewModel(readOnlySimulationState);
        var editToolBarViewModel = new SugarEditToolBarViewModel();
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<SugarEntity, SugarCell, SugarStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        SugarSimulationManager::new, SugarCell::of,
                        new SugarUserAction());

        // View
        var configView = new SugarConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new SugarObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new SugarMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
