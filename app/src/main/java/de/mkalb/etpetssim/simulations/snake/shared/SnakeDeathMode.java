package de.mkalb.etpetssim.simulations.snake.shared;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

/**
 * Defines the death behavior modes for the snake in the Snake simulation.
 * <p>
 * Each mode determines how the snake reacts after a death.
 */
public enum SnakeDeathMode {

    /**
     * The snake is permanently removed from the simulation after death.
     */
    PERMADEATH("snakedeathmode.permadeath"),

    /**
     * The snake respawns at a new position after death and continues the simulation.
     */
    RESPAWN("snakedeathmode.respawn");

    private final String resourceKey;

    SnakeDeathMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource-bundle key for the display label of this enum type.
     *
     * <p>The returned key is intended for localized lookup of the enum type name
     * (that is, the label for the enum as a whole, not for an individual enum constant).</p>
     *
     * @return the resource bundle key for this enum type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return AppLocalizationKeys.ENUM_LABEL_SNAKEDEATHMODE;
    }

    /**
     * Returns the resource bundle key associated with this enum constant.
     * <p>
     * The resource key can be used for localized message lookup via {@code AppLocalization}.
     *
     * @return the resource bundle key for this enum constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}
