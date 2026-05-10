package de.mkalb.etpetssim.engine.neighborhood;

/**
 * Outcome of an entity's interaction with a grid boundary.
 *
 * <p>Each constant represents a deterministic action taken when an entity
 * reaches the grid edge according to the configured {@link de.mkalb.etpetssim.engine.EdgeBehavior}.
 * Use these values to drive movement resolution logic in the grid engine.
 *
 * @see de.mkalb.etpetssim.engine.EdgeBehavior
 * @see de.mkalb.etpetssim.engine.GridEdgeBehavior
 */
public enum EdgeBehaviorAction {

    /** Movement is valid and allowed; no special edge handling applies. */
    VALID,

    /** Movement is blocked by the grid boundary; the entity cannot cross. */
    BLOCKED,

    /** The entity is wrapped to the opposite side of the grid. */
    WRAPPED,

    /** The entity is absorbed by the boundary and removed from the simulation. */
    ABSORBED

}
