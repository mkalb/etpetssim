package de.mkalb.etpetssim.engine;

import java.util.*;

public final class SparseGridModel<T> implements GridModel<T> {

    private final GridStructure structure;
    private final T defaultValue;
    private final Map<GridCoordinate, T> data;

    public SparseGridModel(GridStructure structure, T defaultValue) {
        Objects.requireNonNull(structure);
        Objects.requireNonNull(defaultValue);
        this.structure = Objects.requireNonNull(structure);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        data = new HashMap<>(); // Optimize initial size
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
    public GridModel<T> copy() {
        SparseGridModel<T> clone = new SparseGridModel<>(structure, defaultValue);
        clone.data.putAll(data);
        return clone;
    }

    @Override
    public GridModel<T> copyWithDefaultValues() {
        return new SparseGridModel<>(structure, defaultValue);
    }

    @Override
    public T getValue(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return data.getOrDefault(coordinate, defaultValue);
    }

    @Override
    public void setValue(GridCoordinate coordinate, T value) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(value);
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        if (defaultValue.equals(value)) {
            data.remove(coordinate);
        } else {
            data.put(coordinate, value);
        }
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return "SparseGridModel{" +
                "structure=" + structure +
                ", defaultValue=" + defaultValue +
                '}';
    }

}
