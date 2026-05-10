package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

public enum SnakeDeathMode {

    PERMADEATH("snakedeathmode.permadeath"),

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
     * Returns the resource key associated with this death mode.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this death mode
     */
    public String resourceKey() {
        return resourceKey;
    }

}
