package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class DefaultControlViewModelTest {

    @Test
    void testControlConfigurationIsDisabledWhileInitializing() {
        var state = new SimpleObjectProperty<>(SimulationState.INITIALIZING);
        var viewModel = new DefaultControlViewModel(state);

        assertTrue(viewModel.isControlConfigDisabled());
    }

    @Test
    void testControlConfigurationIsEnabledWhenReady() {
        var state = new SimpleObjectProperty<>(SimulationState.READY);
        var viewModel = new DefaultControlViewModel(state);

        assertFalse(viewModel.isControlConfigDisabled());
    }

}
