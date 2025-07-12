package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridOffset;

import java.util.*;
import java.util.function.*;

/**
 * Functional interface representing a fixed pattern of entities on a grid.
 * <p>
 * A {@code GridPattern} provides a mapping from {@link GridOffset} to {@link GridEntity} instances,
 * describing how entities are arranged relative to an origin (0, 0).
 * <p>
 * <b>Normalization:</b> It is recommended that all patterns are created in normalized form,
 * meaning the top-left corner of the pattern's bounding box is always at offset (0, 0).
 * If a pattern is not normalized, this must be clearly documented in its implementation.
 *
 * @param <T> the type of {@link GridEntity} contained in the pattern
 */
@FunctionalInterface
public interface GridPattern<T extends GridEntity> {

    /**
     * Returns the mapping of offsets to entities that defines this pattern.
     * <p>
     * It is recommended that the offsets are normalized so that the top-left corner
     * of its bounding box is at offset (0, 0).
     *
     * @return the map of offsets to entities
     */
    Map<GridOffset, T> offsetMap();

    /**
     * Returns the number of entities in this pattern.
     *
     * @return the size of the pattern
     */
    default int size() {
        return offsetMap().size();
    }

    /**
     * Returns {@code true} if this pattern contains no entities.
     *
     * @return {@code true} if the pattern is empty, {@code false} otherwise
     */
    default boolean isEmpty() {
        return offsetMap().isEmpty();
    }

    /**
     * Returns the width of the pattern's bounding box, in grid units.
     *
     * @return the width of the pattern
     */
    default int width() {
        return (offsetMap().keySet().stream()
                           .mapToInt(GridOffset::dx)
                           .max()
                           .orElse(0) - offsetMap().keySet().stream()
                                                   .mapToInt(GridOffset::dx)
                                                   .min()
                                                   .orElse(0)) + 1;
    }

    /**
     * Returns the height of the pattern's bounding box, in grid units.
     *
     * @return the height of the pattern
     */
    default int height() {
        return (offsetMap().keySet().stream()
                           .mapToInt(GridOffset::dy)
                           .max()
                           .orElse(0) - offsetMap().keySet().stream()
                                                   .mapToInt(GridOffset::dy)
                                                   .min()
                                                   .orElse(0)) + 1;
    }

    /**
     * Returns a new pattern with all offsets shifted by the given offset.
     *
     * @param offset the offset to add to each pattern entry
     * @return a shifted pattern
     */
    default GridPattern<T> shifted(GridOffset offset) {
        return () -> {
            Map<GridOffset, T> shifted = new HashMap<>();
            for (var entry : offsetMap().entrySet()) {
                GridOffset o = entry.getKey();
                shifted.put(new GridOffset(o.dx() + offset.dx(), o.dy() + offset.dy()), entry.getValue());
            }
            return shifted;
        };
    }

    /**
     * Returns {@code true} if the top-left corner of the pattern's bounding box is at offset (0, 0).
     *
     * @return {@code true} if the pattern is normalized, {@code false} otherwise
     */
    default boolean isTopLeftAtOrigin() {
        var keys = offsetMap().keySet();
        int minDx = keys.stream().mapToInt(GridOffset::dx).min().orElse(0);
        int minDy = keys.stream().mapToInt(GridOffset::dy).min().orElse(0);
        return (minDx == 0) && (minDy == 0);
    }

    /**
     * Returns a normalized version of this pattern, so that the top-left corner
     * of its bounding box is at offset (0, 0).
     *
     * @return a normalized pattern
     */
    default GridPattern<T> normalized() {
        var keys = offsetMap().keySet();
        int minDx = keys.stream().mapToInt(GridOffset::dx).min().orElse(0);
        int minDy = keys.stream().mapToInt(GridOffset::dy).min().orElse(0);
        if ((minDx == 0) && (minDy == 0)) {
            return this;
        }
        return shifted(new GridOffset(-minDx, -minDy));
    }

    /**
     * Returns a horizontally mirrored version of this pattern (flipped along the vertical axis).
     *
     * @return a pattern flipped horizontally
     */
    default GridPattern<T> flipX() {
        return () -> {
            Map<GridOffset, T> original = offsetMap();
            int maxDx = original.keySet().stream().mapToInt(GridOffset::dx).max().orElse(0);
            int minDx = original.keySet().stream().mapToInt(GridOffset::dx).min().orElse(0);
            int mid = minDx + maxDx;
            Map<GridOffset, T> flipped = new HashMap<>();
            for (var entry : original.entrySet()) {
                GridOffset o = entry.getKey();
                flipped.put(new GridOffset(mid - o.dx(), o.dy()), entry.getValue());
            }
            return flipped;
        };
    }

    /**
     * Returns a vertically mirrored version of this pattern (flipped along the horizontal axis).
     *
     * @return a pattern flipped vertically
     */
    default GridPattern<T> flipY() {
        return () -> {
            Map<GridOffset, T> original = offsetMap();
            int maxDy = original.keySet().stream().mapToInt(GridOffset::dy).max().orElse(0);
            int minDy = original.keySet().stream().mapToInt(GridOffset::dy).min().orElse(0);
            int mid = minDy + maxDy;
            Map<GridOffset, T> flipped = new HashMap<>();
            for (var entry : original.entrySet()) {
                GridOffset o = entry.getKey();
                flipped.put(new GridOffset(o.dx(), mid - o.dy()), entry.getValue());
            }
            return flipped;
        };
    }

    /**
     * Returns a new pattern rotated 90 degrees clockwise around its bounding box.
     * <p>
     * The top-left of the rotated pattern is at offset (0, 0).
     *
     * @return a pattern rotated 90° clockwise
     */
    default GridPattern<T> rotate90() {
        Map<GridOffset, T> original = offsetMap();
        int height = height();
        int minDx = original.keySet().stream().mapToInt(GridOffset::dx).min().orElse(0);
        int minDy = original.keySet().stream().mapToInt(GridOffset::dy).min().orElse(0);
        Map<GridOffset, T> rotated = new HashMap<>();
        for (var entry : original.entrySet()) {
            int x = entry.getKey().dx() - minDx;
            int y = entry.getKey().dy() - minDy;
            // (x, y) -> (height - 1 - y, x)
            rotated.put(new GridOffset(height - 1 - y, x), entry.getValue());
        }
        return () -> rotated;
    }

    /**
     * Returns a new pattern rotated 180 degrees clockwise around its bounding box.
     * <p>
     * The top-left of the rotated pattern is at offset (0, 0).
     *
     * @return a pattern rotated 180° clockwise
     */
    default GridPattern<T> rotate180() {
        Map<GridOffset, T> original = offsetMap();
        int width = width();
        int height = height();
        int minDx = original.keySet().stream().mapToInt(GridOffset::dx).min().orElse(0);
        int minDy = original.keySet().stream().mapToInt(GridOffset::dy).min().orElse(0);
        Map<GridOffset, T> rotated = new HashMap<>();
        for (var entry : original.entrySet()) {
            int x = entry.getKey().dx() - minDx;
            int y = entry.getKey().dy() - minDy;
            // (x, y) -> (width - 1 - x, height - 1 - y)
            rotated.put(new GridOffset(width - 1 - x, height - 1 - y), entry.getValue());
        }
        return () -> rotated;
    }

    /**
     * Returns a new pattern rotated 270 degrees clockwise (or 90 degrees counterclockwise) around its bounding box.
     * <p>
     * The top-left of the rotated pattern is at offset (0, 0).
     *
     * @return a pattern rotated 270° clockwise
     */
    default GridPattern<T> rotate270() {
        Map<GridOffset, T> original = offsetMap();
        int width = width();
        int minDx = original.keySet().stream().mapToInt(GridOffset::dx).min().orElse(0);
        int minDy = original.keySet().stream().mapToInt(GridOffset::dy).min().orElse(0);
        Map<GridOffset, T> rotated = new HashMap<>();
        for (var entry : original.entrySet()) {
            int x = entry.getKey().dx() - minDx;
            int y = entry.getKey().dy() - minDy;
            // (x, y) -> (y, width - 1 - x)
            rotated.put(new GridOffset(y, width - 1 - x), entry.getValue());
        }
        return () -> rotated;
    }

    /**
     * Returns a new pattern by applying the given mapping function to all entities in this pattern.
     * <p>
     * The mapping function is applied to each entity, while the offsets remain unchanged.
     * The returned pattern contains the same offsets as this pattern, but with mapped entities.
     *
     * @param mapper the function to apply to each entity
     * @param <R> the type of the resulting entities
     * @return a new pattern with mapped entities and the same offsets as this pattern
     */
    default <R extends GridEntity> GridPattern<R> mapValues(Function<T, R> mapper) {
        return () -> {
            Map<GridOffset, R> mapped = new HashMap<>();
            for (var entry : offsetMap().entrySet()) {
                mapped.put(entry.getKey(), mapper.apply(entry.getValue()));
            }
            return mapped;
        };
    }

}
