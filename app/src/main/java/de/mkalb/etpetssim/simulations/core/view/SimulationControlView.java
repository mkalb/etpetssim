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
     *
     * @param stepCount current step count
     */
    void updateStepCount(int stepCount);

}
