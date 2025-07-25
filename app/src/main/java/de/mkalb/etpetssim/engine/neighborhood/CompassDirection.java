package de.mkalb.etpetssim.engine.neighborhood;

import java.util.*;
import java.util.stream.*;

/**
 * Represents the 16-point compass directions in a fixed, clockwise order.
 * <p>
 * This enum is used throughout the grid framework to describe directions between cells,
 * supporting both edge and vertex neighbor relationships (see {@link CellNeighbor} and {@link CellConnectionType}).
 * The values are intentionally ordered clockwise, starting from North (N), to allow for efficient navigation
 * and sorting based on compass direction. This order enables methods like {@link #nextClockwise()} and {@link #allClockwise()}
 * to traverse the compass directions in their natural sequence.
 * </p>
 * <p>
 * Each direction has an associated abbreviation resource key, name resource key,
 * a "level", and a Unicode arrow symbol. The {@code level} indicates the granularity
 * of the direction: 0 for main directions (N, E, S, W), 1 for intercardinal (NE, SE, SW, NW),
 * and 2 for secondary intercardinal directions (e.g., NNE, ENE). In everyday terms,
 * a lower level means a more general direction, while a higher level is more specific.
 * </p>
 *
 * @see CellNeighbor
 * @see CellConnectionType
 */
@SuppressWarnings({"MagicNumber", "FieldNamingConvention"})
public enum CompassDirection {

    N("compass.abbr.n", "compass.name.n", 0, "↑"),
    NNE("compass.abbr.nne", "compass.name.nne", 2, "↑↗"),
    NE("compass.abbr.ne", "compass.name.ne", 1, "↗"),
    ENE("compass.abbr.ene", "compass.name.ene", 2, "→↗"),
    E("compass.abbr.e", "compass.name.e", 0, "→"),
    ESE("compass.abbr.ese", "compass.name.ese", 2, "→↘"),
    SE("compass.abbr.se", "compass.name.se", 1, "↘"),
    SSE("compass.abbr.sse", "compass.name.sse", 2, "↓↘"),
    S("compass.abbr.s", "compass.name.s", 0, "↓"),
    SSW("compass.abbr.ssw", "compass.name.ssw", 2, "↓↙"),
    SW("compass.abbr.sw", "compass.name.sw", 1, "↙"),
    WSW("compass.abbr.wsw", "compass.name.wsw", 2, "←↙"),
    W("compass.abbr.w", "compass.name.w", 0, "←"),
    WNW("compass.abbr.wnw", "compass.name.wnw", 2, "←↖"),
    NW("compass.abbr.nw", "compass.name.nw", 1, "↖"),
    NNW("compass.abbr.nnw", "compass.name.nnw", 2, "↑↖");

    private static final CompassDirection[] VALUES = values();

    private final String abbrResourceKey;
    private final String nameResourceKey;
    private final int level;
    private final String arrow;

    CompassDirection(String abbrResourceKey, String nameResourceKey, int level, String arrow) {
        this.abbrResourceKey = abbrResourceKey;
        this.nameResourceKey = nameResourceKey;
        this.level = level;
        this.arrow = arrow;
    }

    /**
     * Returns the resource key for the compass label.
     *
     * @return the label resource key
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "compass.label";
    }

    /**
     * Returns the resource key for the abbreviation of this direction.
     *
     * @return the abbreviation resource key
     */
    public String abbrResourceKey() {
        return abbrResourceKey;
    }

    /**
     * Returns the resource key for the full name of this direction.
     *
     * @return the name resource key
     */
    public String nameResourceKey() {
        return nameResourceKey;
    }

    /**
     * Returns the granularity level of this direction.
     * <p>
     * Level 0: Main directions (N, E, S, W)<br>
     * Level 1: Intercardinal directions (NE, SE, SW, NW)<br>
     * Level 2: Secondary intercardinal directions (e.g., NNE, ENE)
     * </p>
     * In everyday language, a lower level means a more general direction,
     * while a higher level is more specific.
     *
     * @return the level of this direction (0, 1, or 2)
     */
    public int level() {
        return level;
    }

    /**
     * Returns the Unicode arrow symbol(s) representing this direction.
     * <p>
     * For directions with {@code level} 0 or 1, a single Unicode arrow character is used,
     * matching the exact compass direction (e.g., ↑, →, ↗, ↘, etc.).
     * For {@code level} 2 directions, where no single Unicode character exists,
     * a combination of two arrows is used as a substitute (e.g., ↑↗ for NNE).
     * The returned string therefore contains either one or two Unicode arrow characters.
     * </p>
     *
     * @return the arrow symbol(s) for this direction
     */
    public String arrow() {
        return arrow;
    }

    /**
     * Returns the direction opposite to this one (180 degrees away).
     *
     * @return the opposite compass direction
     */
    public CompassDirection opposite() {
        return VALUES[(ordinal() + 8) % 16];
    }

    /**
     * Returns the next compass direction in clockwise order.
     *
     * @return the next clockwise direction
     */
    public CompassDirection nextClockwise() {
        return VALUES[(ordinal() + 1) % 16];
    }

    /**
     * Returns the next compass direction in counterclockwise order.
     *
     * @return the next counterclockwise direction
     */
    public CompassDirection nextCounterClockwise() {
        return VALUES[(ordinal() + 15) % 16];
    }

    /**
     * Returns a list of all compass directions starting from this one,
     * traversing clockwise and wrapping around to the start.
     *
     * @return list of directions in clockwise order, starting from this
     */
    public List<CompassDirection> allClockwise() {
        List<CompassDirection> result = new ArrayList<>();
        CompassDirection current = this;
        do {
            result.add(current);
            current = current.nextClockwise();
        } while (current != this);
        return result;
    }

    /**
     * Returns a list of all compass directions starting from this one,
     * traversing counterclockwise and wrapping around to the start.
     *
     * @return list of directions in counterclockwise order, starting from this
     */
    public List<CompassDirection> allCounterClockwise() {
        List<CompassDirection> result = new ArrayList<>();
        CompassDirection current = this;
        do {
            result.add(current);
            current = current.nextCounterClockwise();
        } while (current != this);
        return result;
    }

    /**
     * Returns a stream of all compass directions starting from this one,
     * traversing clockwise and wrapping around to the start.
     *
     * @return stream of directions in clockwise order, starting from this
     */
    public Stream<CompassDirection> streamClockwise() {
        return Stream.iterate(this, CompassDirection::nextClockwise)
                     .limit(VALUES.length);
    }

    /**
     * Returns a stream of all compass directions starting from this one,
     * traversing counterclockwise and wrapping around to the start.
     *
     * @return stream of directions in counterclockwise order, starting from this
     */
    public Stream<CompassDirection> streamCounterClockwise() {
        return Stream.iterate(this, CompassDirection::nextCounterClockwise)
                     .limit(VALUES.length);
    }

}