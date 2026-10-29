package de.mkalb.etpetssim.simulations.langton;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonCell;
import de.mkalb.etpetssim.simulations.langton.model.LangtonSimulationManager;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.langton.model.LangtonUserAction;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.view.LangtonConfigView;
import de.mkalb.etpetssim.simulations.langton.view.LangtonMainView;
import de.mkalb.etpetssim.simulations.langton.view.LangtonObservationView;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonConfigViewModel;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonEditToolBarViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Factory for the Langton's Ant main view.
 */
public final class LangtonFactory {

    private LangtonFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofCollection(LangtonEntity.allEntityDescriptorProviders());

        // ViewModel
        var configViewModel = new LangtonConfigViewModel(readOnlySimulationState);
        var editToolBarViewModel = new LangtonEditToolBarViewModel();
        var controlViewModel = DefaultControlViewModel.createWithMinStepDuration(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<LangtonEntity, LangtonCell, LangtonStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        LangtonSimulationManager::new, LangtonCell::from, new LangtonUserAction());

        // View
        var configView = new LangtonConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new LangtonObservationView(observationViewModel, entityDescriptorRegistry);
        return new LangtonMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
