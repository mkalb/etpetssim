package de.mkalb.etpetssim.simulations.sugar.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarConfig;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class SugarConfigViewModel
        extends AbstractConfigViewModel<SugarConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.SQUARE,
            GridEdgeBehavior.BLOCK_XY,
            List.of(GridEdgeBehavior.BLOCK_XY),
            100,
            50,
            1_000,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            100,
            50,
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
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(
            NEIGHBORHOOD_MODE_INITIAL,
            NeighborhoodMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

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
                10,
                20,
                1,
                1,
                4,
                neighborhoodMode.getValue()
        );
    }

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

}
