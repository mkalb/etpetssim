package de.mkalb.etpetssim.simulations.snake;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeSimulationManager;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntityDescribable;
import de.mkalb.etpetssim.simulations.snake.view.SnakeConfigView;
import de.mkalb.etpetssim.simulations.snake.view.SnakeMainView;
import de.mkalb.etpetssim.simulations.snake.view.SnakeObservationView;
import de.mkalb.etpetssim.simulations.snake.viewmodel.SnakeConfigViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class SnakeFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SnakeFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(SnakeEntityDescribable.values());

        // ViewModel
        var configViewModel = new SnakeConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<SnakeEntity, SnakeStatistics>(readOnlySimulationState);
        var viewModel = new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel,
                observationViewModel, SnakeSimulationManager::new, ReadableGridModel::getGridCell);

        // View
        var configView = new SnakeConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new SnakeObservationView(observationViewModel);
        var view = new SnakeMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
