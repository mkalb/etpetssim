package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Preset rule sets for Conway-style cellular automata with hexagon cells.
 * <p>
 * Each constant provides a well-known S/B rule string for hexagonal grids.
 * In hexagonal grids each cell has at most 6 neighbors, so valid digit range is 0–6.
 * {@link #EMPTY} represents no preset selection.
 * <p>
 * Constants are ordered from no selection to increasingly complex rule sets,
 * with {@link #EMPTY} first and all other presets sorted alphabetically by display name
 * (ASCII order: digit-prefixed names before letter-prefixed names).
 */
public enum ConwayRulePresetHexagon implements ConwayRulePreset {

    EMPTY("", "", -1),
    HEX_34_LIFE("34/34", "Hex 34 Life", 40),
    HEX_GLIDERS("3/245", "Hex Gliders", 15),
    HEX_HIGHLIFE("23/36", "Hex HighLife", 30),
    HEX_LIFE("23/34", "Hex Life", 30),
    SNOWFLAKE("34/2", "Snowflake", 10),
    SUGAR("3/2456", "Sugar", 15);

    private final String ruleString;
    private final String displayName;
    private final int recommendedDensityPercent;

    ConwayRulePresetHexagon(String ruleString, String displayName, int recommendedDensityPercent) {
        this.ruleString = ruleString;
        this.displayName = displayName;
        this.recommendedDensityPercent = recommendedDensityPercent;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return ruleString;
    }

    @Override
    public int recommendedDensityPercent() {
        return recommendedDensityPercent;
    }

}
