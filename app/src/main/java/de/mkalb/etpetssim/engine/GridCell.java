package de.mkalb.etpetssim.engine;

import java.util.*;

public record GridCell<T>(GridCoordinate coordinate, T value) {

    public GridCell {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(value);
    }

}