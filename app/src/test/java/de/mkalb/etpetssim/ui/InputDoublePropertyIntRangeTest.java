package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.*;

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

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testConstructorAcceptsNonIntegerInitialValueWithinRange() {
        InputDoublePropertyIntRange property = new InputDoublePropertyIntRange(
                new SimpleDoubleProperty(NON_INTEGER_INITIAL_VALUE), MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertEquals(NON_INTEGER_INITIAL_VALUE, property.getValue()),
                () -> assertTrue(property.isValid()),
                () -> assertFalse(property.isMin()),
                () -> assertFalse(property.isMax()),
                () -> assertEquals(NON_INTEGER_INITIAL_VALUE, property.asObjectProperty().get()),
                () -> assertEquals(String.format("%.1f", NON_INTEGER_INITIAL_VALUE), property.asStringBinding("%.1f").get())
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
    void testSetValueAtRangeBoundariesUpdatesDerivedState() {
        InputDoublePropertyIntRange property = InputDoublePropertyIntRange.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

        property.setValue(MIN_VALUE);
        assertAll(
                () -> assertEquals(MIN_VALUE, property.getValue()),
                () -> assertTrue(property.isValid()),
                () -> assertTrue(property.isMin()),
                () -> assertFalse(property.isMax())
        );

        property.setValue(MAX_VALUE);
        assertAll(
                () -> assertEquals(MAX_VALUE, property.getValue()),
                () -> assertTrue(property.isValid()),
                () -> assertFalse(property.isMin()),
                () -> assertTrue(property.isMax())
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
        assertAll(
                () -> assertEquals(FRACTIONAL_HIGH, property.getValue()),
                () -> assertTrue(property.isValid())
        );
    }

    @Test
    void testConstructorRejectsMinGreaterThanOrEqualToMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new InputDoublePropertyIntRange(new SimpleDoubleProperty(INITIAL_VALUE), MIN_VALUE, MIN_VALUE));

        assertTrue(exception.getMessage().contains("min must be less than max"));
    }

    @Test
    void testOfRejectsMinGreaterThanOrEqualToMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputDoublePropertyIntRange.of(INITIAL_VALUE, MIN_VALUE, MIN_VALUE));

        assertTrue(exception.getMessage().contains("min must be less than max"));
    }

    @Test
    void testOfRejectsInitialValueOutsideRange() {
        assertAll(
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> InputDoublePropertyIntRange.of((int) ABOVE_MAX, MIN_VALUE, MAX_VALUE));

                    assertTrue(exception.getMessage().contains("Initial value is not valid"));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> InputDoublePropertyIntRange.of((int) BELOW_MIN, MIN_VALUE, MAX_VALUE));

                    assertTrue(exception.getMessage().contains("Initial value is not valid"));
                }
        );
    }

}

