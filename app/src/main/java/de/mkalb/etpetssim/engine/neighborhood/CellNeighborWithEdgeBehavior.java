package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;

/**
 * Represents the relationship between a cell and one of its neighbors in a two-dimensional grid,
 * including the result of applying edge behavior to the neighbor coordinate.
 * <p>
 * This record extends the information of {@link CellNeighbor} by including both the original
 * (theoretical) neighbor coordinate and the mapped coordinate after applying the grid's edge behavior,
 * as well as the resulting {@link EdgeBehaviorAction}.
 * <p>
 * This is useful for simulations where it is important to know not only the geometric neighbor
 * relationship but also how the grid's edge rules affect neighbor access (e.g., wrapping, blocking, reflecting).
 *
 * @param startCoordinate            the coordinate of the start (source) cell
 * @param direction                  the compass direction from the start cell to the neighbor
 * @param connection                 the type of connection (edge or vertex) between the cells
 * @param originalNeighborCoordinate the theoretical neighbor coordinate before edge behavior is applied
 * @param mappedNeighborCoordinate   the resulting neighbor coordinate after applying edge behavior
 * @param edgeBehaviorAction         the action taken as a result of the edge behavior
 *
 * @see CellNeighbor
 * @see EdgeBehaviorResult
 * @see EdgeBehaviorAction
 */
public record CellNeighborWithEdgeBehavior(
        GridCoordinate startCoordinate,
        CompassDirection direction,
        CellConnectionType connection,
        GridCoordinate originalNeighborCoordinate,
        GridCoordinate mappedNeighborCoordinate,
        EdgeBehaviorAction edgeBehaviorAction
) {

    /**
     * Creates a {@link CellNeighborWithEdgeBehavior} from a given {@link CellNeighbor} and the result of applying edge behavior.
     * <p>
     * This factory method combines the geometric neighbor relationship with the outcome of edge behavior processing,
     * producing a record that contains both the original and mapped neighbor coordinates, as well as the edge behavior action.
     *
     * @param neighbor   the theoretical neighbor relationship (ignoring edge behavior)
     * @param edgeResult the result of applying edge behavior to the neighbor coordinate
     * @return a new {@link CellNeighborWithEdgeBehavior} representing the neighbor relationship with edge behavior applied
     */
    public static CellNeighborWithEdgeBehavior of(CellNeighbor neighbor, EdgeBehaviorResult edgeResult) {
        return new CellNeighborWithEdgeBehavior(
                neighbor.startCoordinate(),
                neighbor.direction(),
                neighbor.connection(),
                edgeResult.original(),
                edgeResult.mapped(),
                edgeResult.action()
        );
    }

    /**
     * Returns {@code true} if the connection to the neighbor is via a shared edge.
     *
     * @return {@code true} if the connection type is {@link CellConnectionType#EDGE}, {@code false} otherwise
     */
    public boolean isEdgeConnection() {
        return connection == CellConnectionType.EDGE;
    }

    /**
     * Returns {@code true} if the connection to the neighbor is via a shared vertex only.
     *
     * @return {@code true} if the connection type is {@link CellConnectionType#VERTEX}, {@code false} otherwise
     */
    public boolean isVertexConnection() {
        return connection == CellConnectionType.VERTEX;
    }

    /**
     * Returns a concise, human-readable string representation of this cell neighbor relationship,
     * including edge behavior action and mapped coordinate.
     * <p>
     * Format: [start] [arrow] [originalNeighbor] → [mappedNeighbor] ([connection type], [edgeBehaviorAction])
     *
     * @return a display string describing this cell neighbor with edge behavior
     */
    public String toDisplayString() {
        return String.format(
                "%s %s %s → %s (%s, %s)",
                startCoordinate.toDisplayString(),
                direction.arrow(),
                originalNeighborCoordinate.toDisplayString(),
                mappedNeighborCoordinate.toDisplayString(),
                connection.name().toLowerCase(),
                edgeBehaviorAction.name().toLowerCase()
        );
    }

}
