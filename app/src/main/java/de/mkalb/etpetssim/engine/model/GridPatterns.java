package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridOffset;

import java.util.*;

/**
 * Utility class providing various static factory methods for creating {@link GridPattern} instances.
 * <p>
 * A {@code GridPattern} provides a mapping from {@link GridOffset} to {@link GridEntity} instances,
 * describing how entities are arranged relative to an origin (0, 0).
 * <p>
 * <b>Normalization:</b> It is recommended that all patterns are created in normalized form,
 * meaning the top-left corner of the pattern's bounding box is always at offset (0, 0).
 * If a pattern is not normalized, this must be clearly documented in its implementation.
 */
public final class GridPatterns {

    /**
     * Private constructor to prevent instantiation.
     */
    private GridPatterns() {
    }

    /**
     * Returns an empty {@code GridPattern} containing no entities.
     *
     * @param <T> the type of {@link GridEntity}
     * @return an empty pattern
     */
    public static <T extends GridEntity> GridPattern<T> empty() {
        return Collections::emptyMap;
    }

    /**
     * Creates a {@code GridPattern} from the given map of offsets to entities.
     * <p>
     * The returned pattern contains all entries from the provided map. The pattern is
     * <b>not guaranteed to be normalized</b>; offsets may have arbitrary values.
     * <p>
     * Modifications to the original map after calling this method do not affect the pattern.
     *
     * @param map the map of offsets to entities
     * @param <T> the type of {@link GridEntity}
     * @return a pattern containing the specified mapping
     */
    public static <T extends GridEntity> GridPattern<T> of(Map<GridOffset, T> map) {
        return () -> new HashMap<>(map);
    }

    /**
     * Creates a {@code GridPattern} where the specified entity is placed at multiple offsets.
     * <p>
     * The returned pattern contains the given entity at each offset in the provided collection.
     * The pattern is <b>not guaranteed to be normalized</b>; offsets may have arbitrary values.
     * <p>
     * Modifications to the original collection of offsets after calling this method do not affect the pattern.
     *
     * @param entity the entity to place at each offset
     * @param offsets the collection of offsets where the entity will be placed
     * @param <T> the type of {@link GridEntity}
     * @return a pattern containing the entity at the specified offsets
     */
    public static <T extends GridEntity> GridPattern<T> of(T entity, Collection<GridOffset> offsets) {
        Map<GridOffset, T> map = new HashMap<>();
        for (GridOffset offset : offsets) {
            map.put(offset, entity);
        }
        return () -> map;
    }

    /**
     * Creates a {@code GridPattern} containing a single entity at the specified offset.
     * <p>
     * The returned pattern contains exactly one entry at the given offset. The pattern is
     * <b>not guaranteed to be normalized</b>; the offset may be any value.
     *
     * @param <T>    the type of {@link GridEntity}
     * @param entity the entity to place
     * @param offset the offset of the entity
     * @return a singleton pattern containing the entity at the specified offset
     */
    public static <T extends GridEntity> GridPattern<T> singleton(T entity, GridOffset offset) {
        return () -> Map.of(offset, entity);
    }

    /**
     * Combines multiple {@code GridPattern} instances into a single pattern.
     * <p>
     * All entries from the provided patterns are merged into one pattern. If multiple patterns
     * contain the same offset, the entity from the last pattern in the argument list is used.
     * <p>
     * The resulting pattern is <b>not guaranteed to be normalized</b>.
     *
     * @param patterns the patterns to combine
     * @param <T> the type of {@link GridEntity}
     * @return a combined pattern containing all entries from the input patterns
     */
    @SafeVarargs
    public static <T extends GridEntity> GridPattern<T> combine(GridPattern<T>... patterns) {
        Map<GridOffset, T> combined = new HashMap<>();
        for (GridPattern<T> pattern : patterns) {
            combined.putAll(pattern.offsetMap());
        }
        return () -> combined;
    }

    /**
     * Creates a {@code GridPattern} representing a horizontal line of the given length,
     * starting at offset (0, 0) and extending to the right.
     * <p>
     * The returned pattern contains the specified entity at each offset (x, 0) for x in [0, length).
     * The pattern is <b>normalized</b> with its top-left at (0, 0).
     *
     * @param entity the entity to place at each position in the line
     * @param length the length of the line (number of entities)
     * @param <T>    the type of {@link GridEntity}
     * @return a pattern representing a horizontal line
     */
    public static <T extends GridEntity> GridPattern<T> horizontalLine(T entity, int length) {
        Map<GridOffset, T> map = new HashMap<>();
        for (int x = 0; x < length; x++) {
            map.put(new GridOffset(x, 0), entity);
        }
        return () -> map;
    }

    /**
     * Creates a {@code GridPattern} representing a vertical line of the given length,
     * starting at offset (0, 0) and extending downward.
     * <p>
     * The returned pattern contains the specified entity at each offset (0, y) for y in [0, length).
     * The pattern is <b>normalized</b> with its top-left at (0, 0).
     *
     * @param entity the entity to place at each position in the line
     * @param length the length of the line (number of entities)
     * @param <T>    the type of {@link GridEntity}
     * @return a pattern representing a vertical line
     */
    public static <T extends GridEntity> GridPattern<T> verticalLine(T entity, int length) {
        Map<GridOffset, T> map = new HashMap<>();
        for (int y = 0; y < length; y++) {
            map.put(new GridOffset(0, y), entity);
        }
        return () -> map;
    }

    /**
     * Creates a {@code GridPattern} representing the border (stroke) of a rectangle
     * with the given width and height, starting at offset (0, 0).
     * <p>
     * The returned pattern contains the specified entity at all positions on the border
     * (top, bottom, left, and right edges) of the rectangle. The pattern is <b>normalized</b>
     * with its top-left at (0, 0).
     *
     * @param stroke the entity to place at each border position
     * @param width  the width of the rectangle (number of columns)
     * @param height the height of the rectangle (number of rows)
     * @param <T>    the type of {@link GridEntity}
     * @return a pattern representing the rectangle border
     */
    public static <T extends GridEntity> GridPattern<T> rectangle(T stroke, int width, int height) {
        Map<GridOffset, T> map = new HashMap<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((y == 0) || (y == (height - 1)) || (x == 0) || (x == (width - 1))) {
                    map.put(new GridOffset(x, y), stroke);
                }
            }
        }
        return () -> map;
    }

    /**
     * Creates a {@code GridPattern} representing the border (stroke) of a circle
     * with the given integer radius, using the midpoint circle algorithm.
     * <p>
     * The returned pattern contains the specified entity at all positions on the border
     * of a circle centered at (radius, radius). The pattern is <b>normalized</b>
     * so that the top-left of its bounding box is at offset (0, 0).
     *
     * @param stroke the entity to place at each border position
     * @param radius the radius of the circle (in grid units)
     * @param <T>    the type of {@link GridEntity}
     * @return a pattern representing the circle border
     */
    public static <T extends GridEntity> GridPattern<T> circle(T stroke, int radius) {
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

        // Normalize offsets so top-left is at (0, 0)
        int minDx = offsets.stream().mapToInt(GridOffset::dx).min().orElse(0);
        int minDy = offsets.stream().mapToInt(GridOffset::dy).min().orElse(0);
        Map<GridOffset, T> map = new HashMap<>();
        for (GridOffset o : offsets) {
            map.put(new GridOffset(o.dx() - minDx, o.dy() - minDy), stroke);
        }
        return () -> map;
    }

}
