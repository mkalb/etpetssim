package de.mkalb.etpetssim.simulations.core.viewmodel;

import javafx.beans.property.*;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.concurrent.*;

/**
 * Encapsulates user seed input and derived seed preview for simulations.
 * <p>
 * Interpretation rules:
 * blank input yields a random seed, numeric input yields the parsed {@code long} value,
 * and all other input is converted into a stable hash-based {@code long}.
 * <p>
 * The {@code labelProperty} is updated only by {@link #computeSeedAndUpdateLabel()}.
 */
public final class SeedProperty {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int LONG_SHIFT = 32;
    private static final long LONG_MASK = 0xFFFF_FFFFL;

    private final StringProperty stringProperty;
    private final StringProperty labelProperty;

    /**
     * Constructs a SeedProperty with the given initial value.
     *
     * @param initialValue the initial seed value as a string (may be blank or null for random)
     */
    public SeedProperty(@Nullable String initialValue) {
        stringProperty = new SimpleStringProperty(initialValue);
        labelProperty = new SimpleStringProperty("");
    }

    /**
     * Checks if the given text represents a random seed (null or blank).
     *
     * @param text the seed string to check (may be null)
     * @return {@code true} if the text is null or blank
     */
    static boolean isRandomSeed(@Nullable String text) {
        return (text == null) || text.isBlank();
    }

    /**
     * Checks if the given text can be parsed as a numeric seed (long).
     *
     * @param text the seed string to check (may be null)
     * @return {@code true} if the text can be parsed as a long
     */
    static boolean isNumericSeed(@Nullable String text) {
        if (isRandomSeed(text)) {
            return false;
        }
        try {
            parseNumericSeed(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses the given text as a numeric seed (long).
     *
     * @param text the seed string to parse (must be non-null and numeric)
     * @return the parsed long value
     * @throws NumberFormatException if the text cannot be parsed as a long
     */
    static long parseNumericSeed(String text) {
        return Long.parseLong(text.trim());
    }

    /**
     * Hashes the given text using SHA-256 and returns the first 8 bytes as a long seed.
     *
     * @param text the seed string to hash (must be non-null)
     * @return a long value derived from the hash
     * @throws NoSuchAlgorithmException if SHA-256 is not available
     */
    static long hashSeed(String text) throws NoSuchAlgorithmException {
        // Hash the string using SHA-256 and use the first 8 bytes as a long seed
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return ByteBuffer.wrap(hash).getLong();
    }

    /**
     * Fallback: combines the hashCode of the text into a long value using bit shifting and masking.
     * Used if the SHA-256 algorithm is not available.
     *
     * @param text the seed string to hash (must be non-null)
     * @return a long value derived from the hashCode
     */
    static long fallbackHashSeed(String text) {
        int hashCode = text.hashCode();
        // Combine hashCode into a long by shifting and masking
        return ((long) hashCode << LONG_SHIFT) | (hashCode & LONG_MASK);
    }

    /**
     * Computes a seed value from the given text, using random, numeric, or hash-based strategies.
     * <ul>
     *   <li>If the text is null or blank, a random seed is generated.</li>
     *   <li>If the text is numeric, it is parsed as a long.</li>
     *   <li>Otherwise, a hash-based seed is computed from the text.</li>
     * </ul>
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
     * @return seed input property
     */
    public StringProperty stringProperty() {
        return stringProperty;
    }

    /**
     * Returns the JavaFX StringProperty representing the seed label (typically the computed seed as a string).
     *
     * @return computed-seed label property
     */
    public StringProperty labelProperty() {
        return labelProperty;
    }

    /**
     * Computes the seed from the current string property, updates the label property with the seed value,
     * and returns the seed.
     *
     * @return the computed seed as a long
     */
    public long computeSeedAndUpdateLabel() {
        long computedSeed = computeSeed(stringProperty.get());
        labelProperty.set(Long.toString(computedSeed));
        return computedSeed;
    }

}
