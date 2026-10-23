package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
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

    private static InputIntegerProperty createProperty() {
        return InputIntegerProperty.of(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, STEP);
    }

    private static InputIntegerProperty createRecord(int initialValue, int min, int max, int step) {
        return new InputIntegerProperty(new SimpleIntegerProperty(initialValue), min, max, step);
    }

    @Test
    void testRecord() {
        IntegerProperty integerProperty = new SimpleIntegerProperty(INITIAL_VALUE);

        InputIntegerProperty property = new InputIntegerProperty(integerProperty, MIN_VALUE, MAX_VALUE, STEP);

        assertAll(
                () -> assertSame(integerProperty, property.property()),
                () -> assertEquals(MIN_VALUE, property.min()),
                () -> assertEquals(MAX_VALUE, property.max()),
                () -> assertEquals(STEP, property.step()),
                () -> assertEquals(MIN_VALUE, createRecord(MIN_VALUE, MIN_VALUE, MAX_VALUE, STEP).getValue()),
                () -> assertEquals(MAX_VALUE, createRecord(MAX_VALUE, MIN_VALUE, MAX_VALUE, STEP).getValue()),
                () -> assertEquals(3, createRecord(3, 0, 4, 1).getValue())
        );
    }

    @Test
    void testConstructorRejectsMinNotLessThanMax() {
        assertThrows(IllegalArgumentException.class,
                () -> createRecord(INITIAL_VALUE, MIN_VALUE, MIN_VALUE, STEP));
    }

    @Test
    void testConstructorRejectsNonPositiveStep() {
        assertThrows(IllegalArgumentException.class,
                () -> createRecord(INITIAL_VALUE, MIN_VALUE, MAX_VALUE, 0));
    }

    @Test
    void testConstructorRejectsRangeNotDivisibleByStep() {
        assertThrows(IllegalArgumentException.class,
                () -> createRecord(INITIAL_VALUE, MIN_VALUE, OFF_STEP_HIGH, STEP));
    }

    @Test
    void testConstructorRejectsInvalidInitialValue() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> createRecord(OFF_STEP_MID, MIN_VALUE, MAX_VALUE, STEP)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> createRecord(ABOVE_MAX, MIN_VALUE, MAX_VALUE, STEP)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> createRecord(BELOW_MIN, MIN_VALUE, MAX_VALUE, STEP))
        );
    }

    @Test
    void testOf() {
        InputIntegerProperty property = createProperty();

        assertAll(
                () -> assertEquals(INITIAL_VALUE, property.getValue()),
                () -> assertEquals(MIN_VALUE, property.min()),
                () -> assertEquals(MAX_VALUE, property.max()),
                () -> assertEquals(STEP, property.step())
        );
    }

    @Test
    void testIsInvalidValue() {
        assertAll(
                () -> assertFalse(InputIntegerProperty.isInvalidValue(MIN_VALUE, MIN_VALUE, MAX_VALUE, STEP)),
                () -> assertFalse(InputIntegerProperty.isInvalidValue(MAX_VALUE, MIN_VALUE, MAX_VALUE, STEP)),
                () -> assertTrue(InputIntegerProperty.isInvalidValue(OFF_STEP_MID, MIN_VALUE, MAX_VALUE, STEP)),
                () -> assertTrue(InputIntegerProperty.isInvalidValue(ABOVE_MAX, MIN_VALUE, MAX_VALUE, STEP)),
                () -> assertTrue(InputIntegerProperty.isInvalidValue(BELOW_MIN, MIN_VALUE, MAX_VALUE, STEP))
        );
    }

    @Test
    void testAsObjectProperty() {
        InputIntegerProperty property = createProperty();

        ObjectProperty<Integer> objectProperty = property.asObjectProperty();
        property.setValue(MAX_VALUE);

        assertEquals(MAX_VALUE, objectProperty.get());
    }

    @Test
    void testAsStringBinding() {
        InputIntegerProperty property = createProperty();

        var binding = property.asStringBinding("%d");
        String initialBinding = binding.get();
        property.setValue(MAX_VALUE);

        assertAll(
                () -> assertEquals("10", initialBinding),
                () -> assertEquals("20", binding.get())
        );
    }

    @Test
    void testSetValue() {
        InputIntegerProperty property = createProperty();

        property.setValue(MAX_VALUE);

        assertEquals(MAX_VALUE, property.getValue());
    }

    @Test
    void testSetValueAcceptsInvalidValue() {
        InputIntegerProperty property = createProperty();

        property.setValue(ABOVE_MAX);
        int aboveMax = property.getValue();
        property.setValue(BELOW_MIN);
        int belowMin = property.getValue();
        property.setValue(OFF_STEP_HIGH);
        int offStep = property.getValue();

        assertAll(
                () -> assertEquals(ABOVE_MAX, aboveMax),
                () -> assertEquals(BELOW_MIN, belowMin),
                () -> assertEquals(OFF_STEP_HIGH, offStep)
        );
    }

    @Test
    void testAdjustValue() {
        InputIntegerProperty property = createProperty();

        assertAll(
                () -> assertEquals(MIN_VALUE, property.adjustValue(MIN_VALUE)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(MAX_VALUE)),
                () -> assertEquals(INITIAL_VALUE, property.adjustValue(INITIAL_VALUE)),
                () -> assertEquals(INITIAL_VALUE, property.adjustValue(OFF_STEP_LOW)),
                () -> assertEquals(EXPECTED_STEP_MID, property.adjustValue(OFF_STEP_MID)),
                () -> assertEquals(EXPECTED_STEP_MID, property.adjustValue(OFF_STEP_HIGH)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(OFF_STEP_HIGHER)),
                () -> assertEquals(MAX_VALUE, property.adjustValue(ABOVE_MAX)),
                () -> assertEquals(MIN_VALUE, property.adjustValue(BELOW_MIN))
        );
    }

}
