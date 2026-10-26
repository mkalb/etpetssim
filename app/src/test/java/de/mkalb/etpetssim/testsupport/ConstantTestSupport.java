package de.mkalb.etpetssim.testsupport;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reads and checks the {@code public static final String} constants of a constant holder class, such as localization
 * keys or style class names.
 *
 * <p>The constants are read in the order of {@link Class#getDeclaredFields()}, which is the declaration order on the
 * JDKs used by this project.
 */
public final class ConstantTestSupport {

    private ConstantTestSupport() {
    }

    /**
     * Returns the {@code public static final String} constants of the given class.
     *
     * @param type the class that declares the constants
     * @return the constant values by constant name, in declaration order
     */
    public static SequencedMap<String, String> stringConstants(Class<?> type) {
        SequencedMap<String, String> constants = new LinkedHashMap<>();
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                    && (field.getType() == String.class)) {
                constants.put(field.getName(), readValue(field));
            }
        }
        return constants;
    }

    /**
     * Asserts that the class declares at least one string constant and that the constant names are sorted
     * alphabetically.
     *
     * @param type the class that declares the constants
     */
    public static void assertConstantNamesSorted(Class<?> type) {
        List<String> names = List.copyOf(stringConstants(type).sequencedKeySet());
        assertAll(
                () -> assertFalse(names.isEmpty()),
                () -> assertEquals(names.stream().sorted().toList(), names)
        );
    }

    /**
     * Asserts that the string constants of the class have unique, non-blank values.
     *
     * @param type the class that declares the constants
     */
    public static void assertConstantValuesUniqueAndNonBlank(Class<?> type) {
        Collection<String> values = stringConstants(type).values();
        assertAll(
                () -> assertTrue(values.stream().noneMatch(String::isBlank)),
                () -> assertEquals(values.size(), new HashSet<>(values).size())
        );
    }

    /**
     * Asserts that every string constant value of the class matches the given regular expression.
     *
     * @param type  the class that declares the constants
     * @param regex the regular expression that each value must match completely
     */
    public static void assertConstantValuesMatch(Class<?> type, String regex) {
        List<String> mismatches = stringConstants(type).values()
                                                       .stream()
                                                       .filter(value -> !value.matches(regex))
                                                       .toList();
        assertEquals(List.of(), mismatches);
    }

    private static String readValue(Field field) {
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to read constant: " + field.getName(), e);
        }
    }

}
