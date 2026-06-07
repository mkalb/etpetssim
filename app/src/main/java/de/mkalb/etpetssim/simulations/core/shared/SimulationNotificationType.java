package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Notification categories emitted during a simulation lifecycle.
 *
 * <p>Each constant maps to a resource bundle key usable for localized user messages.
 * The string value held by each enum constant is the lookup key.
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
