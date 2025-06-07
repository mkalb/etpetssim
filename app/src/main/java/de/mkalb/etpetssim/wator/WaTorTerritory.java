package de.mkalb.etpetssim.wator;

import org.jspecify.annotations.Nullable;

import java.util.*;

final class WaTorTerritory {

    private final int xSize;
    private final int ySize;
    private final @Nullable Long[] grid;

    WaTorTerritory(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        grid = new Long[xSize * ySize];
    }

    boolean isEmpty(WaTorCoordinate coordinate) {
        return grid[coordinate.territoryIndex(xSize)] == null;
    }

    boolean isNotEmpty(WaTorCoordinate coordinate) {
        return grid[coordinate.territoryIndex(xSize)] != null;
    }

    Optional<Long> findIdAt(WaTorCoordinate coordinate) {
        return Optional.ofNullable(grid[coordinate.territoryIndex(xSize)]);
    }

    void placeIdAt(long id, WaTorCoordinate coordinate) {
        if (!isEmpty(coordinate)) {
            throw new IllegalStateException("placeIdAt called for non-empty coordinate '" + coordinate + "' with id " + "'" + id);
        }
        grid[coordinate.territoryIndex(xSize)] = id;
    }

    void moveId(long id, WaTorCoordinate oldCoordinate, WaTorCoordinate newCoordinate) {
        if (isEmpty(oldCoordinate)) {
            throw new IllegalStateException("moveId called for empty oldCoordinate '" + oldCoordinate + "' with id " + "'" + id);
        }
        if (!isEmpty(newCoordinate)) {
            throw new IllegalStateException("moveId called for non-empty newCoordinate '" + newCoordinate + "' with id " + "'" + id);
        }
        removeIdAt(oldCoordinate);
        placeIdAt(id, newCoordinate);
    }

    void removeIdAt(WaTorCoordinate coordinate) {
        if (isEmpty(coordinate)) {
            throw new IllegalStateException("removeIdAt called for empty coordinate '" + coordinate + "'");
        }
        grid[coordinate.territoryIndex(xSize)] = null;
    }

}
