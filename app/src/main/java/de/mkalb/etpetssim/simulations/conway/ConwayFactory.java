package de.mkalb.etpetssim.simulations.conway;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.view.*;
import de.mkalb.etpetssim.simulations.conway.viewmodel.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import javafx.beans.property.*;

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
