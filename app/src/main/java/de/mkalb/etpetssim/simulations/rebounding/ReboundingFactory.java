package de.mkalb.etpetssim.simulations.rebounding;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.rebounding.model.*;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;
import de.mkalb.etpetssim.simulations.rebounding.view.*;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingConfigViewModel;
import javafx.beans.property.*;

public final class ReboundingFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private ReboundingFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new ReboundingConfigViewModel(readOnlySimulationState);
        var controlViewModel = DefaultControlViewModel.withMinStepDuration(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<ReboundingEntity, GridCell<ReboundingEntity>, ReboundingStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        ReboundingSimulationManager::new, ReadableGridModel::getGridCell, new ReboundingUserAction());

        // View
        var configView = new ReboundingConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new ReboundingObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new ReboundingMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
