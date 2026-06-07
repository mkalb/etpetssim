package de.mkalb.etpetssim.simulations.wator;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.NoUserAction;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.wator.model.*;
import de.mkalb.etpetssim.simulations.wator.model.entity.*;
import de.mkalb.etpetssim.simulations.wator.view.*;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import javafx.beans.property.*;

public final class WatorFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private WatorFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new WatorConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<WatorEntity, GridCell<WatorEntity>, WatorStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        WatorSimulationManager::new, ReadableGridModel::getGridCell, new NoUserAction<>());

        // View
        var configView = new WatorConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new WatorObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new WatorMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
