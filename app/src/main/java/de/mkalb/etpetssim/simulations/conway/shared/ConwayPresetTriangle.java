package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Preset rule sets for Conway-style cellular automata with triangle cells.
 * <p>
 * Each constant provides a well-known S/B rule string for triangular grids.
 * {@link #EMPTY} represents no preset selection.
 * <p>
 * Constants are ordered from no selection to increasingly complex rule sets.
 */
public enum ConwayPresetTriangle {

    EMPTY("", ""),
    TRI_45_456("45/456", "Tri 45/456"),
    TRI_25_3("25/3", "Tri 25/3");

    private final String ruleString;
    private final String displayName;

    ConwayPresetTriangle(String ruleString, String displayName) {
        this.ruleString = ruleString;
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this preset, suitable for use in a ComboBox.
     *
     * @return the display name
     */
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

}
