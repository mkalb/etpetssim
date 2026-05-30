package de.mkalb.etpetssim.simulations.conway.shared;

/**
 * Preset rule sets for Conway-style cellular automata with square cells.
 * <p>
 * Each constant provides a well-known S/B rule string for square grids.
 * {@link #EMPTY} represents no preset selection.
 * <p>
 * Declaration order: {@link #EMPTY} is first (no selection), {@link #CONWAYS_LIFE} is second,
 * followed by all other presets sorted alphabetically by display name
 * (ASCII order: digit-prefixed names before letter-prefixed names).
 */
@SuppressWarnings("SpellCheckingInspection")
public enum ConwayPresetSquare implements ConwayPreset {

    EMPTY("", "", -1),
    CONWAYS_LIFE("23/3", "Conway's Life", 30),
    TWO_X_TWO("125/36", "2x2", 40),
    THIRTY_FOUR_LIFE("34/34", "34 Life", 40),
    AMOEBA("1358/357", "Amoeba", 40),
    ANNEAL("35678/4678", "Anneal", 50),
    COAGULATIONS("235678/378", "Coagulations", 40),
    CORAL("45678/3", "Coral", 40),
    DAY_AND_NIGHT("3678/34678", "Day & Night", 40),
    DIAMOEBA("5678/35678", "Diamoeba", 50),
    GNARL("1/1", "Gnarl", 30),
    HIGHLIFE("23/36", "HighLife", 30),
    LIFE_WITHOUT_DEATH("012345678/3", "Life Without Death", 20),
    MAZE("12345/3", "Maze", 30),
    MAZECTRIC("1234/3", "Mazectric", 30),
    MOVE("245/368", "Move", 40),
    REPLICATOR("1357/1357", "Replicator", 5),
    SEEDS("/2", "Seeds", 15);

    private final String ruleString;
    private final String displayName;
    private final int recommendedDensityPercent;

    ConwayPresetSquare(String ruleString, String displayName, int recommendedDensityPercent) {
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
