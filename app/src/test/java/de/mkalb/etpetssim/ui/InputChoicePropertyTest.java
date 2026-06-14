package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLogger;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class InputChoicePropertyTest {

    private static final List<String> VALID_VALUES = List.of("alpha", "beta", "gamma");

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @Test
    void testConstructorMakesDefensiveCopyOfValidValues() {
        List<String> validValues = new ArrayList<>(List.of("alpha", "beta"));
        InputChoiceProperty<String> property = new InputChoiceProperty<>(
                new SimpleObjectProperty<>("alpha"),
                validValues,
                String::toUpperCase);

        validValues.clear();

        assertEquals(List.of("alpha", "beta"), property.validValues());
    }

    @Test
    void testSetInvalidValueUpdatesProperty() {
        InputChoiceProperty<String> property = InputChoiceProperty.ofList(
                "alpha",
                List.of("alpha", "beta"),
                String::toUpperCase);

        property.setValue("gamma");

        assertAll(
                () -> assertEquals("gamma", property.getValue()),
                () -> assertFalse(property.isValue("alpha")),
                () -> assertTrue(property.hasMultipleValidValues())
        );
    }

    @Test
    void testSetValidValueUpdatesProperty() {
        InputChoiceProperty<String> property = InputChoiceProperty.ofList(
                "alpha",
                List.of("alpha", "beta"),
                String::toUpperCase);

        property.setValue("beta");

        assertAll(
                () -> assertEquals("beta", property.getValue()),
                () -> assertTrue(property.isValue("beta")),
                () -> assertTrue(property.hasMultipleValidValues())
        );
    }

    @Test
    void testGetValidValuesReturnsUnmodifiableList() {
        InputChoiceProperty<String> property = InputChoiceProperty.ofList(
                "alpha",
                List.of("alpha", "beta"),
                String::toUpperCase);

        assertThrows(UnsupportedOperationException.class,
                () -> property.validValues().add("gamma"));
    }

    @Test
    void testSingletonValidValuesListReportsNoMultipleValues() {
        InputChoiceProperty<String> property = InputChoiceProperty.ofList(
                "alpha",
                List.of("alpha"),
                String::toUpperCase);

        assertAll(
                () -> assertFalse(property.hasMultipleValidValues()),
                () -> assertTrue(property.isValue("alpha"))
        );
    }

    @Test
    void testAsStringBindingUsesDisplayNameProviderAndTracksUpdates() {
        InputChoiceProperty<String> property = InputChoiceProperty.ofList(
                "alpha",
                List.of("alpha", "beta"),
                value -> "display-" + value.toUpperCase(Locale.ROOT));

        var binding = property.asStringBinding("Selected: %s");
        String initialBinding = binding.get();
        property.setValue("beta");

        assertAll(
                () -> assertEquals("Selected: display-ALPHA", initialBinding),
                () -> assertEquals("Selected: display-BETA", binding.get())
        );
    }

    @Test
    void testOfRejectsEmptyValidValuesList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputChoiceProperty.ofList("alpha", List.of(), String::toUpperCase));

        assertTrue(exception.getMessage().contains("validValues must not be empty"));
    }

    @Test
    void testOfRejectsInitialValueOutsideValidValuesList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputChoiceProperty.ofList("gamma",
                        List.of("alpha", "beta"),
                        String::toUpperCase));

        assertTrue(exception.getMessage().startsWith("Initial value is not valid: "));
    }

    @Test
    void testOfRejectsDuplicateValidValuesList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> InputChoiceProperty.ofList("alpha",
                        List.of("alpha", "alpha"),
                        String::toUpperCase));

        assertTrue(exception.getMessage().contains("validValues must not contain duplicates"));
    }

    @Test
    void testIsInvalidValueReturnsFalseForContainedValue() {
        assertFalse(InputChoiceProperty.isInvalidValue("alpha", VALID_VALUES));
    }

    @Test
    void testIsInvalidValueReturnsTrueForValueOutsideList() {
        assertTrue(InputChoiceProperty.isInvalidValue("delta", VALID_VALUES));
    }

    @Test
    void testOfEnumUsesAllEnumConstants() {
        InputChoiceProperty<TestMode> property = InputChoiceProperty.ofEnum(
                TestMode.MODE_BETA,
                TestMode.class,
                mode -> "display-" + mode.name());

        assertAll(
                () -> assertEquals(TestMode.MODE_BETA, property.getValue()),
                () -> assertEquals(List.of(TestMode.values()), property.validValues()),
                () -> assertTrue(property.hasMultipleValidValues()),
                () -> assertEquals("display-MODE_BETA", property.displayNameProvider().apply(property.getValue()))
        );
    }

    private enum TestMode {
        MODE_ALPHA,
        MODE_BETA,
        MODE_GAMMA
    }

}

