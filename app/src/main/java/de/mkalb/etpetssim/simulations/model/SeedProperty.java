package de.mkalb.etpetssim.simulations.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.*;

/**
 * SeedProperty encapsulates a string-based seed for simulations, supporting random, numeric, and hash-based seeds.
 * <p>
 * The seed can be set as a string, interpreted as a random value (if blank), a numeric value, or a hash of the string.
 * The class provides helper methods for seed type detection and conversion, and exposes JavaFX properties for UI binding.
 */
public final class SeedProperty {

    private static final String LABEL_RANDOM = "Random";
    private static final String LABEL_NUMBER = "Number";
    private static final String LABEL_HASH = "Hash";

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int LONG_SHIFT = 32;
    private static final long LONG_MASK = 0xFFFF_FFFFL;

    private final StringProperty stringProperty;
    private final StringProperty labelProperty;

    /**
     * Constructs a SeedProperty with the given initial value.
     * <p>
     * The labelProperty is automatically updated to reflect the seed type (Random, Number, or Hash).
     *
     * @param initialValue the initial seed value as a string (may be blank or null for random)
     */
    public SeedProperty(@Nullable String initialValue) {
        stringProperty = new SimpleStringProperty();
        labelProperty = new SimpleStringProperty();

        stringProperty.addListener((_, _, newVal) -> {
            if (isRandomSeed(newVal)) {
                labelProperty.set(LABEL_RANDOM);
            } else if (isNumericSeed(newVal)) {
                labelProperty.set(LABEL_NUMBER);
            } else {
                labelProperty.set(LABEL_HASH);
            }
        });

        stringProperty.set(initialValue);
    }

    /**
     * Checks if the given text represents a random seed (null or blank).
     *
     * @param text the seed string to check
     * @return true if the text is null or blank, false otherwise
     */
    static boolean isRandomSeed(@Nullable String text) {
        return (text == null) || text.isBlank();
    }

    /**
     * Checks if the given text can be parsed as a numeric seed (long).
     *
     * @param text the seed string to check
     * @return true if the text can be parsed as a long, false otherwise
     */
    static boolean isNumericSeed(@Nullable String text) {
        if ((text == null) || text.isBlank()) {
            return false;
        }
        try {
            Long.parseLong(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses the given text as a numeric seed (long).
     *
     * @param text the seed string to parse (must be numeric)
     * @return the parsed long value
     * @throws NumberFormatException if the text cannot be parsed as a long
     */
    static long parseNumericSeed(String text) {
        return Long.parseLong(text.trim());
    }

    /**
     * Hashes the given text using SHA-256 and returns the first 8 bytes as a long seed.
     *
     * @param text the seed string to hash
     * @return a long value derived from the hash
     * @throws NoSuchAlgorithmException if SHA-256 is not available
     */
    static long hashSeed(String text) throws NoSuchAlgorithmException {
        // Hash the string using SHA-256 and use the first 8 bytes as a long seed
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.wrap(hash);
        return buffer.getLong();
    }

    /**
     * Fallback: combines the hashCode of the text into a long value using bit shifting and masking.
     *
     * @param text the seed string to hash
     * @return a long value derived from the hashCode
     */
    static long fallbackHashSeed(String text) {
        int h = text.hashCode();
        // Combine hashCode into a long by shifting and masking
        return ((long) h << LONG_SHIFT) | (h & LONG_MASK);
    }

    /**
     * Computes a seed value from the given text, using random, numeric, or hash-based strategies.
     *
     * @param text the seed string (may be null or blank for random)
     * @return the computed seed as a long
     */
    static long computeSeed(@Nullable String text) {
        if (isRandomSeed(text)) {
            // Use a random long if no seed is provided
            return ThreadLocalRandom.current().nextLong();
        }
        if (isNumericSeed(text)) {
            return parseNumericSeed(text);
        }
        try {
            return hashSeed(text);
        } catch (NoSuchAlgorithmException e) {
            return fallbackHashSeed(text);
        }
    }

    /**
     * Returns the JavaFX StringProperty representing the seed value.
     *
     * @return the StringProperty for the seed
     */
    public StringProperty stringProperty() {
        return stringProperty;
    }

    /**
     * Returns the JavaFX StringProperty representing the seed type label (Random, Number, or Hash).
     *
     * @return the StringProperty for the label
     */
    public StringProperty labelProperty() {
        return labelProperty;
    }

    /**
     * Computes the seed value based on the current stringProperty value.
     *
     * @return the computed seed as a long
     */
    public long computeSeed() {
        return computeSeed(stringProperty.get());
    }

}
