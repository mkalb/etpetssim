package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Preset rule sets for Conway-style cellular automata with square cells.
 * <p>
 * Each constant provides a well-known S/B rule string for square grids.
 * {@link #EMPTY} represents no preset selection.
 * <p>
 * Constants are ordered from no selection to increasingly complex rule sets.
 */
public enum ConwayPresetSquare {

    EMPTY("", ""),
    CONWAYS_LIFE("23/3", "Conway's Life"),
    HIGHLIFE("23/36", "HighLife"),
    SEEDS("/2", "Seeds"),
    DAY_AND_NIGHT("3678/34678", "Day & Night");

    private final String ruleString;
    private final String displayName;

    ConwayPresetSquare(String ruleString, String displayName) {
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
