package de.mkalb.etpetssim.engine.model;

/**
 * Functional interface for initializing a {@link GridModel} with entities before starting a simulation.
 * <p>
 * Implementations of this interface can be used to populate or modify a grid model with entities of type {@code T}.
 * This interface supports composition via the {@link #andThen(GridInitializer)} method, allowing multiple initializers
 * to be chained and applied in sequence.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 * @see GridModifier
 */
@FunctionalInterface
public interface GridInitializer<T extends GridEntity> {

    /**
     * Initializes the given {@link GridModel} with entities or modifies its state.
     *
     * @param model the grid model to initialize
     */
    void initialize(GridModel<T> model);

    /**
     * Returns a composed {@code GridInitializer} that performs, in sequence, this initializer
     * followed by the {@code after} initializer on the same {@link GridModel}.
     *
     * @param after the initializer to apply after this initializer
     * @return a composed {@code GridInitializer} that performs in sequence this initializer followed by the {@code after} initializer
     */
    default GridInitializer<T> andThen(GridInitializer<T> after) {
        return (GridModel<T> model) -> {
            initialize(model);
            after.initialize(model);
        };
    }

}