package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LangtonDirectionOptions {

    private LangtonDirectionOptions() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<CompassDirection> selectableInitialDirections(CellShape cellShape) {
        return switch (cellShape) {
            case SQUARE -> List.of(CompassDirection.N, CompassDirection.E, CompassDirection.S, CompassDirection.W);
            case HEXAGON ->
                    List.of(CompassDirection.N, CompassDirection.NE, CompassDirection.SE, CompassDirection.S, CompassDirection.SW, CompassDirection.NW);
            case TRIANGLE -> List.of();
        };
    }

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
