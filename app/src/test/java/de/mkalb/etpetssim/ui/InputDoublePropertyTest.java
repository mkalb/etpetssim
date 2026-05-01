package de.mkalb.etpetssim.ui;

import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class InputDoublePropertyTest {

    private static final double MIN_VALUE = 0.0d;
    private static final double MAX_VALUE = 10.0d;
    private static final double INITIAL_VALUE = 3.0d;
    private static final double FRACTIONAL_IN_RANGE = 6.6d;
    private static final double ABOVE_MAX = 99.0d;
    private static final double BELOW_MIN = -99.0d;

    @Test
    void testAdjustValueKeepsAlreadyValidValues() {
        InputDoubleProperty property = InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertEquals(MIN_VALUE, property.adjustValue(MIN_VALUE)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(MAX_VALUE)),
                () -> assertEquals(INITIAL_VALUE, property.adjustValue(INITIAL_VALUE)),
                () -> assertEquals(FRACTIONAL_IN_RANGE, property.adjustValue(FRACTIONAL_IN_RANGE))
        );
    }

    @Test
    void testAdjustValueClampsOutOfRangeValues() {
        InputDoubleProperty property = InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertEquals(MAX_VALUE, property.adjustValue(ABOVE_MAX)),
                () -> assertEquals(MIN_VALUE, property.adjustValue(BELOW_MIN))
        );
    }

    @Test
    void testSetValueOutsideRangeUpdatesPropertyAndSetsInvalidState() {
        InputDoubleProperty property = InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        property.setValue(ABOVE_MAX);
        assertEquals(ABOVE_MAX, property.getValue());
        assertFalse(property.isValid());

        property.setValue(BELOW_MIN);
        assertEquals(BELOW_MIN, property.getValue());
        assertFalse(property.isValid());
    }

    @Test
    void testSetValueWithinRangeUpdatesPropertyAndKeepsValidState() {
        InputDoubleProperty property = InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        property.setValue(FRACTIONAL_IN_RANGE);

        assertAll(
                () -> assertEquals(FRACTIONAL_IN_RANGE, property.getValue()),
                () -> assertTrue(property.isValid())
        );
    }

    @Test
    void testConstructorRejectsMinGreaterThanOrEqualToMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new InputDoubleProperty(new SimpleDoubleProperty(INITIAL_VALUE), MIN_VALUE, MIN_VALUE));

        assertTrue(exception.getMessage().contains("min must be less than max"));
    }

    @Test
    void testConstructorRejectsInitialValueOutsideRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new InputDoubleProperty(new SimpleDoubleProperty(ABOVE_MAX), MIN_VALUE, MAX_VALUE));

        assertTrue(exception.getMessage().contains("Initial value is not valid"));
    }

    @Test
    void testOfRejectsMinGreaterThanOrEqualToMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MIN_VALUE));

        assertTrue(exception.getMessage().contains("min must be less than max"));
    }

    @Test
    void testOfRejectsInitialValueOutsideRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputDoubleProperty.of(ABOVE_MAX, MIN_VALUE, MAX_VALUE));

        assertTrue(exception.getMessage().contains("Initial value is not valid"));
    }

}

