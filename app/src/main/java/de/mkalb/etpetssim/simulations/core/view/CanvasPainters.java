package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.ui.FXGridCanvasPainter;

/**
 * Painters of the three canvas layers of a main view.
 * <p>
 * The record groups the painters without copying them.
 *
 * @param base    painter of the static base layer
 * @param dynamic painter of the layer that changes with every step
 * @param overlay painter of the topmost layer for selections and notifications
 */
public record CanvasPainters(FXGridCanvasPainter base, FXGridCanvasPainter dynamic, FXGridCanvasPainter overlay) {

}
