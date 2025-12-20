package de.mkalb.etpetssim.simulations.wator;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorSimulationManager;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntityDescribable;
import de.mkalb.etpetssim.simulations.wator.view.WatorConfigView;
import de.mkalb.etpetssim.simulations.wator.view.WatorMainView;
import de.mkalb.etpetssim.simulations.wator.view.WatorObservationView;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class WatorFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private WatorFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(WatorEntityDescribable.values());

        // ViewModel
        var configViewModel = new WatorConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<WatorEntity, WatorStatistics>(readOnlySimulationState);
        var viewModel = new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel,
                observationViewModel, WatorSimulationManager::new);

        // View
        var configView = new WatorConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new WatorObservationView(observationViewModel);
        var view = new WatorMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}
