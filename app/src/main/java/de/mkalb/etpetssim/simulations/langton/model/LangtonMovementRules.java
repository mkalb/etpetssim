package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;

import java.util.*;

/**
 * Describes the cyclic turn sequence used by Langton-style ant simulations.
 *
 * @param turns the ordered turn sequence applied to successive cell states
 */
public record LangtonMovementRules(
        List<AntTurn> turns) {

    public static final int MIN_RULE_COUNT = 2;
    public static final int MAX_RULE_COUNT = 16;

    private static final int INITIAL_CAPACITY_DISPLAY_STRING = 32;

    public LangtonMovementRules(List<AntTurn> turns) {
        if (turns.size() < MIN_RULE_COUNT) {
            throw new IllegalArgumentException("At least " + MIN_RULE_COUNT + " rules required, got: " + turns.size());
        }
        if (turns.size() > MAX_RULE_COUNT) {
            throw new IllegalArgumentException("At most " + MAX_RULE_COUNT + " rules allowed, got: " + turns.size());
        }
        this.turns = List.copyOf(turns);
    }

    /**
     * Parses a compact turn string such as {@code LR}, {@code L2R2}, or {@code LRU}.
     *
     * @param ruleString the compact rule string to parse
     * @return the parsed movement rules
     */
    public static LangtonMovementRules fromString(String ruleString) {
        String preparedString = ruleString.trim().toUpperCase(Locale.ROOT);
        List<AntTurn> result = new ArrayList<>();
        for (int i = 0; i < preparedString.length(); ) {
            char c = preparedString.charAt(i);
            switch (c) {
                case 'L' -> {
                    if (((i + 1) < preparedString.length()) && (preparedString.charAt(i + 1) == '2')) {
                        result.add(AntTurn.LEFT2);
                        i += 2;
                    } else if (((i + 1) < preparedString.length()) && (preparedString.charAt(i + 1) == '1')) {
                        result.add(AntTurn.LEFT);
                        i += 2;
                    } else {
                        result.add(AntTurn.LEFT);
                        i++;
                    }
                }
                case 'R' -> {
                    if (((i + 1) < preparedString.length()) && (preparedString.charAt(i + 1) == '2')) {
                        result.add(AntTurn.RIGHT2);
                        i += 2;
                    } else if (((i + 1) < preparedString.length()) && (preparedString.charAt(i + 1) == '1')) {
                        result.add(AntTurn.RIGHT);
                        i += 2;
                    } else {
                        result.add(AntTurn.RIGHT);
                        i++;
                    }
                }
                case 'N', 'C', 'F' -> {
                    result.add(AntTurn.NONE);
                    i++;
                }
                case 'U', 'B' -> {
                    result.add(AntTurn.U_TURN);
                    i++;
                }
                default -> throw new IllegalArgumentException("Unknown rule character: " + c);
            }
        }
        return new LangtonMovementRules(result);
    }

    /**
     * Returns the turn for the given state index, wrapping around the configured rule list.
     *
     * @param state the visited-state index
     * @return the turn to apply for that state
     */
    public AntTurn getTurnForState(int state) {
        return turns.get(state % turns.size());
    }

    /**
     * Returns the number of configured movement rules.
     *
     * @return the rule count
     */
    public int getRuleCount() {
        return turns.size();
    }

    /**
     * Checks whether all configured turns are valid for the given cell shape.
     *
     * @param cellShape the cell shape to validate against
     * @return {@code true} if the rules can be used with the shape, otherwise {@code false}
     */
    public boolean isValidForCellShape(CellShape cellShape) {
        if (cellShape == CellShape.TRIANGLE) {
            for (AntTurn turn : turns) {
                if ((turn != AntTurn.LEFT) && (turn != AntTurn.RIGHT) && (turn != AntTurn.U_TURN)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the compact rule string representation.
     *
     * @return the compact rule string
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder(INITIAL_CAPACITY_DISPLAY_STRING);
        for (AntTurn turn : turns) {
            sb.append(switch (turn) {
                case LEFT -> "L";
                case RIGHT -> "R";
                case LEFT2 -> "L2";
                case RIGHT2 -> "R2";
                case NONE -> "N";
                case U_TURN -> "U";
            });
        }
        return sb.toString();
    }

    public enum AntTurn {
        LEFT, RIGHT, LEFT2, RIGHT2, NONE, U_TURN
    }

}
