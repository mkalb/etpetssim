package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_EDGE_LENGTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_EDGE_LENGTH_MAX;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_EDGE_LENGTH_MIN;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_SHAPE_DEFAULT;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_HEIGHT_DEFAULT;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_HEIGHT_MAX;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_HEIGHT_MIN;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_HEIGHT_STEP;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_WIDTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_WIDTH_MAX;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_WIDTH_MIN;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.GRID_WIDTH_STEP;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.SEED_DEFAULT;

public final class LangtonConfigViewModel
        extends AbstractConfigViewModel<LangtonConfig> {

    private static final String LANGTON_CONFIG_VALIDATION_MOVEMENT_RULES = "langton.config.validation.movementrules";

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
            SEED_DEFAULT
    );

    // Rules
    private final LangtonRuleProperty rule = new LangtonRuleProperty();

    public LangtonConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
        registerConfigValidationRule(
                Bindings.createBooleanBinding(
                        () -> {
                            try {
                                String ruleString = rule.stringProperty().get();
                                if (ruleString == null) {
                                    return true;
                                }
                                LangtonMovementRules rules = LangtonMovementRules.parse(ruleString);
                                return !rules.isValidForCellShape(cellShapeProperty().getValue());
                            } catch (Exception e) {
                                return true;
                            }
                        },
                        rule.stringProperty(),
                        cellShapeProperty().property()
                ),
                () -> AppLocalization.getText(LANGTON_CONFIG_VALIDATION_MOVEMENT_RULES)
        );
    }

    @Override
    public LangtonConfig createConfig() {
        return new LangtonConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                NEIGHBORHOOD_MODE_DEFAULT,
                ruleProperty().computeRuleAndUpdateProperties(cellShapeProperty().property().getValue())
        );
    }

    public LangtonRuleProperty ruleProperty() {
        return rule;
    }

}
