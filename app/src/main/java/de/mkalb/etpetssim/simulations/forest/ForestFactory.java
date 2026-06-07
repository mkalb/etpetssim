package de.mkalb.etpetssim.simulations.forest;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.forest.model.*;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import de.mkalb.etpetssim.simulations.forest.view.*;
import de.mkalb.etpetssim.simulations.forest.viewmodel.ForestConfigViewModel;
import javafx.beans.property.*;

public final class ForestFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private ForestFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(ForestEntity.values());

        // ViewModel
        var configViewModel = new ForestConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<ForestEntity, GridCell<ForestEntity>, ForestStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        ForestSimulationManager::new, ReadableGridModel::getGridCell, new ForestUserAction());

        // View
        var configView = new ForestConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new ForestObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new ForestMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
