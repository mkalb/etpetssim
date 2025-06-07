package de.mkalb.etpetssim.wator;

import java.util.*;

public record WaTorCoordinate(int x, int y) {

    public int gridIndex(int xSize) {
        return x + (xSize * y);
    }

    public WaTorCoordinate topCoordinate(int xSize, int ySize) {
        return new WaTorCoordinate(x, ((y - 1) < 0) ? (ySize - 1) : (y - 1));
    }

    public WaTorCoordinate bottomCoordinate(int xSize, int ySize) {
        return new WaTorCoordinate(x, ((y + 1) >= ySize) ? 0 : (y + 1));
    }

    public WaTorCoordinate leftCoordinate(int xSize, int ySize) {
        return new WaTorCoordinate(((x - 1) < 0) ? (xSize - 1) : (x - 1), y);
    }

    public WaTorCoordinate rightCoordinate(int xSize, int ySize) {
        return new WaTorCoordinate(((x + 1) >= xSize) ? 0 : (x + 1), y);
    }

    public List<WaTorCoordinate> neighbors(int xSize, int ySize) {
        return List.of(
                rightCoordinate(xSize, ySize),
                bottomCoordinate(xSize, ySize),
                leftCoordinate(xSize, ySize),
                topCoordinate(xSize, ySize)
        );
    }

}
