package de.mkalb.etpetssim.simulations.core.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Encapsulates user seed input and derived seed preview for simulations.
 *
 * <p>Interpretation rules: blank input yields a random seed, numeric input yields the parsed {@code long} value, and
 * all other input is converted into a stable hash-based {@code long} (the first 8 bytes of its SHA-256 hash).
 *
 * <p>The {@code labelProperty} is updated only by {@link #computeSeedAndUpdateLabel()}.
 */
public final class SeedProperty {

    private static final String HASH_ALGORITHM = "SHA-256";

    private final StringProperty stringProperty;
    private final StringProperty labelProperty;

    /**
     * Creates a seed property with the given initial input.
     *
     * @param initialValue the initial seed input; {@code null} or blank for a random seed
     */
    public SeedProperty(@Nullable String initialValue) {
        stringProperty = new SimpleStringProperty(initialValue);
        labelProperty = new SimpleStringProperty("");
    }

    private static long computeSeed(@Nullable String text) {
        if ((text == null) || text.isBlank()) {
            return ThreadLocalRandom.current().nextLong();
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException _) {
            return hashSeed(text);
        }
    }

    private static long hashSeed(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.wrap(hash).getLong();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash seed: algorithm=" + HASH_ALGORITHM, e);
        }
    }

    /**
     * Returns the JavaFX {@code StringProperty} representing the seed input.
     *
     * @return seed input property
     */
    public StringProperty stringProperty() {
        return stringProperty;
    }

    /**
     * Returns the JavaFX {@code StringProperty} representing the seed label (the last computed seed as a string).
     *
     * @return computed-seed label property
     */
    public StringProperty labelProperty() {
        return labelProperty;
    }

    /**
     * Computes the seed from the current input, updates the label property with the seed value, and returns the
     * seed.
     *
     * @return the computed seed
     */
    public long computeSeedAndUpdateLabel() {
        long computedSeed = computeSeed(stringProperty.get());
        labelProperty.set(Long.toString(computedSeed));
        return computedSeed;
    }

}
