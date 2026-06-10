package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class InputEnumPropertyTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testConstructorMakesDefensiveCopyOfValidValues() {
        List<TestMode> validValues = new ArrayList<>(List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA));
        InputEnumProperty<TestMode> property = new InputEnumProperty<>(
                new SimpleObjectProperty<>(TestMode.MODE_ALPHA),
                validValues,
                Enum::name);

        validValues.clear();

        assertEquals(List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA), property.getValidValues());
    }

    @Test
    void testSetInvalidValueUpdatesPropertyAndSetsInvalidState() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_ALPHA,
                List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA),
                Enum::name);

        property.setValue(TestMode.MODE_GAMMA);

        assertAll(
                () -> assertEquals(TestMode.MODE_GAMMA, property.getValue()),
                () -> assertFalse(property.isValid()),
                () -> assertEquals(-1, property.getIndex()),
                () -> assertFalse(property.isValue(TestMode.MODE_ALPHA))
        );
    }

    @Test
    void testSetValidValueUpdatesPropertyAndKeepsValidState() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(TestMode.MODE_ALPHA,
                List.of(TestMode.MODE_ALPHA, TestMode.MODE_BETA),
                Enum::name);

        property.setValue(TestMode.MODE_BETA);

        assertAll(
                () -> assertEquals(TestMode.MODE_BETA, property.getValue()),
                () -> assertTrue(property.isValid()),
                () -> assertEquals(1, property.getIndex()),
                () -> assertEquals(1, property.getMaxIndex()),
                () -> assertTrue(property.isValue(TestMode.MODE_BETA)),
                () -> assertTrue(property.hasMultipleValidValues())
        );
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
    void testOfWithEnumClassUsesAllEnumConstants() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(
                TestMode.MODE_BETA,
                TestMode.class,
                mode -> "display-" + mode.name());

        assertAll(
                () -> assertEquals(TestMode.MODE_BETA, property.getValue()),
                () -> assertEquals(List.of(TestMode.values()), property.getValidValues()),
                () -> assertEquals(1, property.getIndex()),
                () -> assertEquals(2, property.getMaxIndex()),
                () -> assertTrue(property.hasMultipleValidValues()),
                () -> assertEquals("display-MODE_BETA", property.displayNameProvider().apply(property.getValue()))
        );
    }

    @Test
    void testSingletonValidValuesListReportsNoMultipleValues() {
        InputEnumProperty<TestMode> property = InputEnumProperty.of(
                TestMode.MODE_ALPHA,
                List.of(TestMode.MODE_ALPHA),
                Enum::name);

        assertAll(
                () -> assertTrue(property.isValid()),
                () -> assertFalse(property.hasMultipleValidValues()),
                () -> assertEquals(0, property.getIndex()),
                () -> assertEquals(0, property.getMaxIndex())
        );
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

    private enum TestMode {
        MODE_ALPHA,
        MODE_BETA,
        MODE_GAMMA
    }

}

