package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.langton.shared.*;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.RULE_DEFAULT;

public final class LangtonRuleProperty {

    private static final LangtonRulePresetTriangle PRESET_TRIANGLE_INITIAL = LangtonRulePresetTriangle.EMPTY;
    private static final LangtonRulePresetSquare PRESET_SQUARE_INITIAL = LangtonRulePresetSquare.EMPTY;
    private static final LangtonRulePresetHexagon PRESET_HEXAGON_INITIAL = LangtonRulePresetHexagon.EMPTY;

    private final StringProperty stringProperty;
    private final StringProperty labelProperty;
    private final InputEnumProperty<LangtonRulePresetTriangle> presetTriangleProperty;
    private final InputEnumProperty<LangtonRulePresetSquare> presetSquareProperty;
    private final InputEnumProperty<LangtonRulePresetHexagon> presetHexagonProperty;

    public LangtonRuleProperty() {
        stringProperty = new SimpleStringProperty(RULE_DEFAULT);
        labelProperty = new SimpleStringProperty("");

        presetTriangleProperty = InputEnumProperty.of(PRESET_TRIANGLE_INITIAL, LangtonRulePresetTriangle.class,
                LangtonRulePresetTriangle::displayName);
        presetSquareProperty = InputEnumProperty.of(PRESET_SQUARE_INITIAL, LangtonRulePresetSquare.class,
                LangtonRulePresetSquare::displayName);
        presetHexagonProperty = InputEnumProperty.of(PRESET_HEXAGON_INITIAL, LangtonRulePresetHexagon.class,
                LangtonRulePresetHexagon::displayName);

        presetTriangleProperty.property().addListener((_, _, newVal) -> {
            stringProperty.set(newVal.toString());
            labelProperty.set("");
        });
        presetSquareProperty.property().addListener((_, _, newVal) -> {
            stringProperty.set(newVal.toString());
            labelProperty.set("");
        });
        presetHexagonProperty.property().addListener((_, _, newVal) -> {
            stringProperty.set(newVal.toString());
            labelProperty.set("");
        });
    }

    public StringProperty stringProperty() {
        return stringProperty;
    }

    public StringProperty labelProperty() {
        return labelProperty;
    }

    public InputEnumProperty<LangtonRulePresetTriangle> presetTriangleProperty() {
        return presetTriangleProperty;
    }

    public InputEnumProperty<LangtonRulePresetSquare> presetSquareProperty() {
        return presetSquareProperty;
    }

    public InputEnumProperty<LangtonRulePresetHexagon> presetHexagonProperty() {
        return presetHexagonProperty;
    }

    public LangtonMovementRules computeRuleAndUpdateProperties(CellShape cellShape) {
        LangtonMovementRules rules;
        try {
            rules = LangtonMovementRules.fromString(stringProperty.get());
            if (!rules.isValidForCellShape(cellShape)) {
                throw new IllegalArgumentException("Rule not valid for cell shape! cellShape=" + cellShape + ", input=" + stringProperty.get());
            }
        } catch (IllegalArgumentException e) {
            AppLogger.errorf(e, "Invalid Langton rule string '%s', falling back to '%s'", stringProperty.get(), RULE_DEFAULT);
            rules = LangtonMovementRules.fromString(RULE_DEFAULT);
        }
        String rulesDisplayString = rules.toDisplayString();
        stringProperty.set(rulesDisplayString);
        labelProperty.set(rulesDisplayString);
        return rules;
    }

}
