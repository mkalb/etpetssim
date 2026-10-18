package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Preset rule sets for Conway-style cellular automata with triangle cells.
 *
 * <p>Each constant provides a well-known S/B rule string for triangular grids.
 * In triangular grids with edge-and-vertex neighborhood each cell has up to 12 neighbors;
 * however, the S/B notation is limited to single digits (0–9), so neighbor counts 10–12
 * cannot be specified in the rule string.
 * {@link #EMPTY} represents no preset selection.
 *
 * <p>Named rules for triangular grids are rare in the CA community due to the asymmetric
 * topology (alternating upward/downward triangles). The presets below are the most
 * widely cited rule families adapted to the 12-neighbor triangular model.
 *
 * <p>Declaration order: {@link #EMPTY} is first (no selection), followed by all other presets
 * sorted alphabetically by display name (ASCII order: digit-prefixed names before letter-prefixed names).
 */
public enum ConwayRulePresetTriangle implements ConwayRulePreset {

    EMPTY("", "", -1),
    SIERPINSKI("/1", "Sierpinski", 1),
    TRI_25_3("25/3", "Tri 25/3", 4),
    TRI_45_456("45/456", "Tri 45/456", 20),
    TRI_LIFE("34/45", "Tri Life", 25),
    TRI_MAJORITY("56789/6789", "Tri Majority", 40);

    private final String ruleString;
    private final String displayName;
    private final int recommendedDensityPercent;

    ConwayRulePresetTriangle(String ruleString, String displayName, int recommendedDensityPercent) {
        this.ruleString = ruleString;
        this.displayName = displayName;
        this.recommendedDensityPercent = recommendedDensityPercent;
    }

    @Override
    public String ruleString() {
        return ruleString;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public int recommendedDensityPercent() {
        return recommendedDensityPercent;
    }

}
