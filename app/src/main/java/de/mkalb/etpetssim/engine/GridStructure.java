package de.mkalb.etpetssim.engine;

import java.util.*;
import java.util.stream.*;

/**
 * Represents the logical structure of a two-dimensional simulation grid.
 * <p>
 * The grid is defined by its {@link GridTopology}, which specifies the cell shape
 * and edge behavior for both axes.
 * <p>
 * The {@link GridSize} determines the grid's logical dimensions, i.e., the number of columns (width)
 * and rows (height). These values represent cell counts, not pixel sizes.
 * <p>
 * This record provides utility methods for accessing cell shape, edge behavior, coordinate bounds,
 * and for iterating over all valid cell positions within the grid.
 *
 * @param topology the grid topology (cell shape and edge behavior)
 * @param size     the grid size (number of columns and rows)
 *
 * @see GridTopology
 * @see GridSize
 * @see CellShape
 * @see EdgeBehavior
 * @see GridCoordinate
 */
public record GridStructure(GridTopology topology, GridSize size) {

    /**
     * Validates that the provided {@link GridSize} is compatible with the given {@link GridTopology}.
     * <p>
     * The grid width and height must be multiples of the required values as specified by the topology.
     * If these constraints are not met, an {@link IllegalArgumentException} is thrown.
     *
     * @throws IllegalArgumentException if the grid width or height is not a valid multiple for the topology
     */
    public GridStructure {
        int widthMultiple = topology.requiredWidthMultiple();
        int heightMultiple = topology.requiredHeightMultiple();
        if ((size.width() % widthMultiple) != 0) {
            throw new IllegalArgumentException(
                    String.format("Grid width (%d) must be a multiple of %d for topology %s", size.width(), widthMultiple, topology.toDisplayString())
            );
        }
        if ((size.height() % heightMultiple) != 0) {
            throw new IllegalArgumentException(
                    String.format("Grid height (%d) must be a multiple of %d for topology %s", size.height(), heightMultiple, topology.toDisplayString())
            );
        }
    }

    /**
     * Checks whether the specified {@link GridSize} is valid for the given {@link GridTopology}.
     * <p>
     * The grid width and height must be multiples of the required values as defined by the topology.
     * This method returns {@code true} if both dimensions are valid, otherwise {@code false}.
     *
     * @param topology the grid topology (cell shape and edge behavior)
     * @param size the grid size (width and height in cells)
     * @return {@code true} if the size is valid for the topology, {@code false} otherwise
     */
    public static boolean isValid(GridTopology topology, GridSize size) {
        int widthMultiple = topology.requiredWidthMultiple();
        int heightMultiple = topology.requiredHeightMultiple();
        return ((size.width() % widthMultiple) == 0) && ((size.height() % heightMultiple) == 0);
    }

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
        return coordinate.isWithinBounds(minCoordinateInclusive(), maxCoordinateExclusive());
    }

    /**
     * Returns a stream of all valid coordinates in the grid.
     * <p>
     * The coordinates are produced in row-major order: for each row from top to bottom,
     * all columns from left to right are included, starting at (0, 0) and ending at the bottom-right corner.
     *
     * @return a stream of all valid grid coordinates in row-major order
     */
    @SuppressWarnings("GrazieInspection")
    public Stream<GridCoordinate> coordinatesStream() {
        return IntStream.range(0, size.width())
                        .boxed()
                        .flatMap(x -> IntStream.range(0, size.height())
                                               .mapToObj(y -> new GridCoordinate(x, y)));
    }

    /**
     * Returns a list of all valid coordinates in the grid.
     * <p>
     * The coordinates are ordered row by row: for each row from top to bottom,
     * all columns from left to right are included, starting at (0, 0) and ending at the bottom-right corner.
     *
     * @return a list of all valid grid coordinates in row-major order
     */
    public List<GridCoordinate> coordinatesList() {
        List<GridCoordinate> coordinates = new ArrayList<>(size().area());
        for (int y = 0; y < size().height(); y++) {
            for (int x = 0; x < size().width(); x++) {
                coordinates.add(new GridCoordinate(x, y));
            }
        }
        return coordinates;
    }

    /**
     * Returns a short, human-readable string representation of this grid structure.
     * <p>
     * Format: {@code TOPOLOGY SIZE}
     * <br>
     * Example: {@code [SQUARE ABSORB] 10 × 20}
     *
     * @return a concise display string for this grid structure
     */
    public String toDisplayString() {
        return String.format("%s %s", topology.toDisplayString(), size.toDisplayString());
    }

}
