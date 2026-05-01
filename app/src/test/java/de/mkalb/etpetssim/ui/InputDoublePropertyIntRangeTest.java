package de.mkalb.etpetssim.ui;

import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class InputDoublePropertyIntRangeTest {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 10;
    private static final int INITIAL_VALUE = 3;
    private static final double NON_INTEGER_INITIAL_VALUE = 1.5d;
    private static final double FRACTIONAL_HIGH = 6.6d;
    private static final double FRACTIONAL_LOW = 6.4d;
    private static final double EXPECTED_ROUND_HIGH = 7.0d;
    private static final double EXPECTED_ROUND_LOW = 6.0d;
    private static final double ABOVE_MAX = 99.0d;
    private static final double BELOW_MIN = -99.0d;

    @Test
    void testConstructorAcceptsNonIntegerInitialValueWithinRange() {
        InputDoublePropertyIntRange property = new InputDoublePropertyIntRange(
                new SimpleDoubleProperty(NON_INTEGER_INITIAL_VALUE), MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertEquals(NON_INTEGER_INITIAL_VALUE, property.getValue()),
                () -> assertTrue(property.isValid())
        );
    }

    @Test
    void testAdjustValueRoundsToNearestIntegerAndClampsRange() {
        InputDoublePropertyIntRange property = InputDoublePropertyIntRange.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertEquals(EXPECTED_ROUND_HIGH, property.adjustValue(FRACTIONAL_HIGH)),
                () -> assertEquals(EXPECTED_ROUND_LOW, property.adjustValue(FRACTIONAL_LOW)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(ABOVE_MAX)),
                () -> assertEquals(MIN_VALUE, property.adjustValue(BELOW_MIN))
        );
    }

    @Test
    void testSetValueOutsideRangeUpdatesPropertyAndSetsInvalidState() {
        InputDoublePropertyIntRange property = InputDoublePropertyIntRange.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        property.setValue(ABOVE_MAX);
        assertEquals(ABOVE_MAX, property.getValue());
        assertFalse(property.isValid());

        property.setValue(BELOW_MIN);
        assertEquals(BELOW_MIN, property.getValue());
        assertFalse(property.isValid());
    }

    @Test
    void testSetValueFractionWithinRangeUpdatesPropertyAndKeepsValidState() {
        InputDoublePropertyIntRange property = InputDoublePropertyIntRange.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        property.setValue(FRACTIONAL_HIGH);
        assertEquals(FRACTIONAL_HIGH, property.getValue());
        assertTrue(property.isValid());
    }

}

