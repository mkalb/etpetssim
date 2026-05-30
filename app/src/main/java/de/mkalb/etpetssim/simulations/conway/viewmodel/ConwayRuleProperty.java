package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.conway.shared.*;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jspecify.annotations.Nullable;

import java.util.function.*;

/**
 * Encapsulates the preset selection and free-text S/B input for Conway transition rules.
 * <p>
 * Three {@link InputEnumProperty} instances hold shape-specific preset selections
 * (one per {@link de.mkalb.etpetssim.engine.CellShape}). Selecting a preset sets the
 * {@link #stringProperty()} to the corresponding S/B rule string, which in turn
 * triggers the registered {@link #setOnRulesChanged(Consumer) onRulesChanged} callback
 * so the ViewModel can update the checkbox grid.
 * <p>
 * Manual S/B text entered in the text field also triggers the callback; parsing errors
 * are silently ignored and the checkbox grid remains unchanged.
 * The combo boxes are never updated by manual checkbox or text-field changes.
 */
public final class ConwayRuleProperty {

    private static final ConwayPresetTriangle PRESET_TRIANGLE_INITIAL = ConwayPresetTriangle.EMPTY;
    private static final ConwayPresetSquare PRESET_SQUARE_INITIAL = ConwayPresetSquare.EMPTY;
    private static final ConwayPresetHexagon PRESET_HEXAGON_INITIAL = ConwayPresetHexagon.EMPTY;

    private final StringProperty stringProperty;
    private final StringProperty labelProperty;
    private final InputEnumProperty<ConwayPresetTriangle> presetTriangleProperty;
    private final InputEnumProperty<ConwayPresetSquare> presetSquareProperty;
    private final InputEnumProperty<ConwayPresetHexagon> presetHexagonProperty;

    private @Nullable Consumer<ConwayTransitionRules> onRulesChanged;

    public ConwayRuleProperty() {
        stringProperty = new SimpleStringProperty("");
        labelProperty = new SimpleStringProperty("");

        presetTriangleProperty = InputEnumProperty.of(PRESET_TRIANGLE_INITIAL, ConwayPresetTriangle.class,
                ConwayPresetTriangle::displayName);
        presetSquareProperty = InputEnumProperty.of(PRESET_SQUARE_INITIAL, ConwayPresetSquare.class,
                ConwayPresetSquare::displayName);
        presetHexagonProperty = InputEnumProperty.of(PRESET_HEXAGON_INITIAL, ConwayPresetHexagon.class,
                ConwayPresetHexagon::displayName);

        presetTriangleProperty.property().addListener((_, _, newVal) -> stringProperty.set(newVal.toString()));
        presetSquareProperty.property().addListener((_, _, newVal) -> stringProperty.set(newVal.toString()));
        presetHexagonProperty.property().addListener((_, _, newVal) -> stringProperty.set(newVal.toString()));

        stringProperty.addListener((_, _, newVal) -> {
            if ((newVal == null) || newVal.isBlank()) {
                return;
            }
            try {
                ConwayTransitionRules rules = ConwayTransitionRules.of(newVal);
                if (onRulesChanged != null) {
                    onRulesChanged.accept(rules);
                }
            } catch (IllegalArgumentException e) {
                // Silently ignore invalid S/B input — checkbox grid remains unchanged
            }
        });
    }

    /**
     * Registers a callback that is invoked whenever a valid {@link ConwayTransitionRules} is
     * derived from a preset selection or a text-field change.
     *
     * @param callback the consumer to call with the parsed rules
     */
    public void setOnRulesChanged(Consumer<ConwayTransitionRules> callback) {
        onRulesChanged = callback;
    }

    /**
     * Returns the S/B text input property, bound bidirectionally to the text field.
     *
     * @return the string property
     */
    public StringProperty stringProperty() {
        return stringProperty;
    }

    /**
     * Returns the label property (always empty for Conway; present for API symmetry with Langton).
     *
     * @return the label property
     */
    public StringProperty labelProperty() {
        return labelProperty;
    }

    public InputEnumProperty<ConwayPresetTriangle> presetTriangleProperty() {
        return presetTriangleProperty;
    }

    public InputEnumProperty<ConwayPresetSquare> presetSquareProperty() {
        return presetSquareProperty;
    }

    public InputEnumProperty<ConwayPresetHexagon> presetHexagonProperty() {
        return presetHexagonProperty;
    }

}
