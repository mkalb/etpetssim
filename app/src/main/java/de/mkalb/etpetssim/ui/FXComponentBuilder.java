package de.mkalb.etpetssim.ui;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.*;

/**
 * A utility class for creating JavaFX UI components with specific styles.
 * <p>
 * This class provides methods to create commonly used UI components with predefined styles.
 * It will grow during the development of the Extraterrestrial Pets Simulation application.
 */
@SuppressWarnings("MagicNumber")
public final class FXComponentBuilder {

    /**
     * Private constructor to prevent instantiation.
     */
    private FXComponentBuilder() {
    }

    /**
     * Creates a Label with the specified text and CSS class.
     * @param text the text to display in the label
     * @param cssClass the CSS class to apply to the label
     * @return a Label with the specified text and CSS class
     */
    public static Label createLabel(String text, String cssClass) {
        Label label = new Label(text);
        label.getStyleClass().add(cssClass);
        return label;
    }

    /**
     * Creates a labeled integer slider with a bound double value, formatted label, tooltip, and custom style class.
     * <p>
     * The slider is configured for integer values in the range defined by the given {@link ExtendedDoublePropertyIntRange}.
     * The slider's value is bidirectionally bound to the underlying {@link DoubleProperty} of the record.
     * The label displays the current value using the provided format string. Both the label and the slider share
     * the same tooltip for improved accessibility.
     * <p>
     * This method is suitable for sliders with a moderate integer value range.
     *
     * @param extendedDoublePropertyIntRange the {@link ExtendedDoublePropertyIntRange} defining the integer range and value binding
     * @param labelFormatString the format string for the label (e.g., "%d px")
     * @param tooltip the tooltip text for both the label and the slider
     * @param styleClass the CSS style class to apply to the slider
     * @return a {@link FXComponentBuilder.LabeledControl} containing the label and the slider
     */
    public static LabeledControl createLabeledIntSlider(ExtendedDoublePropertyIntRange extendedDoublePropertyIntRange,
                                                        String labelFormatString,
                                                        String tooltip,
                                                        String styleClass) {

        Slider slider = new Slider(extendedDoublePropertyIntRange.min(), extendedDoublePropertyIntRange.max(),
                extendedDoublePropertyIntRange.getValue());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(extendedDoublePropertyIntRange.max() - extendedDoublePropertyIntRange.min());
        slider.setMinorTickCount(extendedDoublePropertyIntRange.max() - extendedDoublePropertyIntRange.min() - 1);
        slider.setBlockIncrement(1.0d);
        slider.setSnapToTicks(true);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(extendedDoublePropertyIntRange.property());

        Label label = new Label();
        label.textProperty().bind(extendedDoublePropertyIntRange.asStringBinding(labelFormatString));
        label.setLabelFor(slider);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        slider.setTooltip(tooltipValue);

        return new LabeledControl(label, slider);
    }

    /**
     * Creates a labeled integer spinner with a bound value, formatted label, tooltip, and custom style class.
     * <p>
     * The spinner is configured for integer values in the range and step defined by the given {@link ExtendedIntegerProperty}.
     * The spinner and the property are kept in sync via listeners and normalization logic. The label displays the current value using
     * the provided format string. Both the label and the spinner share the same tooltip for improved accessibility.
     * <p>
     * The spinner is editable, allowing direct user input, and uses the specified CSS style class.
     * Input is validated and normalized to ensure only valid values are accepted.
     *
     * @param extendedIntegerProperty the {@link ExtendedIntegerProperty} defining range, step, and value binding
     * @param labelFormatString the format string for the label (e.g., "Width: %d")
     * @param tooltip the tooltip text for both the label and the spinner
     * @param styleClass the CSS style class to apply to the spinner
     * @return a {@link FXComponentBuilder.LabeledControl} containing the label and the spinner
     */
    public static LabeledControl createLabeledIntSpinner(
            ExtendedIntegerProperty extendedIntegerProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {

        // Create spinner with min, max, initial value, and step
        Spinner<Integer> spinner = new Spinner<>(
                extendedIntegerProperty.min(),
                extendedIntegerProperty.max(),
                extendedIntegerProperty.getValue(),
                extendedIntegerProperty.step());
        spinner.setEditable(true);
        spinner.getStyleClass().add(styleClass);

        // Only allow integer input in the editor
        TextFormatter<Integer> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*")) {
                return change;
            }
            return null;
        });
        spinner.getEditor().setTextFormatter(formatter);

        // Keep spinner in sync with property changes
        extendedIntegerProperty.asObjectProperty().addListener((_, _, newVal) -> {
            if (!spinner.getValueFactory().getValue().equals(newVal)) {
                spinner.getValueFactory().setValue(newVal);
            }
        });

        // Normalize and sync on spinner value change
        spinner.valueProperty().addListener((_, _, _) -> normalizeAndSyncSpinnerValue(spinner, extendedIntegerProperty));

        // Normalize and sync when editor loses focus
        spinner.getEditor().focusedProperty().addListener((_, _, isNowFocused) -> {
            if (!isNowFocused) {
                normalizeAndSyncSpinnerValue(spinner, extendedIntegerProperty);
            }
        });

        // Normalize and sync on Enter key in editor
        spinner.getEditor().setOnAction(_ -> normalizeAndSyncSpinnerValue(spinner, extendedIntegerProperty));

        // Label setup
        Label label = new Label();
        label.textProperty().bind(extendedIntegerProperty.asStringBinding(labelFormatString));
        label.setLabelFor(spinner);

        // Tooltip for both label and spinner
        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        spinner.setTooltip(tooltipValue);

        return new LabeledControl(label, spinner);
    }

    /**
     * Normalizes and synchronizes the value of a {@link Spinner}'s editor with its value factory and the associated {@link ExtendedIntegerProperty}.
     * <p>
     * Attempts to parse the editor text as an integer, clamps and snaps it to the valid range and step
     * defined by the given {@link ExtendedIntegerProperty}, and updates both the spinner and the property with the normalized value.
     * If parsing fails, resets the editor text and spinner value to the current property value.
     *
     * @param spinner the {@link Spinner} whose editor input is to be normalized and synchronized
     * @param extendedIntegerProperty the {@link ExtendedIntegerProperty} providing range and step constraints
     */
    private static void normalizeAndSyncSpinnerValue(Spinner<Integer> spinner,
                                                     ExtendedIntegerProperty extendedIntegerProperty) {
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            String text = spinner.getEditor().getText();
            try {
                int parsedValue = Integer.parseInt(text);
                int normalizedValue = extendedIntegerProperty.adjustValue(parsedValue);

                // 1. Update editor text if normalization changed the value
                if (parsedValue != normalizedValue) {
                    spinner.getEditor().setText(String.valueOf(normalizedValue));
                }

                // 2. Update property if needed
                if (extendedIntegerProperty.getValue() != normalizedValue) {
                    extendedIntegerProperty.setValue(normalizedValue);
                }

                // 3. Update spinner if needed
                if (!valueFactory.getValue().equals(normalizedValue)) {
                    valueFactory.setValue(normalizedValue);
                }
            } catch (NumberFormatException e) {
                // Reset to current property value if parsing fails
                int currentValue = extendedIntegerProperty.getValue();
                spinner.getEditor().setText(String.valueOf(currentValue));
                valueFactory.setValue(currentValue);
            }
        }
    }

    /**
     * Creates a labeled percent slider with a bound value, formatted label, tooltip, and custom style class.
     * <p>
     * The slider is configured for percent values in the range defined by the given {@link ExtendedDoubleProperty}.
     * The slider's value is bidirectionally bound to the property. The label displays the current value using
     * the provided format string. Both the label and the slider share the same tooltip for improved accessibility.
     * <p>
     * The slider is suitable for values between 0.0 and 1.0 (inclusive).
     *
     * @param extendedDoubleProperty the {@link ExtendedDoubleProperty} defining range and value binding
     * @param labelFormatString the format string for the label (e.g., "%.2f %%")
     * @param tooltip the tooltip text for both the label and the slider
     * @param styleClass the CSS style class to apply to the slider
     * @return a {@link FXComponentBuilder.LabeledControl} containing the label and the slider
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static LabeledControl createLabeledPercentSlider(
            ExtendedDoubleProperty extendedDoubleProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        double min = extendedDoubleProperty.min();
        double max = extendedDoubleProperty.max();

        double minRounded = Math.max(0.0d, Math.round(min * 100.0d) / 100.0d);
        double maxRounded = Math.min(1.0d, Math.round(max * 100.0d) / 100.0d);
        double valueRounded = Math.round(extendedDoubleProperty.getValue() * 100.0d) / 100.0d;
        double majorTickUnit = (maxRounded - minRounded);
        int range = ((int) (maxRounded * 100)) - ((int) (minRounded * 100));
        int minorTickCount = calculateMinorTickCountForPercentSlider(range);

        double value = Math.max(minRounded, Math.min(maxRounded, valueRounded));
        extendedDoubleProperty.setValue(value);

        Slider slider = new Slider(minRounded, maxRounded, value);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(majorTickUnit);
        slider.setMinorTickCount(minorTickCount);
        slider.setBlockIncrement(0.01d);
        slider.setSnapToTicks(false);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(extendedDoubleProperty.property());
        slider.valueProperty().addListener((_, _, newValue) -> {
            double rounded = Math.round(newValue.doubleValue() * 100.0d) / 100.0d;
            slider.setValue(rounded);
        });

        Label label = new Label();
        label.textProperty().bind(extendedDoubleProperty.property().multiply(100).asString(labelFormatString));
        label.setLabelFor(slider);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        slider.setTooltip(tooltipValue);

        return new LabeledControl(label, slider);
    }

    /**
     * Calculates the number of minor tick marks for a percent slider based on the given range.
     * <p>
     * This method determines the number of minor ticks by checking if the range is divisible
     * by specific divisors. If a divisor matches, the method returns the divisor minus one.
     * If no divisor matches, it returns 0.
     * <p>
     * The range is expected to be between 2 and 100 (inclusive). If the range is outside this
     * interval, the method will return 0 without performing any calculations.
     *
     * @param range the range of the slider (difference between max and min values multiplied by 100)
     * @return the number of minor ticks (divisor - 1) if a matching divisor is found, or 0 otherwise
     */
    private static int calculateMinorTickCountForPercentSlider(int range) {
        // Ensure the range is within the valid interval [2, 100]
        if ((range >= 2) && (range <= 100)) {
            // Array of divisors to check for divisibility
            int[] divisors = {10, 8, 6, 4, 9, 7, 5, 11, 13, 17, 19, 3, 2};
            for (int divisor : divisors) {
                // If the range is divisible by the current divisor, return divisor - 1
                if ((range % divisor) == 0) {
                    return divisor - 1;
                }
            }
        }
        // Return 0 if no divisor matches or the range is out of bounds
        return 0;
    }

    /**
     * Bundles a {@link Label} and a {@link Control} (e.g., a {@link Slider}) for use in UIs.
     * This record is typically used to return both the label and the associated control as a pair.
     *
     * @param label   the label describing the control
     * @param control the UI control (e.g., slider, spinner) associated with the label
     */
    public record LabeledControl(Label label, Control control) {}

}
