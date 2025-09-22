package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class LangtonRuleProperty {

    private final String initialValue;
    private final StringProperty stringProperty;
    private final StringProperty labelProperty;

    public LangtonRuleProperty(String initialValue) {
        this.initialValue = initialValue;
        stringProperty = new SimpleStringProperty(initialValue);
        labelProperty = new SimpleStringProperty("");
    }

    public StringProperty stringProperty() {
        return stringProperty;
    }

    public StringProperty labelProperty() {
        return labelProperty;
    }

    public LangtonMovementRules computeRuleAndUpdateProperties(CellShape cellShape) {
        LangtonMovementRules rules;
        try {
            rules = LangtonMovementRules.fromString(stringProperty.get());
            if (!rules.isValidForCellShape(cellShape)) {
                throw new IllegalArgumentException("Rule not valid for cell shape! cellShape=" + cellShape + ", input=" + stringProperty.get());
            }
        } catch (IllegalArgumentException e) {
            AppLogger.error("Invalid Langton rule string: " + stringProperty.get() + ", using initial value: " + initialValue, e);
            rules = LangtonMovementRules.fromString(initialValue);
        }
        String rulesDisplayString = rules.toDisplayString();
        stringProperty.set(rulesDisplayString);
        labelProperty.set(rulesDisplayString);
        return rules;
    }

}
