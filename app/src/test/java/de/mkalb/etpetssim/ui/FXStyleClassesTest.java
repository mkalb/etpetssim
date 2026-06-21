package de.mkalb.etpetssim.ui;

import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class FXStyleClassesTest {

    private static List<String> getStyleClassConstantValues() {
        return Arrays.stream(FXStyleClasses.class.getDeclaredFields())
                     .filter(field -> Modifier.isPublic(field.getModifiers()))
                     .filter(field -> Modifier.isStatic(field.getModifiers()))
                     .filter(field -> Modifier.isFinal(field.getModifiers()))
                     .filter(field -> field.getType() == String.class)
                     .map(FXStyleClassesTest::getStringValue)
                     .toList();
    }

    private static String getStringValue(Field field) {
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to read style class constant: " + field.getName(), e);
        }
    }

    @Test
    void testStyleClassConstantsAreUniqueAndNonBlank() {
        List<String> values = getStyleClassConstantValues();

        assertAll(
                () -> assertFalse(values.isEmpty()),
                () -> assertTrue(values.stream().noneMatch(String::isBlank)),
                () -> assertEquals(values.size(), new LinkedHashSet<>(values).size())
        );
    }

    @Test
    void testStyleClassConstantsUseCssTokenFormat() {
        List<String> values = getStyleClassConstantValues();

        assertTrue(values.stream().allMatch(value -> value.matches("[a-z][a-z0-9-]*")));
    }

    @Test
    void testStyleClassConstantNamesAreAlphabeticallySorted() {
        List<String> names = Arrays.stream(FXStyleClasses.class.getDeclaredFields())
                                   .filter(field -> Modifier.isPublic(field.getModifiers()))
                                   .filter(field -> Modifier.isStatic(field.getModifiers()))
                                   .filter(field -> Modifier.isFinal(field.getModifiers()))
                                   .filter(field -> field.getType() == String.class)
                                   .map(Field::getName)
                                   .toList();
        List<String> sortedNames = names.stream().sorted().toList();

        assertEquals(sortedNames, names);
    }

}
