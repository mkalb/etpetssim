package de.mkalb.etpetssim.engine;

/**
 * Specifies the possible outcomes of an entity's interaction with a grid edge,
 * based on the configured {@link EdgeBehavior}.
 * <p>
 * Each constant represents a distinct action that can occur when an entity reaches
 * a grid boundary during simulation. These actions are used to determine whether
 * movement is allowed, blocked, wrapped to the opposite edge, absorbed (removed),
 * or reflected (bounced back).
 * <p>
 * This enum is used throughout the grid framework to standardize edge behavior handling.
 *
 * @see EdgeBehavior
 * @see GridEdgeBehavior
 */
public enum EdgeBehaviorAction {

    /**
     * The entity's movement is valid and allowed; no special edge behavior is triggered.
     */
    VALID,

    /**
     * The entity's movement is blocked by the grid edge; it cannot cross the boundary.
     */
    BLOCKED,

    /**
     * The entity is wrapped to the opposite edge of the grid.
     */
    WRAPPED,

    /**
     * The entity is absorbed by the grid edge and removed from the simulation.
     */
    ABSORBED,

    /**
     * The entity is reflected by the grid edge and bounces back.
     */
    REFLECTED

}
