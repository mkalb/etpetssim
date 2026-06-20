package de.mkalb.etpetssim.simulations.langton;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.view.*;
import de.mkalb.etpetssim.simulations.langton.viewmodel.*;
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
        var editToolBarViewModel = new LangtonEditToolBarViewModel();
        var controlViewModel = DefaultControlViewModel.withMinStepDuration(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<LangtonEntity, LangtonCell, LangtonStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        LangtonSimulationManager::new, LangtonCell::of, new LangtonUserAction());

        // View
        var configView = new LangtonConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new LangtonObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new LangtonMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
