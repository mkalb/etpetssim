package de.mkalb.etpetssim.simulations.core.viewmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class SeedPropertyTest {

    @Test
    void testConstructor() {
        SeedProperty seedProperty = new SeedProperty("42");

        assertAll(
                () -> assertEquals("42", seedProperty.stringProperty().get()),
                () -> assertEquals("", seedProperty.labelProperty().get())
        );
    }

    @Test
    void testConstructorAcceptsNullInitialValue() {
        SeedProperty seedProperty = new SeedProperty(null);

        assertAll(
                () -> assertNull(seedProperty.stringProperty().get()),
                () -> assertEquals("", seedProperty.labelProperty().get())
        );
    }

    @Test
    void testStringPropertyDoesNotUpdateLabel() {
        SeedProperty seedProperty = new SeedProperty("42");
        seedProperty.computeSeedAndUpdateLabel();

        seedProperty.stringProperty().set("7");

        assertEquals("42", seedProperty.labelProperty().get());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "42|42",
            "' 42 '|42",
            "-7|-7",
            "0|0",
            "9223372036854775807|9223372036854775807",
            "-9223372036854775808|-9223372036854775808"
    })
    void testComputeSeedAndUpdateLabelParsesNumericInput(String input, long expectedSeed) {
        SeedProperty seedProperty = new SeedProperty(input);

        long seed = seedProperty.computeSeedAndUpdateLabel();

        assertAll(
                () -> assertEquals(expectedSeed, seed),
                () -> assertEquals(Long.toString(expectedSeed), seedProperty.labelProperty().get())
        );
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "abc|-5010229573455851542",
            "' 42x'|7277467802850525897",
            "9223372036854775808|-4196621543906624989"
    })
    void testComputeSeedAndUpdateLabelHashesText(String input, long expectedSeed) {
        SeedProperty seedProperty = new SeedProperty(input);

        long seed = seedProperty.computeSeedAndUpdateLabel();

        assertAll(
                () -> assertEquals(expectedSeed, seed),
                () -> assertEquals(Long.toString(expectedSeed), seedProperty.labelProperty().get())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void testComputeSeedAndUpdateLabelReturnsRandomSeedForBlankInput(String input) {
        SeedProperty seedProperty = new SeedProperty(input);

        long seed = seedProperty.computeSeedAndUpdateLabel();

        assertEquals(Long.toString(seed), seedProperty.labelProperty().get());
    }

}
