package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;

/**
 * Represents the result of applying an edge behavior to a grid coordinate.
 * <p>
 * Contains the original coordinate, the mapped coordinate after applying the edge behavior,
 * and the resulting {@link EdgeBehaviorAction}.
 * <p>
 * For actions {@code VALID}, {@code BLOCKED}, and {@code ABSORBED}, the {@code mapped} coordinate
 * is identical to {@code original}. For actions like {@code WRAPPED} or {@code REFLECTED},
 * {@code mapped} may differ from {@code original}.
 *
 * @param original the original grid coordinate before edge behavior is applied
 * @param mapped   the resulting grid coordinate after edge behavior is applied
 * @param action   the action taken as a result of the edge behavior
 */
public record EdgeBehaviorResult(
        GridCoordinate original,
        GridCoordinate mapped,
        EdgeBehaviorAction action) {

    /**
     * Returns a short, human-readable string representation of this edge behavior result.
     * <p>
     * Format: {@code [ACTION] mapped: (x, y)}
     * <br>
     * Example: {@code [WRAPPED] mapped: (15, 0)}
     *
     * @return a concise display string for this edge behavior result
     */
    public String toDisplayString() {
        return String.format("[%s] mapped: %s", action().name(), mapped().toDisplayString());
    }

}
