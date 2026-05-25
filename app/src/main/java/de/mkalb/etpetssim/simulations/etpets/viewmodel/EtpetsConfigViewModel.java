package de.mkalb.etpetssim.simulations.etpets.viewmodel;

import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsConfig;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints.*;

public final class EtpetsConfigViewModel extends AbstractConfigViewModel<EtpetsConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CELL_SHAPE_DEFAULT,
            CELL_SHAPE_VALUES,
            GRID_EDGE_BEHAVIOR_DEFAULT,
            GRID_EDGE_BEHAVIOR_VALUES,
            GRID_WIDTH_DEFAULT,
            GRID_WIDTH_MIN,
            GRID_WIDTH_MAX,
            GRID_WIDTH_STEP,
            GRID_HEIGHT_DEFAULT,
            GRID_HEIGHT_MIN,
            GRID_HEIGHT_MAX,
            GRID_HEIGHT_STEP,
            CELL_EDGE_LENGTH_DEFAULT,
            CELL_EDGE_LENGTH_MIN,
            CELL_EDGE_LENGTH_MAX,
            CELL_DISPLAY_MODE_DEFAULT,
            CELL_DISPLAY_MODE_VALUES,
            SEED_INITIAL
    );

    // Initialization
    private final InputIntegerProperty rockPercent = InputIntegerProperty.of(
            ROCK_PERCENT_DEFAULT,
            PERCENT_MIN,
            PERCENT_MAX,
            PERCENT_STEP);
    private final InputIntegerProperty waterPercent = InputIntegerProperty.of(
            WATER_PERCENT_DEFAULT,
            PERCENT_MIN,
            PERCENT_MAX,
            PERCENT_STEP);
    private final InputIntegerProperty plantPercent = InputIntegerProperty.of(
            PLANT_PERCENT_DEFAULT,
            PERCENT_MIN,
            PERCENT_MAX,
            PERCENT_STEP);
    private final InputIntegerProperty insectPercent = InputIntegerProperty.of(
            INSECT_PERCENT_DEFAULT,
            PERCENT_MIN,
            PERCENT_MAX,
            PERCENT_STEP);
    private final InputIntegerProperty petCount = InputIntegerProperty.of(
            PET_COUNT_DEFAULT,
            PET_COUNT_MIN,
            PET_COUNT_MAX,
            PET_COUNT_STEP);

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
                NEIGHBORHOOD_MODE_DEFAULT
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
