package de.mkalb.etpetssim.simulations.rebounding;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.rebounding.model.*;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;
import de.mkalb.etpetssim.simulations.rebounding.view.*;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.*;
import javafx.beans.property.*;

/**
 * Factory for the Rebounding Entities main view.
 */
public final class ReboundingFactory {

    private ReboundingFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new ReboundingConfigViewModel(readOnlySimulationState);
        var editToolBarViewModel = new ReboundingEditToolBarViewModel();
        var controlViewModel = DefaultControlViewModel.createWithMinStepDuration(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<ReboundingEntity, GridCell<ReboundingEntity>, ReboundingStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        ReboundingSimulationManager::new, ReadableGridModel::getGridCell, new ReboundingUserAction());

        // View
        var configView = new ReboundingConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new ReboundingObservationView(observationViewModel, entityDescriptorRegistry);
        return new ReboundingMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
