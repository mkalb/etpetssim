package de.mkalb.etpetssim.simulations.sugar.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarConfig;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class SugarConfigViewModel
        extends AbstractConfigViewModel<SugarConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.SQUARE,
            List.of(CellShape.SQUARE, CellShape.HEXAGON),
            GridEdgeBehavior.BLOCK_XY,
            List.of(GridEdgeBehavior.BLOCK_XY,
                    GridEdgeBehavior.WRAP_XY,
                    GridEdgeBehavior.BLOCK_X_WRAP_Y,
                    GridEdgeBehavior.WRAP_X_BLOCK_Y),
            50,
            10,
            1_000,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            50,
            10,
            1_000,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            12,
            4,
            32,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE),
            ""
    );
    // Initialization
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_ONLY;
    // Rules

    // Initialization
    // Rules

    public SugarConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public SugarConfig getConfig() {
        return new SugarConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                0.1d,
                0.05d,
                6,
                5,
                1,
                1,
                4,
                NEIGHBORHOOD_MODE_INITIAL
        );
    }

}
