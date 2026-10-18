package de.mkalb.etpetssim.simulations.langton.shared;

/**
 * Preset rule sets for Langton-style ant simulations with hexagon cells.
 *
 * <p>Each constant provides a compact turn-sequence rule string for hexagonal grids.
 * In hexagonal grids the ant can turn left, right, by double steps (L2, R2), not at all (N), or back (U).
 * {@link #EMPTY} represents no preset selection.
 *
 * <p>Declaration order: {@link #EMPTY} is first (no selection), followed by all other presets
 * sorted by increasing rule string length, then alphabetically.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum LangtonRulePresetHexagon implements LangtonRulePreset {

    EMPTY(""),
    NR("NR"),
    RL("RL"),
    NR2("NR2"),
    R2N("R2N"),
    RL2("RL2"),
    R2RR("R2RR"),
    R2NNRR2R("R2NNRR2R"),
    RR2NUR2RL2("RR2NUR2RL2");

    private final String ruleString;

    LangtonRulePresetHexagon(String ruleString) {
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
