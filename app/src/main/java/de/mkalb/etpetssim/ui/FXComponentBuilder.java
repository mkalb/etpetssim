package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.PropertyAdjuster;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
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
     * Creates a labeled integer slider with a bound value, formatted label, tooltip, and custom style class.
     * <p>
     * The slider is configured for integer values in the specified range and is bidirectionally bound to the given property.
     * The label displays the current value using the provided format string.
     * Both the label and the slider share the same tooltip for improved accessibility.
     * <p>
     * <b>Note:</b> This method is suitable for sliders with a moderate value range (not too large).
     *
     * @param min              the minimum slider value (inclusive)
     * @param max              the maximum slider value (inclusive)
     * @param bindProperty     the {@link DoubleProperty} to bind the slider's value to
     * @param labelFormatString the format string for the label (e.g., "%d px")
     * @param tooltip          the tooltip text for both the label and the slider
     * @param styleClass       the CSS style class to apply to the slider
     * @return a {@link FXComponentBuilder.LabeledControl} containing the label and the slider
     * @throws IllegalArgumentException if {@code min >= max}
     */
    public static LabeledControl createLabeledIntSlider(int min,
                                                        int max,
                                                        DoubleProperty bindProperty,
                                                        String labelFormatString,
                                                        String tooltip,
                                                        String styleClass) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Slider slider = new Slider(min, max, bindProperty.getValue().intValue());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(max - min);
        slider.setMinorTickCount(max - min - 1);
        slider.setBlockIncrement(1.0d);
        slider.setSnapToTicks(true);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(bindProperty);

        Label label = new Label();
        label.textProperty().bind(bindProperty.asString(labelFormatString));
        label.setLabelFor(slider);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        slider.setTooltip(tooltipValue);

        return new LabeledControl(label, slider);
    }

    /**
     * Creates a labeled integer spinner with a bound value, formatted label, tooltip, and custom style class.
     * <p>
     * The spinner is configured for integer values in the specified range, with the given step size,
     * and is bidirectionally bound to the provided property. The label displays the current value using
     * the provided format string. Both the label and the spinner share the same tooltip for improved accessibility.
     * <p>
     * The spinner is editable, allowing direct user input, and uses the specified CSS style class.
     *
     * @param min               the minimum spinner value (inclusive)
     * @param max               the maximum spinner value (inclusive)
     * @param step              the step size for increment/decrement
     * @param bindProperty      the {@link IntegerProperty} to bind the spinner's value to
     * @param labelFormatString the format string for the label (e.g., "Width: %d")
     * @param tooltip           the tooltip text for both the label and the spinner
     * @param styleClass        the CSS style class to apply to the spinner
     * @return a {@link FXComponentBuilder.LabeledControl} containing the label and the spinner
     * @throws IllegalArgumentException if {@code min >= max}
     */
    public static LabeledControl createLabeledIntSpinner(
            int min,
            int max,
            int step,
            IntegerProperty bindProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Spinner<Integer> spinner = new Spinner<>(min, max, bindProperty.get(), step);
        spinner.setEditable(true);
        spinner.getStyleClass().add(styleClass);
        spinner.getValueFactory().valueProperty().bindBidirectional(bindProperty.asObject());
        spinner.getEditor().focusedProperty().addListener((_, _, isNowFocused) -> {
            if (!isNowFocused) {
                processSpinnerEditorInput(spinner, min, max, step);
                // Ensure the editor displays the bound property value
                spinner.getEditor().setText(String.valueOf(bindProperty.get()));
            }
        });
        spinner.getEditor().setOnAction(_ -> {
            processSpinnerEditorInput(spinner, min, max, step);
            // Ensure the editor displays the bound property value
            spinner.getEditor().setText(String.valueOf(bindProperty.get()));
        });

        Label label = new Label();
        label.textProperty().bind(bindProperty.asString(labelFormatString));
        label.setLabelFor(spinner);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        spinner.setTooltip(tooltipValue);

        return new LabeledControl(label, spinner);
    }

    /**
     * Validates and normalizes the value entered in a {@link Spinner}'s editor.
     * <p>
     * Parses the editor text as an integer, clamps and snaps it to the specified range and step,
     * and sets the normalized value in the spinner's value factory. If parsing fails, resets the
     * editor text to the current spinner value.
     *
     * @param spinner the {@link Spinner} whose editor input is to be processed
     * @param min     the minimum allowed value (inclusive)
     * @param max     the maximum allowed value (inclusive)
     * @param step    the step size for normalization
     */
    private static void processSpinnerEditorInput(Spinner<Integer> spinner, int min, int max, int step) {
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            String text = spinner.getEditor().getText().trim();
            try {
                int parsedValue = Integer.parseInt(text);
                int normalizedValue = PropertyAdjuster.adjustIntValue(parsedValue, min, max, step);
                valueFactory.setValue(normalizedValue);
            } catch (NumberFormatException e) {
                spinner.getEditor().setText(String.valueOf(spinner.getValue()));
            }
        }
    }

    /**
     * Creates a labeled percent slider with a bound value, formatted label, tooltip, and custom style class.
     * <p>
     * The slider is configured for percent values in the specified range and is bidirectionally bound to the given property.
     * The label displays the current value using the provided format string.
     * Both the label and the slider share the same tooltip for improved accessibility.
     *
     * @param min              the minimum slider value (inclusive, between 0.0 and 1.0)
     * @param max              the maximum slider value (inclusive, between 0.0 and 1.0)
     * @param bindProperty     the {@link DoubleProperty} to bind the slider's value to
     * @param labelFormatString the format string for the label (e.g., "%.2f %%")
     * @param tooltip          the tooltip text for both the label and the slider
     * @param styleClass       the CSS style class to apply to the slider
     * @return a {@link FXComponentBuilder.LabeledControl} containing the label and the slider
     * @throws IllegalArgumentException if {@code min >= max}
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static LabeledControl createLabeledPercentSlider(
            double min,
            double max,
            DoubleProperty bindProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        double minRounded = Math.max(0.0d, Math.round(min * 100.0d) / 100.0d);
        double maxRounded = Math.min(1.0d, Math.round(max * 100.0d) / 100.0d);
        double valueRounded = Math.round(bindProperty.get() * 100.0d) / 100.0d;
        double majorTickUnit = (maxRounded - minRounded);
        int range = ((int) (maxRounded * 100)) - ((int) (minRounded * 100));
        int minorTickCount = calculateMinorTickCountForPercentSlider(range);

        double value = Math.max(minRounded, Math.min(maxRounded, valueRounded));
        bindProperty.set(value);

        Slider slider = new Slider(minRounded, maxRounded, value);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(majorTickUnit);
        slider.setMinorTickCount(minorTickCount);
        slider.setBlockIncrement(0.01d);
        slider.setSnapToTicks(false);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(bindProperty);
        slider.valueProperty().addListener((_, _, newValue) -> {
            double rounded = Math.round(newValue.doubleValue() * 100.0d) / 100.0d;
            slider.setValue(rounded);
        });

        Label label = new Label();
        label.textProperty().bind(bindProperty.multiply(100).asString(labelFormatString));
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
