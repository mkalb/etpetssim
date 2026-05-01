package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;

/**
 * Draw callback for rendering a single grid cell.
 */
@FunctionalInterface
public interface CellDrawer<ENT extends GridEntity> {

    /**
     * Draws one simulation cell on the painter surface.
     *
     * @param descriptor descriptor metadata of the entity in the cell
     * @param painter drawing facade for the grid canvas
     * @param cell coordinate and entity data of the cell to draw
     * @param stepCount current simulation step count
     */
    void draw(GridEntityDescriptor descriptor,
              FXGridCanvasPainter painter,
              GridCell<ENT> cell,
              int stepCount);

}
