package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.support.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.*;

import java.util.*;

/**
 * Catalog of the predefined {@link GridPattern} choices of Conway's Game of Life.
 *
 * <p>Each pattern is normalized (top-left at offset (0, 0)) and fully filled with {@link ConwayEntity#ALIVE} and
 * {@link ConwayEntity#DEAD} cells, surrounded by a border of dead cells. The patterns are classic configurations of
 * the categories Still Life, Oscillator, Methuselah, Replicator, and Spaceship; the catalog order follows these
 * categories. A pattern is available only for square cells and the rules it is intended for: the HighLife rules
 * ({@code 23/36}) for the replicator, Conway's Life rules for all other patterns.
 *
 * @see GridPattern
 * @see ConwayEntity
 */
@SuppressWarnings("SpellCheckingInspection")
public final class ConwayPatterns {

    private static final String PATTERN_ID_ACORN = "conway.acorn";
    private static final String PATTERN_ID_BEACON = "conway.beacon";
    private static final String PATTERN_ID_BEEHIVE = "conway.beehive";
    private static final String PATTERN_ID_BLINKER = "conway.blinker";
    private static final String PATTERN_ID_BLOCK = "conway.block";
    private static final String PATTERN_ID_BOAT = "conway.boat";
    private static final String PATTERN_ID_GLIDER = "conway.glider";
    private static final String PATTERN_ID_HIGHLIFE_REPLICATOR = "conway.highlife.replicator";
    private static final String PATTERN_ID_LIGHTWEIGHT_SPACESHIP = "conway.lwss";
    private static final String PATTERN_ID_LOAF = "conway.loaf";
    private static final String PATTERN_ID_PENTADECATHLON = "conway.pentadecathlon";
    private static final String PATTERN_ID_PULSAR = "conway.pulsar";
    private static final String PATTERN_ID_R_PENTOMINO = "conway.rpentomino";
    private static final String PATTERN_ID_TOAD = "conway.toad";
    private static final String PATTERN_ID_TUB = "conway.tub";

    private static final String PATTERN_LABEL_KEY_ACORN = "conway.pattern.acorn";
    private static final String PATTERN_LABEL_KEY_BEACON = "conway.pattern.beacon";
    private static final String PATTERN_LABEL_KEY_BEEHIVE = "conway.pattern.beehive";
    private static final String PATTERN_LABEL_KEY_BLINKER = "conway.pattern.blinker";
    private static final String PATTERN_LABEL_KEY_BLOCK = "conway.pattern.block";
    private static final String PATTERN_LABEL_KEY_BOAT = "conway.pattern.boat";
    private static final String PATTERN_LABEL_KEY_GLIDER = "conway.pattern.glider";
    private static final String PATTERN_LABEL_KEY_HIGHLIFE_REPLICATOR = "conway.pattern.highlife.replicator";
    private static final String PATTERN_LABEL_KEY_LIGHTWEIGHT_SPACESHIP = "conway.pattern.lwss";
    private static final String PATTERN_LABEL_KEY_LOAF = "conway.pattern.loaf";
    private static final String PATTERN_LABEL_KEY_PENTADECATHLON = "conway.pattern.pentadecathlon";
    private static final String PATTERN_LABEL_KEY_PULSAR = "conway.pattern.pulsar";
    private static final String PATTERN_LABEL_KEY_R_PENTOMINO = "conway.pattern.rpentomino";
    private static final String PATTERN_LABEL_KEY_TOAD = "conway.pattern.toad";
    private static final String PATTERN_LABEL_KEY_TUB = "conway.pattern.tub";

    private static final ConwayTransitionRules CONWAYS_LIFE_TRANSITION_RULES = ConwayTransitionRules.parse(
            ConwayRulePresetSquare.CONWAYS_LIFE.ruleString());
    private static final ConwayTransitionRules HIGHLIFE_TRANSITION_RULES = ConwayTransitionRules.parse(
            ConwayRulePresetSquare.HIGHLIFE.ruleString());

    private static final List<ConwayPatternChoice> CATALOG_CHOICES = List.of(
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

    private ConwayPatterns() {
    }

    /**
     * Returns all registered Conway pattern choices in stable catalog order.
     *
     * <p>The returned list includes patterns for all supported square rule presets, including patterns that are not
     * available for the current configuration.
     *
     * @return all registered pattern choices in stable catalog order
     */
    public static List<ConwayPatternChoice> choices() {
        return CATALOG_CHOICES;
    }

    /**
     * Returns the registered Conway pattern choices available for the specified configuration.
     *
     * <p>Availability depends on the configured cell shape and transition rules. Unsupported cell shapes and rule sets
     * return an empty list.
     *
     * @param config the Conway configuration to test against the pattern availability rules
     * @return the pattern choices available for the specified configuration, in stable catalog order
     */
    public static List<ConwayPatternChoice> availableChoices(ConwayConfig config) {
        return CATALOG_CHOICES.stream()
                              .filter(choice -> choice.isAvailableFor(config.cellShape(), config.transitionRules()))
                              .toList();
    }

    private static boolean isAvailableForClassicSquareRules(CellShape cellShape,
                                                            ConwayTransitionRules transitionRules) {
        return (cellShape == CellShape.SQUARE)
                && CONWAYS_LIFE_TRANSITION_RULES.equals(transitionRules);
    }

    private static boolean isAvailableForHighLifeSquareRules(CellShape cellShape,
                                                             ConwayTransitionRules transitionRules) {
        return (cellShape == CellShape.SQUARE)
                && HIGHLIFE_TRANSITION_RULES.equals(transitionRules);
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
        return GridPatterns.of(map);
    }

    private static GridPattern<ConwayEntity> beehive() {
        return createPattern(
                "DDDDDD",
                "DDAADD",
                "DADDAD",
                "DDAADD",
                "DDDDDD");
    }

    private static GridPattern<ConwayEntity> block() {
        return createPattern(
                "DDDD",
                "DAAD",
                "DAAD",
                "DDDD");
    }

    private static GridPattern<ConwayEntity> boat() {
        return createPattern(
                "DDDDD",
                "DAADD",
                "DADAD",
                "DDADD",
                "DDDDD");
    }

    private static GridPattern<ConwayEntity> loaf() {
        return createPattern(
                "DDDDDD",
                "DDAADD",
                "DADDAD",
                "DDADAD",
                "DDDADD",
                "DDDDDD");
    }

    private static GridPattern<ConwayEntity> tub() {
        return createPattern(
                "DDDDD",
                "DDADD",
                "DADAD",
                "DDADD",
                "DDDDD");
    }

    private static GridPattern<ConwayEntity> beacon() {
        return createPattern(
                "DDDDDD",
                "DAADDD",
                "DADDDD",
                "DDDDAD",
                "DDDAAD",
                "DDDDDD");
    }

    private static GridPattern<ConwayEntity> blinker() {
        return createPattern(
                "DDDDD",
                "DDDDD",
                "DAAAD",
                "DDDDD",
                "DDDDD");
    }

    private static GridPattern<ConwayEntity> pentadecathlon() {
        return createPattern(
                "DDDDDDDDDDDD",
                "DDDADDDDADDD",
                "DAADAAAADAAD",
                "DDDADDDDADDD",
                "DDDDDDDDDDDD");
    }

    private static GridPattern<ConwayEntity> pulsar() {
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

    private static GridPattern<ConwayEntity> toad() {
        return createPattern(
                "DDDDDD",
                "DDAAAD",
                "DAAADD",
                "DDDDDD");
    }

    private static GridPattern<ConwayEntity> acorn() {
        return createPattern(
                "DDDDDDDDD",
                "DDADDDDDD",
                "DDDDADDDD",
                "DAADDAAAD",
                "DDDDDDDDD");
    }

    private static GridPattern<ConwayEntity> rPentomino() {
        return createPattern(
                "DDDDD",
                "DDAAD",
                "DAADD",
                "DDADD",
                "DDDDD");
    }

    private static GridPattern<ConwayEntity> highlifeReplicator() {
        return createPattern(
                "DDDDDDD",
                "DDDAAAD",
                "DDADDAD",
                "DADDDAD",
                "DADDADD",
                "DAAADDD",
                "DDDDDDD");
    }

    private static GridPattern<ConwayEntity> glider() {
        return createPattern(
                "DDDDD",
                "DDADD",
                "DDDAD",
                "DAAAD",
                "DDDDD");
    }

    private static GridPattern<ConwayEntity> lightweightSpaceship() {
        return createPattern(
                "DDDDDDD",
                "DDADDAD",
                "DADDDDD",
                "DADDDAD",
                "DAAAADD",
                "DDDDDDD");
    }

}
