package de.mkalb.etpetssim.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Creates styled JavaFX controls and containers, and labeled controls bound to input properties.
 *
 * <p>A labeled control consists of a label that is bound to the value of the property, the control itself, and a
 * tooltip that the label and the control share.
 */
public final class FXComponentFactory {

    private static final String BUTTON_TEXT_ADOPT = "↩";
    private static final String BUTTON_TEXT_CLEAR = "✕";

    private FXComponentFactory() {
    }

    public static Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    public static HBox createHBox(String styleClass) {
        HBox hbox = new HBox();
        hbox.getStyleClass().add(styleClass);
        return hbox;
    }

    public static VBox createVBox(String styleClass) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add(styleClass);
        return vbox;
    }

    /**
     * Creates a labeled group of radio buttons for selecting values from a fixed choice set, with display names,
     * formatted label, tooltip, and custom style class.
     *
     * <p>Each radio button represents a valid value from the provided InputChoiceProperty and shows the display name
     * supplied by the property's display name provider. All radio buttons are added to the given container pane,
     * which can be any Pane (e.g., VBox, HBox) for flexible layout. The radio buttons are grouped using a ToggleGroup,
     * ensuring only one can be selected at a time. Selection changes are synchronized with the property, and vice
     * versa. The label displays the current value using the provided format string.
     * Both the label and the radio buttons share the same tooltip for improved accessibility.
     *
     * @param inputChoiceProperty      the InputChoiceProperty defining valid values, display names, and value binding
     * @param radioButtonContainerPane the Pane to contain the radio buttons (e.g., VBox, HBox)
     * @param labelFormatString        the format string for the label (e.g., "%s")
     * @param tooltip                  the tooltip text for both the label and the radio buttons
     * @param styleClass               the CSS style class to apply to the radio buttons
     * @param <T>                      the choice value type
     * @param <P>                      the pane type
     * @return a LabeledControl containing the label and the container pane with radio buttons
     */
    @SuppressWarnings("unchecked")
    public static <T, P extends Pane> LabeledControl<P> createLabeledChoiceRadioButtons(
            InputChoiceProperty<T> inputChoiceProperty,
            P radioButtonContainerPane,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        ToggleGroup toggleGroup = new ToggleGroup();

        List<RadioButton> radioButtons = inputChoiceProperty.validValues()
                .stream()
                .map(value -> {
                    RadioButton radioButton = new RadioButton(inputChoiceProperty.displayNameProvider().apply(value));
                    radioButton.setToggleGroup(toggleGroup);
                    radioButton.getStyleClass().add(styleClass);
                    radioButton.setUserData(value);
                    return radioButton;
                })
                .toList();

        for (RadioButton rb : radioButtons) {
            if (inputChoiceProperty.isValue((T) rb.getUserData())) {
                rb.setSelected(true);
            }
        }

        toggleGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
            if (newToggle != null) {
                T selectedValue = (T) newToggle.getUserData();
                inputChoiceProperty.setValue(selectedValue);
            }
        });

        inputChoiceProperty.property().addListener((_, _, newVal) -> {
            for (RadioButton rb : radioButtons) {
                rb.setSelected(Objects.equals(rb.getUserData(), newVal));
            }
        });

        radioButtonContainerPane.getChildren().addAll(radioButtons);

        Label label = createBoundLabel(inputChoiceProperty.asStringBinding(labelFormatString), radioButtonContainerPane,
                tooltip, radioButtons.toArray(Control[]::new));
        return new LabeledControl<>(label, radioButtonContainerPane);
    }

    /**
     * Creates a labeled checkbox for a choice property with two valid values, mapping checked and unchecked states to
     * specific values.
     *
     * <p>The checkbox reflects the current value of the InputChoiceProperty and updates the property when toggled.
     * The label displays the current value using the provided format string.
     * Both the label and the checkbox share the same tooltip for improved accessibility.
     *
     * <p>This method is suitable for choice properties with exactly two valid values.
     * It validates the mapping values fail-fast and does not modify the property during initialization.
     *
     * @param inputChoiceProperty the InputChoiceProperty defining valid values and value binding
     * @param checkedValue        the value to set when the checkbox is checked; must be part of valid values
     * @param uncheckedValue      the value to set when the checkbox is unchecked; must be part of valid values
     * @param labelFormatString   the format string for the label (e.g., "%s")
     * @param tooltip             the tooltip text for both the label and the checkbox
     * @param styleClass          the CSS style class to apply to the checkbox
     * @param <T>                 the choice value type
     * @return a LabeledControl containing the label and the checkbox
     * @throws IllegalArgumentException if the property does not define exactly two valid values,
     *                                  if either mapping value is not valid, or if both mapping values are equal
     */
    public static <T> LabeledControl<CheckBox> createLabeledChoiceCheckBox(
            InputChoiceProperty<T> inputChoiceProperty,
            T checkedValue,
            T uncheckedValue,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        List<T> validValues = inputChoiceProperty.validValues();
        if (validValues.size() != 2) {
            throw new IllegalArgumentException(
                    "InputChoiceProperty must define exactly 2 valid values for checkbox usage.");
        }
        if (!validValues.contains(checkedValue)) {
            throw new IllegalArgumentException("checkedValue must be part of valid values: " + checkedValue);
        }
        if (!validValues.contains(uncheckedValue)) {
            throw new IllegalArgumentException("uncheckedValue must be part of valid values: " + uncheckedValue);
        }
        if (Objects.equals(checkedValue, uncheckedValue)) {
            throw new IllegalArgumentException("checkedValue and uncheckedValue must be different.");
        }

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(inputChoiceProperty.isValue(checkedValue));
        checkBox.getStyleClass().add(styleClass);

        checkBox.selectedProperty().addListener((_, _, isSelected) -> {
            if (isSelected) {
                inputChoiceProperty.setValue(checkedValue);
            } else {
                inputChoiceProperty.setValue(uncheckedValue);
            }
        });
        inputChoiceProperty.property()
                .addListener((_, _, newVal) -> checkBox.setSelected(Objects.equals(newVal, checkedValue)));

        Label label = createBoundLabel(inputChoiceProperty.asStringBinding(labelFormatString), checkBox, tooltip,
                checkBox);
        return new LabeledControl<>(label, checkBox);
    }

    /**
     * Creates a labeled combo box for selecting values from a fixed choice set, with display names,
     * formatted label, tooltip, and custom style class.
     *
     * <p>The combo box displays only valid values as provided by the InputChoiceProperty.
     * Display names for each value are supplied by the property's display name provider.
     * Selection changes are synchronized with the property, and vice versa.
     * Null selections from the combo box are ignored.
     * The label displays the current value using the provided format string.
     * Both the label and the combo box share the same tooltip for improved accessibility.
     *
     * @param inputChoiceProperty the InputChoiceProperty defining valid values, display names, and value binding
     * @param labelFormatString   the format string for the label (e.g., "%s")
     * @param tooltip             the tooltip text for both the label and the combo box
     * @param styleClass          the CSS style class to apply to the combo box
     * @param <T>                 the choice value type
     * @return a LabeledControl containing the label and the combo box
     */
    public static <T> LabeledControl<ComboBox<T>> createLabeledChoiceComboBox(
            InputChoiceProperty<T> inputChoiceProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        return createLabeledChoiceComboBox(inputChoiceProperty, labelFormatString, tooltip, styleClass, _ -> {});
    }

    /**
     * Creates a labeled combo box for selecting values from a fixed choice set and registers cleanup for listeners and
     * bindings created by this factory method.
     *
     * @param inputChoiceProperty the InputChoiceProperty defining valid values, display names, and value binding
     * @param labelFormatString   the format string for the label (e.g., "%s")
     * @param tooltip             the tooltip text for both the label and the combo box
     * @param styleClass          the CSS style class to apply to the combo box
     * @param cleanupRegistrar    accepts a cleanup action that removes listeners and bindings created by this method
     * @param <T>                 the choice value type
     * @return a LabeledControl containing the label and the combo box
     */
    @SuppressWarnings("ConstantValue")
    public static <T> LabeledControl<ComboBox<T>> createLabeledChoiceComboBox(
            InputChoiceProperty<T> inputChoiceProperty,
            String labelFormatString,
            String tooltip,
            String styleClass,
            Consumer<Runnable> cleanupRegistrar) {
        Map<T, String> displayNames = inputChoiceProperty
                .validValues()
                .stream()
                .collect(Collectors.toMap(Function.identity(), inputChoiceProperty.displayNameProvider()));

        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(inputChoiceProperty.validValues());
        comboBox.setValue(inputChoiceProperty.getValue());
        comboBox.setCellFactory(_ -> createDisplayNameListCell(displayNames));
        comboBox.setButtonCell(createDisplayNameListCell(displayNames));
        comboBox.getStyleClass().add(styleClass);

        ChangeListener<T> comboBoxSelectionListener = (_, _, newVal) -> {
            if (newVal != null) {
                inputChoiceProperty.setValue(newVal);
            }
        };
        comboBox.getSelectionModel().selectedItemProperty().addListener(comboBoxSelectionListener);

        ChangeListener<T> inputChoicePropertyListener = (_, _, newVal) -> {
            if (newVal != null) {
                comboBox.setValue(newVal);
            }
        };
        inputChoiceProperty.property().addListener(inputChoicePropertyListener);

        var labelTextBinding = inputChoiceProperty.asStringBinding(labelFormatString);
        Label label = createBoundLabel(labelTextBinding, comboBox, tooltip, comboBox);

        cleanupRegistrar.accept(() -> {
            comboBox.getSelectionModel().selectedItemProperty().removeListener(comboBoxSelectionListener);
            inputChoiceProperty.property().removeListener(inputChoicePropertyListener);
            label.textProperty().unbind();
            labelTextBinding.dispose();
        });

        return new LabeledControl<>(label, comboBox);
    }

    /**
     * Creates a labeled integer slider with a bound double value, formatted label, tooltip, and custom style class.
     *
     * <p>The slider is configured for integer values in the range defined by the given InputDoubleIntRangeProperty.
     * The slider's value is bidirectionally bound to the underlying DoubleProperty of the record.
     * The label displays the current value using the provided format string. Both the label and the slider share
     * the same tooltip for improved accessibility.
     *
     * <p>This method is suitable for sliders with a moderate integer value range.
     *
     * @param inputDoubleIntRangeProperty the InputDoubleIntRangeProperty defining the integer range and value binding
     * @param labelFormatString           the format string for the label (e.g., "%d px")
     * @param tooltip                     the tooltip text for both the label and the slider
     * @param styleClass                  the CSS style class to apply to the slider
     * @return a LabeledControl containing the label and the slider
     */
    @SuppressWarnings("ExtractMethodRecommender")
    public static LabeledControl<Slider> createLabeledIntSlider(
            InputDoubleIntRangeProperty inputDoubleIntRangeProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {

        Slider slider = new Slider(inputDoubleIntRangeProperty.min(), inputDoubleIntRangeProperty.max(),
                inputDoubleIntRangeProperty.getValue());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(inputDoubleIntRangeProperty.max() - inputDoubleIntRangeProperty.min());
        slider.setMinorTickCount(inputDoubleIntRangeProperty.max() - inputDoubleIntRangeProperty.min() - 1);
        slider.setBlockIncrement(1.0d);
        slider.setSnapToTicks(true);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(inputDoubleIntRangeProperty.property());

        Label label = createBoundLabel(inputDoubleIntRangeProperty.asStringBinding(labelFormatString), slider, tooltip,
                slider);
        return new LabeledControl<>(label, slider);
    }

    /**
     * Creates a labeled integer spinner with a bound value, formatted label, tooltip, and custom style class.
     *
     * <p>The spinner is configured for integer values in the range and step defined by the given InputIntegerProperty.
     * The spinner and the property are kept in sync via listeners and normalization logic. The label displays the
     * current value using the provided format string. Both the label and the spinner share the same tooltip for
     * improved accessibility.
     *
     * <p>The spinner is editable, allowing direct user input, and uses the specified CSS style class.
     * Input is validated and normalized to ensure only valid values are accepted.
     *
     * <p>This method is suitable for spinners with integer values and step-based constraints.
     *
     * @param inputIntegerProperty the InputIntegerProperty defining range, step, and value binding
     * @param labelFormatString    the format string for the label (e.g., "Width: %d")
     * @param tooltip              the tooltip text for both the label and the spinner
     * @param styleClass           the CSS style class to apply to the spinner
     * @return a LabeledControl containing the label and the spinner
     */
    public static LabeledControl<Spinner<Integer>> createLabeledIntSpinner(
            InputIntegerProperty inputIntegerProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {

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

        inputIntegerProperty.asObjectProperty().addListener((_, _, newVal) -> {
            if (!spinner.getValueFactory().getValue().equals(newVal)) {
                spinner.getValueFactory().setValue(newVal);
            }
        });

        spinner.valueProperty().addListener((_, _, _) -> normalizeAndSyncSpinnerValue(spinner, inputIntegerProperty));
        spinner.getEditor().focusedProperty().addListener((_, _, isNowFocused) -> {
            if (!isNowFocused) {
                normalizeAndSyncSpinnerValue(spinner, inputIntegerProperty);
            }
        });
        // The editor fires its action on the Enter key.
        spinner.getEditor().setOnAction(_ -> normalizeAndSyncSpinnerValue(spinner, inputIntegerProperty));

        Label label = createBoundLabel(inputIntegerProperty.asStringBinding(labelFormatString), spinner, tooltip,
                spinner);
        return new LabeledControl<>(label, spinner);
    }

    /**
     * Creates a labeled percent slider with a bound value, formatted label, tooltip, and custom style class.
     *
     * <p>The slider is configured for percent values in the range defined by the given InputDoubleProperty.
     * The slider's value is bidirectionally bound to the property. The label displays the current value using
     * the provided format string. Both the label and the slider share the same tooltip for improved accessibility.
     *
     * <p>This method is suitable for percent values between 0.0 and 1.0 (inclusive).
     * The range is limited to 0.0..1.0 and rounded to two decimal places. As a side effect, the current
     * value of the property is rounded and clamped to this range.
     *
     * @param inputDoubleProperty the InputDoubleProperty defining range and value binding
     * @param labelFormatString   the format string for the label (e.g., "%.2f %%")
     * @param tooltip             the tooltip text for both the label and the slider
     * @param styleClass          the CSS style class to apply to the slider
     * @return a LabeledControl containing the label and the slider
     */
    @SuppressWarnings("MagicNumber")
    public static LabeledControl<Slider> createLabeledPercentSlider(
            InputDoubleProperty inputDoubleProperty,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        double minRounded = Math.max(0.0d, Math.round(inputDoubleProperty.min() * 100.0d) / 100.0d);
        double maxRounded = Math.min(1.0d, Math.round(inputDoubleProperty.max() * 100.0d) / 100.0d);
        int range = Math.toIntExact(Math.round(maxRounded * 100.0d) - Math.round(minRounded * 100.0d));

        Slider slider = createRoundingSlider(inputDoubleProperty, minRounded, maxRounded, 100.0d,
                computeMinorTickCountForPercentSlider(range), styleClass);

        Label label = createBoundLabel(inputDoubleProperty.property().multiply(100).asString(labelFormatString), slider,
                tooltip, slider);
        return new LabeledControl<>(label, slider);
    }

    /**
     * Creates a labeled double slider with configurable decimal precision, formatted label, tooltip, and custom style
     * class.
     *
     * <p>The slider is configured for double values in the range defined by the given InputDoubleProperty.
     * The decimal places parameter is clamped to the range [0, 9] to avoid excessively large precision and
     * non-finite scaling factors. The slider's value is rounded to the specified number of decimal places and is
     * bidirectionally bound to the property. The label displays the current value using the provided format string.
     * Both the label and the slider share the same tooltip for improved accessibility.
     *
     * <p>This method is suitable for sliders requiring decimal precision and a continuous value range.
     * As a side effect, the current value of the property is rounded and clamped to the property range.
     *
     * @param inputDoubleProperty the InputDoubleProperty defining range and value binding
     * @param decimalPlaces       the requested number of decimal places to round the slider's value to; will be
     *                            clamped to the range 0..9
     * @param labelFormatString   the format string for the label (e.g., "%.2f")
     * @param tooltip             the tooltip text for both the label and the slider
     * @param styleClass          the CSS style class to apply to the slider
     * @return a LabeledControl containing the label and the slider
     */
    @SuppressWarnings("MagicNumber")
    public static LabeledControl<Slider> createLabeledDoubleSlider(
            InputDoubleProperty inputDoubleProperty,
            int decimalPlaces,
            String labelFormatString,
            String tooltip,
            String styleClass) {
        double factor = Math.pow(10.0d, Math.clamp(decimalPlaces, 0, 9));
        Slider slider = createRoundingSlider(inputDoubleProperty, inputDoubleProperty.min(), inputDoubleProperty.max(),
                factor, 0, styleClass);

        Label label = createBoundLabel(inputDoubleProperty.asStringBinding(labelFormatString), slider, tooltip, slider);
        return new LabeledControl<>(label, slider);
    }

    /**
     * Creates a labeled text box with a clear button, formatted label, prompt text, tooltips, and custom style class.
     *
     * <p>The text field is bidirectionally bound to the given StringProperty. The label displays the current value
     * using the provided format string and is bound to the given label property. A clear button allows users to quickly
     * clear the text field and includes its own tooltip. Both the label and the text field share the same tooltip for
     * improved accessibility. The entire control is wrapped in an HBox for layout.
     *
     * <p>This method is suitable for string input fields with a clear button and formatted label.
     *
     * @param inputProperty      the StringProperty to bind to the text field
     * @param labelProperty      the StringProperty to bind to the label
     * @param labelFormatString  the format string for the label (e.g., "Name: %s")
     * @param promptText         the prompt text to display in the text field
     * @param tooltip            the tooltip text for both the label and the text field
     * @param tooltipClearButton the tooltip text for the clear button
     * @param styleClass         the CSS style class to apply to the text field and container
     * @return a LabeledControl containing the label and the HBox with the text field and clear button
     */
    public static LabeledControl<HBox> createLabeledStringTextBox(
            StringProperty inputProperty,
            StringProperty labelProperty,
            String labelFormatString,
            String promptText,
            String tooltip,
            String tooltipClearButton,
            String styleClass) {
        TextField textField = createBoundTextField(inputProperty, promptText, styleClass);
        Button clearButton = createTextFieldButton(BUTTON_TEXT_CLEAR, tooltipClearButton, _ -> textField.clear());

        HBox hBox = new HBox(textField, clearButton);
        hBox.getStyleClass().add(styleClass);

        Label label = createBoundLabel(Bindings.format(labelFormatString, labelProperty), textField, tooltip,
                textField);
        return new LabeledControl<>(label, hBox);
    }

    /**
     * Creates a labeled text box with an adopt button and a clear button, formatted label, prompt text, tooltips, and
     * custom style class.
     *
     * <p>The text field is bidirectionally bound to the given StringProperty. The label displays the current value
     * using the provided format string and is bound to the given label property. An adopt button allows users to
     * transfer the value from the read-only adopt value property into the text field; it is disabled when
     * the adopt value property is empty. A clear button allows users to quickly clear the text field.
     * Both the label and the text field share the same tooltip for improved accessibility.
     * The entire control is wrapped in an HBox for layout.
     *
     * <p>This method is suitable for string input fields with adopt and clear buttons and formatted label.
     *
     * @param inputProperty      the StringProperty to bind to the text field
     * @param labelProperty      the StringProperty to bind to the label
     * @param adoptValueProperty the ReadOnlyStringProperty providing the value to adopt into the text field
     * @param labelFormatString  the format string for the label (e.g., "Name: %s")
     * @param promptText         the prompt text to display in the text field
     * @param tooltip            the tooltip text for both the label and the text field
     * @param tooltipAdoptButton the tooltip text for the adopt button
     * @param tooltipClearButton the tooltip text for the clear button
     * @param styleClass         the CSS style class to apply to the text field and container
     * @return a LabeledControl containing the label and the HBox with the text field, adopt button, and clear button
     */
    public static LabeledControl<HBox> createLabeledStringTextBoxWithAdopt(
            StringProperty inputProperty,
            StringProperty labelProperty,
            ReadOnlyStringProperty adoptValueProperty,
            String labelFormatString,
            String promptText,
            String tooltip,
            String tooltipAdoptButton,
            String tooltipClearButton,
            String styleClass) {
        TextField textField = createBoundTextField(inputProperty, promptText, styleClass);
        Button adoptButton = createTextFieldButton(BUTTON_TEXT_ADOPT, tooltipAdoptButton,
                _ -> textField.setText(adoptValueProperty.get()));
        adoptButton.disableProperty().bind(adoptValueProperty.isEmpty());
        Button clearButton = createTextFieldButton(BUTTON_TEXT_CLEAR, tooltipClearButton, _ -> textField.clear());

        HBox hBox = new HBox(textField, adoptButton, clearButton);
        hBox.getStyleClass().add(styleClass);

        Label label = createBoundLabel(Bindings.format(labelFormatString, labelProperty), textField, tooltip,
                textField);
        return new LabeledControl<>(label, hBox);
    }

    /**
     * Creates a ListCell for displaying values using their display names.
     *
     * <p>The cell uses the provided map to look up display names for each value.
     * If a display name is not found, the value's toString() value is used as a fallback.
     *
     * @param displayNames a map from values to their display names
     * @param <T>          the value type
     * @return a ListCell that displays the display name for each value
     */
    private static <T> ListCell<T> createDisplayNameListCell(Map<T, String> displayNames) {
        return new ListCell<>() {
            @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
            @Override
            protected void updateItem(@Nullable T item, boolean empty) {
                // Follow the JavaFX Cell#updateItem(...) contract: call super first and clear the cell for empty or
                // null items.
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
     * Normalizes and synchronizes the value of a Spinner's editor with its value factory and the associated
     * InputIntegerProperty.
     *
     * <p>Attempts to parse the editor text as an integer, clamps and snaps it to the valid range and step
     * defined by the given InputIntegerProperty, and updates both the spinner and the property with the normalized
     * value. If parsing fails, resets the editor text and spinner value to the current property value.
     *
     * @param spinner              the Spinner whose editor input is to be normalized and synchronized
     * @param inputIntegerProperty the InputIntegerProperty providing range and step constraints
     */
    private static void normalizeAndSyncSpinnerValue(
            Spinner<Integer> spinner,
            InputIntegerProperty inputIntegerProperty) {
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            String text = spinner.getEditor().getText();
            try {
                int parsedValue = Integer.parseInt(text);
                int normalizedValue = inputIntegerProperty.adjustValue(parsedValue);
                if (parsedValue != normalizedValue) {
                    spinner.getEditor().setText(String.valueOf(normalizedValue));
                }
                if (inputIntegerProperty.getValue() != normalizedValue) {
                    inputIntegerProperty.setValue(normalizedValue);
                }
                if (!valueFactory.getValue().equals(normalizedValue)) {
                    valueFactory.setValue(normalizedValue);
                }
            } catch (NumberFormatException e) {
                int currentValue = inputIntegerProperty.getValue();
                spinner.getEditor().setText(String.valueOf(currentValue));
                valueFactory.setValue(currentValue);
            }
        }
    }

    /**
     * Computes the number of minor tick marks for a percent slider based on the given range.
     *
     * <p>Determines the number of minor ticks by checking if the range is divisible
     * by specific divisors. If a divisor matches, the method returns the divisor minus one.
     * If no divisor matches, it returns 0.
     *
     * <p>The range is expected to be between 2 and 100 (inclusive). If the range is outside this
     * interval, the method will return 0 without performing any calculations.
     *
     * @param range the range of the slider (difference between max and min values multiplied by 100)
     * @return the number of minor ticks (divisor - 1) if a matching divisor is found, or 0 otherwise
     */
    @SuppressWarnings("MagicNumber")
    private static int computeMinorTickCountForPercentSlider(int range) {
        if ((range >= 2) && (range <= 100)) {
            int[] divisors = {10, 8, 6, 4, 9, 7, 5, 11, 13, 17, 19, 3, 2};
            for (int divisor : divisors) {
                if ((range % divisor) == 0) {
                    return divisor - 1;
                }
            }
        }
        return 0;
    }

    /**
     * Creates a slider whose value is rounded to {@code 1 / factor} and bidirectionally bound to the property.
     *
     * <p>As a side effect, the current value of the property is rounded and clamped to the range.
     *
     * @param inputDoubleProperty the property to bind to the slider
     * @param min                 the minimum value of the slider
     * @param max                 the maximum value of the slider
     * @param factor              the rounding factor, a power of ten (e.g., {@code 100.0} for two decimal places)
     * @param minorTickCount      the number of minor ticks between the major ticks at {@code min} and {@code max}
     * @param styleClass          the CSS style class to apply to the slider
     * @return the configured slider
     */
    private static Slider createRoundingSlider(InputDoubleProperty inputDoubleProperty, double min, double max,
                                               double factor, int minorTickCount, String styleClass) {
        double value = Math.clamp(Math.round(inputDoubleProperty.getValue() * factor) / factor, min, max);
        inputDoubleProperty.setValue(value);

        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(max - min);
        slider.setMinorTickCount(minorTickCount);
        slider.setBlockIncrement(1.0d / factor);
        slider.setSnapToTicks(false);
        slider.getStyleClass().add(styleClass);
        slider.valueProperty().bindBidirectional(inputDoubleProperty.property());
        slider.valueProperty().addListener((_, _, newValue) ->
                slider.setValue(Math.round(newValue.doubleValue() * factor) / factor));
        return slider;
    }

    private static Label createBoundLabel(ObservableValue<String> text, Node labelFor, String tooltip,
                                          Control... tooltipControls) {
        Label label = new Label();
        label.textProperty().bind(text);
        label.setLabelFor(labelFor);

        Tooltip sharedTooltip = new Tooltip(tooltip);
        label.setTooltip(sharedTooltip);
        for (Control control : tooltipControls) {
            control.setTooltip(sharedTooltip);
        }
        return label;
    }

    private static TextField createBoundTextField(StringProperty inputProperty, String promptText, String styleClass) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(inputProperty);
        textField.getStyleClass().add(styleClass);
        textField.setPromptText(promptText);
        return textField;
    }

    private static Button createTextFieldButton(String text, String tooltip, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        button.setOnAction(action);
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }

    /**
     * Bundles a Label and a Region for use in UIs.
     *
     * <p>The control region can be either a direct Control (e.g., Slider, Spinner)
     * or a Region that itself contains one or more controls (e.g., HBox, VBox).
     * This record is typically used to return both the label and the associated region as a pair.
     *
     * <p>The record groups the label and the control region without copying them.
     *
     * @param <R>           the type of the control region
     * @param label         the label describing the control or region
     * @param controlRegion the UI region (control or container) associated with the label
     */
    public record LabeledControl<R extends Region>(Label label, R controlRegion) {
    }

}
