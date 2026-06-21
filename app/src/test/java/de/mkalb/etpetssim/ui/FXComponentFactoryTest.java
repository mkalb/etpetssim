package de.mkalb.etpetssim.ui;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("MagicNumber")
final class FXComponentFactoryTest {

    private static final String STYLE_CLASS = "test-style";
    private static final String TOOLTIP = "tooltip";

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        FxTestSupport.ensureStarted();
    }

    @Test
    void testCreateLabelHBoxAndVBoxApplyTextAndStyleClass() {
        FxTestSupport.runAndWait(() -> {
            Label label = FXComponentFactory.createLabel("Label", STYLE_CLASS);
            HBox hBox = FXComponentFactory.createHBox(STYLE_CLASS);
            VBox vBox = FXComponentFactory.createVBox(STYLE_CLASS);

            assertAll(
                    () -> assertEquals("Label", label.getText()),
                    () -> assertTrue(label.getStyleClass().contains(STYLE_CLASS)),
                    () -> assertTrue(hBox.getStyleClass().contains(STYLE_CLASS)),
                    () -> assertTrue(vBox.getStyleClass().contains(STYLE_CLASS))
            );
        });
    }

    @Test
    void testCreateLabeledEnumRadioButtonsSynchronizesSelectionAndProperty() {
        FxTestSupport.runAndWait(() -> {
            InputEnumProperty<TestMode> property = InputEnumProperty.of(
                    TestMode.ALPHA,
                    TestMode.class,
                    TestMode::name);
            VBox container = new VBox();

            FXComponentFactory.LabeledControl<VBox> control = FXComponentFactory.createLabeledEnumRadioButtons(
                    property,
                    mode -> "display-" + mode.name(),
                    container,
                    "Mode: %s",
                    TOOLTIP,
                    STYLE_CLASS);

            RadioButton alphaButton = (RadioButton) container.getChildren().getFirst();
            RadioButton betaButton = (RadioButton) container.getChildren().get(1);
            betaButton.setSelected(true);
            property.setValue(TestMode.GAMMA);
            RadioButton gammaButton = (RadioButton) container.getChildren().get(2);

            assertAll(
                    () -> assertSame(container, control.controlRegion()),
                    () -> assertEquals("Mode: GAMMA", control.label().getText()),
                    () -> assertSame(container, control.label().getLabelFor()),
                    () -> assertEquals(3, container.getChildren().size()),
                    () -> assertTrue(alphaButton.getStyleClass().contains(STYLE_CLASS)),
                    () -> assertSame(control.label().getTooltip(), alphaButton.getTooltip()),
                    () -> assertEquals(TestMode.BETA, betaButton.getUserData()),
                    () -> assertEquals(TestMode.GAMMA, property.getValue()),
                    () -> assertTrue(gammaButton.isSelected())
            );
        });
    }

    @Test
    void testCreateLabeledEnumCheckBoxSynchronizesSelectionAndProperty() {
        FxTestSupport.runAndWait(() -> {
            InputEnumProperty<TestToggle> property = InputEnumProperty.of(
                    TestToggle.OFF,
                    TestToggle.class,
                    TestToggle::name);

            FXComponentFactory.LabeledControl<CheckBox> control = FXComponentFactory.createLabeledEnumCheckBox(
                    property,
                    TestToggle.ON,
                    TestToggle.OFF,
                    "Toggle: %s",
                    TOOLTIP,
                    STYLE_CLASS);
            CheckBox checkBox = control.controlRegion();

            checkBox.setSelected(true);
            TestToggle afterSelection = property.getValue();
            property.setValue(TestToggle.OFF);

            assertAll(
                    () -> assertEquals(TestToggle.ON, afterSelection),
                    () -> assertEquals(TestToggle.OFF, property.getValue()),
                    () -> assertFalse(checkBox.isSelected()),
                    () -> assertEquals("Toggle: OFF", control.label().getText()),
                    () -> assertSame(checkBox, control.label().getLabelFor()),
                    () -> assertSame(control.label().getTooltip(), checkBox.getTooltip()),
                    () -> assertTrue(checkBox.getStyleClass().contains(STYLE_CLASS))
            );
        });
    }

    @Test
    void testCreateLabeledEnumCheckBoxRejectsInvalidMappings() {
        FxTestSupport.runAndWait(() -> {
            InputEnumProperty<TestMode> threeValueProperty = InputEnumProperty.of(
                    TestMode.ALPHA,
                    TestMode.class,
                    TestMode::name);
            InputEnumProperty<TestToggle> twoValueProperty = InputEnumProperty.of(
                    TestToggle.OFF,
                    TestToggle.class,
                    TestToggle::name);

            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> FXComponentFactory.createLabeledEnumCheckBox(
                                    threeValueProperty, TestMode.ALPHA, TestMode.BETA, "%s", TOOLTIP, STYLE_CLASS)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> FXComponentFactory.createLabeledEnumCheckBox(
                                    twoValueProperty, TestToggle.ON, TestToggle.ON, "%s", TOOLTIP, STYLE_CLASS))
            );
        });
    }

    @Test
    void testCreateLabeledChoiceComboBoxSynchronizesValuesAndCleanupStopsSynchronization() {
        FxTestSupport.runAndWait(() -> {
            InputChoiceProperty<String> property = InputChoiceProperty.ofList(
                    "alpha",
                    List.of("alpha", "beta", "gamma"),
                    String::toUpperCase);
            AtomicReference<@Nullable Runnable> cleanup = new AtomicReference<>();

            FXComponentFactory.LabeledControl<ComboBox<String>> control = FXComponentFactory.createLabeledChoiceComboBox(
                    property,
                    "Choice: %s",
                    TOOLTIP,
                    STYLE_CLASS,
                    cleanup::set);
            ComboBox<String> comboBox = control.controlRegion();

            comboBox.setValue("beta");
            String valueAfterComboChange = property.getValue();
            property.setValue("gamma");
            String comboValueAfterPropertyChange = comboBox.getValue();
            Runnable cleanupRunnable = cleanup.get();
            assertNotNull(cleanupRunnable);
            cleanupRunnable.run();
            property.setValue("alpha");

            assertAll(
                    () -> assertEquals(List.of("alpha", "beta", "gamma"), comboBox.getItems()),
                    // Cleanup unbinds the label, so it stays at the last synchronized value ("gamma").
                    () -> assertEquals("Choice: GAMMA", control.label().getText()),
                    () -> assertSame(comboBox, control.label().getLabelFor()),
                    () -> assertSame(control.label().getTooltip(), comboBox.getTooltip()),
                    () -> assertTrue(comboBox.getStyleClass().contains(STYLE_CLASS)),
                    () -> assertEquals("beta", valueAfterComboChange),
                    () -> assertEquals("gamma", comboValueAfterPropertyChange),
                    () -> assertEquals("gamma", comboBox.getValue())
            );
        });
    }

    @Test
    void testCreateLabeledIntSliderBindsValueAndConfiguresSlider() {
        FxTestSupport.runAndWait(() -> {
            InputDoublePropertyIntRange property = InputDoublePropertyIntRange.of(3, 0, 10);

            FXComponentFactory.LabeledControl<Slider> control = FXComponentFactory.createLabeledIntSlider(
                    property,
                    "%.0f px",
                    TOOLTIP,
                    STYLE_CLASS);
            Slider slider = control.controlRegion();

            slider.setValue(7.0d);
            double propertyValueAfterSliderChange = property.getValue();
            property.setValue(2.0d);

            assertAll(
                    () -> assertEquals(0.0d, slider.getMin()),
                    () -> assertEquals(10.0d, slider.getMax()),
                    () -> assertEquals(10.0d, slider.getMajorTickUnit()),
                    () -> assertEquals(9, slider.getMinorTickCount()),
                    () -> assertTrue(slider.isSnapToTicks()),
                    () -> assertTrue(slider.isShowTickLabels()),
                    () -> assertTrue(slider.isShowTickMarks()),
                    () -> assertEquals(7.0d, propertyValueAfterSliderChange),
                    () -> assertEquals(2.0d, slider.getValue()),
                    () -> assertEquals("2 px", control.label().getText()),
                    () -> assertSame(slider, control.label().getLabelFor())
            );
        });
    }

    @Test
    void testCreateLabeledIntSpinnerNormalizesEditorInputAndSyncsProperty() {
        FxTestSupport.runAndWait(() -> {
            InputIntegerProperty property = InputIntegerProperty.of(10, 0, 20, 5);

            FXComponentFactory.LabeledControl<Spinner<Integer>> control = FXComponentFactory.createLabeledIntSpinner(
                    property,
                    "%d items",
                    TOOLTIP,
                    STYLE_CLASS);
            Spinner<Integer> spinner = control.controlRegion();

            spinner.getEditor().setText("14");
            spinner.getEditor().getOnAction().handle(null);
            int normalizedPropertyValue = property.getValue();
            String normalizedEditorText = spinner.getEditor().getText();
            spinner.getEditor().setText("invalid");
            spinner.getEditor().getOnAction().handle(null);

            assertAll(
                    () -> assertTrue(spinner.isEditable()),
                    () -> assertEquals(15, normalizedPropertyValue),
                    () -> assertEquals("15", normalizedEditorText),
                    () -> assertEquals("15", spinner.getEditor().getText()),
                    () -> assertEquals(Integer.valueOf(15), spinner.getValueFactory().getValue()),
                    () -> assertEquals("15 items", control.label().getText()),
                    () -> assertSame(spinner, control.label().getLabelFor())
            );
        });
    }

    @Test
    void testCreateLabeledPercentSliderRoundsValueAndBindsLabel() {
        FxTestSupport.runAndWait(() -> {
            InputDoubleProperty property = InputDoubleProperty.of(0.255d, 0.0d, 1.0d);

            FXComponentFactory.LabeledControl<Slider> control = FXComponentFactory.createLabeledPercentSlider(
                    property,
                    "%.0f %%",
                    TOOLTIP,
                    STYLE_CLASS);
            Slider slider = control.controlRegion();

            double initialRoundedValue = property.getValue();
            slider.setValue(0.333d);

            assertAll(
                    () -> assertEquals(0.26d, initialRoundedValue, 0.000_001d),
                    () -> assertEquals(0.33d, slider.getValue(), 0.000_001d),
                    () -> assertEquals(0.33d, property.getValue(), 0.000_001d),
                    () -> assertEquals("33 %", control.label().getText()),
                    () -> assertEquals(0.01d, slider.getBlockIncrement(), 0.000_001d),
                    () -> assertSame(slider, control.label().getLabelFor())
            );
        });
    }

    @Test
    void testCreateLabeledDoubleSliderRoundsValueAndBindsLabel() {
        FxTestSupport.runAndWait(() -> {
            InputDoubleProperty property = InputDoubleProperty.of(0.255d, 0.0d, 1.0d);

            FXComponentFactory.LabeledControl<Slider> control = FXComponentFactory.createLabeledDoubleSlider(
                    property,
                    2,
                    "%.2f",
                    TOOLTIP,
                    STYLE_CLASS);
            Slider slider = control.controlRegion();

            double initialRoundedValue = property.getValue();
            slider.setValue(0.333d);

            assertAll(
                    () -> assertEquals(0.26d, initialRoundedValue, 0.000_001d),
                    () -> assertEquals(0.33d, slider.getValue(), 0.000_001d),
                    () -> assertEquals(0.33d, property.getValue(), 0.000_001d),
                    // The label uses DoubleProperty.asString, which formats with the default locale.
                    () -> assertEquals(String.format("%.2f", 0.33d), control.label().getText()),
                    () -> assertEquals(0.01d, slider.getBlockIncrement(), 0.000_001d),
                    () -> assertSame(slider, control.label().getLabelFor())
            );
        });
    }

    @Test
    void testCreateLabeledStringTextBoxBindsTextAndClearButton() {
        FxTestSupport.runAndWait(() -> {
            StringProperty inputProperty = new SimpleStringProperty("initial");
            StringProperty labelProperty = new SimpleStringProperty("label");

            FXComponentFactory.LabeledControl<HBox> control = FXComponentFactory.createLabeledStringTextBox(
                    inputProperty,
                    labelProperty,
                    "Label: %s",
                    "prompt",
                    TOOLTIP,
                    "clear",
                    STYLE_CLASS);
            TextField textField = (TextField) control.controlRegion().getChildren().getFirst();
            Button clearButton = (Button) control.controlRegion().getChildren().get(1);

            textField.setText("changed");
            String valueAfterTextChange = inputProperty.get();
            clearButton.fire();

            assertAll(
                    () -> assertEquals("changed", valueAfterTextChange),
                    () -> assertEquals("", inputProperty.get()),
                    () -> assertEquals("Label: label", control.label().getText()),
                    () -> assertEquals("prompt", textField.getPromptText()),
                    () -> assertSame(textField, control.label().getLabelFor()),
                    () -> assertNotNull(clearButton.getTooltip()),
                    () -> assertTrue(textField.getStyleClass().contains(STYLE_CLASS)),
                    () -> assertTrue(control.controlRegion().getStyleClass().contains(STYLE_CLASS))
            );
        });
    }

    @Test
    void testCreateLabeledStringTextBoxWithAdoptCopiesValueAndTracksDisableState() {
        FxTestSupport.runAndWait(() -> {
            StringProperty inputProperty = new SimpleStringProperty("initial");
            StringProperty labelProperty = new SimpleStringProperty("label");
            StringProperty adoptValueProperty = new SimpleStringProperty("adopted");

            FXComponentFactory.LabeledControl<HBox> control = FXComponentFactory.createLabeledStringTextBoxWithAdopt(
                    inputProperty,
                    labelProperty,
                    adoptValueProperty,
                    "Label: %s",
                    "prompt",
                    TOOLTIP,
                    "adopt",
                    "clear",
                    STYLE_CLASS);
            TextField textField = (TextField) control.controlRegion().getChildren().getFirst();
            Button adoptButton = (Button) control.controlRegion().getChildren().get(1);
            Button clearButton = (Button) control.controlRegion().getChildren().get(2);

            adoptButton.fire();
            String valueAfterAdopt = inputProperty.get();
            adoptValueProperty.set("");
            boolean disabledAfterEmptyAdoptValue = adoptButton.isDisabled();
            clearButton.fire();

            assertAll(
                    () -> assertEquals("adopted", valueAfterAdopt),
                    () -> assertTrue(disabledAfterEmptyAdoptValue),
                    () -> assertEquals("", inputProperty.get()),
                    () -> assertEquals("Label: label", control.label().getText()),
                    () -> assertSame(textField, control.label().getLabelFor())
            );
        });
    }

    private enum TestMode {
        ALPHA,
        BETA,
        GAMMA
    }

    private enum TestToggle {
        OFF,
        ON
    }

}
