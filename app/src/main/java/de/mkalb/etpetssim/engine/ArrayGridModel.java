// ArrayGridModel.java
package de.mkalb.etpetssim.engine;

import java.util.*;

public class ArrayGridModel<T> implements GridModel<T> {

    private final GridStructure structure;
    private final T defaultValue;
    private final Object[][] data;

    public ArrayGridModel(GridStructure structure, T defaultValue) {
        Objects.requireNonNull(structure);
        Objects.requireNonNull(defaultValue);
        this.structure = Objects.requireNonNull(structure);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        data = new Object[structure.size().height()][structure.size().width()];
        clear();
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    @Override
    public void clear() {
        for (int y = 0; y < structure.size().height(); y++) {
            Arrays.fill(data[y], defaultValue);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return (T) data[coordinate.y()][coordinate.x()];
    }

    @Override
    public void setValue(GridCoordinate coordinate, T value) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(value);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        data[coordinate.y()][coordinate.x()] = Objects.requireNonNull(value, "value must not be null");
    }

    @Override
    public ArrayGridModel<T> copy() {
        ArrayGridModel<T> clone = new ArrayGridModel<>(structure, defaultValue);
        for (int y = 0; y < structure.size().height(); y++) {
            System.arraycopy(data[y], 0, clone.data[y], 0, structure.size().width());
        }
        return clone;
    }

    @Override
    public ArrayGridModel<T> copyBlank() {
        return new ArrayGridModel<>(structure, defaultValue);
    }

    @Override
    public String toString() {
        return "ArrayGridModel{" +
                "structure=" + structure +
                ", defaultValue=" + defaultValue +
                '}';
    }

}
