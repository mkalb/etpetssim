package de.mkalb.etpetssim.simulations.core.view;

import javafx.scene.layout.Region;

public interface SimulationControlView {

    Region buildControlRegion();

    void updateStepCount(int stepCount);

}
