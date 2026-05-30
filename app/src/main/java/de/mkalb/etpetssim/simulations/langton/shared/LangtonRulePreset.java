package de.mkalb.etpetssim.simulations.langton.shared;

/**
 * Common sealed interface for Langton-style preset rule sets.
 * <p>
 * Exactly three implementations are permitted, one per supported cell shape:
 * {@link LangtonRulePresetSquare}, {@link LangtonRulePresetTriangle}, and {@link LangtonRulePresetHexagon}.
 * Each implementation provides a compact turn-sequence rule string used by {@link LangtonMovementRules}.
 */
public sealed interface LangtonRulePreset
        permits LangtonRulePresetHexagon, LangtonRulePresetSquare, LangtonRulePresetTriangle {

    /**
     * Returns the human-readable display name for this preset, suitable for use in a ComboBox.
     * Returns an empty string for the {@code EMPTY} preset (no selection).
     *
     * @return the display name
     */
    String displayName();

    /**
     * Returns the compact turn-sequence rule string for this preset,
     * or an empty string for the {@code EMPTY} preset.
     *
     * @return the rule string
     */
    @Override
    String toString();

}

