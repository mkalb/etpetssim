package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Static factories for {@link GridPattern} instances: basic patterns from given offsets, the combination of
 * patterns, and simple shapes.
 *
 * <p>All returned patterns are unmodifiable and independent of the arguments. The shape factories return
 * normalized patterns (top-left corner of the bounding box at offset (0, 0)); {@code of}, {@code singleton}, and
 * {@code combine} keep the given offsets.
 */
public final class GridPatterns {

    private GridPatterns() {
    }

    /**
     * Returns an empty pattern.
     *
     * @param <ENT> the type of grid entity
     * @return a pattern without entities
     */
    public static <ENT extends GridEntity> GridPattern<ENT> empty() {
        return Collections::emptyMap;
    }

    /**
     * Returns a pattern with the entities of the given map at their offsets.
     *
     * @param map   the entities by offset
     * @param <ENT> the type of grid entity
     * @return a pattern with a copy of the map
     */
    public static <ENT extends GridEntity> GridPattern<ENT> of(Map<GridOffset, ENT> map) {
        Map<GridOffset, ENT> mapCopy = Map.copyOf(map);
        return () -> mapCopy;
    }

    /**
     * Returns a pattern with the given entity at each of the given offsets; duplicate offsets count once.
     *
     * @param entity  the entity at each offset
     * @param offsets the offsets of the entity
     * @param <ENT>   the type of grid entity
     * @return a pattern with the entity at the offsets
     */
    public static <ENT extends GridEntity> GridPattern<ENT> of(ENT entity, Collection<GridOffset> offsets) {
        Map<GridOffset, ENT> map = new HashMap<>();
        for (GridOffset offset : offsets) {
            map.put(offset, entity);
        }
        return of(map);
    }

    /**
     * Returns a pattern with the given entity at a single offset.
     *
     * @param entity the entity
     * @param offset the offset of the entity
     * @param <ENT>  the type of grid entity
     * @return a pattern with one entity
     */
    public static <ENT extends GridEntity> GridPattern<ENT> singleton(ENT entity, GridOffset offset) {
        return () -> Map.of(offset, entity);
    }

    /**
     * Returns a pattern with the entries of all given patterns; for an offset in several patterns, the entity of the
     * last of them wins.
     *
     * @param patterns the patterns to combine, in increasing priority
     * @param <ENT>    the type of grid entity
     * @return a pattern with the combined entries
     */
    @SafeVarargs
    public static <ENT extends GridEntity> GridPattern<ENT> combine(GridPattern<ENT>... patterns) {
        Map<GridOffset, ENT> combined = new HashMap<>();
        for (GridPattern<ENT> pattern : patterns) {
            combined.putAll(pattern.offsetMap());
        }
        return of(combined);
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
     * Returns the outline of a rectangle of the given entity: the first and last row and column of a bounding box
     * with the given size.
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
        List<GridOffset> offsets = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((y == 0) || (y == (height - 1)) || (x == 0) || (x == (width - 1))) {
                    offsets.add(new GridOffset(x, y));
                }
            }
        }
        return of(entity, offsets);
    }

    /**
     * Returns the outline of a circle of the given entity around the offset (radius, radius), computed with the
     * midpoint circle algorithm.
     *
     * <p>The algorithm assumes square cells; on other cell shapes, the outline is a distorted ring. A radius of
     * {@code 0} returns a single entity at (0, 0).
     *
     * @param entity the entity of the outline
     * @param radius the radius in cells, at least 0
     * @param <ENT>  the type of grid entity
     * @return a normalized pattern of the outline
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public static <ENT extends GridEntity> GridPattern<ENT> circleOutline(ENT entity, int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be >= 0, but was: " + radius);
        }
        Set<GridOffset> offsets = new HashSet<>();
        int x = radius;
        int y = 0;
        int err = 0;
        while (x >= y) {
            offsets.add(new GridOffset(radius + x, radius + y));
            offsets.add(new GridOffset(radius + y, radius + x));
            offsets.add(new GridOffset(radius - y, radius + x));
            offsets.add(new GridOffset(radius - x, radius + y));
            offsets.add(new GridOffset(radius - x, radius - y));
            offsets.add(new GridOffset(radius - y, radius - x));
            offsets.add(new GridOffset(radius + y, radius - x));
            offsets.add(new GridOffset(radius + x, radius - y));

            y++;
            if (err <= 0) {
                err += (2 * y) + 1;
            } else {
                x--;
                err -= (2 * x) + 1;
            }
        }
        return of(entity, offsets);
    }

    private static void checkPositive(int value, String name) {
        if (value < 1) {
            throw new IllegalArgumentException(name + " must be >= 1, but was: " + value);
        }
    }

}
