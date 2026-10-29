package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class InputChoicePropertyTest {

    private static final List<String> VALID_VALUES = List.of("alpha", "beta", "gamma");

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static InputChoiceProperty<String> createProperty() {
        return InputChoiceProperty.ofList("alpha", List.of("alpha", "beta"), String::toUpperCase);
    }

    @Test
    void testRecord() {
        ObjectProperty<String> objectProperty = new SimpleObjectProperty<>("alpha");
        List<String> validValues = new ArrayList<>(List.of("alpha", "beta"));
        Function<String, String> displayNameProvider = String::toUpperCase;

        InputChoiceProperty<String> property = new InputChoiceProperty<>(objectProperty, validValues,
                displayNameProvider);
        validValues.clear();

        assertAll(
                () -> assertSame(objectProperty, property.property()),
                () -> assertEquals(List.of("alpha", "beta"), property.validValues()),
                () -> assertThrows(UnsupportedOperationException.class, () -> property.validValues().add("gamma")),
                () -> assertSame(displayNameProvider, property.displayNameProvider())
        );
    }

    @Test
    void testConstructorRejectsEmptyValidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new InputChoiceProperty<>(new SimpleObjectProperty<>("alpha"), List.of(), String::toUpperCase));
    }

    @Test
    void testConstructorRejectsDuplicateValidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new InputChoiceProperty<>(new SimpleObjectProperty<>("alpha"), List.of("alpha", "alpha"),
                        String::toUpperCase));
    }

    @Test
    void testConstructorRejectsInvalidInitialValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new InputChoiceProperty<>(new SimpleObjectProperty<>("gamma"), List.of("alpha", "beta"),
                        String::toUpperCase));
    }

    @Test
    void testOfList() {
        List<String> validValues = new ArrayList<>(List.of("alpha", "beta"));

        InputChoiceProperty<String> property = InputChoiceProperty.ofList("beta", validValues, String::toUpperCase);
        validValues.clear();

        assertAll(
                () -> assertEquals("beta", property.getValue()),
                () -> assertEquals(List.of("alpha", "beta"), property.validValues()),
                () -> assertEquals("BETA", property.displayNameProvider().apply(property.getValue()))
        );
    }

    @Test
    void testOfEnum() {
        InputChoiceProperty<TestMode> property = InputChoiceProperty.ofEnum(
                TestMode.MODE_BETA,
                TestMode.class,
                mode -> "display-" + mode.name());

        assertAll(
                () -> assertEquals(TestMode.MODE_BETA, property.getValue()),
                () -> assertEquals(List.of(TestMode.values()), property.validValues()),
                () -> assertEquals("display-MODE_BETA", property.displayNameProvider().apply(property.getValue()))
        );
    }

    @Test
    void testIsInvalidValue() {
        assertAll(
                () -> assertFalse(InputChoiceProperty.isInvalidValue("alpha", VALID_VALUES)),
                () -> assertTrue(InputChoiceProperty.isInvalidValue("delta", VALID_VALUES))
        );
    }

    @Test
    void testAsStringBinding() {
        InputChoiceProperty<String> property = InputChoiceProperty.ofList(
                "alpha",
                List.of("alpha", "beta"),
                value -> "display-" + value.toUpperCase(Locale.ROOT));

        var binding = property.asStringBinding("Selected: %s");
        String initialBinding = binding.get();
        property.setValue("beta");

        assertAll(
                () -> assertEquals("Selected: display-ALPHA", initialBinding),
                () -> assertEquals("Selected: display-BETA", binding.get())
        );
    }

    @Test
    void testSetValue() {
        InputChoiceProperty<String> property = createProperty();

        property.setValue("beta");

        assertEquals("beta", property.getValue());
    }

    @Test
    void testSetValueAcceptsInvalidValue() {
        InputChoiceProperty<String> property = createProperty();

        property.setValue("gamma");

        assertEquals("gamma", property.getValue());
    }

    @Test
    void testIsValue() {
        InputChoiceProperty<String> property = createProperty();

        assertAll(
                () -> assertTrue(property.isValue("alpha")),
                () -> assertFalse(property.isValue("beta"))
        );
    }

    @Test
    void testHasMultipleValidValues() {
        assertAll(
                () -> assertTrue(createProperty().hasMultipleValidValues()),
                () -> assertFalse(InputChoiceProperty.ofList("alpha", List.of("alpha"), String::toUpperCase)
                        .hasMultipleValidValues())
        );
    }

    private enum TestMode {
        MODE_ALPHA,
        MODE_BETA,
        MODE_GAMMA
    }

}
