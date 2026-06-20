package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.*;

import java.util.*;

/**
 * Utility class providing static factory methods for well-known {@link GridPattern} instances
 * used in Conway's Game of Life.
 * <p>
 * Each method returns a normalized pattern (top-left at (0, 0)), fully filled with
 * {@link de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity#ALIVE} and {@link de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity#DEAD} cells, representing classic
 * Game of Life configurations grouped into the well-known categories Still Life, Oscillator,
 * Methuselah, Replicator, and Spaceship. The exposed choice order follows these groups.
 * <p>
 * This class cannot be instantiated.
 *
 * @see de.mkalb.etpetssim.engine.support.GridPattern
 * @see de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity
 */
@SuppressWarnings("MagicNumber")
public final class ConwayPatterns {

    private static final String PATTERN_ID_BEEHIVE = "conway.beehive";
    private static final String PATTERN_ID_BLOCK = "conway.block";
    private static final String PATTERN_ID_BOAT = "conway.boat";
    private static final String PATTERN_ID_LOAF = "conway.loaf";
    private static final String PATTERN_ID_TUB = "conway.tub";
    private static final String PATTERN_ID_BEACON = "conway.beacon";
    private static final String PATTERN_ID_BLINKER = "conway.blinker";
    private static final String PATTERN_ID_PENTADECATHLON = "conway.pentadecathlon";
    private static final String PATTERN_ID_PULSAR = "conway.pulsar";
    private static final String PATTERN_ID_TOAD = "conway.toad";
    private static final String PATTERN_ID_ACORN = "conway.acorn";
    private static final String PATTERN_ID_R_PENTOMINO = "conway.rpentomino";
    private static final String PATTERN_ID_HIGHLIFE_REPLICATOR = "conway.highlife.replicator";
    private static final String PATTERN_ID_GLIDER = "conway.glider";
    private static final String PATTERN_ID_LIGHTWEIGHT_SPACESHIP = "conway.lwss";

    private static final String PATTERN_LABEL_KEY_BEEHIVE = "conway.pattern.beehive";
    private static final String PATTERN_LABEL_KEY_BLOCK = "conway.pattern.block";
    private static final String PATTERN_LABEL_KEY_BOAT = "conway.pattern.boat";
    private static final String PATTERN_LABEL_KEY_LOAF = "conway.pattern.loaf";
    private static final String PATTERN_LABEL_KEY_TUB = "conway.pattern.tub";
    private static final String PATTERN_LABEL_KEY_BEACON = "conway.pattern.beacon";
    private static final String PATTERN_LABEL_KEY_BLINKER = "conway.pattern.blinker";
    private static final String PATTERN_LABEL_KEY_PENTADECATHLON = "conway.pattern.pentadecathlon";
    private static final String PATTERN_LABEL_KEY_PULSAR = "conway.pattern.pulsar";
    private static final String PATTERN_LABEL_KEY_TOAD = "conway.pattern.toad";
    private static final String PATTERN_LABEL_KEY_ACORN = "conway.pattern.acorn";
    private static final String PATTERN_LABEL_KEY_R_PENTOMINO = "conway.pattern.rpentomino";
    private static final String PATTERN_LABEL_KEY_HIGHLIFE_REPLICATOR = "conway.pattern.highlife.replicator";
    private static final String PATTERN_LABEL_KEY_GLIDER = "conway.pattern.glider";
    private static final String PATTERN_LABEL_KEY_LIGHTWEIGHT_SPACESHIP = "conway.pattern.lwss";

    private static final ConwayTransitionRules CONWAYS_LIFE_TRANSITION_RULES = ConwayTransitionRules.of(
            ConwayPresetSquare.CONWAYS_LIFE.toString());
    private static final ConwayTransitionRules HIGHLIFE_TRANSITION_RULES = ConwayTransitionRules.of(
            ConwayPresetSquare.HIGHLIFE.toString());

    private static final List<ConwayPatternChoice> CLASSIC_CHOICES = List.of(
            new ConwayPatternChoice(
                    PATTERN_ID_BEEHIVE,
                    PATTERN_LABEL_KEY_BEEHIVE,
                    ConwayPatterns::beehive,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_BLOCK,
                    PATTERN_LABEL_KEY_BLOCK,
                    ConwayPatterns::block,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_BOAT,
                    PATTERN_LABEL_KEY_BOAT,
                    ConwayPatterns::boat,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_LOAF,
                    PATTERN_LABEL_KEY_LOAF,
                    ConwayPatterns::loaf,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_TUB,
                    PATTERN_LABEL_KEY_TUB,
                    ConwayPatterns::tub,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_BEACON,
                    PATTERN_LABEL_KEY_BEACON,
                    ConwayPatterns::beacon,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_BLINKER,
                    PATTERN_LABEL_KEY_BLINKER,
                    ConwayPatterns::blinker,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_PENTADECATHLON,
                    PATTERN_LABEL_KEY_PENTADECATHLON,
                    ConwayPatterns::pentadecathlon,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_PULSAR,
                    PATTERN_LABEL_KEY_PULSAR,
                    ConwayPatterns::pulsar,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_TOAD,
                    PATTERN_LABEL_KEY_TOAD,
                    ConwayPatterns::toad,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_ACORN,
                    PATTERN_LABEL_KEY_ACORN,
                    ConwayPatterns::acorn,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_R_PENTOMINO,
                    PATTERN_LABEL_KEY_R_PENTOMINO,
                    ConwayPatterns::rPentomino,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_HIGHLIFE_REPLICATOR,
                    PATTERN_LABEL_KEY_HIGHLIFE_REPLICATOR,
                    ConwayPatterns::highlifeReplicator,
                    ConwayPatterns::isAvailableForHighLifeSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_GLIDER,
                    PATTERN_LABEL_KEY_GLIDER,
                    ConwayPatterns::glider,
                    ConwayPatterns::isAvailableForClassicSquareRules),
            new ConwayPatternChoice(
                    PATTERN_ID_LIGHTWEIGHT_SPACESHIP,
                    PATTERN_LABEL_KEY_LIGHTWEIGHT_SPACESHIP,
                    ConwayPatterns::lightweightSpaceship,
                    ConwayPatterns::isAvailableForClassicSquareRules)
    );

    /**
     * Private constructor to prevent instantiation.
     */
    private ConwayPatterns() {
    }

    private static boolean isAvailableForClassicSquareRules(ConwayConfig config) {
        return (config.cellShape() == CellShape.SQUARE)
                && CONWAYS_LIFE_TRANSITION_RULES.equals(config.transitionRules());
    }

    private static boolean isAvailableForHighLifeSquareRules(ConwayConfig config) {
        return (config.cellShape() == CellShape.SQUARE)
                && HIGHLIFE_TRANSITION_RULES.equals(config.transitionRules());
    }

    public static List<ConwayPatternChoice> choices() {
        return CLASSIC_CHOICES;
    }

    public static List<ConwayPatternChoice> availableChoices(ConwayConfig config) {
        return CLASSIC_CHOICES.stream()
                              .filter(choice -> choice.availableFor(config))
                              .toList();
    }

    private static GridPattern<ConwayEntity> createPattern(String... rows) {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(rows.length * rows[0].length());
        for (int y = 0; y < rows.length; y++) {
            String row = rows[y];
            for (int x = 0; x < row.length(); x++) {
                ConwayEntity entity = switch (row.charAt(x)) {
                    case 'A' -> ConwayEntity.ALIVE;
                    case 'D' -> ConwayEntity.DEAD;
                    default -> throw new IllegalArgumentException("Unsupported pattern cell: " + row.charAt(x));
                };
                map.put(new GridOffset(x, y), entity);
            }
        }
        return () -> map;
    }

    /**
     * Returns a normalized 6x5 {@link GridPattern} representing the "Beehive" Still Life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D D
     * D D A A D D
     * D A D D A D
     * D D A A D D
     * D D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 6x5 beehive pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> beehive() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(30);
        // Row 0 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(5, 2), ConwayEntity.DEAD);
        // Row 3
        map.put(new GridOffset(0, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 3), ConwayEntity.DEAD);
        // Row 4 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 4), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 4x4 {@link GridPattern} representing the "Block" Still Life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D
     * D A A D
     * D A A D
     * D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 4x4 block pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> block() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(16);
        // Row 0 (border)
        for (int x = 0; x < 4; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 2), ConwayEntity.DEAD);
        // Row 3 (border)
        for (int x = 0; x < 4; x++) {
            map.put(new GridOffset(x, 3), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 5x5 {@link GridPattern} representing the "Boat" Still Life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D
     * D A A D D
     * D A D A D
     * D D A D D
     * D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 5x5 boat pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> boat() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(25);
        // Row 0 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 2), ConwayEntity.DEAD);
        // Row 3
        map.put(new GridOffset(0, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 3), ConwayEntity.DEAD);
        // Row 4 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 4), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 6x6 {@link GridPattern} representing the "Loaf" Still Life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D D
     * D D A A D D
     * D A D D A D
     * D D A D A D
     * D D D A D D
     * D D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 6x6 loaf pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> loaf() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(36);
        // Row 0 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(5, 2), ConwayEntity.DEAD);
        // Row 3
        map.put(new GridOffset(0, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(5, 3), ConwayEntity.DEAD);
        // Row 4
        map.put(new GridOffset(0, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 4), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 4), ConwayEntity.DEAD);
        // Row 5 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 5), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 5x5 {@link GridPattern} representing the "Tub" Still Life.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells and includes a
     * DEAD border so placement overwrites the complete footprint.
     *
     * @return a normalized 5x5 tub pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> tub() {
        return createPattern(
                "DDDDD",
                "DDADD",
                "DADAD",
                "DDADD",
                "DDDDD");
    }

    /**
     * Returns a normalized 6x6 {@link GridPattern} representing the "Beacon" Oscillator.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D D
     * D A A D D D
     * D A D D D D
     * D D D D A D
     * D D D A A D
     * D D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 6x6 beacon pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> beacon() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(36);
        // Row 0 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 2), ConwayEntity.DEAD);
        // Row 3
        map.put(new GridOffset(0, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(5, 3), ConwayEntity.DEAD);
        // Row 4
        map.put(new GridOffset(0, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 4), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 4), ConwayEntity.ALIVE);
        map.put(new GridOffset(5, 4), ConwayEntity.DEAD);
        // Row 5 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 5), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 5x5 {@link GridPattern} representing the "Blinker" Oscillator
     * (horizontal phase).
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D
     * D D D D D
     * D A A A D
     * D D D D D
     * D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 5x5 blinker pattern with DEAD border (horizontal phase)
     */
    public static GridPattern<ConwayEntity> blinker() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(25);
        // Row 0 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 1), ConwayEntity.DEAD);
        }
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 2), ConwayEntity.DEAD);
        // Row 3 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 3), ConwayEntity.DEAD);
        }
        // Row 4 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 4), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 12x5 {@link GridPattern} representing the "Pentadecathlon" Oscillator.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells and includes a
     * DEAD border so placement overwrites the complete footprint.
     *
     * @return a normalized 12x5 pentadecathlon pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> pentadecathlon() {
        return createPattern(
                "DDDDDDDDDDDD",
                "DDDADDDDADDD",
                "DAADAAAADAAD",
                "DDDADDDDADDD",
                "DDDDDDDDDDDD");
    }

    /**
     * Returns a normalized 15x15 {@link GridPattern} representing the "Pulsar" Oscillator.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells and includes a
     * DEAD border so placement overwrites the complete footprint.
     *
     * @return a normalized 15x15 pulsar pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> pulsar() {
        return createPattern(
                "DDDDDDDDDDDDDDD",
                "DDDAAADDDDAAADD",
                "DDDDDDDDDDDDDDD",
                "DADDDDADADDDDAD",
                "DADDDDADADDDDAD",
                "DADDDDADADDDDAD",
                "DDDAAADDDDAAADD",
                "DDDDDDDDDDDDDDD",
                "DDDAAADDDDAAADD",
                "DADDDDADADDDDAD",
                "DADDDDADADDDDAD",
                "DADDDDADADDDDAD",
                "DDDDDDDDDDDDDDD",
                "DDDAAADDDDAAADD",
                "DDDDDDDDDDDDDDD");
    }

    /**
     * Returns a normalized 6x4 {@link GridPattern} representing the "Toad" Oscillator.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D D
     * D D A A A D
     * D A A A D D
     * D D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 6x4 toad pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> toad() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(24);
        // Row 0 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(5, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 2), ConwayEntity.DEAD);
        // Row 3 (border)
        for (int x = 0; x < 6; x++) {
            map.put(new GridOffset(x, 3), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 9x5 {@link GridPattern} representing the "Acorn" Methuselah.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells and includes a
     * DEAD border so placement overwrites the complete footprint.
     *
     * @return a normalized 9x5 acorn pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> acorn() {
        return createPattern(
                "DDDDDDDDD",
                "DDADDDDDD",
                "DDDDADDDD",
                "DAADDAAAD",
                "DDDDDDDDD");
    }

    /**
     * Returns a normalized 5x5 {@link GridPattern} representing the "R-pentomino" Methuselah.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells and includes a
     * DEAD border so placement overwrites the complete footprint.
     *
     * @return a normalized 5x5 R-pentomino pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> rPentomino() {
        return createPattern(
                "DDDDD",
                "DDAAD",
                "DAADD",
                "DDADD",
                "DDDDD");
    }

    /**
     * Returns a normalized 7x7 {@link GridPattern} representing the HighLife "Replicator".
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells and includes a
     * DEAD border so placement overwrites the complete footprint. This pattern is intended for the HighLife rule
     * ({@code 23/36}).
     *
     * @return a normalized 7x7 HighLife replicator pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> highlifeReplicator() {
        return createPattern(
                "DDDDDDD",
                "DDDAAAD",
                "DDADDAD",
                "DADDDAD",
                "DADDADD",
                "DAAADDD",
                "DDDDDDD");
    }

    /**
     * Returns a normalized 5x5 {@link GridPattern} representing the classic "Glider" Spaceship
     * from Conway's Game of Life.
     * <p>
     * The pattern is fully filled with {@link de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity#ALIVE} and {@link de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D
     * D D A D D
     * D D D A D
     * D A A A D
     * D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity#DEAD}</li>
     *   <li>A = {@link de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 5x5 glider pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> glider() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(25);
        // Row 0 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 2), ConwayEntity.DEAD);
        // Row 3
        map.put(new GridOffset(0, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 3), ConwayEntity.DEAD);
        // Row 4 (border)
        for (int x = 0; x < 5; x++) {
            map.put(new GridOffset(x, 4), ConwayEntity.DEAD);
        }
        return () -> map;
    }

    /**
     * Returns a normalized 7x6 {@link GridPattern} representing the "Lightweight Spaceship"
     * (LWSS) Spaceship.
     * <p>
     * The pattern is fully filled with {@link ConwayEntity#ALIVE} and {@link ConwayEntity#DEAD} cells,
     * surrounded by a border of DEAD cells:
     * <pre>
     * D D D D D D D
     * D D A D D A D
     * D A D D D D D
     * D A D D D A D
     * D A A A A D D
     * D D D D D D D
     * </pre>
     * <ul>
     *   <li>D = {@link ConwayEntity#DEAD}</li>
     *   <li>A = {@link ConwayEntity#ALIVE}</li>
     * </ul>
     * The top-left of the pattern is at offset (0, 0).
     *
     * @return a normalized 7x6 lightweight spaceship pattern with DEAD border
     */
    public static GridPattern<ConwayEntity> lightweightSpaceship() {
        Map<GridOffset, ConwayEntity> map = HashMap.newHashMap(42);
        // Row 0 (border)
        for (int x = 0; x < 7; x++) {
            map.put(new GridOffset(x, 0), ConwayEntity.DEAD);
        }
        // Row 1
        map.put(new GridOffset(0, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(2, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 1), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 1), ConwayEntity.ALIVE);
        map.put(new GridOffset(6, 1), ConwayEntity.DEAD);
        // Row 2
        map.put(new GridOffset(0, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 2), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 2), ConwayEntity.DEAD);
        map.put(new GridOffset(6, 2), ConwayEntity.DEAD);
        // Row 3
        map.put(new GridOffset(0, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(3, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(4, 3), ConwayEntity.DEAD);
        map.put(new GridOffset(5, 3), ConwayEntity.ALIVE);
        map.put(new GridOffset(6, 3), ConwayEntity.DEAD);
        // Row 4
        map.put(new GridOffset(0, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(1, 4), ConwayEntity.ALIVE);
        map.put(new GridOffset(2, 4), ConwayEntity.ALIVE);
        map.put(new GridOffset(3, 4), ConwayEntity.ALIVE);
        map.put(new GridOffset(4, 4), ConwayEntity.ALIVE);
        map.put(new GridOffset(5, 4), ConwayEntity.DEAD);
        map.put(new GridOffset(6, 4), ConwayEntity.DEAD);
        // Row 5 (border)
        for (int x = 0; x < 7; x++) {
            map.put(new GridOffset(x, 5), ConwayEntity.DEAD);
        }
        return () -> map;
    }

}
