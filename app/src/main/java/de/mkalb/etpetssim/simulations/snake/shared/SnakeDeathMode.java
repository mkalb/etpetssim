package de.mkalb.etpetssim.simulations.snake.shared;

/**
 * Defines the death behavior modes for the snake in the Snake simulation.
 *
 * <p>Each mode determines how the snake reacts after a death.
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
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "snakedeathmode.label";
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}
