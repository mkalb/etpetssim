package de.mkalb.etpetssim.simulations.langton;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonSimulationManager;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.view.LangtonConfigView;
import de.mkalb.etpetssim.simulations.langton.view.LangtonMainView;
import de.mkalb.etpetssim.simulations.langton.view.LangtonObservationView;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonConfigViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class LangtonFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private LangtonFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.fromCollection(LangtonEntity.allEntityDescribable());

        // ViewModel
        var configViewModel = new LangtonConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<LangtonEntity, LangtonStatistics>(readOnlySimulationState);
        var viewModel = new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel,
                observationViewModel, LangtonSimulationManager::new,
                (langtonGridModel, selectedCoordinate) -> {
                    if (!langtonGridModel.antModel().isDefaultEntity(selectedCoordinate)) {
                        return new GridCell<>(selectedCoordinate, langtonGridModel.antModel().getEntity(selectedCoordinate));
                    } else {
                        return new GridCell<>(selectedCoordinate, langtonGridModel.groundModel().getEntity(selectedCoordinate));
                    }
                });

        // View
        var configView = new LangtonConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new LangtonObservationView(observationViewModel);
        var view = new LangtonMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
