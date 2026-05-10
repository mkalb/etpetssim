package de.mkalb.etpetssim.simulations.snake.model;

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
     * Returns the resource key for the label (title) of the enum SnakeDeathMode.
     *
     * @return the resource key for the label of the enum SnakeDeathMode
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
