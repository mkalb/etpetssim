package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

final class InputDoublePropertyTest {

    private static final double MIN_VALUE = 0.0d;
    private static final double MAX_VALUE = 10.0d;
    private static final double INITIAL_VALUE = 3.0d;
    private static final double FRACTIONAL_IN_RANGE = 6.6d;
    private static final double ABOVE_MAX = 99.0d;
    private static final double BELOW_MIN = -99.0d;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testConstructorAcceptsValidInitialValueAndExposesAccessors() {
        InputDoubleProperty property = new InputDoubleProperty(new SimpleDoubleProperty(INITIAL_VALUE), MIN_VALUE, MAX_VALUE);

        assertAll(
                () -> assertEquals(INITIAL_VALUE, property.getValue()),
                () -> assertTrue(property.isValid()),
                () -> assertFalse(property.isMin()),
                () -> assertFalse(property.isMax()),
                () -> assertEquals(INITIAL_VALUE, property.asObjectProperty().get()),
                () -> assertEquals(String.format("%.1f", INITIAL_VALUE), property.asStringBinding("%.1f").get())
        );
    }

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
    void testSetValueAtRangeBoundariesUpdatesDerivedState() {
        InputDoubleProperty property = InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE);

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
    void testConstructorRejectsMinGreaterThanOrEqualToMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new InputDoubleProperty(new SimpleDoubleProperty(INITIAL_VALUE), MIN_VALUE, MIN_VALUE));

        assertTrue(exception.getMessage().contains("min must be less than max"));
    }

    @Test
    void testConstructorRejectsInitialValueOutsideRange() {
        assertAll(
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> new InputDoubleProperty(new SimpleDoubleProperty(ABOVE_MAX), MIN_VALUE, MAX_VALUE));

                    assertTrue(exception.getMessage().contains("Initial value is not valid"));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> new InputDoubleProperty(new SimpleDoubleProperty(BELOW_MIN), MIN_VALUE, MAX_VALUE));

                    assertTrue(exception.getMessage().contains("Initial value is not valid"));
                }
        );
    }

    @Test
    void testOfRejectsMinGreaterThanOrEqualToMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputDoubleProperty.of(INITIAL_VALUE, MIN_VALUE, MIN_VALUE));

        assertTrue(exception.getMessage().contains("min must be less than max"));
    }

    @Test
    void testOfRejectsInitialValueOutsideRange() {
        assertAll(
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> InputDoubleProperty.of(ABOVE_MAX, MIN_VALUE, MAX_VALUE));

                    assertTrue(exception.getMessage().contains("Initial value is not valid"));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> InputDoubleProperty.of(BELOW_MIN, MIN_VALUE, MAX_VALUE));

                    assertTrue(exception.getMessage().contains("Initial value is not valid"));
                }
        );
    }

}

