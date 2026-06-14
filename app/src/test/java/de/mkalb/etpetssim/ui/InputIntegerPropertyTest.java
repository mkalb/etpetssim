package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

final class InputIntegerPropertyTest {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 20;
    private static final int STEP = 5;
    private static final int INITIAL_VALUE = 10;
    private static final int OFF_STEP_LOW = 11;
    private static final int OFF_STEP_MID = 14;
    private static final int OFF_STEP_HIGH = 17;
    private static final int OFF_STEP_HIGHER = 18;
    private static final int EXPECTED_STEP_MID = 15;
    private static final int ABOVE_MAX = 25;
    private static final int BELOW_MIN = -5;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testConstructorAcceptsValidInitialValueAndExposesAccessors() {
        InputIntegerProperty property = new InputIntegerProperty(
                new SimpleIntegerProperty(INITIAL_VALUE),
                MIN_VALUE,
                MAX_VALUE,
                STEP);

        assertAll(
                () -> assertEquals(INITIAL_VALUE, property.getValue()),
                () -> assertFalse(InputIntegerProperty.isInvalidValue(property.getValue(), MIN_VALUE, MAX_VALUE, STEP)),
                () -> assertEquals(INITIAL_VALUE, property.asObjectProperty().get()),
                () -> assertEquals("10", property.asStringBinding("%d").get())
        );
    }

    @Test
    void testAdjustValueKeepsAlreadyValidValues() {
        InputIntegerProperty property = InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, STEP);

        assertAll(
                () -> assertEquals(MIN_VALUE, property.adjustValue(MIN_VALUE)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(MAX_VALUE)),
                () -> assertEquals(INITIAL_VALUE, property.adjustValue(INITIAL_VALUE))
        );
    }

    @Test
    void testAdjustValueSnapsOffStepValuesToNearestStep() {
        InputIntegerProperty property = InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, STEP);

        assertAll(
                () -> assertEquals(INITIAL_VALUE, property.adjustValue(OFF_STEP_LOW)),
                () -> assertEquals(EXPECTED_STEP_MID, property.adjustValue(OFF_STEP_MID)),
                () -> assertEquals(EXPECTED_STEP_MID, property.adjustValue(OFF_STEP_HIGH)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(OFF_STEP_HIGHER)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(ABOVE_MAX)),
                () -> assertEquals(MIN_VALUE, property.adjustValue(BELOW_MIN))
        );
    }

    @Test
    void testSetValueAcceptsValidValueOnStep() {
        InputIntegerProperty property = InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, STEP);

        property.setValue(MAX_VALUE);
        assertAll(
                () -> assertEquals(MAX_VALUE, property.getValue()),
                () -> assertFalse(InputIntegerProperty.isInvalidValue(property.getValue(), MIN_VALUE, MAX_VALUE, STEP))
        );
    }

    @Test
    void testSetValueAllowsInvalidValuesOutsideRange() {
        InputIntegerProperty property = InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, STEP);

        property.setValue(ABOVE_MAX);
        assertEquals(ABOVE_MAX, property.getValue());
        assertTrue(InputIntegerProperty.isInvalidValue(property.getValue(), MIN_VALUE, MAX_VALUE, STEP));

        property.setValue(BELOW_MIN);
        assertEquals(BELOW_MIN, property.getValue());
        assertTrue(InputIntegerProperty.isInvalidValue(property.getValue(), MIN_VALUE, MAX_VALUE, STEP));
    }

    @Test
    void testSetValueAllowsInvalidValuesOffStep() {
        InputIntegerProperty property = InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, STEP);

        property.setValue(OFF_STEP_HIGH);
        assertEquals(OFF_STEP_HIGH, property.getValue());
        assertTrue(InputIntegerProperty.isInvalidValue(property.getValue(), MIN_VALUE, MAX_VALUE, STEP));

        property.setValue(OFF_STEP_HIGHER);
        assertEquals(OFF_STEP_HIGHER, property.getValue());
        assertTrue(InputIntegerProperty.isInvalidValue(property.getValue(), MIN_VALUE, MAX_VALUE, STEP));
    }

    @Test
    void testOfWithStepOneCreatesInstanceAndAcceptsValue() {
        InputIntegerProperty property = InputIntegerProperty.of(3, 0, 4, 1);

        assertAll(
                () -> assertEquals(3, property.getValue()),
                () -> assertFalse(InputIntegerProperty.isInvalidValue(property.getValue(), 0, 4, 1))
        );
    }

    @Test
    void testConstructorRejectsInvalidConfigurationOrInitialValue() {
        assertAll(
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> new InputIntegerProperty(
                                    new SimpleIntegerProperty(INITIAL_VALUE),
                                    MIN_VALUE,
                                    MIN_VALUE,
                                    STEP));

                    assertTrue(exception.getMessage().contains("min must be less than max"));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> new InputIntegerProperty(
                                    new SimpleIntegerProperty(INITIAL_VALUE),
                                    MIN_VALUE,
                                    MAX_VALUE,
                                    0));

                    assertTrue(exception.getMessage().contains("step must be positive"));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> new InputIntegerProperty(
                                    new SimpleIntegerProperty(INITIAL_VALUE),
                                    MIN_VALUE,
                                    OFF_STEP_HIGH,
                                    STEP));

                    assertTrue(exception.getMessage().contains("must be divisible by step"));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> new InputIntegerProperty(
                                    new SimpleIntegerProperty(OFF_STEP_MID),
                                    MIN_VALUE,
                                    MAX_VALUE,
                                    STEP));

                    assertTrue(exception.getMessage().contains("Initial value is not valid"));
                }
        );
    }

    @Test
    void testOfRejectsMinGreaterThanOrEqualToMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MIN_VALUE, STEP));

        assertTrue(exception.getMessage().contains("min must be less than max"));
    }

    @Test
    void testOfRejectsNonPositiveStepValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, 0));

        assertTrue(exception.getMessage().contains("step must be positive"));
    }

    @Test
    void testOfRejectsRangeNotDivisibleByStepSize() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, OFF_STEP_HIGH, STEP));

        assertTrue(exception.getMessage().contains("must be divisible by step"));
    }

    @Test
    void testOfRejectsInitialValueOutsideConfiguredStepGrid() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputIntegerProperty.of(OFF_STEP_MID, MIN_VALUE, MAX_VALUE, STEP));

        assertTrue(exception.getMessage().contains("Initial value is not valid"));
    }

}

