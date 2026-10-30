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

@Execution(ExecutionMode.SAME_THREAD)
final class InputDoubleIntRangePropertyTest {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 10;
    private static final int INITIAL_VALUE = 3;
    private static final double NON_INTEGER_INITIAL_VALUE = 1.5d;
    private static final double FRACTIONAL_HIGH = 6.6d;
    private static final double FRACTIONAL_LOW = 6.4d;
    private static final double FRACTIONAL_TIE = 6.5d;
    private static final double EXPECTED_ROUND_HIGH = 7.0d;
    private static final double EXPECTED_ROUND_LOW = 6.0d;
    private static final double ABOVE_MAX = 99.0d;
    private static final double BELOW_MIN = -99.0d;
    private static final double TOLERANCE = 1.0e-9d;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static InputDoubleIntRangeProperty createProperty() {
        return InputDoubleIntRangeProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);
    }

    private static InputDoubleIntRangeProperty createRecord(double initialValue, int min, int max) {
        return new InputDoubleIntRangeProperty(new SimpleDoubleProperty(initialValue), min, max);
    }

    @Test
    void testRecord() {
        DoubleProperty doubleProperty = new SimpleDoubleProperty(NON_INTEGER_INITIAL_VALUE);

        InputDoubleIntRangeProperty property = new InputDoubleIntRangeProperty(doubleProperty, MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertSame(doubleProperty, property.property()),
                () -> assertEquals(MIN_VALUE, property.min()),
                () -> assertEquals(MAX_VALUE, property.max()),
                () -> assertEquals(NON_INTEGER_INITIAL_VALUE, property.getValue(), TOLERANCE),
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
        InputDoubleIntRangeProperty property = createProperty();

        assertAll(
                () -> assertEquals(INITIAL_VALUE, property.getValue(), TOLERANCE),
                () -> assertEquals(MIN_VALUE, property.min()),
                () -> assertEquals(MAX_VALUE, property.max())
        );
    }

    @Test
    void testIsInvalidValue() {
        assertAll(
                () -> assertFalse(InputDoubleIntRangeProperty.isInvalidValue(MIN_VALUE, MIN_VALUE, MAX_VALUE)),
                () -> assertFalse(InputDoubleIntRangeProperty.isInvalidValue(FRACTIONAL_HIGH, MIN_VALUE, MAX_VALUE)),
                () -> assertFalse(InputDoubleIntRangeProperty.isInvalidValue(MAX_VALUE, MIN_VALUE, MAX_VALUE)),
                () -> assertTrue(InputDoubleIntRangeProperty.isInvalidValue(ABOVE_MAX, MIN_VALUE, MAX_VALUE)),
                () -> assertTrue(InputDoubleIntRangeProperty.isInvalidValue(BELOW_MIN, MIN_VALUE, MAX_VALUE)),
                () -> assertTrue(InputDoubleIntRangeProperty.isInvalidValue(Double.NaN, MIN_VALUE, MAX_VALUE))
        );
    }

    @Test
    void testAsObjectProperty() {
        InputDoubleIntRangeProperty property = createProperty();

        ObjectProperty<Double> objectProperty = property.asObjectProperty();
        property.setValue(FRACTIONAL_HIGH);

        assertEquals(FRACTIONAL_HIGH, objectProperty.get(), TOLERANCE);
    }

    @Test
    void testAsStringBinding() {
        InputDoubleIntRangeProperty property = createProperty();

        var binding = property.asStringBinding("%.1f");
        String initialBinding = binding.get();
        property.setValue(FRACTIONAL_HIGH);

        assertAll(
                () -> assertEquals(String.format("%.1f", (double) INITIAL_VALUE), initialBinding),
                () -> assertEquals(String.format("%.1f", FRACTIONAL_HIGH), binding.get())
        );
    }

    @Test
    void testSetValue() {
        InputDoubleIntRangeProperty property = createProperty();

        property.setValue(FRACTIONAL_HIGH);

        assertEquals(FRACTIONAL_HIGH, property.getValue(), TOLERANCE);
    }

    @Test
    void testSetValueAcceptsInvalidValue() {
        InputDoubleIntRangeProperty property = createProperty();

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
        InputDoubleIntRangeProperty property = createProperty();

        assertAll(
                () -> assertEquals(EXPECTED_ROUND_HIGH, property.adjustValue(FRACTIONAL_HIGH), TOLERANCE),
                () -> assertEquals(EXPECTED_ROUND_LOW, property.adjustValue(FRACTIONAL_LOW), TOLERANCE),
                () -> assertEquals(EXPECTED_ROUND_LOW, property.adjustValue(FRACTIONAL_TIE), TOLERANCE),
                () -> assertEquals(MAX_VALUE, property.adjustValue(ABOVE_MAX), TOLERANCE),
                () -> assertEquals(MIN_VALUE, property.adjustValue(BELOW_MIN), TOLERANCE)
        );
    }

}
