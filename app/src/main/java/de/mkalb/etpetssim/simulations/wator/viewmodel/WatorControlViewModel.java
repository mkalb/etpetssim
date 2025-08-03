package de.mkalb.etpetssim.simulations.wator.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractControlViewModel;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class WatorControlViewModel extends AbstractControlViewModel {

    private static final int STEP_DURATION_INITIAL = 700;
    private static final int STEP_DURATION_MIN = 100;
    private static final int STEP_DURATION_MAX = 2_000;

    private final InputDoublePropertyIntRange stepDuration = InputDoublePropertyIntRange.of(STEP_DURATION_INITIAL,
            STEP_DURATION_MIN, STEP_DURATION_MAX);
    private final BooleanProperty actionButtonRequested = new SimpleBooleanProperty(false);
    private final BooleanProperty cancelButtonRequested = new SimpleBooleanProperty(false);

    public WatorControlViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

    public BooleanProperty actionButtonRequestedProperty() {
        return actionButtonRequested;
    }

    public BooleanProperty cancelButtonRequestedProperty() {
        return cancelButtonRequested;
    }

    public void requestActionButton() {
        actionButtonRequested.set(true);
    }

    public void requestCancelButton() {
        cancelButtonRequested.set(true);
    }

    public InputDoublePropertyIntRange stepDurationProperty() {
        return stepDuration;
    }

}