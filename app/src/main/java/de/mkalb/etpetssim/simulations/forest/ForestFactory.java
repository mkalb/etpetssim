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

/**
 * Factory for the forest-fire model main view.
 */
public final class ForestFactory {

    private ForestFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
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
        return new ForestMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
