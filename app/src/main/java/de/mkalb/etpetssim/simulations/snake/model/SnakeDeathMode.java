package de.mkalb.etpetssim.simulations.snake.model;

public enum SnakeDeathMode {

    PERMADEATH("snakedeathmode.permadeath"),

    RESPAWN("snakedeathmode.respawn");

    private final String resourceKey;

    SnakeDeathMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum NeighborhoodMode.
     *
     * @return the resource key for the label of the enum NeighborhoodMode
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "snakedeathmode.label";
    }

    /**
     * Returns the resource key associated with this neighborhood mode.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this neighborhood mode
     */
    public String resourceKey() {
        return resourceKey;
    }

}
