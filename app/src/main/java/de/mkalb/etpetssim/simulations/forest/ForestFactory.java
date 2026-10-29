package de.mkalb.etpetssim.simulations.forest;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestSimulationManager;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.forest.model.ForestUserAction;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import de.mkalb.etpetssim.simulations.forest.view.ForestConfigView;
import de.mkalb.etpetssim.simulations.forest.view.ForestMainView;
import de.mkalb.etpetssim.simulations.forest.view.ForestObservationView;
import de.mkalb.etpetssim.simulations.forest.viewmodel.ForestConfigViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

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
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(ForestEntity.values());

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
