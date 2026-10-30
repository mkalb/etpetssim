package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SameParameterValue")
@Execution(ExecutionMode.SAME_THREAD)
final class InputDoublePropertyTest {

    private static final double MIN_VALUE = 0.0d;
    private static final double MAX_VALUE = 10.0d;
    private static final double INITIAL_VALUE = 3.0d;
    private static final double FRACTIONAL_IN_RANGE = 6.6d;
    private static final double ABOVE_MAX = 99.0d;
    private static final double BELOW_MIN = -99.0d;
    private static final double TOLERANCE = 1.0e-9d;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static InputDoubleProperty createProperty() {
        return InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);
    }

    private static InputDoubleProperty createRecord(double initialValue, double min, double max) {
        return new InputDoubleProperty(new SimpleDoubleProperty(initialValue), min, max);
    }

    @Test
    void testRecord() {
        DoubleProperty doubleProperty = new SimpleDoubleProperty(INITIAL_VALUE);

        InputDoubleProperty property = new InputDoubleProperty(doubleProperty, MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertSame(doubleProperty, property.property()),
                () -> assertEquals(MIN_VALUE, property.min(), TOLERANCE),
                () -> assertEquals(MAX_VALUE, property.max(), TOLERANCE),
                () -> assertEquals(MIN_VALUE, createRecord(MIN_VALUE, MIN_VALUE, MAX_VALUE).getValue(), TOLERANCE),
                () -> assertEquals(MAX_VALUE, createRecord(MAX_VALUE, MIN_VALUE, MAX_VALUE).getValue(), TOLERANCE)
        );
    }

    @Test
    void testConstructorRejectsMinNotLessThanMax() {
        assertThrows(IllegalArgumentException.class, () -> createRecord(INITIAL_VALUE, MIN_VALUE, MIN_VALUE));
    }

    @Test
    void testConstructorRejectsInvalidInitialValue() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> createRecord(ABOVE_MAX, MIN_VALUE, MAX_VALUE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> createRecord(BELOW_MIN, MIN_VALUE, MAX_VALUE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> createRecord(Double.NaN, MIN_VALUE, MAX_VALUE))
        );
    }

    @Test
    void testOf() {
        InputDoubleProperty property = createProperty();

        assertAll(
                () -> assertEquals(INITIAL_VALUE, property.getValue(), TOLERANCE),
                () -> assertEquals(MIN_VALUE, property.min(), TOLERANCE),
                () -> assertEquals(MAX_VALUE, property.max(), TOLERANCE)
        );
    }

    @Test
    void testIsInvalidValue() {
        assertAll(
                () -> assertFalse(InputDoubleProperty.isInvalidValue(MIN_VALUE, MIN_VALUE, MAX_VALUE)),
                () -> assertFalse(InputDoubleProperty.isInvalidValue(FRACTIONAL_IN_RANGE, MIN_VALUE, MAX_VALUE)),
                () -> assertFalse(InputDoubleProperty.isInvalidValue(MAX_VALUE, MIN_VALUE, MAX_VALUE)),
                () -> assertTrue(InputDoubleProperty.isInvalidValue(ABOVE_MAX, MIN_VALUE, MAX_VALUE)),
                () -> assertTrue(InputDoubleProperty.isInvalidValue(BELOW_MIN, MIN_VALUE, MAX_VALUE)),
                () -> assertTrue(InputDoubleProperty.isInvalidValue(Double.NaN, MIN_VALUE, MAX_VALUE))
        );
    }

    @Test
    void testAsObjectProperty() {
        InputDoubleProperty property = createProperty();

        ObjectProperty<Double> objectProperty = property.asObjectProperty();
        property.setValue(FRACTIONAL_IN_RANGE);

        assertEquals(FRACTIONAL_IN_RANGE, objectProperty.get(), TOLERANCE);
    }

    @Test
    void testAsStringBinding() {
        InputDoubleProperty property = createProperty();

        var binding = property.asStringBinding("%.1f");
        String initialBinding = binding.get();
        property.setValue(FRACTIONAL_IN_RANGE);

        assertAll(
                () -> assertEquals(String.format("%.1f", INITIAL_VALUE), initialBinding),
                () -> assertEquals(String.format("%.1f", FRACTIONAL_IN_RANGE), binding.get())
        );
    }

    @Test
    void testSetValue() {
        InputDoubleProperty property = createProperty();

        property.setValue(FRACTIONAL_IN_RANGE);

        assertEquals(FRACTIONAL_IN_RANGE, property.getValue(), TOLERANCE);
    }

    @Test
    void testSetValueAcceptsInvalidValue() {
        InputDoubleProperty property = createProperty();

        property.setValue(ABOVE_MAX);
        double aboveMax = property.getValue();
        property.setValue(BELOW_MIN);
        double belowMin = property.getValue();

        assertAll(
                () -> assertEquals(ABOVE_MAX, aboveMax, TOLERANCE),
                () -> assertEquals(BELOW_MIN, belowMin, TOLERANCE)
        );
    }

    @Test
    void testAdjustValue() {
        InputDoubleProperty property = createProperty();

        assertAll(
                () -> assertEquals(MIN_VALUE, property.adjustValue(MIN_VALUE), TOLERANCE),
                () -> assertEquals(MAX_VALUE, property.adjustValue(MAX_VALUE), TOLERANCE),
                () -> assertEquals(FRACTIONAL_IN_RANGE, property.adjustValue(FRACTIONAL_IN_RANGE), TOLERANCE),
                () -> assertEquals(MAX_VALUE, property.adjustValue(ABOVE_MAX), TOLERANCE),
                () -> assertEquals(MIN_VALUE, property.adjustValue(BELOW_MIN), TOLERANCE)
        );
    }

}
