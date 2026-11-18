package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationState;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.shared.LabBaseLayer;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabDistribution;
import de.mkalb.etpetssim.simulations.lab.shared.LabPattern;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.BASE_LAYER_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.BASE_LAYER_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_DISPLAY_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_EDGE_LENGTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_EDGE_LENGTH_MAX;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_EDGE_LENGTH_MIN;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_SHAPE_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.COLOR_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.COLOR_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.DISTRIBUTION_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.DISTRIBUTION_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_EDGE_BEHAVIOR_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_HEIGHT_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_HEIGHT_MAX;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_HEIGHT_MIN;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_HEIGHT_STEP;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_WIDTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_WIDTH_MAX;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_WIDTH_MIN;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.GRID_WIDTH_STEP;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.HIGHLIGHTED_PERCENT_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.HIGHLIGHTED_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.HIGHLIGHTED_PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.PATTERN_DEFAULT;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.PATTERN_VALUES;
import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.SEED_DEFAULT;

/**
 * Config ViewModel of the Simulation Lab.
 */
public final class LabConfigViewModel extends AbstractConfigViewModel<LabConfig> {

    private static final String LAB_CONFIG_VALIDATION_GRID_STRUCTURE = "lab.config.validation.gridstructure";

    private static final CommonConfigConstraints COMMON_CONSTRAINTS = new CommonConfigConstraints(
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
            SEED_DEFAULT
    );

    // Layout
    private final InputChoiceProperty<LabColorMode> colorMode = InputChoiceProperty.ofList(COLOR_MODE_DEFAULT,
            COLOR_MODE_VALUES,
            e -> AppLocalization.getText(e.resourceKey()));
    private final InputChoiceProperty<LabBaseLayer> baseLayer = InputChoiceProperty.ofList(BASE_LAYER_DEFAULT,
            BASE_LAYER_VALUES,
            e -> AppLocalization.getText(e.resourceKey()));

    // Initialization
    private final InputChoiceProperty<LabDistribution> distribution = InputChoiceProperty.ofList(
            DISTRIBUTION_DEFAULT,
            DISTRIBUTION_VALUES,
            e -> AppLocalization.getText(e.resourceKey()));
    private final InputDoubleProperty highlightedPercent = InputDoubleProperty.of(
            HIGHLIGHTED_PERCENT_DEFAULT,
            HIGHLIGHTED_PERCENT_MIN,
            HIGHLIGHTED_PERCENT_MAX);
    private final InputChoiceProperty<LabPattern> pattern = InputChoiceProperty.ofList(PATTERN_DEFAULT,
            PATTERN_VALUES,
            e -> AppLocalization.getText(e.resourceKey()));

    // Rules
    private final InputChoiceProperty<NeighborhoodMode> neighborhoodMode = InputChoiceProperty.ofEnum(
            NEIGHBORHOOD_MODE_DEFAULT,
            NeighborhoodMode.class,
            e -> AppLocalization.getText(e.resourceKey()));

    public LabConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_CONSTRAINTS);
        registerConfigValidationRule(
                Bindings.createBooleanBinding(
                        () -> !GridStructure.isValid(
                                new GridTopology(cellShapeProperty().getValue(), gridEdgeBehaviorProperty().getValue()),
                                new GridSize(gridWidthProperty().getValue(), gridHeightProperty().getValue())
                        ),
                        cellShapeProperty().property(),
                        gridEdgeBehaviorProperty().property(),
                        gridWidthProperty().property(),
                        gridHeightProperty().property()
                ),
                () -> AppLocalization.getText(LAB_CONFIG_VALIDATION_GRID_STRUCTURE)
        );
    }

    @Override
    public LabConfig createConfig() {
        return new LabConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                colorMode.getValue(),
                baseLayer.getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                distribution.getValue(),
                highlightedPercent.getValue(),
                pattern.getValue(),
                neighborhoodMode.getValue()
        );
    }

    public InputChoiceProperty<LabColorMode> colorModeProperty() {
        return colorMode;
    }

    public InputChoiceProperty<LabBaseLayer> baseLayerProperty() {
        return baseLayer;
    }

    public InputChoiceProperty<LabDistribution> distributionProperty() {
        return distribution;
    }

    public InputDoubleProperty highlightedPercentProperty() {
        return highlightedPercent;
    }

    public InputChoiceProperty<LabPattern> patternProperty() {
        return pattern;
    }

    public InputChoiceProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

}
