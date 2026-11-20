package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static factories for {@link GridPattern}: patterns from given offsets, the combination of patterns, and simple
 * shapes.
 *
 * <p>All returned patterns are unmodifiable and independent of the arguments. The shape factories return normalized
 * patterns; {@code of} and {@code combine} keep the given offsets.
 */
public final class GridPatterns {

    private GridPatterns() {
    }

    /**
     * Returns a pattern without entities.
     *
     * @param <ENT> the type of grid entity
     * @return an empty pattern
     */
    public static <ENT extends GridEntity> GridPattern<ENT> empty() {
        return Collections::emptyMap;
    }

    /**
     * Returns a pattern with the entities of the given map at their offsets.
     *
     * @param offsetMap the entities by offset
     * @param <ENT>     the type of grid entity
     * @return a pattern with the entries of the map
     */
    public static <ENT extends GridEntity> GridPattern<ENT> of(Map<GridOffset, ENT> offsetMap) {
        Map<GridOffset, ENT> copy = Map.copyOf(offsetMap);
        return () -> copy;
    }

    /**
     * Returns a pattern with the given entity at each of the given offsets; a repeated offset counts once.
     *
     * @param entity  the entity at each offset
     * @param offsets the offsets of the entity
     * @param <ENT>   the type of grid entity
     * @return a pattern with the entity at the offsets
     */
    public static <ENT extends GridEntity> GridPattern<ENT> of(ENT entity, Collection<GridOffset> offsets) {
        Map<GridOffset, ENT> offsetMap = new HashMap<>();
        for (GridOffset offset : offsets) {
            offsetMap.put(offset, entity);
        }
        return of(offsetMap);
    }

    /**
     * Returns a pattern with the entries of all given patterns; at an offset of several patterns, the entity of the
     * last of them wins.
     *
     * @param patterns the patterns to combine, in increasing priority
     * @param <ENT>    the type of grid entity
     * @return a pattern with the combined entries; an empty pattern if no pattern is given
     */
    @SafeVarargs
    public static <ENT extends GridEntity> GridPattern<ENT> combine(GridPattern<ENT>... patterns) {
        Map<GridOffset, ENT> offsetMap = new HashMap<>();
        for (GridPattern<ENT> pattern : patterns) {
            offsetMap.putAll(pattern.offsetMap());
        }
        return of(offsetMap);
    }

    /**
     * Returns a horizontal line of the given entity at the offsets (x, 0) for x in [0, length).
     *
     * @param entity the entity of the line
     * @param length the number of entities, at least 1
     * @param <ENT>  the type of grid entity
     * @return a normalized pattern of the line
     * @throws IllegalArgumentException if {@code length} is less than 1
     */
    public static <ENT extends GridEntity> GridPattern<ENT> horizontalLine(ENT entity, int length) {
        checkPositive(length, "length");
        List<GridOffset> offsets = new ArrayList<>(length);
        for (int x = 0; x < length; x++) {
            offsets.add(new GridOffset(x, 0));
        }
        return of(entity, offsets);
    }

    /**
     * Returns a vertical line of the given entity at the offsets (0, y) for y in [0, length).
     *
     * @param entity the entity of the line
     * @param length the number of entities, at least 1
     * @param <ENT>  the type of grid entity
     * @return a normalized pattern of the line
     * @throws IllegalArgumentException if {@code length} is less than 1
     */
    public static <ENT extends GridEntity> GridPattern<ENT> verticalLine(ENT entity, int length) {
        checkPositive(length, "length");
        List<GridOffset> offsets = new ArrayList<>(length);
        for (int y = 0; y < length; y++) {
            offsets.add(new GridOffset(0, y));
        }
        return of(entity, offsets);
    }

    /**
     * Returns the outline of a rectangle of the given entity: the first and last row and column of a bounding box of
     * the given size.
     *
     * @param entity the entity of the outline
     * @param width  the number of columns, at least 1
     * @param height the number of rows, at least 1
     * @param <ENT>  the type of grid entity
     * @return a normalized pattern of the outline
     * @throws IllegalArgumentException if {@code width} or {@code height} is less than 1
     */
    public static <ENT extends GridEntity> GridPattern<ENT> rectangleOutline(ENT entity, int width, int height) {
        checkPositive(width, "width");
        checkPositive(height, "height");
        List<GridOffset> offsets = new ArrayList<>(2 * (width + height));
        for (int x = 0; x < width; x++) {
            offsets.add(new GridOffset(x, 0));
            offsets.add(new GridOffset(x, height - 1));
        }
        for (int y = 1; y < (height - 1); y++) {
            offsets.add(new GridOffset(0, y));
            offsets.add(new GridOffset(width - 1, y));
        }
        return of(entity, offsets);
    }

    private static void checkPositive(int value, String name) {
        if (value < 1) {
            throw new IllegalArgumentException(name + " must be >= 1, but was: " + value);
        }
    }

}
