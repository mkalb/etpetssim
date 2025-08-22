package de.mkalb.etpetssim.simulations.model;

/**
 * Defines the possible notification types that can occur during the lifecycle of a simulation.
 * <p>
 * Each constant represents a distinct notification or error state, used to inform the user
 * about important events or issues in the simulation process.
 * <p>
 * The {@code resourceKey} field allows for resource lookup, such as internationalized messages.
 *
 * @see de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel
 * @see de.mkalb.etpetssim.simulations.view.AbstractMainView
 */
public enum SimulationNotificationType {

    /**
     * No notification; the simulation is running normally.
     */
    NONE("notification.none"),

    /**
     *An exception occurred during the simulation.
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

    /**
     * Constructs a notification type with the specified resource key.
     *
     * @param resourceKey the resource key for this notification type
     */
    SimulationNotificationType(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key associated with this notification type.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this notification type
     */
    public String resourceKey() {
        return resourceKey;
    }

}
