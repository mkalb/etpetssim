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
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Static factories for {@link GridInitializer} instances that fill a {@link WritableGridModel} or place entities
 * on it, by fixed patterns or at random.
 *
 * <p>The {@code fill...} factories set every cell of the grid; the {@code place...} factories set only selected
 * cells and keep the others. Factories with a {@code Runnable cancellationCheck} run the check before the first cell
 * and then regularly (see {@link WorkCheckpoints}); the check stops a long initialization by throwing an exception.
 * The parameters follow the order amount ({@code percent} or {@code probability}), entities, replacement rule or
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
     * Returns an initializer that fills each cell of the grid independently at the given probability with a new
     * entity from the supplier, and otherwise with the fallback.
     *
     * <p>The number of supplied entities is random with the expected value {@code probability} times the grid area;
     * use {@link #fillWithPercent(double, Supplier, GridEntity, Random, Runnable)} for an exact number.
     *
     * @param probability       the probability of a supplied entity per cell (0.0 to 1.0)
     * @param entitySupplier    the supplier, called once per supplied entity
     * @param fallback          the entity for the other cells
     * @param random            the random number generator, called once per cell in row-major order
     * @param cancellationCheck the check that may stop the initialization
     * @param <ENT>             the type of grid entity
     * @return a grid initializer that fills the grid at the given probability
     * @throws IllegalArgumentException if {@code probability} is outside the range [0.0, 1.0]
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> fillWithProbability(double probability,
                                                                                    Supplier<ENT> entitySupplier,
                                                                                    ENT fallback,
                                                                                    Random random,
                                                                                    Runnable cancellationCheck) {
        checkFraction(probability, "probability");
        return model -> forEachCoordinate(model.structure().size(), cancellationCheck,
                coordinate -> model.setEntity(coordinate,
                        (random.nextDouble() < probability) ? entitySupplier.get() : fallback));
    }

    /**
     * Returns an initializer that fills the given fraction of the grid cells, chosen at random, with new entities
     * from the supplier, and the other cells with the fallback.
     *
     * <p>The number of supplied entities is {@code percent} times the grid area, rounded; every selection of cells
     * with this number is equally likely. Use
     * {@link #fillWithProbability(double, Supplier, GridEntity, Random, Runnable)} to decide each cell independently.
     *
     * @param percent           the fraction of cells with supplied entities (0.0 to 1.0)
     * @param entitySupplier    the supplier, called once per supplied entity
     * @param fallback          the entity for the other cells
     * @param random            the random number generator, called once per cell in row-major order
     * @param cancellationCheck the check that may stop the initialization
     * @param <ENT>             the type of grid entity
     * @return a grid initializer that fills the grid with the given fraction of supplied entities
     * @throws IllegalArgumentException if {@code percent} is outside the range [0.0, 1.0]
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> fillWithPercent(double percent,
                                                                                Supplier<ENT> entitySupplier,
                                                                                ENT fallback,
                                                                                Random random,
                                                                                Runnable cancellationCheck) {
        checkFraction(percent, "percent");
        return model -> fillSelectedCells(model, percent, entitySupplier, fallback, random, cancellationCheck);
    }

    /**
     * Returns an initializer that places the entities of the given cells at their coordinates, in list order.
     *
     * <p>Existing entities at these coordinates are overwritten.
     *
     * @param cells             the cells to place
     * @param cancellationCheck the check that may stop the initialization
     * @param <ENT>             the type of grid entity
     * @return a grid initializer that places the given cells
     * @throws IndexOutOfBoundsException if a coordinate of the cells is not valid for the grid
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeCells(List<GridCell<ENT>> cells,
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
     * Returns an initializer that places the given entity at the outermost rows and columns of the grid.
     *
     * @param entity the entity for the border cells
     * @param <ENT>  the type of grid entity
     * @return a grid initializer that places the entity at the border
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeBorder(ENT entity) {
        return model -> {
            GridSize size = model.structure().size();
            int lastX = size.width() - 1;
            int lastY = size.height() - 1;
            for (int x = 0; x <= lastX; x++) {
                model.setEntity(new GridCoordinate(x, 0), entity);
                model.setEntity(new GridCoordinate(x, lastY), entity);
            }
            for (int y = 1; y < lastY; y++) {
                model.setEntity(new GridCoordinate(0, y), entity);
                model.setEntity(new GridCoordinate(lastX, y), entity);
            }
        };
    }

    /**
     * Returns an initializer that places the entities of the given pattern relative to an anchor coordinate.
     *
     * <p>Each pattern entry is placed at the anchor plus its offset. Entries whose target coordinate lies outside
     * the grid are skipped, and existing entities at valid target coordinates are overwritten.
     *
     * <p>The offsets are added to the anchor unchanged; on triangle and hexagon grids, a pattern keeps its shape only
     * at anchors with the same column and row parity as the anchor it was designed for.
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
     * Returns an initializer that places each of the given entities at a random cell whose current entity may be
     * replaced.
     *
     * <p>The replaceable cells are shuffled with {@code random}, and the entities are placed in list order at the
     * first {@code entities.size()} of them. To place a number of equal entities, pass
     * {@link Collections#nCopies(int, Object)}.
     *
     * @param entities           the entities to place, each exactly once
     * @param canReplaceExisting the predicate that tells whether the current entity of a cell may be replaced
     * @param random             the random number generator for the shuffle
     * @param cancellationCheck  the check that may stop the initialization
     * @param <ENT>              the type of grid entity
     * @return a grid initializer that places all entities at random cells
     * @throws IllegalStateException if fewer cells than {@code entities.size()} may be replaced; the initializer
     *                               then places no entity
     */
    public static <ENT extends GridEntity> GridInitializer<ENT> placeAtRandomCells(List<ENT> entities,
                                                                                   Predicate<ENT> canReplaceExisting,
                                                                                   Random random,
                                                                                   Runnable cancellationCheck) {
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

    private static <ENT extends GridEntity> void fillSelectedCells(WritableGridModel<ENT> model, double percent,
                                                                   Supplier<ENT> entitySupplier, ENT fallback,
                                                                   Random random, Runnable cancellationCheck) {
        GridSize size = model.structure().size();
        int remainingCells = size.area();
        int remainingEntities = Math.toIntExact(Math.round(percent * remainingCells));
        int index = 0;
        for (int y = 0; y < size.height(); y++) {
            for (int x = 0; x < size.width(); x++) {
                if (WorkCheckpoints.isCancellationCheckpoint(index)) {
                    cancellationCheck.run();
                }
                index++;
                // Selection sampling: a cell is selected with the probability remainingEntities / remainingCells.
                if (random.nextInt(remainingCells) < remainingEntities) {
                    model.setEntity(new GridCoordinate(x, y), entitySupplier.get());
                    remainingEntities--;
                } else {
                    model.setEntity(new GridCoordinate(x, y), fallback);
                }
                remainingCells--;
            }
        }
    }

}
