package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Common sealed interface for Conway-style preset rule sets.
 * <p>
 * Exactly three implementations are permitted, one per supported cell shape:
 * {@link ConwayPresetSquare}, {@link ConwayPresetTriangle}, and {@link ConwayPresetHexagon}.
 * Each implementation provides a well-known S/B rule string, a human-readable
 * display name, and a recommended initial cell density.
 */
public sealed interface ConwayPreset
        permits ConwayPresetHexagon, ConwayPresetSquare, ConwayPresetTriangle {

    /**
     * Returns the human-readable display name for this preset, suitable for use in a ComboBox.
     *
     * @return the display name
     */
    String displayName();

    /**
     * Returns the recommended initial density of alive cells as a percentage (0–100),
     * based on the birth and survival conditions of this rule.
     * Returns {@code -1} for the {@code EMPTY} preset (no selection).
     *
     * @return the recommended starting density in percent, or {@code -1} if not applicable
     */
    int recommendedDensityPercent();

    /**
     * Returns the S/B rule string for this preset, or an empty string for the {@code EMPTY} preset.
     *
     * @return the S/B rule string
     */
    @Override
    String toString();

}

