package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class LangtonRuleProperty {

    private final LangtonRulePreset INITIAL = LangtonRulePreset.RL;
    private final StringProperty stringProperty;
    private final StringProperty labelProperty;
    private final InputEnumProperty<LangtonRulePreset> presetProperty;

    public LangtonRuleProperty() {
        stringProperty = new SimpleStringProperty(INITIAL.toString());
        labelProperty = new SimpleStringProperty("");
        presetProperty = InputEnumProperty.of(INITIAL, LangtonRulePreset.class, Enum::toString);
        presetProperty.property().addListener((_, _, newVal) -> {
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

    public InputEnumProperty<LangtonRulePreset> presetProperty() {
        return presetProperty;
    }

    public LangtonMovementRules computeRuleAndUpdateProperties(CellShape cellShape) {
        LangtonMovementRules rules;
        try {
            rules = LangtonMovementRules.fromString(stringProperty.get());
            if (!rules.isValidForCellShape(cellShape)) {
                throw new IllegalArgumentException("Rule not valid for cell shape! cellShape=" + cellShape + ", input=" + stringProperty.get());
            }
        } catch (IllegalArgumentException e) {
            AppLogger.error("Invalid Langton rule string: " + stringProperty.get() + ", using initial value: " + INITIAL, e);
            rules = LangtonMovementRules.fromString(INITIAL.toString());
        }
        String rulesDisplayString = rules.toDisplayString();
        stringProperty.set(rulesDisplayString);
        labelProperty.set(rulesDisplayString);
        return rules;
    }

    public enum LangtonRulePreset {
        RL,
        RLR,
        RLLR,
        LLRR,
        LRRRRRLLR,
        LLRRRLRLRLLR,
        RRLLLRLLLRRR,
        L2NNLL2L
    }

}
