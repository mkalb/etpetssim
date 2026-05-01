package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;

/**
 * Draw callback for rendering coordinate-focused overlays.
 */
@FunctionalInterface
public interface CoordinateDrawer {

    /**
     * Draws an overlay element for one grid coordinate.
     *
     * @param painter drawing facade for the grid canvas
     * @param coordinate coordinate to visualize
     * @param stepCount current simulation step count
     */
    void draw(FXGridCanvasPainter painter,
              GridCoordinate coordinate,
              int stepCount);

}
