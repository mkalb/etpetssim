package de.mkalb.etpetssim.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * A utility class for creating JavaFX UI components with specific styles.
 * <p>
 * This class provides methods to create commonly used UI components with predefined styles.
 * It will grow during the development of the Extraterrestrial Pets Simulation application.
 */
@SuppressWarnings("MagicNumber")
public final class FXComponentFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private FXComponentFactory() {
    }

    /**
     * Creates a Label with the specified text and CSS class.
     * @param text the text to display in the label
     * @param styleClass the style class to add to the label
     * @return a Label with the specified text and CSS class
     */
    public static Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    /**
     * Creates an {@link HBox} with the specified style class.
     *
     * @param styleClass the style class to add to the HBox
     * @return a new HBox with the given style class
     */
    public static HBox createHBox(String styleClass) {
        HBox hbox = new HBox();
        hbox.getStyleClass().add(styleClass);
        return hbox;
    }

    /**
     * Creates a {@link VBox} with the specified style class.
     *
     * @param styleClass the style class to add to the VBox
     * @return a new VBox with the given style class
     */
    public static VBox createVBox(String styleClass) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add(styleClass);
        return vbox;
    }

    /**
     * Creates a labeled group of radio buttons for selecting enum values, with display names, formatted label, tooltip, and custom style class.
     * <p>
     * Each radio button represents a valid enum value from the provided {@link InputEnumProperty}. All radio buttons are added to the given container pane,
     * which can be any {@link Pane} (e.g., {@link VBox}, {@link HBox}) for flexible layout. The radio buttons are grouped using a {@link ToggleGroup},
     * ensuring only one can be selected at a time. Selection changes are synchronized with the property, and vice versa.
     * The label displays the current value using the provided format string.
     * Both the label and the radio buttons share the same tooltip for accessibility.
     * <p>
     * This method is suitable for enum-based selections with custom display names and flexible layout requirements.
     *
     * @param inputEnumProperty the {@link InputEnumProperty} defining valid values and value binding
     * @param displayNameProvider a function mapping enum values to their display names
     * @param radioButtonContainerPane the {@link Pane} to contain the radio buttons (e.g., {@link VBox}, {@link HBox})
     * @param labelFormatString the format string for the label (e.g., "%s")
     * @param tooltip the tooltip text for both the label and the radio buttons
     * @param styleClass the CSS style class to apply to the radio buttons
     * @param <E> the enum type
     * @param <P> the pane type
     * @return a {@link LabeledControl} containing the label and the container pane with radio buttons
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>, P extends Pane> LabeledControl<P> createLabeledEnumRadioButtons(InputEnumProperty<E> inputEnumProperty,
                                                                                                      Function<E, String> displayNameProvider,
                                                                                                      P radioButtonContainerPane,
                                                                                                      String labelFormatString,
                                                                                                      String tooltip,
                                                                                                      String styleClass) {
        ToggleGroup toggleGroup = new ToggleGroup();

        List<RadioButton> radioButtons = inputEnumProperty.getValidValues()
                                                          .stream()
                                                          .map(enumValue -> {
                                                              RadioButton radioButton = new RadioButton(displayNameProvider.apply(enumValue));
                                                              radioButton.setToggleGroup(toggleGroup);
                                                              radioButton.getStyleClass().add(styleClass);
                                                              radioButton.setUserData(enumValue);
                                                              return radioButton;
                                                          })
                                                          .toList();

        // Set the initial selection.
        for (RadioButton rb : radioButtons) {
            if (inputEnumProperty.isValue((E) rb.getUserData())) {
                rb.setSelected(true);
            }
        }

        // Update the property when the selection changes.
        toggleGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
            if (newToggle != null) {
                E selectedValue = (E) newToggle.getUserData();
                inputEnumProperty.setValue(selectedValue);
            }
        });

        // Update the selection when the property changes.
        inputEnumProperty.property().addListener((_, _, newVal) -> {
            for (RadioButton rb : radioButtons) {
                rb.setSelected(rb.getUserData() == newVal);
            }
        });

        radioButtonContainerPane.getChildren().addAll(radioButtons);

        Label label = new Label(labelFormatString);
        label.textProperty().bind(inputEnumProperty.asStringBinding(labelFormatString));
        label.setLabelFor(radioButtonContainerPane);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        radioButtons.forEach(rb -> rb.setTooltip(tooltipValue));

        return new LabeledControl<>(label, radioButtonContainerPane);
    }

    /**
     * Creates a labeled checkbox for a binary enum property, mapping checked and unchecked states to specific enum values.
     * <p>
     * The checkbox reflects the current value of the {@link InputEnumProperty} and updates the property when toggled.
     * The label displays the current value using the provided format string.
     * Both the label and the checkbox share the same tooltip for improved accessibility.
     * <p>
     * This method is suitable for enum properties with two valid states, allowing the user to toggle between them via a checkbox.
     *
     * @param inputEnumProperty the {@link InputEnumProperty} defining valid values and value binding
     * @param checkedEnumValue the enum value to set when the checkbox is checked
     * @param uncheckedEnumValue the enum value to set when the checkbox is unchecked
     * @param labelFormatString the format string for the label (e.g., "%s")
     * @param tooltip the tooltip text for both the label and the checkbox
     * @param styleClass the CSS style class to apply to the checkbox
     * @return a {@link FXComponentFactory.LabeledControl} containing the label and the checkbox
     */
    public static <E extends Enum<E>> LabeledControl<CheckBox> createLabeledEnumCheckBox(InputEnumProperty<E> inputEnumProperty,
                                                                                         E checkedEnumValue,
                                                                                         E uncheckedEnumValue,
                                                                                         String labelFormatString,
                                                                                         String tooltip,
                                                                                         String styleClass) {
        CheckBox checkBox = new CheckBox();
        if (inputEnumProperty.isValue(checkedEnumValue)) {
            checkBox.setSelected(true);
        } else {
            checkBox.setSelected(false);
            inputEnumProperty.setValue(uncheckedEnumValue);
        }
        checkBox.getStyleClass().add(styleClass);

        checkBox.selectedProperty().addListener((_, _, isSelected) -> {
            if (isSelected) {
                inputEnumProperty.setValue(checkedEnumValue);
            } else {
                inputEnumProperty.setValue(uncheckedEnumValue);
            }
        });
        inputEnumProperty.property().addListener((_, _, newVal) -> checkBox.setSelected(newVal == checkedEnumValue));

        Label label = new Label(labelFormatString);
        label.textProperty().bind(inputEnumProperty.asStringBinding(labelFormatString));
        label.setLabelFor(checkBox);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        checkBox.setTooltip(tooltipValue);

        return new LabeledControl<>(label, checkBox);
    }

    /**
     * Creates a labeled combo box for selecting enum values, with display names, formatted label, tooltip, and custom style class.
     * <p>
     * The combo box displays only valid enum values as provided by the {@link InputEnumProperty}.
     * Display names for each enum value are supplied via the {@code displayNameProvider} function, allowing for localization or custom labels.
     * The combo box and the property are kept in sync via listeners. The label displays the current value using the provided format string.
     * Both the label and the combo box share the same tooltip for improved accessibility.
     * <p>
     * This method is suitable for enum-based selections with custom display names.
     *
     * @param inputEnumProperty the {@link InputEnumProperty} defining valid values and value binding
     * @param displayNameProvider a function mapping enum values to their display names
     * @param labelFormatString the format string for the label (e.g., "%s")
     * @param tooltip the tooltip text for both the label and the combo box
     * @param styleClass the CSS style class to apply to the combo box
     * @return a {@link FXComponentFactory.LabeledControl} containing the label and the combo box
     */
    public static <E extends Enum<E>> LabeledControl<ComboBox<E>> createLabeledEnumComboBox(InputEnumProperty<E> inputEnumProperty,
                                                                                            Function<E, String> displayNameProvider,
                                                                                            String labelFormatString,
                                                                                            String tooltip,
                                                                                            String styleClass) {
        Map<E, String> displayNames = inputEnumProperty
                .getValidValues()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        displayNameProvider,
                        (v1, _) -> v1 // Merge function, in case the list contains duplicates.
                ));

        ComboBox<E> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(inputEnumProperty.getValidValues());
        comboBox.setValue(inputEnumProperty.getValue());
        comboBox.setCellFactory(_ -> createDisplayNameListCell(displayNames));
        comboBox.setButtonCell(createDisplayNameListCell(displayNames));
        comboBox.getStyleClass().add(styleClass);
        comboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> inputEnumProperty.setValue(newVal));

        Label label = new Label();
        label.textProperty().bind(inputEnumProperty.asStringBinding(labelFormatString));
        label.setLabelFor(comboBox);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        comboBox.setTooltip(tooltipValue);

        return new LabeledControl<>(label, comboBox);
    }

    /**
     * Creates a {@link ListCell} for displaying enum values using their display names.
     * <p>
     * The cell uses the provided map to look up display names for each enum value.
     * If a display name is not found, the enum's {@code toString()} value is used as a fallback.
     *
     * @param displayNames a map from enum values to their display names
     * @param <E> the enum type
     * @return a {@link ListCell} that displays the display name for each enum value
     */
    private static <E extends Enum<E>> ListCell<E> createDisplayNameListCell(Map<E, String> displayNames) {
        return new ListCell<>() {
            @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
            @Override
            protected void updateItem(@Nullable E item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || (item == null)) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(displayNames.getOrDefault(item, item.toString()));
                }
            }
        };
    }

    /**
     * Creates a labeled integer slider with a bound double value, formatted label, tooltip, and custom style class.
     * <p>
     * The slider is configured for integer values in the range defined by the given {@link InputDoublePropertyIntRange}.
     * The slider's value is bidirectionally bound to the underlying {@link DoubleProperty} of the record.
     * The label displays the current value using the provided format string. Both the label and the slider share
     * the same tooltip for improved accessibility.
     * <p>
     * This method is suitable for sliders with a moderate integer value range.
     *
     * @param inputDoublePropertyIntRange the {@link InputDoublePropertyIntRange} defining the integer range and value binding
     * @param labelFormatString the format string for the label (e.g., "%d px")
     * @param tooltip the tooltip text for both the label and the slider
     * @param styleClass the CSS style class to apply to the slider
     * @return a {@link FXComponentFactory.LabeledControl} containing the label and the slider
     */
    public static LabeledControl<Slider> createLabeledIntSlider(InputDoublePropertyIntRange inputDoublePropertyIntRange,
                                                                String labelFormatString,
                                                                String tooltip,
                                                                String styleClass) {

        Slider slider = new Slider(inputDoublePropertyIntRange.min(), inputDoublePropertyIntRange.max(),
                inputDoublePropertyIntRange.getValue());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(inputDoublePropertyIntRange.max() - inputDoublePropertyIntRange.min());
        slider.setMinorTickCount(inputDoublePropertyIntRange.max() - inputDoublePropertyIntRange.min() - 1);
        slider.setBlockIncrement(1.0d);
        slider.setSnapToTicks(true);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(inputDoublePropertyIntRange.property());

        Label label = new Label();
        label.textProperty().bind(inputDoublePropertyIntRange.asStringBinding(labelFormatString));
        label.setLabelFor(slider);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        slider.setTooltip(tooltipValue);

        return new LabeledControl<>(label, slider);
    }

    /**
     * Creates a labeled integer spinner with a bound value, formatted label, tooltip, and custom style class.
     * <p>
     * The spinner is configured for integer values in the range and step defined by the given {@link InputIntegerProperty}.
     * The spinner and the property are kept in sync via listeners and normalization logic. The label displays the current value using
     * the provided format string. Both the label and the spinner share the same tooltip for improved accessibility.
     * <p>
     * The spinner is editable, allowing direct user input, and uses the specified CSS style class.
     * Input is validated and normalized to ensure only valid values are accepted.
     *
     * @param inputIntegerProperty the {@link InputIntegerProperty} defining range, step, and value binding
     * @param labelFormatString the format string for the label (e.g., "Width: %d")
     * @param tooltip the tooltip text for both the label and the spinner
     * @param styleClass the CSS style class to apply to the spinner
     * @return a {@link FXComponentFactory.LabeledControl} containing the label and the spinner
     */
    public static LabeledControl<Spinner<Integer>> createLabeledIntSpinner(
            InputIntegerProperty inputIntegerProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {

        // Create the spinner with min, max, initial value, and step.
        Spinner<Integer> spinner = new Spinner<>(
                inputIntegerProperty.min(),
                inputIntegerProperty.max(),
                inputIntegerProperty.getValue(),
                inputIntegerProperty.step());
        spinner.setEditable(true);
        spinner.getStyleClass().add(styleClass);

        // Only allow integer input in the editor.
        TextFormatter<Integer> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*")) {
                return change;
            }
            return null;
        });
        spinner.getEditor().setTextFormatter(formatter);

        // Keep the spinner in sync with property changes.
        inputIntegerProperty.asObjectProperty().addListener((_, _, newVal) -> {
            if (!spinner.getValueFactory().getValue().equals(newVal)) {
                spinner.getValueFactory().setValue(newVal);
            }
        });

        // Normalize and sync on spinner value change.
        spinner.valueProperty().addListener((_, _, _) -> normalizeAndSyncSpinnerValue(spinner, inputIntegerProperty));

        // Normalize and sync when the editor loses focus.
        spinner.getEditor().focusedProperty().addListener((_, _, isNowFocused) -> {
            if (!isNowFocused) {
                normalizeAndSyncSpinnerValue(spinner, inputIntegerProperty);
            }
        });

        // Normalize and sync on Enter key in the editor.
        spinner.getEditor().setOnAction(_ -> normalizeAndSyncSpinnerValue(spinner, inputIntegerProperty));

        // Set up the label.
        Label label = new Label();
        label.textProperty().bind(inputIntegerProperty.asStringBinding(labelFormatString));
        label.setLabelFor(spinner);

        // Tooltip for both the label and the spinner.
        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        spinner.setTooltip(tooltipValue);

        return new LabeledControl<>(label, spinner);
    }

    /**
     * Normalizes and synchronizes the value of a {@link Spinner}'s editor with its value factory and the associated {@link InputIntegerProperty}.
     * <p>
     * Attempts to parse the editor text as an integer, clamps and snaps it to the valid range and step
     * defined by the given {@link InputIntegerProperty}, and updates both the spinner and the property with the normalized value.
     * If parsing fails, resets the editor text and spinner value to the current property value.
     *
     * @param spinner the {@link Spinner} whose editor input is to be normalized and synchronized
     * @param inputIntegerProperty the {@link InputIntegerProperty} providing range and step constraints
     */
    private static void normalizeAndSyncSpinnerValue(Spinner<Integer> spinner,
                                                     InputIntegerProperty inputIntegerProperty) {
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            String text = spinner.getEditor().getText();
            try {
                int parsedValue = Integer.parseInt(text);
                int normalizedValue = inputIntegerProperty.adjustValue(parsedValue);

                // 1. Update the editor text if normalization changed the value.
                if (parsedValue != normalizedValue) {
                    spinner.getEditor().setText(String.valueOf(normalizedValue));
                }

                // 2. Update the property if needed.
                if (inputIntegerProperty.getValue() != normalizedValue) {
                    inputIntegerProperty.setValue(normalizedValue);
                }

                // 3. Update the spinner if needed.
                if (!valueFactory.getValue().equals(normalizedValue)) {
                    valueFactory.setValue(normalizedValue);
                }
            } catch (NumberFormatException e) {
                // Reset to the current property value if parsing fails.
                int currentValue = inputIntegerProperty.getValue();
                spinner.getEditor().setText(String.valueOf(currentValue));
                valueFactory.setValue(currentValue);
            }
        }
    }

    /**
     * Creates a labeled percent slider with a bound value, formatted label, tooltip, and custom style class.
     * <p>
     * The slider is configured for percent values in the range defined by the given {@link InputDoubleProperty}.
     * The slider's value is bidirectionally bound to the property. The label displays the current value using
     * the provided format string. Both the label and the slider share the same tooltip for improved accessibility.
     * <p>
     * The slider is suitable for values between 0.0 and 1.0 (inclusive).
     *
     * @param inputDoubleProperty the {@link InputDoubleProperty} defining range and value binding
     * @param labelFormatString the format string for the label (e.g., "%.2f %%")
     * @param tooltip the tooltip text for both the label and the slider
     * @param styleClass the CSS style class to apply to the slider
     * @return a {@link FXComponentFactory.LabeledControl} containing the label and the slider
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static LabeledControl<Slider> createLabeledPercentSlider(
            InputDoubleProperty inputDoubleProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        double min = inputDoubleProperty.min();
        double max = inputDoubleProperty.max();

        double minRounded = Math.max(0.0d, Math.round(min * 100.0d) / 100.0d);
        double maxRounded = Math.min(1.0d, Math.round(max * 100.0d) / 100.0d);
        double valueRounded = Math.round(inputDoubleProperty.getValue() * 100.0d) / 100.0d;
        double majorTickUnit = (maxRounded - minRounded);
        int range = ((int) (maxRounded * 100)) - ((int) (minRounded * 100));
        int minorTickCount = computeMinorTickCountForPercentSlider(range);

        double value = Math.max(minRounded, Math.min(maxRounded, valueRounded));
        inputDoubleProperty.setValue(value);

        Slider slider = new Slider(minRounded, maxRounded, value);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(majorTickUnit);
        slider.setMinorTickCount(minorTickCount);
        slider.setBlockIncrement(0.01d);
        slider.setSnapToTicks(false);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(inputDoubleProperty.property());
        slider.valueProperty().addListener((_, _, newValue) -> {
            double rounded = Math.round(newValue.doubleValue() * 100.0d) / 100.0d;
            slider.setValue(rounded);
        });

        Label label = new Label();
        label.textProperty().bind(inputDoubleProperty.property().multiply(100).asString(labelFormatString));
        label.setLabelFor(slider);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        slider.setTooltip(tooltipValue);

        return new LabeledControl<>(label, slider);
    }

    /**
     * Computes the number of minor tick marks for a percent slider based on the given range.
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
    private static int computeMinorTickCountForPercentSlider(int range) {
        // Ensure the range is within the valid interval [2, 100].
        if ((range >= 2) && (range <= 100)) {
            // Array of divisors to check for divisibility.
            int[] divisors = {10, 8, 6, 4, 9, 7, 5, 11, 13, 17, 19, 3, 2};
            for (int divisor : divisors) {
                // If the range is divisible by the current divisor, return divisor - 1.
                if ((range % divisor) == 0) {
                    return divisor - 1;
                }
            }
        }
        // Return 0 if no divisor matches or the range is out of bounds.
        return 0;
    }

    /**
     * Creates a labeled text box with a clear button, formatted label, prompt text, tooltips, and custom style class.
     * <p>
     * The text field is bidirectionally bound to the given {@link StringProperty}. The label displays the current value
     * using the provided format string and is bound to the given label property. A clear button allows users to quickly
     * clear the text field and includes its own tooltip. Both the label and the text field share the same tooltip for
     * accessibility. The entire control is wrapped in an {@link HBox} for layout.
     *
     * @param inputProperty the {@link StringProperty} to bind to the text field
     * @param labelProperty the {@link StringProperty} to bind to the label
     * @param labelFormatString the format string for the label (e.g., "Name: %s")
     * @param promptText the prompt text to display in the text field
     * @param tooltip the tooltip text for both the label and the text field
     * @param tooltipButton the tooltip text for the clear button
     * @param styleClass the CSS style class to apply to the text field and container
     * @return a {@link LabeledControl} containing the label and the HBox with the text field and clear button
     */
    public static LabeledControl<HBox> createLabeledStringTextBox(StringProperty inputProperty,
                                                                  StringProperty labelProperty,
                                                                  String labelFormatString,
                                                                  String promptText,
                                                                  String tooltip,
                                                                  String tooltipButton,
                                                                  String styleClass) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(inputProperty);
        textField.getStyleClass().add(styleClass);
        textField.setPromptText(promptText);

        // Add a clear button for the text field.
        Button clearButton = new Button("✕");
        clearButton.setFocusTraversable(false);
        clearButton.setOnAction(_ -> textField.clear());
        clearButton.setTooltip(new Tooltip(tooltipButton));

        HBox hBox = new HBox(textField, clearButton);
        hBox.getStyleClass().add(styleClass);

        Label label = new Label(labelFormatString);
        label.textProperty().bind(Bindings.format(labelFormatString, labelProperty));
        label.setLabelFor(textField);

        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);
        textField.setTooltip(tooltipValue);

        return new LabeledControl<>(label, hBox);
    }

    /**
     * Bundles a {@link Label} and a {@link Region} for use in UIs.
     * <p>
     * The {@code controlRegion} can be either a direct {@link Control} (e.g., {@link Slider}, {@link Spinner})
     * or a {@link Region} that itself contains one or more controls (e.g., {@link HBox}, {@link VBox}).
     * This record is typically used to return both the label and the associated region as a pair.
     *
     * @param label         the label describing the control or region
     * @param controlRegion the UI region (control or container) associated with the label
     */
    public record LabeledControl<R extends Region>(Label label, R controlRegion) {}

}
