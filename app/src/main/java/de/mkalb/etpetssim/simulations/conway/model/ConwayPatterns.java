package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.model.GridPattern;

import java.util.*;

/**
 * Utility class providing static factory methods for well-known {@link GridPattern} instances
 * used in Conway's Game of Life.
 * <p>
 * Each method returns a normalized pattern (top-left at (0, 0)), fully filled with
 * {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells, representing classic
 * Game of Life configurations such as the glider, block, beehive, and blinker.
 * <p>
 * This class cannot be instantiated.
 *
 * @see de.mkalb.etpetssim.engine.model.GridPattern
 * @see ConwayEntity
 */
@SuppressWarnings("MagicNumber")
public final class ConwayPatterns {

    /**
     * Private constructor to prevent instantiation.
     */
    private ConwayPatterns() {
    }

    /**
     * Returns a normalized 3x3 {@link GridPattern} representing the classic "Glider" from Conway's Game of Life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells:
     * <pre>
     * D A D
     * D D A
     * A A A
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 3x3 glider pattern
     */
    public static GridPattern<ConwayEntity> glider() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(9);
        // Row 0
        map.put(new GridOffset(0, 0), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 0), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 0), ConwayEntity.DEAD);
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.ALIVE);
        return () -> map;
    }

    /**
     * Returns a normalized 2x2 {@link GridPattern} representing the "Block" still life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells:
     * <pre>
     * A A
     * A A
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 2x2 block pattern
     */
    public static GridPattern<ConwayEntity> block() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(4);
        map.put(new GridOffset(0, 0), ConwayEntity.ALIVE);
        map.put(new GridOffset(1, 0), ConwayEntity.ALIVE);
        map.put(new GridOffset(0, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(1, 1), ConwayEntity.ALIVE);
        return () -> map;
    }

    /**
     * Returns a normalized 4x2 {@link GridPattern} representing the "Beehive" still life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells:
     * <pre>
     * D A A D
     * A D D A
     * D A A D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 4x3 beehive pattern
     */
    public static GridPattern<ConwayEntity> beehive() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(12);
        // Row 0
        map.put(new GridOffset(0, 0), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 0), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 0), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 0), ConwayEntity.DEAD);
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(1, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 1), ConwayEntity.ALIVE);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 2), ConwayEntity.DEAD);
        return () -> map;
    }

    /**
     * Returns a normalized 3x3 {@link GridPattern} representing the "Blinker" oscillator (horizontal phase).
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells:
     * <pre>
     * D D D
     * A A A
     * D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 3x3 blinker pattern (horizontal phase)
     */
    public static GridPattern<ConwayEntity> blinker() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(9);
        // Row 0
        map.put(new GridOffset(0, 0), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 0), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 0), ConwayEntity.DEAD);
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(1, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 2), ConwayEntity.DEAD);
        return () -> map;
    }

}
