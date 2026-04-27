package de.mkalb.etpetssim.simulations.etpets.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsConfig;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class EtpetsConfigViewModel extends AbstractConfigViewModel<EtpetsConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.HEXAGON,
            List.of(CellShape.HEXAGON),
            GridEdgeBehavior.BLOCK_XY,
            List.of(GridEdgeBehavior.BLOCK_XY),
            50,
            20,
            200,
            GridTopology.HEXAGON_MAX_REQUIRED_WIDTH_MULTIPLE,
            20,
            20,
            200,
            GridTopology.HEXAGON_MAX_REQUIRED_HEIGHT_MULTIPLE,
            10,
            5,
            50,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE),
            ""
    );

    // Initialization
    private static final int PET_COUNT_INITIAL = 10;
    private static final int PET_COUNT_MIN = 0;
    private static final int PET_COUNT_MAX = 20;
    private static final int PERCENT_MIN = 0;
    private static final int PERCENT_MAX = 100;
    private static final int STEP = 1;

    private static final int ROCK_PERCENT_INITIAL = 1;
    private static final int WATER_PERCENT_INITIAL = 2;
    private static final int PLANT_PERCENT_INITIAL = 5;
    private static final int INSECT_PERCENT_INITIAL = 1;

    // Rules
    private static final NeighborhoodMode NEIGHBORHOOD_MODE = NeighborhoodMode.EDGES_ONLY;

    // Initialization properties
    private final InputIntegerProperty rockPercent = InputIntegerProperty.of(ROCK_PERCENT_INITIAL, PERCENT_MIN, PERCENT_MAX, STEP);
    private final InputIntegerProperty waterPercent = InputIntegerProperty.of(WATER_PERCENT_INITIAL, PERCENT_MIN, PERCENT_MAX, STEP);
    private final InputIntegerProperty plantPercent = InputIntegerProperty.of(PLANT_PERCENT_INITIAL, PERCENT_MIN, PERCENT_MAX, STEP);
    private final InputIntegerProperty insectPercent = InputIntegerProperty.of(INSECT_PERCENT_INITIAL, PERCENT_MIN, PERCENT_MAX, STEP);
    private final InputIntegerProperty petCount = InputIntegerProperty.of(PET_COUNT_INITIAL, PET_COUNT_MIN, PET_COUNT_MAX, STEP);

    public EtpetsConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public EtpetsConfig getConfig() {
        return new EtpetsConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                rockPercent.getValue(),
                waterPercent.getValue(),
                plantPercent.getValue(),
                insectPercent.getValue(),
                petCount.getValue(),
                NEIGHBORHOOD_MODE
        );
    }

    public InputIntegerProperty rockPercentProperty() {
        return rockPercent;
    }

    public InputIntegerProperty waterPercentProperty() {
        return waterPercent;
    }

    public InputIntegerProperty plantPercentProperty() {
        return plantPercent;
    }

    public InputIntegerProperty insectPercentProperty() {
        return insectPercent;
    }

    public InputIntegerProperty petCountProperty() {
        return petCount;
    }

}

