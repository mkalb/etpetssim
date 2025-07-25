package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.engine.GridCoordinate;

/**
 * Represents the relationship between a cell and one of its neighbors in a two-dimensional grid.
 * <p>
 * This record encapsulates the start cell's coordinate, the direction and type of the connection,
 * and the neighbor cell's coordinate. It is used to describe how a cell is connected to its neighbor,
 * including whether the connection is via a shared edge or vertex.
 *
 * @param startCoordinate    the coordinate of the start (source) cell
 * @param direction         the compass direction from the start cell to the neighbor
 * @param connection        the type of connection (edge or vertex) between the cells
 * @param neighborCoordinate the coordinate of the neighboring cell
 *
 * @see de.mkalb.etpetssim.engine.GridCoordinate
 * @see CompassDirection
 * @see CellConnectionType
 */
public record CellNeighbor(
        GridCoordinate startCoordinate,
        CompassDirection direction,
        CellConnectionType connection,
        GridCoordinate neighborCoordinate) {

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
     * Returns a concise, human-readable string representation of this cell neighbor relationship.
     * <p>
     * Format: {@code [start] [arrow] [neighbor] ([connection type])}
     *
     * @return a display string describing this cell neighbor
     */
    public String toDisplayString() {
        return String.format(
                "%s %s %s (%s)",
                startCoordinate.toDisplayString(),
                direction.arrow(),
                neighborCoordinate.toDisplayString(),
                connection.name().toLowerCase()
        );
    }

}