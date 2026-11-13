package de.mkalb.etpetssim.simulations.snake;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.ControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.ControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.ObservationViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeSimulationManager;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.SnakeUserAction;
import de.mkalb.etpetssim.simulations.snake.model.entity.EntityDescriptors;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.view.SnakeConfigView;
import de.mkalb.etpetssim.simulations.snake.view.SnakeMainView;
import de.mkalb.etpetssim.simulations.snake.view.SnakeObservationView;
import de.mkalb.etpetssim.simulations.snake.viewmodel.SnakeConfigViewModel;
import de.mkalb.etpetssim.simulations.snake.viewmodel.SnakeEditToolBarViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

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
        var controlViewModel = ControlViewModel.createWithMinStepDuration(readOnlySimulationState);
        var observationViewModel =
                new ObservationViewModel<SnakeEntity, GridCell<SnakeEntity>, SnakeStatistics>();
        var viewModel =
                new MainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        SnakeSimulationManager::new, ReadableGridModel::getGridCell, new SnakeUserAction());

        // View
        var configView = new SnakeConfigView(configViewModel);
        var controlView = new ControlView(controlViewModel);
        var observationView = new SnakeObservationView(observationViewModel, entityDescriptorRegistry);
        return new SnakeMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView,
                observationView);
    }

}
