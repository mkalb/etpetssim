package de.mkalb.etpetssim.simulations.snake;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.snake.model.*;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;
import de.mkalb.etpetssim.simulations.snake.view.*;
import de.mkalb.etpetssim.simulations.snake.viewmodel.*;
import javafx.beans.property.*;

/**
 * Factory for the Snake main view.
 */
public final class SnakeFactory {

    private SnakeFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new SnakeConfigViewModel(readOnlySimulationState);
        var editToolBarViewModel = new SnakeEditToolBarViewModel();
        var controlViewModel = DefaultControlViewModel.createWithMinStepDuration(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<SnakeEntity, GridCell<SnakeEntity>, SnakeStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        SnakeSimulationManager::new, ReadableGridModel::getGridCell, new SnakeUserAction());

        // View
        var configView = new SnakeConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new SnakeObservationView(observationViewModel, entityDescriptorRegistry);
        return new SnakeMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
