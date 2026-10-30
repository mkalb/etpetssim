package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Static factories for {@link GridInitializer} instances that fill a {@link WritableGridModel} or place entities
 * on it, by fixed patterns or at random.
 *
 * <p>The {@code fill...} factories set every cell of the grid; the {@code place...} factories set only selected
 * cells and keep the others. Factories with a {@code Runnable cancellationCheck} run the check regularly (see
 * {@link WorkCheckpoints}); the check stops a long initialization by throwing an exception. The parameters follow
 * the order amount ({@code count}, {@code percent}, or {@code probability}), entities, replacement rule or
 * fallback, {@code random}, {@code cancellationCheck}.
 */
public final class GridInitializers {

    private GridInitializers() {
    }

    /**
     * Returns an initializer that leaves the grid unchanged.
     *
     * @param <ENT> the type of grid entity
     * @return a no-op grid initializer
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> identity() {
        return _ -> {};
    }

    /**
     * Returns an initializer that sets the cells of the given list, in list order.
     *
     * @param cells             the cells to set
     * @param cancellationCheck the check that may stop the initialization
     * @param <ENT>             the type of grid entity
     * @return a grid initializer that sets the given cells
     * @throws IndexOutOfBoundsException if a coordinate of the cells is not valid for the grid
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> fromList(List<GridCell<ENT>> cells,
                                                                         Runnable cancellationCheck) {
        return model -> {
            for (int index = 0; index < cells.size(); index++) {
                if (WorkCheckpoints.isCancellationCheckpoint(index)) {
                    cancellationCheck.run();
                }
                model.setEntity(cells.get(index));
            }
        };
    }

    /**
     * Returns an initializer that fills every cell of the grid with the given entity.
     *
     * @param entity the entity for every cell
     * @param <ENT>  the type of grid entity
     * @return a grid initializer that fills the grid with the entity
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> fillConstant(ENT entity) {
        return model -> model.fill(entity);
    }

    /**
     * Returns an initializer that fills every cell of the grid with a new entity from the supplier.
     *
     * @param entitySupplier the supplier, called once per cell in row-major order
     * @param <ENT>          the type of grid entity
     * @return a grid initializer that fills the grid from the supplier
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> fillFromSupplier(Supplier<ENT> entitySupplier) {
        return model -> model.fill(entitySupplier);
    }

    /**
     * Returns an initializer that fills every cell of the grid with an entity that the generator creates from the
     * random number generator.
     *
     * @param entityGenerator the generator, called once per cell in row-major order with {@code random}
     * @param random          the random number generator passed to the generator
     * @param <ENT>           the type of grid entity
     * @return a grid initializer that fills the grid with generated entities
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> fillRandomly(Function<Random, ENT> entityGenerator,
                                                                             Random random) {
        return model -> model.fill(() -> entityGenerator.apply(random));
    }

    /**
     * Returns an initializer that fills every cell of the grid independently with the entity at the given
     * probability, otherwise with the fallback.
     *
     * <p>The number of entities is random with the expected value {@code probability} times the grid area; use
     * {@link #fillRandomPercent(double, Supplier, GridEntity, Random, Runnable)} for an exact number.
     *
     * @param probability the probability of the entity per cell (0.0 to 1.0)
     * @param entity      the entity to set at the given probability
     * @param fallback    the entity for the other cells
     * @param random      the random number generator, called once per cell in row-major order
     * @param <ENT>       the type of grid entity
     * @return a grid initializer that fills the grid at the given probability
     * @throws IllegalArgumentException if {@code probability} is outside the range [0.0, 1.0]
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> fillWithProbability(double probability, ENT entity,
                                                                                    ENT fallback, Random random) {
        checkFraction(probability, "probability");
        return model -> model.fill(() -> (random.nextDouble() < probability) ? entity : fallback);
    }

    /**
     * Returns an initializer that fills the grid so that the given fraction of its cells holds entities from the
     * supplier and the other cells hold the fallback.
     *
     * <p>The number of supplied entities is {@code percent} times the grid area, rounded. For a fraction up to
     * {@code 0.8}, the initializer fills the grid with the fallback and then places the supplied entities at random
     * cells; for a larger fraction, it fills the grid from the supplier and then places the fallback at random cells.
     * In the second case, the supplier is called once per cell, so its side effects occur up to the grid area times.
     *
     * @param percent           the fraction of cells with supplied entities (0.0 to 1.0)
     * @param entitySupplier    the supplier of the entities to place
     * @param fallback          the entity for the other cells
     * @param random            the random number generator for the cell selection
     * @param cancellationCheck the check that may stop the initialization; it also runs before any cell is set
     * @param <ENT>             the type of grid entity
     * @return a grid initializer that fills the grid with the given fraction of supplied entities
     * @throws IllegalArgumentException if {@code percent} is outside the range [0.0, 1.0]
     */
    @SuppressWarnings({"NumericCastThatLosesPrecision", "MagicNumber"})
    public static <ENT extends GridEntity> GridInitializer<ENT> fillRandomPercent(double percent,
                                                                                  Supplier<ENT> entitySupplier,
                                                                                  ENT fallback,
                                                                                  Random random,
                                                                                  Runnable cancellationCheck) {
        checkFraction(percent, "percent");
        return model -> {
            cancellationCheck.run();
            int gridArea = model.structure().size().area();
            if (percent <= 0.8d) {
                int entityCount = (int) Math.round(percent * gridArea);
                fillCells(model, () -> fallback, cancellationCheck);
                placeRandomCount(model, entityCount, entitySupplier, fallback::equals, random, cancellationCheck);
            } else {
                int fallbackCount = (int) Math.round((1.0d - percent) * gridArea);
                fillCells(model, entitySupplier, cancellationCheck);
                placeRandomCount(model, fallbackCount,
                        () -> fallback, Predicate.not(fallback::equals), random, cancellationCheck);
            }
        };
    }

    /**
     * Returns an initializer that sets the outermost rows and columns of the grid to the given entity.
     *
     * @param entity the entity for the border cells
     * @param <ENT>  the type of grid entity
     * @return a grid initializer that places the entity at the border
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeBorder(ENT entity) {
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
     * Returns an initializer that places the entities of the given pattern relative to an anchor coordinate.
     *
     * <p>Each pattern entry is placed at the anchor plus its offset. Entries whose target coordinate lies outside
     * the grid are skipped, and existing entities at valid target coordinates are overwritten.
     *
     * @param pattern the pattern of entities to place
     * @param anchor  the coordinate at which the origin of the pattern is placed
     * @param <ENT>   the type of grid entity
     * @return a grid initializer that places the pattern
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placePattern(GridPattern<ENT> pattern,
                                                                             GridCoordinate anchor) {
        return model -> pattern.offsetMap().forEach((offset, entity) -> {
            GridCoordinate target = anchor.offset(offset);
            if (model.isCoordinateValid(target)) {
                model.setEntity(target, entity);
            }
        });
    }

    /**
     * Returns an initializer that places a new entity from the supplier at every cell independently at the given
     * probability and keeps the other cells.
     *
     * <p>Existing entities are overwritten.
     *
     * @param probability    the probability of an entity per cell (0.0 to 1.0)
     * @param entitySupplier the supplier, called once per placed entity
     * @param random         the random number generator, called once per cell in row-major order
     * @param <ENT>          the type of grid entity
     * @return a grid initializer that places entities at the given probability
     * @throws IllegalArgumentException if {@code probability} is outside the range [0.0, 1.0]
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeWithProbability(double probability,
                                                                                     Supplier<ENT> entitySupplier,
                                                                                     Random random) {
        checkFraction(probability, "probability");
        return model -> {
            for (GridCoordinate coordinate : model.structure().coordinatesList()) {
                if (random.nextDouble() < probability) {
                    model.setEntity(coordinate, entitySupplier.get());
                }
            }
        };
    }

    /**
     * Returns an initializer that places a fixed number of entities at random cells whose current entity may be
     * replaced.
     *
     * <p>For each entity, random cells are tried up to a maximum number of attempts (at least 100, or half of the
     * grid area if larger).
     *
     * @param count              the number of entities to place
     * @param entitySupplier     the supplier of the entities to place
     * @param canReplaceExisting the predicate that tells whether the current entity of a cell may be replaced
     * @param random             the random number generator
     * @param <ENT>              the type of grid entity
     * @return a grid initializer that places the entities at random cells
     * @throws IllegalStateException if an entity cannot be placed within the maximum number of attempts
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeRandomCount(int count,
                                                                                 Supplier<ENT> entitySupplier,
                                                                                 Predicate<ENT> canReplaceExisting,
                                                                                 Random random) {
        return model -> placeRandomCount(model, count, entitySupplier, canReplaceExisting, random, () -> {});
    }

    /**
     * Returns an initializer that places entities at the given fraction of the grid cells, chosen at random among
     * the cells whose current entity may be replaced.
     *
     * <p>The number of entities is {@code percent} times the grid area, rounded; they are placed as by
     * {@link #placeRandomCount(int, Supplier, Predicate, Random)}.
     *
     * @param percent            the fraction of cells to place entities at (0.0 to 1.0)
     * @param entitySupplier     the supplier of the entities to place
     * @param canReplaceExisting the predicate that tells whether the current entity of a cell may be replaced
     * @param random             the random number generator
     * @param <ENT>              the type of grid entity
     * @return a grid initializer that places the entities at random cells
     * @throws IllegalArgumentException if {@code percent} is outside the range [0.0, 1.0]
     * @throws IllegalStateException    if an entity cannot be placed within the maximum number of attempts
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static <ENT extends GridEntity> GridInitializer<ENT> placeRandomPercent(double percent,
                                                                                   Supplier<ENT> entitySupplier,
                                                                                   Predicate<ENT> canReplaceExisting,
                                                                                   Random random) {
        checkFraction(percent, "percent");
        return model -> placeRandomCount(model, (int) Math.round(percent * model.structure().size().area()),
                entitySupplier, canReplaceExisting, random, () -> {});
    }

    /**
     * Returns an initializer that places a fixed number of entities at shuffled cells whose current entity may be
     * replaced.
     *
     * <p>The grid coordinates are shuffled once and visited a single time. A new entity is taken from the supplier
     * only after the previous one has been placed.
     *
     * @param count              the number of entities to place
     * @param entitySupplier     the supplier of the entities to place
     * @param canReplaceExisting the predicate that tells whether the current entity of a cell may be replaced
     * @param random             the random number generator for the shuffle
     * @param cancellationCheck  the check that may stop the initialization
     * @param <ENT>              the type of grid entity
     * @return a grid initializer that places the entities at shuffled cells
     * @throws IllegalStateException if fewer cells than {@code count} may be replaced; the entities placed so far
     *                               stay
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeShuffledCount(int count,
                                                                                   Supplier<ENT> entitySupplier,
                                                                                   Predicate<ENT> canReplaceExisting,
                                                                                   Random random,
                                                                                   Runnable cancellationCheck) {
        return model -> {
            int placed = 0;
            if (count > 0) {
                List<GridCoordinate> coordinates = model.structure().coordinatesList();
                Collections.shuffle(coordinates, random);
                ENT nextEntity = entitySupplier.get();
                for (int index = 0; index < coordinates.size(); index++) {
                    if (WorkCheckpoints.isCancellationCheckpoint(index)) {
                        cancellationCheck.run();
                    }
                    GridCoordinate coordinate = coordinates.get(index);
                    ENT existingEntity = model.getEntity(coordinate);
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
                throw new IllegalStateException(
                        "Unable to place all entities: placed=" + placed + ", count=" + count);
            }
        };
    }

    /**
     * Returns an initializer that places each of the given entities at a random cell whose current entity may be
     * replaced.
     *
     * <p>The replaceable cells are shuffled with {@code random}, and the entities are placed in list order at the
     * first {@code entities.size()} of them. The capacity is checked before any entity is placed.
     *
     * @param entities           the entities to place, each exactly once
     * @param canReplaceExisting the predicate that tells whether the current entity of a cell may be replaced
     * @param random             the random number generator for the shuffle
     * @param cancellationCheck  the check that may stop the initialization
     * @param <ENT>              the type of grid entity
     * @return a grid initializer that places all entities at random cells
     * @throws IllegalStateException if fewer cells than {@code entities.size()} may be replaced; no entity is placed
     *                               then
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeAllAtRandomPositions(
            List<ENT> entities, Predicate<ENT> canReplaceExisting, Random random, Runnable cancellationCheck) {
        return model -> {
            var freeCoordinates = new ArrayList<GridCoordinate>();
            forEachCoordinate(model.structure().size(), cancellationCheck, coordinate -> {
                if (canReplaceExisting.test(model.getEntity(coordinate))) {
                    freeCoordinates.add(coordinate);
                }
            });
            if (freeCoordinates.size() < entities.size()) {
                throw new IllegalStateException("Unable to place all entities: entityCount=" + entities.size()
                        + ", replaceableCellCount=" + freeCoordinates.size());
            }

            Collections.shuffle(freeCoordinates, random);

            for (int index = 0; index < entities.size(); index++) {
                if (WorkCheckpoints.isCancellationCheckpoint(index)) {
                    cancellationCheck.run();
                }
                model.setEntity(freeCoordinates.get(index), entities.get(index));
            }
        };
    }

    private static void checkFraction(double value, String name) {
        if ((value < 0.0d) || (value > 1.0d)) {
            throw new IllegalArgumentException(name + " must be in [0.0, 1.0], but was: " + value);
        }
    }

    private static GridCoordinate randomCoordinate(GridSize size, Random random) {
        int x = random.nextInt(size.width());
        int y = random.nextInt(size.height());
        return new GridCoordinate(x, y);
    }

    private static <ENT extends GridEntity> void placeRandomCount(WritableGridModel<ENT> model, int count,
                                                                  Supplier<ENT> entitySupplier,
                                                                  Predicate<ENT> canReplaceExisting, Random random,
                                                                  Runnable cancellationCheck) {
        int placed = 0;
        int checkpointIndex = 0;
        int gridArea = model.structure().size().area();
        int maxAttempts = Math.max(100, gridArea / 2);
        while (placed < count) {
            ENT nextEntity = entitySupplier.get();
            int attempts = 0;
            boolean nextEntityPlaced = false;
            while (!nextEntityPlaced && (attempts < maxAttempts)) {
                if (WorkCheckpoints.isCancellationCheckpoint(checkpointIndex)) {
                    cancellationCheck.run();
                }
                checkpointIndex++;
                GridCoordinate coordinate = randomCoordinate(model.structure().size(), random);
                ENT existingEntity = model.getEntity(coordinate);
                if (canReplaceExisting.test(existingEntity)) {
                    model.setEntity(coordinate, nextEntity);
                    placed++;
                    nextEntityPlaced = true;
                }
                attempts++;
            }
            if (!nextEntityPlaced) {
                throw new IllegalStateException("Unable to place all entities: placed=" + placed + ", count=" + count
                        + ", maxAttempts=" + maxAttempts);
            }
        }
    }

    private static <ENT extends GridEntity> void fillCells(WritableGridModel<ENT> model, Supplier<ENT> entitySupplier,
                                                           Runnable cancellationCheck) {
        forEachCoordinate(model.structure().size(), cancellationCheck,
                coordinate -> model.setEntity(coordinate, entitySupplier.get()));
    }

    private static void forEachCoordinate(GridSize size, Runnable cancellationCheck,
                                          Consumer<GridCoordinate> action) {
        int index = 0;
        for (int y = 0; y < size.height(); y++) {
            for (int x = 0; x < size.width(); x++) {
                if (WorkCheckpoints.isCancellationCheckpoint(index)) {
                    cancellationCheck.run();
                }
                index++;
                action.accept(new GridCoordinate(x, y));
            }
        }
    }

}
