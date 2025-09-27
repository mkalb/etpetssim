package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridSize;

import java.util.*;
import java.util.function.*;

/**
 * Utility class providing various static factory methods for creating {@link GridInitializer} instances.
 * <p>
 * These initializers can be used to set up or modify the contents of a {@link WritableGridModel}
 * in different ways, such as filling, mapping, or placing entities with specific patterns or randomness.
 * </p>
 */
public final class GridInitializers {

    /**
     * Private constructor to prevent instantiation.
     */
    private GridInitializers() {
    }

    /**
     * Returns an initializer that leaves the grid unchanged.
     *
     * @param <T> the type of grid entity
     * @return a no-op grid initializer
     */
    public static <T extends GridEntity> GridInitializer<T> identity() {
        return _ -> {};
    }

    /**
     * Returns an initializer that clears the grid, setting all cells to the default entity.
     *
     * @param <T> the type of grid entity
     * @return a grid initializer that clears the grid
     */
    public static <T extends GridEntity> GridInitializer<T> clear() {
        return WritableGridModel::clear;
    }

    /**
     * Returns an initializer that fills the entire grid with the specified entity.
     *
     * @param entity the entity to fill the grid with
     * @param <T>    the type of grid entity
     * @return a grid initializer that fills the grid with the given entity
     */
    public static <T extends GridEntity> GridInitializer<T> constant(T entity) {
        return model -> model.fill(entity);
    }

    /**
     * Returns an initializer that sets the border cells of the grid to the specified entity.
     *
     * @param entity the entity to set at the border
     * @param <T>    the type of grid entity
     * @return a grid initializer that sets the border cells
     */
    public static <T extends GridEntity> GridInitializer<T> border(T entity) {
        return model -> {
            GridSize size = model.structure().size();
            for (GridCoordinate coordinate : model.structure().coordinatesList()) {
                int x = coordinate.x();
                int y = coordinate.y();
                if ((x == 0) || (y == 0) || (x == (size.width() - 1)) || (y == (size.height() - 1))) {
                    model.setEntity(coordinate, entity);
                }
            }
        };
    }

    /**
     * Returns an initializer that fills the grid in a checkerboard pattern using two entities.
     *
     * @param entity1 the entity for even cells
     * @param entity2 the entity for odd cells
     * @param <T>     the type of grid entity
     * @return a grid initializer with a checkerboard pattern
     */
    public static <T extends GridEntity> GridInitializer<T> checkerboard(T entity1, T entity2) {
        return model -> {
            for (GridCoordinate coordinate : model.structure().coordinatesList()) {
                int x = coordinate.x();
                int y = coordinate.y();
                T entity = (((x + y) % 2) == 0) ? entity1 : entity2;
                model.setEntity(coordinate, entity);
            }
        };
    }

    /**
     * Returns an initializer that sets the grid cells according to the provided list of cells.
     *
     * @param cells the list of grid cells to set
     * @param <T>   the type of grid entity
     * @return a grid initializer that sets the specified cells
     * @throws IndexOutOfBoundsException if the coordinate is not valid
     */
    public static <T extends GridEntity> GridInitializer<T> fromList(List<GridCell<T>> cells) {
        return model -> {
            for (GridCell<T> cell : cells) {
                model.setEntity(cell);
            }
        };
    }

    /**
     * Returns a random coordinate within the given grid size.
     *
     * @param size   the grid size
     * @param random the random number generator
     * @return a random grid coordinate
     */
    static GridCoordinate randomCoordinate(GridSize size, Random random) {
        int x = random.nextInt(size.width());
        int y = random.nextInt(size.height());
        return new GridCoordinate(x, y);
    }

    /**
     * Returns an initializer that fills the grid with entities generated randomly.
     *
     * @param generator the function to generate entities using the random object
     * @param random    the random number generator
     * @param <T>       the type of grid entity
     * @return a grid initializer that fills the grid randomly
     */
    public static <T extends GridEntity> GridInitializer<T> fillRandomly(Function<Random, T> generator, Random random) {
        return model -> model.fill(() -> generator.apply(random));
    }

    /**
     * Returns an initializer that places a fixed number of entities at random positions.
     * <p>
     * Placement is only performed if the {@code canReplaceExisting} predicate returns {@code true}
     * for the entity currently at the chosen position.
     * </p>
     *
     * @param count              the number of entities to place
     * @param entitySupplier     the supplier for entities to place
     * @param canReplaceExisting predicate to determine if an existing entity can be replaced
     * @param random             the random number generator
     * @param <T>                the type of grid entity
     * @return a grid initializer that places entities at random positions, using the replacement predicate
     * @throws IllegalStateException if not all entities could be placed within the maximum number of attempts
     */
    public static <T extends GridEntity> GridInitializer<T> placeRandomCounted(int count,
                                                                               Supplier<T> entitySupplier,
                                                                               Predicate<T> canReplaceExisting,
                                                                               Random random) {
        return model -> {
            int placed = 0;
            int gridArea = model.structure().size().area();
            int maxAttempts = Math.max(100, gridArea / 2); // Scales with grid size, minimum 100
            while (placed < count) {
                T nextEntity = entitySupplier.get();
                int attempts = 0;
                boolean nextEntityPlaced = false;
                while (!nextEntityPlaced && (attempts < maxAttempts)) {
                    GridCoordinate coordinate = randomCoordinate(model.structure().size(), random);
                    T existingEntity = model.getEntity(coordinate);
                    if (canReplaceExisting.test(existingEntity)) {
                        model.setEntity(coordinate, nextEntity);
                        placed++;
                        nextEntityPlaced = true;
                    }
                    attempts++;
                }
                if (!nextEntityPlaced) {
                    throw new IllegalStateException("Unable to place all entities within the maximum number of attempts.");
                }
            }
        };
    }

    /**
     * Returns an initializer that places entities at a random percentage of grid positions.
     * <p>
     * Placement is only performed if the {@code canReplaceExisting} predicate returns {@code true}
     * for the entity currently at the chosen position.
     * </p>
     *
     * @param entitySupplier     the supplier for entities to place
     * @param canReplaceExisting predicate to determine if an existing entity can be replaced
     * @param percent            the percentage of positions to fill (0.0 to 1.0)
     * @param random             the random number generator
     * @param <T>                the type of grid entity
     * @return a grid initializer that places entities at a random percentage of positions, using the replacement predicate
     * @throws IllegalStateException if not all entities could be placed within the maximum number of attempts
     */
    @SuppressWarnings({"NumericCastThatLosesPrecision"})
    public static <T extends GridEntity> GridInitializer<T> placeRandomPercent(Supplier<T> entitySupplier,
                                                                               Predicate<T> canReplaceExisting,
                                                                               double percent,
                                                                               Random random) {
        return model -> placeRandomCounted((int) Math.round(percent * model.structure().size().area()),
                entitySupplier, canReplaceExisting, random).initialize(model);
    }

    /**
     * Returns an initializer that places a fixed number of entities at shuffled positions.
     * <p>
     * The grid coordinates are shuffled once, and the method iterates over them a single time.
     * For each coordinate, it attempts to place the current entity if {@code canReplaceExisting} returns {@code true}
     * for the existing entity at that position. Only after a successful placement is a new entity generated.
     * If not enough suitable positions are found, placement stops and an exception is thrown.
     * </p>
     *
     * @param count              the number of entities to place
     * @param entitySupplier     the supplier for entities to place
     * @param canReplaceExisting predicate to determine if an existing entity can be replaced
     * @param random             the random number generator
     * @param <T>                the type of grid entity
     * @return a grid initializer that places entities at shuffled positions, skipping blocked cells
     * @throws IllegalStateException if not all entities could be placed
     */
    public static <T extends GridEntity> GridInitializer<T> placeShuffledCounted(int count,
                                                                                 Supplier<T> entitySupplier,
                                                                                 Predicate<T> canReplaceExisting,
                                                                                 Random random) {
        return model -> {
            int placed = 0;
            if (placed < count) {
                List<GridCoordinate> coordinates = model.structure().coordinatesList();
                Collections.shuffle(coordinates, random);
                T nextEntity = entitySupplier.get();
                for (GridCoordinate coordinate : coordinates) {
                    T existingEntity = model.getEntity(coordinate);
                    if (canReplaceExisting.test(existingEntity)) {
                        model.setEntity(coordinate, nextEntity);
                        placed++;
                        if (placed >= count) {
                            break;
                        }
                        nextEntity = entitySupplier.get();
                    }
                }
            }
            if (placed < count) {
                throw new IllegalStateException("Unable to place all entities. Only " + placed + " entities were placed.");
            }
        };
    }

    /**
     * Returns an initializer that places the given entity at each position with a certain probability,
     * otherwise places a fallback entity.
     *
     * @param entity      the entity to place with the given probability
     * @param probability the probability to place the entity (0.0 to 1.0)
     * @param fallback    the fallback entity to place otherwise
     * @param random      the random number generator
     * @param <T>         the type of grid entity
     * @return a grid initializer that places entities probabilistically
     */
    public static <T extends GridEntity> GridInitializer<T> placeWithProbability(T entity, double probability, T fallback, Random random) {
        return model ->
                model.fill(() -> (random.nextDouble() < probability) ? entity : fallback);
    }

    /**
     * Returns an initializer that places entities generated by the supplier at each position with a certain probability.
     *
     * @param entitySupplier the supplier for entities to place
     * @param probability    the probability to place an entity (0.0 to 1.0)
     * @param random         the random number generator
     * @param <T>            the type of grid entity
     * @return a grid initializer that places entities probabilistically
     */
    public static <T extends GridEntity> GridInitializer<T> placeWithProbability(Supplier<T> entitySupplier, double probability, Random random) {
        return model -> {
            for (GridCoordinate coordinate : model.structure().coordinatesList()) {
                if (random.nextDouble() < probability) {
                    model.setEntity(coordinate, entitySupplier.get());
                }
            }
        };
    }

}
