package de.mkalb.etpetssim.ui;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class InputEnumPropertyTest {

    private enum TestMode {
        MODE_ALPHA,
        MODE_BETA,
        MODE_GAMMA
    }

    @Test
    void testSetInvalidValueUpdatesPropertyAndSetsInvalidState() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_ALPHA,
                List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA),
                Enum::name);

        property.setValue(TestMode.MODE_GAMMA);

        assertEquals(TestMode.MODE_GAMMA, property.getValue());
        assertFalse(property.isValid());
    }

    @Test
    void testSetValidValueUpdatesPropertyAndKeepsValidState() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_ALPHA,
                List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA),
                Enum::name);

        property.setValue(TestMode.MODE_BETA);

        assertEquals(TestMode.MODE_BETA, property.getValue());
        assertTrue(property.isValid());
    }

    @Test
    void testGetValidValuesReturnsUnmodifiableList() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_ALPHA,
                List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA),
                Enum::name);

        assertThrows(UnsupportedOperationException.class,
                () -> property.getValidValues().add(TestMode.MODE_GAMMA));
    }

    @Test
    void testOfRejectsEmptyValidValuesList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputEnumProperty.of(TestMode.MODE_ALPHA, List.of(), Enum::name));

        assertTrue(exception.getMessage().contains("validValues must not be empty"));
    }

    @Test
    void testOfRejectsInitialValueOutsideValidValuesList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputEnumProperty.of(TestMode.MODE_GAMMA,
                        List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA),
                        Enum::name));

        assertTrue(exception.getMessage().contains("Initial value must be in validValues"));
    }

}

