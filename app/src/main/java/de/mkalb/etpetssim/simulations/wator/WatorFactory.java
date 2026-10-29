package de.mkalb.etpetssim.simulations.wator;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorSimulationManager;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.simulations.wator.model.WatorUserAction;
import de.mkalb.etpetssim.simulations.wator.model.entity.EntityDescriptors;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;
import de.mkalb.etpetssim.simulations.wator.view.WatorConfigView;
import de.mkalb.etpetssim.simulations.wator.view.WatorMainView;
import de.mkalb.etpetssim.simulations.wator.view.WatorObservationView;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Factory for the Wa-Tor main view.
 */
public final class WatorFactory {

    private WatorFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new WatorConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<WatorEntity, GridCell<WatorEntity>, WatorStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        WatorSimulationManager::new, ReadableGridModel::getGridCell, new WatorUserAction());

        // View
        var configView = new WatorConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new WatorObservationView(observationViewModel, entityDescriptorRegistry);
        return new WatorMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}
