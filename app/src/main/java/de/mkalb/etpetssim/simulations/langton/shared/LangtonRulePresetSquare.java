package de.mkalb.etpetssim.simulations.langton.shared;

/**
 * Preset rule sets for Langton-style ant simulations with square cells.
 * <p>
 * Each constant provides a compact turn-sequence rule string for square grids.
 * {@link #EMPTY} represents no preset selection.
 * <p>
 * Declaration order: {@link #EMPTY} is first (no selection), followed by all other presets
 * sorted by increasing rule string length, then alphabetically.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum LangtonRulePresetSquare implements LangtonRulePreset {

    EMPTY(""),
    RL("RL"),
    RLR("RLR"),
    RLLR("RLLR"),
    RRLL("RRLL"),
    RNNU("RNNU"),
    RLLLLLRRL("RLLLLLRRL"),
    RRLLLRLLLRRR("RRLLLRLLLRRR");

    private final String ruleString;

    LangtonRulePresetSquare(String ruleString) {
        this.ruleString = ruleString;
    }

    /**
     * Returns the human-readable display name for this preset, suitable for use in a ComboBox.
     * Returns an empty string for {@link #EMPTY}.
     *
     * @return the display name
     */
    @Override
    public String displayName() {
        return ruleString;
    }

    /**
     * Returns the compact turn-sequence rule string for this preset,
     * or an empty string for {@link #EMPTY}.
     *
     * @return the rule string
     */
    @Override
    public String toString() {
        return ruleString;
    }

}

