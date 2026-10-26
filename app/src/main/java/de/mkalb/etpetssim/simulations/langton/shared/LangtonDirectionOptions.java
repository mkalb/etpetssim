package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Initial directions of a new ant per cell shape.
 *
 * <p>On square and hexagon grids, the user selects the initial direction from the edge directions of the cell. On
 * triangle grids, the direction follows from the selected cell: north for a cell pointing down, south for a cell
 * pointing up.
 */
public final class LangtonDirectionOptions {

    private LangtonDirectionOptions() {
    }

    /**
     * Returns the initial directions that the user can select for the specified cell shape.
     *
     * @param cellShape the cell shape of the grid
     * @return the selectable directions in clockwise order starting with north, or an empty list for triangles,
     * whose direction follows from the selected cell
     */
    public static List<CompassDirection> selectableInitialDirections(CellShape cellShape) {
        return switch (cellShape) {
            case SQUARE -> List.of(CompassDirection.N, CompassDirection.E, CompassDirection.S, CompassDirection.W);
            case HEXAGON -> List.of(CompassDirection.N, CompassDirection.NE, CompassDirection.SE,
                    CompassDirection.S, CompassDirection.SW, CompassDirection.NW);
            case TRIANGLE -> List.of();
        };
    }

    /**
     * Resolves the initial direction of a new ant from the current selection.
     *
     * @param cellShape          the cell shape of the grid
     * @param selectedCoordinate the selected cell, or {@code null} if no cell is selected; used only for triangles
     * @param selectedDirection  the selected direction, or {@code null} if none is selected; ignored for triangles
     * @return the initial direction, or empty if a triangle grid has no selected cell or the selected direction is
     * missing or not selectable for the cell shape
     */
    public static Optional<CompassDirection> resolveInitialDirection(CellShape cellShape,
                                                                     @Nullable GridCoordinate selectedCoordinate,
                                                                     @Nullable CompassDirection selectedDirection) {
        return switch (cellShape) {
            case TRIANGLE -> (selectedCoordinate == null)
                    ? Optional.empty()
                    : Optional.of(automaticTriangleInitialDirection(selectedCoordinate));
            case SQUARE, HEXAGON -> ((selectedDirection != null)
                    && selectableInitialDirections(cellShape).contains(selectedDirection))
                    ? Optional.of(selectedDirection)
                    : Optional.empty();
        };
    }

    /**
     * Returns the initial directions that are valid for a new ant on the specified cell.
     *
     * @param cellShape          the cell shape of the grid
     * @param selectedCoordinate the cell of the new ant
     * @return the selectable directions for squares and hexagons, or the single automatic direction for triangles
     */
    public static List<CompassDirection> validInitialDirections(CellShape cellShape,
                                                                GridCoordinate selectedCoordinate) {
        return switch (cellShape) {
            case SQUARE, HEXAGON -> selectableInitialDirections(cellShape);
            case TRIANGLE -> List.of(automaticTriangleInitialDirection(selectedCoordinate));
        };
    }

    private static CompassDirection automaticTriangleInitialDirection(GridCoordinate selectedCoordinate) {
        return selectedCoordinate.isTriangleCellPointingDown()
                ? CompassDirection.N
                : CompassDirection.S;
    }

}
