package de.mkalb.etpetssim.simulations.rebounding;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingSimulationManager;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.EntityDescriptors;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingConfigView;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingMainView;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingObservationView;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingConfigViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ReboundingFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private ReboundingFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new ReboundingConfigViewModel(readOnlySimulationState);
        var controlViewModel = DefaultControlViewModel.withMinStepDuration(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics>(readOnlySimulationState);
        var viewModel = new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel,
                observationViewModel, ReboundingSimulationManager::new, ReadableGridModel::getGridCell);

        // View
        var configView = new ReboundingConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new ReboundingObservationView(observationViewModel);
        var view = new ReboundingMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
