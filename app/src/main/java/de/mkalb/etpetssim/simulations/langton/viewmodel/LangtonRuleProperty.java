package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePreset;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetHexagon;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetSquare;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetTriangle;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints.MOVEMENT_RULES_DEFAULT;

/**
 * Rule input of Langton's Ant with one preset selection per cell shape and the editable rule string.
 */
public final class LangtonRuleProperty {

    private static final LangtonRulePresetTriangle PRESET_TRIANGLE_DEFAULT = LangtonRulePresetTriangle.EMPTY;
    private static final LangtonRulePresetSquare PRESET_SQUARE_DEFAULT = LangtonRulePresetSquare.EMPTY;
    private static final LangtonRulePresetHexagon PRESET_HEXAGON_DEFAULT = LangtonRulePresetHexagon.EMPTY;

    private final StringProperty ruleString;
    private final StringProperty label;
    private final InputChoiceProperty<LangtonRulePresetTriangle> presetTriangle;
    private final InputChoiceProperty<LangtonRulePresetSquare> presetSquare;
    private final InputChoiceProperty<LangtonRulePresetHexagon> presetHexagon;

    public LangtonRuleProperty() {
        ruleString = new SimpleStringProperty(MOVEMENT_RULES_DEFAULT);
        label = new SimpleStringProperty("");

        presetTriangle = InputChoiceProperty.ofEnum(PRESET_TRIANGLE_DEFAULT, LangtonRulePresetTriangle.class,
                LangtonRulePresetTriangle::displayName);
        presetSquare = InputChoiceProperty.ofEnum(PRESET_SQUARE_DEFAULT, LangtonRulePresetSquare.class,
                LangtonRulePresetSquare::displayName);
        presetHexagon = InputChoiceProperty.ofEnum(PRESET_HEXAGON_DEFAULT, LangtonRulePresetHexagon.class,
                LangtonRulePresetHexagon::displayName);

        presetTriangle.property().addListener((_, _, newVal) -> applyPreset(newVal));
        presetSquare.property().addListener((_, _, newVal) -> applyPreset(newVal));
        presetHexagon.property().addListener((_, _, newVal) -> applyPreset(newVal));
    }

    public StringProperty stringProperty() {
        return ruleString;
    }

    public StringProperty labelProperty() {
        return label;
    }

    public InputChoiceProperty<LangtonRulePresetTriangle> presetTriangleProperty() {
        return presetTriangle;
    }

    public InputChoiceProperty<LangtonRulePresetSquare> presetSquareProperty() {
        return presetSquare;
    }

    public InputChoiceProperty<LangtonRulePresetHexagon> presetHexagonProperty() {
        return presetHexagon;
    }

    public LangtonMovementRules computeRuleAndUpdateProperties(CellShape cellShape) {
        LangtonMovementRules rules;
        try {
            rules = LangtonMovementRules.parse(ruleString.get());
            if (!rules.isValidForCellShape(cellShape)) {
                throw new IllegalArgumentException("rules must be valid for cellShape " + cellShape
                        + ", but was: " + rules);
            }
        } catch (IllegalArgumentException e) {
            AppLogger.warnf("LangtonRuleProperty: Ignoring invalid rule string: '%s', fallback: '%s', reason: %s",
                    ruleString.get(), MOVEMENT_RULES_DEFAULT, e);
            rules = LangtonMovementRules.parse(MOVEMENT_RULES_DEFAULT);
        }
        String rulesDisplayString = rules.toDisplayString();
        ruleString.set(rulesDisplayString);
        label.set(rulesDisplayString);
        return rules;
    }

    private void applyPreset(LangtonRulePreset preset) {
        ruleString.set(preset.ruleString());
        label.set("");
    }

}
