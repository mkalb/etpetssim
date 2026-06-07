package de.mkalb.etpetssim.simulations.langton;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.view.*;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonConfigViewModel;
import javafx.beans.property.*;

public final class LangtonFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private LangtonFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofCollection(LangtonEntity.allEntityDescriptorProviders());

        // ViewModel
        var configViewModel = new LangtonConfigViewModel(readOnlySimulationState);
        var controlViewModel = DefaultControlViewModel.withMinStepDuration(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<LangtonEntity, GridCell<LangtonEntity>, LangtonStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        LangtonSimulationManager::new,
                        (langtonGridModel, selectedCoordinate) -> {
                            if (!langtonGridModel.antModel().isDefaultEntity(selectedCoordinate)) {
                                return new GridCell<>(selectedCoordinate, langtonGridModel.antModel().getEntity(selectedCoordinate));
                            } else {
                                return new GridCell<>(selectedCoordinate, langtonGridModel.groundModel().getEntity(selectedCoordinate));
                            }
                        },
                        new LangtonUserAction());

        // View
        var configView = new LangtonConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new LangtonObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new LangtonMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
