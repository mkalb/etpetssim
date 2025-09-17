package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.*;
import java.util.function.*;

/**
 * Functional interface for modifying a {@link WritableGridModel} during runtime or in response to events.
 * <p>
 * Implementations can perform targeted or rule-based changes to the grid model.
 *
 * @param <T> the type of {@link GridEntity} in the grid model
 * @see GridInitializer
 */
@FunctionalInterface
public interface GridModifier<T extends GridEntity> {

    /**
     * Returns a no-op modifier that leaves the grid unchanged.
     *
     * @param <T> the type of grid entity
     * @return a no-op grid modifier
     */
    static <T extends GridEntity> GridModifier<T> identity() {
        return _ -> {};
    }

    /**
     * Returns a modifier that clears the grid (sets all cells to the default entity).
     *
     * @param <T> the type of grid entity
     * @return a grid modifier that clears the grid
     */
    static <T extends GridEntity> GridModifier<T> clear() {
        return WritableGridModel::clear;
    }

    /**
     * Returns a modifier that sets the entity at a specific coordinate.
     *
     * @param coordinate the coordinate to modify
     * @param entity     the entity to set
     * @param <T>        the type of grid entity
     * @return a grid modifier that sets the entity at the given coordinate
     */
    static <T extends GridEntity> GridModifier<T> setEntityAt(GridCoordinate coordinate, T entity) {
        return model -> model.setEntity(coordinate, entity);
    }

    /**
     * Returns a modifier that applies another modifier only if a condition is met.
     *
     * @param condition the predicate to test the model
     * @param modifier  the modifier to apply if the condition is true
     * @param <T>       the type of grid entity
     * @return a conditional grid modifier
     */
    static <T extends GridEntity> GridModifier<T> conditional(
            Predicate<WritableGridModel<T>> condition,
            GridModifier<T> modifier) {
        return model -> {
            if (condition.test(model)) {
                modifier.modify(model);
            }
        };
    }

    /**
     * Returns a composed {@code GridModifier} that performs, in sequence, all given modifiers
     * on the same {@link WritableGridModel}. If no modifiers are given, the identity modifier is returned.
     *
     * @param modifiers the modifiers to compose
     * @param <T> the type of grid entity
     * @return a composed {@code GridModifier} that applies all given modifiers in order
     */
    @SafeVarargs
    static <T extends GridEntity> GridModifier<T> compose(GridModifier<T>... modifiers) {
        return Arrays.stream(modifiers)
                     .reduce(identity(), GridModifier::andThen);
    }

    /**
     * Modifies the given {@link WritableGridModel}.
     *
     * @param model the grid model to modify
     */
    void modify(WritableGridModel<T> model);

    /**
     * Returns a composed {@code GridModifier} that performs, in sequence, this modifier
     * followed by the {@code after} modifier on the same {@link WritableGridModel}.
     *
     * @param after the modifier to apply after this modifier
     * @return a composed {@code GridModifier}
     */
    default GridModifier<T> andThen(GridModifier<T> after) {
        return model -> {
            modify(model);
            after.modify(model);
        };
    }

    /**
     * Returns a {@code GridModifier} that applies this modifier only if the given condition is met.
     * <p>
     * The returned modifier tests the condition on the model and applies this modifier if the condition is {@code true}.
     *
     * @param condition the predicate to test the model
     * @return a conditional grid modifier
     */
    default GridModifier<T> onlyIf(Predicate<WritableGridModel<T>> condition) {
        return model -> {
            if (condition.test(model)) {
                modify(model);
            }
        };
    }

}