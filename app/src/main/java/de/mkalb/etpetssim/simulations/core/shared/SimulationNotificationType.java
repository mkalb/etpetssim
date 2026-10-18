package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Notification categories emitted during a simulation lifecycle.
 *
 * <p>Each constant maps to a resource bundle key usable for localized user messages.
 */
public enum SimulationNotificationType {

    /**
     * No notification; simulation is running normally.
     */
    NONE("notification.none"),

    /**
     * An exception occurred during the simulation.
     */
    EXCEPTION("notification.exception"),

    /**
     * The simulation was stopped due to a timeout.
     */
    TIMEOUT("notification.timeout"),

    /**
     * The simulation configuration is invalid and cannot be started.
     */
    INVALID_CONFIG("notification.invalidconfig"),

    /**
     * The canvas size exceeds the allowed limit for the simulation.
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
