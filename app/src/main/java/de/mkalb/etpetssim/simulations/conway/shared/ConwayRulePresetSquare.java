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
public enum ConwayRulePresetSquare implements ConwayRulePreset {

    EMPTY("", "", -1),
    CONWAYS_LIFE("23/3", "Conway's Life", 30),
    TWO_X_TWO("125/36", "2x2", 15),
    THIRTY_FOUR_LIFE("34/34", "34 Life", 10),
    AMOEBA("1358/357", "Amoeba", 15),
    ANNEAL("35678/4678", "Anneal", 50),
    COAGULATIONS("235678/378", "Coagulations", 10),
    CORAL("45678/3", "Coral", 30),
    DAY_AND_NIGHT("3678/34678", "Day & Night", 25),
    DIAMOEBA("5678/35678", "Diamoeba", 50),
    GNARL("1/1", "Gnarl", 1),
    HIGHLIFE("23/36", "HighLife", 30),
    LIFE_WITHOUT_DEATH("012345678/3", "Life Without Death", 2),
    MAZE("12345/3", "Maze", 5),
    MAZECTRIC("1234/3", "Mazectric", 30),
    MOVE("245/368", "Move", 25),
    REPLICATOR("1357/1357", "Replicator", 1),
    SEEDS("/2", "Seeds", 1);

    private final String ruleString;
    private final String displayName;
    private final int recommendedDensityPercent;

    ConwayRulePresetSquare(String ruleString, String displayName, int recommendedDensityPercent) {
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
