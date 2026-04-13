package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;

@FunctionalInterface
public interface CoordinateDrawer {

    void draw(FXGridCanvasPainter painter,
              GridCoordinate coordinate,
              int stepCount);

}
