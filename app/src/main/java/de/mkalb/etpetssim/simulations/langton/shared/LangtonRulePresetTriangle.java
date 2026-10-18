package de.mkalb.etpetssim.simulations.langton.shared;

/**
 * Preset rule sets for Langton-style ant simulations with triangle cells.
 *
 * <p>Each constant provides a compact turn-sequence rule string for triangular grids.
 * Only left ({@code L}), right ({@code R}), and U-turn ({@code U}) directions are valid
 * for triangular grids; double-step turns ({@code L2}, {@code R2}) and no-turn ({@code N})
 * are not supported.
 * {@link #EMPTY} represents no preset selection.
 *
 * <p>Declaration order: {@link #EMPTY} is first (no selection), followed by all other presets
 * sorted by increasing rule string length, then alphabetically.
 */
public enum LangtonRulePresetTriangle implements LangtonRulePreset {

    EMPTY(""),
    RL("RL"),
    RLL("RLL"),
    URR("URR");

    private final String ruleString;

    LangtonRulePresetTriangle(String ruleString) {
        this.ruleString = ruleString;
    }

    @Override
    public String ruleString() {
        return ruleString;
    }

    @Override
    public String displayName() {
        return ruleString;
    }

}

