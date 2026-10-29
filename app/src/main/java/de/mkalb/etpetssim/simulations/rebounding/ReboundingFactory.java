package de.mkalb.etpetssim.simulations.rebounding;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingSimulationManager;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingUserAction;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.EntityDescriptors;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingConfigView;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingMainView;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingObservationView;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingConfigViewModel;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingEditToolBarViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

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
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(EntityDescriptors.values());

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
