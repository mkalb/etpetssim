package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptor;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;

@FunctionalInterface
public interface CellDrawer<ENT extends GridEntity> {

    void draw(GridEntityDescriptor descriptor,
              FXGridCanvasPainter painter,
              GridCell<ENT> cell,
              int stepCount);

}
