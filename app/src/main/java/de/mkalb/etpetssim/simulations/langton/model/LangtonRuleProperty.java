package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class LangtonRuleProperty {

    private static final LangtonRulePresetTriangle PRESET_TRIANGLE_INITIAL = LangtonRulePresetTriangle.EMPTY;
    private static final LangtonRulePresetSquare PRESET_SQUARE_INITIAL = LangtonRulePresetSquare.EMPTY;
    private static final LangtonRulePresetHexagon PRESET_HEXAGON_INITIAL = LangtonRulePresetHexagon.EMPTY;
    private static final String INITIAL_STRING = "RL";

    private final StringProperty stringProperty;
    private final StringProperty labelProperty;
    private final InputEnumProperty<LangtonRulePresetTriangle> presetTriangleProperty;
    private final InputEnumProperty<LangtonRulePresetSquare> presetSquareProperty;
    private final InputEnumProperty<LangtonRulePresetHexagon> presetHexagonProperty;

    public LangtonRuleProperty() {
        stringProperty = new SimpleStringProperty(INITIAL_STRING);
        labelProperty = new SimpleStringProperty("");

        presetTriangleProperty = InputEnumProperty.of(PRESET_TRIANGLE_INITIAL, LangtonRulePresetTriangle.class, Enum::toString);
        presetSquareProperty = InputEnumProperty.of(PRESET_SQUARE_INITIAL, LangtonRulePresetSquare.class, Enum::toString);
        presetHexagonProperty = InputEnumProperty.of(PRESET_HEXAGON_INITIAL, LangtonRulePresetHexagon.class, Enum::toString);

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
            AppLogger.error("Invalid Langton rule string: " + stringProperty.get() + ", using initial value: " + PRESET_SQUARE_INITIAL, e);
            rules = LangtonMovementRules.fromString(PRESET_SQUARE_INITIAL.toString());
        }
        String rulesDisplayString = rules.toDisplayString();
        stringProperty.set(rulesDisplayString);
        labelProperty.set(rulesDisplayString);
        return rules;
    }

    public enum LangtonRulePresetSquare {
        EMPTY,
        RL,
        RLR,
        RLLR,
        RRLL,
        RNNU,
        RLLLLLRRL,
        RRLLLRLLLRRR;

        public String toString() {
            if (this == EMPTY) {
                return "";
            } else {
                return name();
            }
        }
    }

    public enum LangtonRulePresetHexagon {
        EMPTY,
        RL,
        RL2,
        R2N,
        NR,
        NR2,
        R2RR,
        L2NNLL2L;

        public String toString() {
            if (this == EMPTY) {
                return "";
            } else {
                return name();
            }
        }
    }

    public enum LangtonRulePresetTriangle {
        EMPTY,
        RL,
        RLL,
        URR;

        public String toString() {
            if (this == EMPTY) {
                return "";
            } else {
                return name();
            }
        }
    }

}
