package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetHexagon;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetSquare;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetTriangle;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Encapsulates the preset selection and free-text S/B input for Conway transition rules.
 *
 * <p>Three {@link InputChoiceProperty} instances hold shape-specific preset selections
 * (one per {@link de.mkalb.etpetssim.engine.CellShape}). Selecting a preset sets the
 * {@link #stringProperty()} to the corresponding S/B rule string, which in turn
 * triggers the registered {@link #setRulesChangedListener(Consumer) rulesChangedListener} callback
 * so the ViewModel can update the checkbox grid.
 *
 * <p>Manual S/B text entered in the text field also triggers the callback; parsing errors
 * are silently ignored and the checkbox grid remains unchanged.
 * The combo boxes are never updated by manual checkbox or text-field changes.
 */
public final class ConwayRuleProperty {

    private static final ConwayRulePresetTriangle PRESET_TRIANGLE_DEFAULT = ConwayRulePresetTriangle.EMPTY;
    private static final ConwayRulePresetSquare PRESET_SQUARE_DEFAULT = ConwayRulePresetSquare.EMPTY;
    private static final ConwayRulePresetHexagon PRESET_HEXAGON_DEFAULT = ConwayRulePresetHexagon.EMPTY;

    private final StringProperty ruleString;
    private final StringProperty label;
    private final InputChoiceProperty<ConwayRulePresetTriangle> presetTriangle;
    private final InputChoiceProperty<ConwayRulePresetSquare> presetSquare;
    private final InputChoiceProperty<ConwayRulePresetHexagon> presetHexagon;
    private @Nullable Consumer<ConwayTransitionRules> rulesChangedListener;

    public ConwayRuleProperty() {
        ruleString = new SimpleStringProperty("");
        label = new SimpleStringProperty("");

        presetTriangle = InputChoiceProperty.ofEnum(PRESET_TRIANGLE_DEFAULT, ConwayRulePresetTriangle.class,
                ConwayRulePresetTriangle::displayName);
        presetSquare = InputChoiceProperty.ofEnum(PRESET_SQUARE_DEFAULT, ConwayRulePresetSquare.class,
                ConwayRulePresetSquare::displayName);
        presetHexagon = InputChoiceProperty.ofEnum(PRESET_HEXAGON_DEFAULT, ConwayRulePresetHexagon.class,
                ConwayRulePresetHexagon::displayName);

        presetTriangle.property().addListener((_, _, newVal) -> ruleString.set(newVal.ruleString()));
        presetSquare.property().addListener((_, _, newVal) -> ruleString.set(newVal.ruleString()));
        presetHexagon.property().addListener((_, _, newVal) -> ruleString.set(newVal.ruleString()));

        ruleString.addListener((_, _, newVal) -> {
            if ((newVal == null) || newVal.isBlank()) {
                return;
            }
            try {
                ConwayTransitionRules rules = ConwayTransitionRules.parse(newVal);
                if (rulesChangedListener != null) {
                    rulesChangedListener.accept(rules);
                }
            } catch (IllegalArgumentException _) {
                // Silently ignore invalid S/B input; the checkbox grid remains unchanged.
            }
        });
    }

    /**
     * Sets the callback that is invoked whenever a valid {@link ConwayTransitionRules} is
     * derived from a preset selection or a text-field change; replaces any previous callback.
     *
     * @param listener the consumer to call with the parsed rules
     */
    public void setRulesChangedListener(Consumer<ConwayTransitionRules> listener) {
        rulesChangedListener = listener;
    }

    /**
     * Returns the S/B text input property, bound bidirectionally to the text field.
     *
     * @return the string property
     */
    public StringProperty stringProperty() {
        return ruleString;
    }

    /**
     * Returns the label property (always empty for Conway's Game of Life; present for API symmetry with Langton's Ant).
     *
     * @return the label property
     */
    public StringProperty labelProperty() {
        return label;
    }

    public InputChoiceProperty<ConwayRulePresetTriangle> presetTriangleProperty() {
        return presetTriangle;
    }

    public InputChoiceProperty<ConwayRulePresetSquare> presetSquareProperty() {
        return presetSquare;
    }

    public InputChoiceProperty<ConwayRulePresetHexagon> presetHexagonProperty() {
        return presetHexagon;
    }

}
