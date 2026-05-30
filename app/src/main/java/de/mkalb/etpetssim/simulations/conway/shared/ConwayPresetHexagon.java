package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Preset rule sets for Conway-style cellular automata with hexagon cells.
 * <p>
 * Each constant provides a well-known S/B rule string for hexagonal grids.
 * {@link #EMPTY} represents no preset selection.
 * <p>
 * Constants are ordered from no selection to increasingly complex rule sets.
 */
public enum ConwayPresetHexagon {

    EMPTY("", ""),
    HEX_LIFE("23/34", "Hex Life"),
    HEX_34_34("34/34", "Hex 34/34"),
    SUGAR("3/2456", "Sugar");

    private final String ruleString;
    private final String displayName;

    ConwayPresetHexagon(String ruleString, String displayName) {
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
