package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Pattern of entities at offsets relative to an origin (0, 0), for example to place them on a grid with
 * {@link GridInitializers#placePattern}.
 *
 * <p>A pattern is normalized if the top-left corner of its bounding box is at offset (0, 0). Create patterns in
 * normalized form; a pattern that is not normalized documents this where it is created. {@link GridPatterns} provides
 * the static factories.
 *
 * <p>The transformations ({@code shift}, {@code normalize}, {@code flipX}, {@code flipY}, {@code rotate...}, and
 * {@code mapEntities}) compute their result immediately and return unmodifiable patterns that are independent of this
 * pattern; {@code normalize} returns this pattern if it is already normalized. The flips and {@code mapEntities} keep
 * the bounding box, and the rotations return normalized patterns.
 *
 * <p>The offsets are transformed as on square cells. On triangle and hexagon grids, the meaning of an offset depends
 * on the position of the cell (see {@link GridOffset}), so a flipped or rotated pattern keeps its shape only on square
 * grids.
 *
 * @param <ENT> the type of grid entity
 */
@FunctionalInterface
public interface GridPattern<ENT extends GridEntity> {

    private static int minDx(Map<GridOffset, ?> offsetMap) {
        return offsetMap.keySet().stream().mapToInt(GridOffset::dx).min().orElse(0);
    }

    private static int maxDx(Map<GridOffset, ?> offsetMap) {
        return offsetMap.keySet().stream().mapToInt(GridOffset::dx).max().orElse(0);
    }

    private static int minDy(Map<GridOffset, ?> offsetMap) {
        return offsetMap.keySet().stream().mapToInt(GridOffset::dy).min().orElse(0);
    }

    private static int maxDy(Map<GridOffset, ?> offsetMap) {
        return offsetMap.keySet().stream().mapToInt(GridOffset::dy).max().orElse(0);
    }

    /**
     * Returns the entities of this pattern by offset.
     *
     * @return the entities by offset
     */
    Map<GridOffset, ENT> offsetMap();

    /**
     * Returns the number of entities of this pattern.
     *
     * @return the number of entities
     */
    default int size() {
        return offsetMap().size();
    }

    /**
     * Returns {@code true} if this pattern has no entities.
     *
     * @return {@code true} if this pattern is empty, {@code false} otherwise
     */
    default boolean isEmpty() {
        return offsetMap().isEmpty();
    }

    /**
     * Returns the width of the bounding box of this pattern.
     *
     * @return the number of columns of the bounding box; 0 for an empty pattern
     */
    default int width() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        if (offsetMap.isEmpty()) {
            return 0;
        }
        return (maxDx(offsetMap) - minDx(offsetMap)) + 1;
    }

    /**
     * Returns the height of the bounding box of this pattern.
     *
     * @return the number of rows of the bounding box; 0 for an empty pattern
     */
    default int height() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        if (offsetMap.isEmpty()) {
            return 0;
        }
        return (maxDy(offsetMap) - minDy(offsetMap)) + 1;
    }

    /**
     * Returns {@code true} if the top-left corner of the bounding box of this pattern is at offset (0, 0).
     *
     * @return {@code true} if this pattern is normalized or empty, {@code false} otherwise
     */
    default boolean isNormalized() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        return (minDx(offsetMap) == 0) && (minDy(offsetMap) == 0);
    }

    /**
     * Returns this pattern with all offsets shifted by the given offset.
     *
     * @param offset the offset to add to each offset of this pattern
     * @return the shifted pattern
     */
    default GridPattern<ENT> shift(GridOffset offset) {
        Map<GridOffset, ENT> shifted = new HashMap<>();
        for (var entry : offsetMap().entrySet()) {
            shifted.put(entry.getKey().add(offset), entry.getValue());
        }
        return GridPatterns.of(shifted);
    }

    /**
     * Returns this pattern shifted so that the top-left corner of its bounding box is at offset (0, 0).
     *
     * @return the normalized pattern; this pattern if it is already normalized
     */
    default GridPattern<ENT> normalize() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        int minDx = minDx(offsetMap);
        int minDy = minDy(offsetMap);
        if ((minDx == 0) && (minDy == 0)) {
            return this;
        }
        return shift(new GridOffset(-minDx, -minDy));
    }

    /**
     * Returns this pattern mirrored at the vertical center line of its bounding box.
     *
     * @return the horizontally mirrored pattern with the same bounding box
     */
    default GridPattern<ENT> flipX() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        int sumDx = minDx(offsetMap) + maxDx(offsetMap);
        Map<GridOffset, ENT> flipped = new HashMap<>();
        for (var entry : offsetMap.entrySet()) {
            GridOffset offset = entry.getKey();
            flipped.put(new GridOffset(sumDx - offset.dx(), offset.dy()), entry.getValue());
        }
        return GridPatterns.of(flipped);
    }

    /**
     * Returns this pattern mirrored at the horizontal center line of its bounding box.
     *
     * @return the vertically mirrored pattern with the same bounding box
     */
    default GridPattern<ENT> flipY() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        int sumDy = minDy(offsetMap) + maxDy(offsetMap);
        Map<GridOffset, ENT> flipped = new HashMap<>();
        for (var entry : offsetMap.entrySet()) {
            GridOffset offset = entry.getKey();
            flipped.put(new GridOffset(offset.dx(), sumDy - offset.dy()), entry.getValue());
        }
        return GridPatterns.of(flipped);
    }

    /**
     * Returns this pattern rotated by 90 degrees clockwise.
     *
     * @return the rotated, normalized pattern
     */
    default GridPattern<ENT> rotate90() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        int minDx = minDx(offsetMap);
        int minDy = minDy(offsetMap);
        int height = (maxDy(offsetMap) - minDy) + 1;
        Map<GridOffset, ENT> rotated = new HashMap<>();
        for (var entry : offsetMap.entrySet()) {
            int x = entry.getKey().dx() - minDx;
            int y = entry.getKey().dy() - minDy;
            // (x, y) -> (height - 1 - y, x).
            rotated.put(new GridOffset(height - 1 - y, x), entry.getValue());
        }
        return GridPatterns.of(rotated);
    }

    /**
     * Returns this pattern rotated by 180 degrees.
     *
     * @return the rotated, normalized pattern
     */
    default GridPattern<ENT> rotate180() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        int minDx = minDx(offsetMap);
        int minDy = minDy(offsetMap);
        int width = (maxDx(offsetMap) - minDx) + 1;
        int height = (maxDy(offsetMap) - minDy) + 1;
        Map<GridOffset, ENT> rotated = new HashMap<>();
        for (var entry : offsetMap.entrySet()) {
            int x = entry.getKey().dx() - minDx;
            int y = entry.getKey().dy() - minDy;
            // (x, y) -> (width - 1 - x, height - 1 - y).
            rotated.put(new GridOffset(width - 1 - x, height - 1 - y), entry.getValue());
        }
        return GridPatterns.of(rotated);
    }

    /**
     * Returns this pattern rotated by 270 degrees clockwise, which is 90 degrees counterclockwise.
     *
     * @return the rotated, normalized pattern
     */
    default GridPattern<ENT> rotate270() {
        Map<GridOffset, ENT> offsetMap = offsetMap();
        int minDx = minDx(offsetMap);
        int minDy = minDy(offsetMap);
        int width = (maxDx(offsetMap) - minDx) + 1;
        Map<GridOffset, ENT> rotated = new HashMap<>();
        for (var entry : offsetMap.entrySet()) {
            int x = entry.getKey().dx() - minDx;
            int y = entry.getKey().dy() - minDy;
            // (x, y) -> (y, width - 1 - x).
            rotated.put(new GridOffset(y, width - 1 - x), entry.getValue());
        }
        return GridPatterns.of(rotated);
    }

    /**
     * Returns this pattern with each entity replaced by the result of the given function.
     *
     * @param mapper the function to apply to each entity
     * @param <R>    the type of grid entity of the result
     * @return the pattern with the mapped entities at the same offsets
     */
    default <R extends GridEntity> GridPattern<R> mapEntities(Function<? super ENT, ? extends R> mapper) {
        Map<GridOffset, R> mapped = new HashMap<>();
        for (var entry : offsetMap().entrySet()) {
            mapped.put(entry.getKey(), mapper.apply(entry.getValue()));
        }
        return GridPatterns.of(mapped);
    }

}
