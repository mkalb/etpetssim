package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetHexagon;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetSquare;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetTriangle;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.MOVEMENT_RULES_DEFAULT;

public final class LangtonRuleProperty {

    private static final LangtonRulePresetTriangle PRESET_TRIANGLE_INITIAL = LangtonRulePresetTriangle.EMPTY;
    private static final LangtonRulePresetSquare PRESET_SQUARE_INITIAL = LangtonRulePresetSquare.EMPTY;
    private static final LangtonRulePresetHexagon PRESET_HEXAGON_INITIAL = LangtonRulePresetHexagon.EMPTY;

    private final StringProperty stringProperty;
    private final StringProperty labelProperty;
    private final InputChoiceProperty<LangtonRulePresetTriangle> presetTriangleProperty;
    private final InputChoiceProperty<LangtonRulePresetSquare> presetSquareProperty;
    private final InputChoiceProperty<LangtonRulePresetHexagon> presetHexagonProperty;

    public LangtonRuleProperty() {
        stringProperty = new SimpleStringProperty(MOVEMENT_RULES_DEFAULT);
        labelProperty = new SimpleStringProperty("");

        presetTriangleProperty = InputChoiceProperty.ofEnum(PRESET_TRIANGLE_INITIAL, LangtonRulePresetTriangle.class,
                LangtonRulePresetTriangle::displayName);
        presetSquareProperty = InputChoiceProperty.ofEnum(PRESET_SQUARE_INITIAL, LangtonRulePresetSquare.class,
                LangtonRulePresetSquare::displayName);
        presetHexagonProperty = InputChoiceProperty.ofEnum(PRESET_HEXAGON_INITIAL, LangtonRulePresetHexagon.class,
                LangtonRulePresetHexagon::displayName);

        presetTriangleProperty.property().addListener((_, _, newVal) -> {
            stringProperty.set(newVal.ruleString());
            labelProperty.set("");
        });
        presetSquareProperty.property().addListener((_, _, newVal) -> {
            stringProperty.set(newVal.ruleString());
            labelProperty.set("");
        });
        presetHexagonProperty.property().addListener((_, _, newVal) -> {
            stringProperty.set(newVal.ruleString());
            labelProperty.set("");
        });
    }

    public StringProperty stringProperty() {
        return stringProperty;
    }

    public StringProperty labelProperty() {
        return labelProperty;
    }

    public InputChoiceProperty<LangtonRulePresetTriangle> presetTriangleProperty() {
        return presetTriangleProperty;
    }

    public InputChoiceProperty<LangtonRulePresetSquare> presetSquareProperty() {
        return presetSquareProperty;
    }

    public InputChoiceProperty<LangtonRulePresetHexagon> presetHexagonProperty() {
        return presetHexagonProperty;
    }

    public LangtonMovementRules computeRuleAndUpdateProperties(CellShape cellShape) {
        LangtonMovementRules rules;
        try {
            rules = LangtonMovementRules.parse(stringProperty.get());
            if (!rules.isValidForCellShape(cellShape)) {
                throw new IllegalArgumentException("Rule not valid for cell shape! cellShape=" + cellShape + ", input=" + stringProperty.get());
            }
        } catch (IllegalArgumentException e) {
            AppLogger.errorf(e, "Invalid Langton rule string '%s', falling back to '%s'", stringProperty.get(), MOVEMENT_RULES_DEFAULT);
            rules = LangtonMovementRules.parse(MOVEMENT_RULES_DEFAULT);
        }
        String rulesDisplayString = rules.toDisplayString();
        stringProperty.set(rulesDisplayString);
        labelProperty.set(rulesDisplayString);
        return rules;
    }

}
