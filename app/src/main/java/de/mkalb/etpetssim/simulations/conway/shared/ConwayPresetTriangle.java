package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Preset rule sets for Conway-style cellular automata with triangle cells.
 * <p>
 * Each constant provides a well-known S/B rule string for triangular grids.
 * In triangular grids with edge-and-vertex neighborhood each cell has up to 12 neighbors;
 * however, the S/B notation is limited to single digits (0–9), so neighbor counts 10–12
 * cannot be specified in the rule string.
 * {@link #EMPTY} represents no preset selection.
 * <p>
 * Named rules for triangular grids are rare in the CA community due to the asymmetric
 * topology (alternating upward/downward triangles). The presets below are the most
 * widely cited rule families adapted to the 12-neighbor triangular model.
 * <p>
 * Constants are ordered from no selection to increasingly complex rule sets,
 * with {@link #EMPTY} first and all other presets sorted alphabetically by display name
 * (ASCII order: digit-prefixed names before letter-prefixed names).
 */
public enum ConwayPresetTriangle implements ConwayPreset {

    EMPTY("", "", -1),
    SIERPINSKI("/1", "Sierpinski", 1),
    TRI_LIFE("34/45", "Tri Life", 25),
    TRI_MAJORITY("56789/6789", "Tri Majority", 40);

    private final String ruleString;
    private final String displayName;
    private final int recommendedDensityPercent;

    ConwayPresetTriangle(String ruleString, String displayName, int recommendedDensityPercent) {
        this.ruleString = ruleString;
        this.displayName = displayName;
        this.recommendedDensityPercent = recommendedDensityPercent;
    }

    /**
     * Returns the human-readable display name for this preset, suitable for use in a ComboBox.
     *
     * @return the display name
     */
    @Override
    public String displayName() {
        return displayName;
    }

    /**
     * Returns the S/B rule string for this preset, or an empty string for {@link #EMPTY}.
     *
     * @return the S/B rule string
     */
    @Override
    public String toString() {
        return ruleString;
    }

    /**
     * Returns the recommended initial density of alive cells as a percentage (0–100),
     * based on the birth and survival conditions of this rule.
     * Returns {@code -1} for {@link #EMPTY}.
     *
     * @return the recommended starting density in percent
     */
    @Override
    public int recommendedDensityPercent() {
        return recommendedDensityPercent;
    }

}
