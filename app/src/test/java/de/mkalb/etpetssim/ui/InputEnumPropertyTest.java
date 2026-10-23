package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class InputEnumPropertyTest {

    private static final List<TestMode> VALID_VALUES = List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA);

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static InputEnumProperty<TestMode> createProperty() {
        return InputEnumProperty.of(TestMode.MODE_ALPHA, VALID_VALUES, Enum::name);
    }

    @Test
    void testRecord() {
        ObjectProperty<TestMode> objectProperty = new SimpleObjectProperty<>(TestMode.MODE_ALPHA);
        List<TestMode> validValues = new ArrayList<>(VALID_VALUES);
        Function<TestMode, String> displayNameProvider = Enum::name;

        InputEnumProperty<TestMode> property = new InputEnumProperty<>(objectProperty, validValues,
                displayNameProvider);
        validValues.clear();

        assertAll(
                () -> assertSame(objectProperty, property.property()),
                () -> assertEquals(VALID_VALUES, property.validValues()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> property.validValues().add(TestMode.MODE_GAMMA)),
                () -> assertSame(displayNameProvider, property.displayNameProvider())
        );
    }

    @Test
    void testConstructorRejectsEmptyValidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new InputEnumProperty<>(new SimpleObjectProperty<>(TestMode.MODE_ALPHA), List.of(),
                        Enum::name));
    }

    @Test
    void testConstructorRejectsDuplicateValidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new InputEnumProperty<>(new SimpleObjectProperty<>(TestMode.MODE_ALPHA),
                        List.of(TestMode.MODE_ALPHA, TestMode.MODE_ALPHA), Enum::name));
    }

    @Test
    void testConstructorRejectsInvalidInitialValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new InputEnumProperty<>(new SimpleObjectProperty<>(TestMode.MODE_GAMMA), VALID_VALUES,
                        Enum::name));
    }

    @Test
    void testOf() {
        List<TestMode> validValues = new ArrayList<>(VALID_VALUES);

        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_BETA, validValues,
                mode -> "display-" + mode.name());
        validValues.clear();

        assertAll(
                () -> assertEquals(TestMode.MODE_BETA, property.getValue()),
                () -> assertEquals(VALID_VALUES, property.validValues()),
                () -> assertEquals("display-MODE_BETA", property.displayNameProvider().apply(property.getValue()))
        );
    }

    @Test
    void testOfWithEnumClass() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_BETA, TestMode.class,
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
                () -> assertFalse(InputEnumProperty.isInvalidValue(TestMode.MODE_ALPHA, VALID_VALUES)),
                () -> assertTrue(InputEnumProperty.isInvalidValue(TestMode.MODE_GAMMA, VALID_VALUES))
        );
    }

    @Test
    void testAsStringBinding() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_ALPHA, VALID_VALUES,
                mode -> "display-" + mode.name());

        var binding = property.asStringBinding("Selected: %s");
        String initialBinding = binding.get();
        property.setValue(TestMode.MODE_BETA);

        assertAll(
                () -> assertEquals("Selected: display-MODE_ALPHA", initialBinding),
                () -> assertEquals("Selected: display-MODE_BETA", binding.get())
        );
    }

    @Test
    void testSetValue() {
        InputEnumProperty<TestMode> property = createProperty();

        property.setValue(TestMode.MODE_BETA);

        assertEquals(TestMode.MODE_BETA, property.getValue());
    }

    @Test
    void testSetValueAcceptsInvalidValue() {
        InputEnumProperty<TestMode> property = createProperty();

        property.setValue(TestMode.MODE_GAMMA);

        assertEquals(TestMode.MODE_GAMMA, property.getValue());
    }

    @Test
    void testIsValue() {
        InputEnumProperty<TestMode> property = createProperty();

        assertAll(
                () -> assertTrue(property.isValue(TestMode.MODE_ALPHA)),
                () -> assertFalse(property.isValue(TestMode.MODE_BETA))
        );
    }

    @Test
    void testHasMultipleValidValues() {
        assertAll(
                () -> assertTrue(createProperty().hasMultipleValidValues()),
                () -> assertFalse(InputEnumProperty.of(TestMode.MODE_ALPHA, List.of(TestMode.MODE_ALPHA), Enum::name)
                                                   .hasMultipleValidValues())
        );
    }

    private enum TestMode {
        MODE_ALPHA,
        MODE_BETA,
        MODE_GAMMA
    }

}
