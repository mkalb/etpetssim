package de.mkalb.etpetssim.engine;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface GridModel<T> {

    GridStructure structure();

    T defaultValue();

    T getValue(GridCoordinate coordinate);

    void setValue(GridCoordinate coordinate, T value);

    GridModel<T> copy();

    GridModel<T> copyBlank();

    default void setDefaultValue(GridCoordinate coordinate) {
        setValue(coordinate, defaultValue());
    }

    default boolean contains(GridCoordinate coordinate) {
        return structure().isCoordinateValid(coordinate);
    }

    default Optional<T> getValueAsOptional(GridCoordinate coordinate) {
        if (contains(coordinate)) {
            return Optional.of(getValue(coordinate));
        } else {
            return Optional.empty();
        }
    }

    default void fill(T value) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setValue(coordinate, value));
    }

    default void fill(Function<GridCoordinate, T> mapper) {
        structure().coordinatesStream().forEachOrdered(coordinate -> setValue(coordinate, mapper.apply(coordinate)));
    }

    default void clear() {
        fill(defaultValue());
    }

    default boolean isSparse() {
        return false;
    }

    default Map<GridCoordinate, T> toMap(Predicate<T> isValid) {
        Map<GridCoordinate, T> result = new LinkedHashMap<>();
        for (GridCoordinate coordinate : structure().coordinatesList()) {
            T value = getValue(coordinate);
            if (isValid.test(value)) {
                result.put(coordinate, value);
            }
        }
        return result;
    }

    default Map<GridCoordinate, T> toMap() {
        return toMap(value -> !Objects.equals(value, defaultValue()));
    }

    default Stream<T> stream() {
        return structure().coordinatesStream().map(this::getValue);
    }

    default boolean isEmpty() {
        return stream().allMatch(value -> Objects.equals(value, defaultValue()));
    }

}
