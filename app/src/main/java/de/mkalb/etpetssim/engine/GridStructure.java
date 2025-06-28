package de.mkalb.etpetssim.engine;

import java.util.*;
import java.util.stream.*;

/**
 * Represents the structure of a grid in a simulation.
 * A grid is defined by its topology, which includes the shape of each cell (TRIANGLE, SQUARE, HEXAGON)
 * and the edge behavior (how the grid behaves at its edges, such as BLOCK, WRAP, ABSORB, REFLECT).
 * The grid also has a size, which defines its width and height in terms of cells.
 *
 * @param topology the topology of the grid, defining its cell shape and edge behavior
 * @param size the size of the grid, defining its width and height
 */
public record GridStructure(GridTopology topology, GridSize size) {

    /**
     * Returns the shape of each cell in the grid (TRIANGLE, SQUARE, HEXAGON).
     *
     * @return the shape of each cell in the grid
     */
    public CellShape cellShape() {
        return topology.cellShape();
    }

    /**
     * Returns the total number of cells in the grid.
     * This is equivalent to width × height.
     *
     * @return the total number of cells in the grid
     * @see GridSize#area()
     */
    public int cellCount() {
        return size().area();
    }

    /**
     * Returns the edge behavior for the X-axis of the grid.
     *
     * @return the edge behavior for the X-axis
     */
    public EdgeBehavior edgeBehaviorX() {
        return topology.edgeBehaviorX();
    }

    /**
     * Returns the edge behavior for the Y-axis of the grid.
     *
     * @return the edge behavior for the Y-axis
     */
    public EdgeBehavior edgeBehaviorY() {
        return topology.edgeBehaviorY();
    }

    /**
     * Returns the minimum coordinate of the grid, which is always inclusive.
     * This represents the origin of the grid at (0, 0).
     *
     * @return the inclusive minimum coordinate of the grid
     */
    @SuppressWarnings("SameReturnValue")
    public GridCoordinate minCoordinateInclusive() {
        return GridCoordinate.ORIGIN;
    }

    /**
     * Returns the maximum coordinate of the grid, which is exclusive.
     * This coordinate lies just outside the valid grid area and is useful for iteration boundaries.
     *
     * @return the exclusive maximum coordinate of the grid
     */
    public GridCoordinate maxCoordinateExclusive() {
        return new GridCoordinate(size.width(), size.height());
    }

    /**
     * Returns the maximum coordinate of the grid, which is inclusive.
     * This represents the last valid coordinate within the grid.
     *
     * @return the inclusive maximum coordinate of the grid
     */
    public GridCoordinate maxCoordinateInclusive() {
        return new GridCoordinate(size.width() - 1, size.height() - 1);
    }

    /**
     * Checks whether the given coordinate lies within the valid bounds of the grid.
     *
     * @param coordinate the coordinate to check
     * @return true if the coordinate is within the grid bounds, false otherwise
     * @see GridCoordinate#isWithinBounds(GridCoordinate, GridCoordinate)
     */
    public boolean isCoordinateValid(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);
        return coordinate.isWithinBounds(minCoordinateInclusive(), maxCoordinateExclusive());
    }

    /**
     * Returns a stream of all valid coordinates in the grid.
     * The coordinates are ordered row-wise from top-left to bottom-right.
     *
     * @return a stream of all valid grid coordinates
     */
    public Stream<GridCoordinate> allCoordinates() {
        return IntStream.range(0, size.width())
                        .boxed()
                        .flatMap(x -> IntStream.range(0, size.height())
                                               .mapToObj(y -> new GridCoordinate(x, y)));
    }

}
