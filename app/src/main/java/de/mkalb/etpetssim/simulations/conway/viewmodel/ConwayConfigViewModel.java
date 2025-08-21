package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class ConwayConfigViewModel
        extends AbstractConfigViewModel<ConwayConfig> {

    private static final GridStructureSettings STRUCTURE_SETTINGS = new GridStructureSettings(
            CellShape.SQUARE,
            GridEdgeBehavior.WRAP_XY,
            List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY),
            200,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            100,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            4,
            1,
            48);

    private static final double ALIVE_PERCENT_INITIAL = 0.15d;
    private static final double ALIVE_PERCENT_MAX = 1.0d;
    private static final double ALIVE_PERCENT_MIN = 0.0d;

    private final InputDoubleProperty alivePercent = InputDoubleProperty.of(
            ALIVE_PERCENT_INITIAL,
            ALIVE_PERCENT_MIN,
            ALIVE_PERCENT_MAX);

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, STRUCTURE_SETTINGS);
    }

    @Override
    public ConwayConfig getConfig() {
        return new ConwayConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                alivePercent.getValue()
        );
    }

    public InputDoubleProperty alivePercentProperty() {
        return alivePercent;
    }

}
