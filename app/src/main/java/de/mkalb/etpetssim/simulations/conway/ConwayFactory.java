package de.mkalb.etpetssim.simulations.conway;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.ConwayUserAction;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.view.ConwayConfigView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayMainView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayObservationView;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayEditToolBarViewModel;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Factory for the Conway's Game of Life main view.
 */
public final class ConwayFactory {

    private ConwayFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(ConwayEntity.values());

        // ViewModel
        var configViewModel = new ConwayConfigViewModel(readOnlySimulationState);
        var editToolBarViewModel = new ConwayEditToolBarViewModel();
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        ConwaySimulationManager::new, ReadableGridModel::getGridCell, new ConwayUserAction());

        // View
        var configView = new ConwayConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new ConwayObservationView(observationViewModel, entityDescriptorRegistry);
        return new ConwayMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
