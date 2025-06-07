package de.mkalb.etpetssim.wator;

import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WaTorTerritory {

    private final int xSize;
    private final int ySize;
    private final @Nullable Long[] grid;

    public WaTorTerritory(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        grid = new Long[xSize * ySize];
    }

    public boolean isEmpty(WaTorCoordinate coordinate) {
        return grid[coordinate.gridIndex(xSize)] == null;
    }

    public boolean isNotEmpty(WaTorCoordinate coordinate) {
        return grid[coordinate.gridIndex(xSize)] != null;
    }

    public Optional<Long> findIdAt(WaTorCoordinate coordinate) {
        return Optional.ofNullable(grid[coordinate.gridIndex(xSize)]);
    }

    public void placeIdAt(long id, WaTorCoordinate coordinate) {
        if (!isEmpty(coordinate)) {
            throw new IllegalStateException("placeIdAt called for non-empty coordinate: " + coordinate + " with id: " + id);
        }
        grid[coordinate.gridIndex(xSize)] = id;
    }

    public void moveId(long id, WaTorCoordinate oldCoordinate, WaTorCoordinate newCoordinate) {
        if (isEmpty(oldCoordinate)) {
            throw new IllegalStateException("moveId called for empty coordinate: " + oldCoordinate + " with id: " + id);
        }
        if (!isEmpty(newCoordinate)) {
            throw new IllegalStateException("moveId called for non-empty coordinate: " + newCoordinate + " with id: " + id);
        }
        removeIdAt(oldCoordinate);
        placeIdAt(id, newCoordinate);
    }

    public void removeIdAt(WaTorCoordinate coordinate) {
        if (isEmpty(coordinate)) {
            throw new IllegalStateException("removeIdAt called for empty coordinate: " + coordinate);
        }
        grid[coordinate.gridIndex(xSize)] = null;
    }

}
