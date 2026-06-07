package de.mkalb.etpetssim.simulations.sugar;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.NoUserAction;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.sugar.model.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import de.mkalb.etpetssim.simulations.sugar.view.*;
import de.mkalb.etpetssim.simulations.sugar.viewmodel.SugarConfigViewModel;
import javafx.beans.property.*;

public final class SugarFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SugarFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new SugarConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<SugarEntity, GridCell<SugarEntity>, SugarStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        SugarSimulationManager::new,
                        (sugarGridModel, selectedCoordinate) -> {
                            if (!sugarGridModel.agentModel().isDefaultEntity(selectedCoordinate)) {
                                return new GridCell<>(selectedCoordinate, sugarGridModel.agentModel().getEntity(selectedCoordinate));
                            } else {
                                return new GridCell<>(selectedCoordinate, sugarGridModel.resourceModel().getEntity(selectedCoordinate));
                            }
                        },
                        new NoUserAction<>());

        // View
        var configView = new SugarConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new SugarObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new SugarMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
