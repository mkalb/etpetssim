package de.mkalb.etpetssim.simulations.core.view;

import javafx.scene.layout.Region;

/**
 * Contract for the simulation control region and step counter updates.
 */
public interface SimulationControlView {

    /**
     * Builds the control UI region.
     *
     * @return root region for control actions
     */
    Region buildControlRegion();

    /**
     * Updates the displayed simulation step count.
     * <p>
     * <strong>Threading:</strong> Must be called on the JavaFX Application Thread.
     * Callers originating from a background thread must marshal the call via
     * {@code Platform.runLater(...)}.
     *
     * @param stepCount current step count
     */
    void updateStepCount(int stepCount);

}
