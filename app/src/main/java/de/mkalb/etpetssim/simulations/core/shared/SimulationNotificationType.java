package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Notification categories emitted during a simulation lifecycle.
 *
 * <p>Each constant maps to a resource bundle key usable for localized user messages.
 */
public enum SimulationNotificationType {

    /**
     * No notification is shown.
     */
    NONE("notification.none"),

    /**
     * An exception occurred during the simulation.
     */
    EXCEPTION("notification.exception"),

    /**
     * The timed simulation was paused because a step or its drawing took too long.
     */
    TIMEOUT("notification.timeout"),

    /**
     * The simulation configuration is invalid and cannot be started.
     */
    INVALID_CONFIG("notification.invalidconfig"),

    /**
     * The grid is larger than the maximum canvas size; the canvas shows only part of it.
     */
    CANVAS_SIZE_LIMIT("notification.canvassizelimit");

    private final String resourceKey;

    SimulationNotificationType(String resourceKey) {
        this.resourceKey = resourceKey;
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
