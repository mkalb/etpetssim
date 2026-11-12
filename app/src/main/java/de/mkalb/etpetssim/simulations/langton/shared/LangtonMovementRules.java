package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.CellShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    /**
     * Creates movement rules with an unmodifiable copy of the turn sequence.
     *
     * @throws IllegalArgumentException if the sequence has fewer than {@link #MIN_RULE_COUNT} or more than
     *                                  {@link #MAX_RULE_COUNT} turns
     */
    public LangtonMovementRules {
        if (turns.size() < MIN_RULE_COUNT) {
            throw new IllegalArgumentException("turns.size() must be >= " + MIN_RULE_COUNT
                    + ", but was: " + turns.size());
        }
        if (turns.size() > MAX_RULE_COUNT) {
            throw new IllegalArgumentException("turns.size() must be <= " + MAX_RULE_COUNT
                    + ", but was: " + turns.size());
        }
        turns = List.copyOf(turns);
    }

    /**
     * Parses a compact turn string such as {@code LR}, {@code L2R2}, or {@code LRU}.
     *
     * <p>The string is trimmed and case-insensitive. {@code L} and {@code R} may be followed by {@code 1} (one
     * direction step) or {@code 2} (two direction steps); {@code N}, {@code C}, and {@code F} keep the direction, and
     * {@code U} and {@code B} reverse it.
     *
     * @param ruleString the compact rule string to parse
     * @return the parsed movement rules
     * @throws IllegalArgumentException if the string contains an unknown character or too few or too many turns
     */
    public static LangtonMovementRules parse(String ruleString) {
        String preparedString = ruleString.trim().toUpperCase(Locale.ROOT);
        List<AntTurn> result = new ArrayList<>();
        for (int i = 0; i < preparedString.length(); ) {
            char symbol = preparedString.charAt(i);
            switch (symbol) {
                case 'L', 'R' -> {
                    char next = ((i + 1) < preparedString.length()) ? preparedString.charAt(i + 1) : ' ';
                    boolean twoSteps = next == '2';
                    if (symbol == 'L') {
                        result.add(twoSteps ? AntTurn.LEFT2 : AntTurn.LEFT);
                    } else {
                        result.add(twoSteps ? AntTurn.RIGHT2 : AntTurn.RIGHT);
                    }
                    i += (twoSteps || (next == '1')) ? 2 : 1;
                }
                case 'N', 'C', 'F' -> {
                    result.add(AntTurn.NONE);
                    i++;
                }
                case 'U', 'B' -> {
                    result.add(AntTurn.U_TURN);
                    i++;
                }
                default -> throw new IllegalArgumentException("Unable to parse ruleString: unknown character '"
                        + symbol + "', ruleString=" + ruleString);
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
    public AntTurn turnForState(int state) {
        return turns.get(state % turns.size());
    }

    public int ruleCount() {
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

    /**
     * Turn that an ant makes before its next move.
     *
     * <p>One direction step is 90 degrees on square cells and 60 degrees on hexagon and triangle cells.
     */
    public enum AntTurn {

        /**
         * Turns one direction step counterclockwise.
         */
        LEFT,

        /**
         * Turns one direction step clockwise.
         */
        RIGHT,

        /**
         * Turns two direction steps counterclockwise; acts as {@link #U_TURN} on square cells and is not supported
         * on triangle cells.
         */
        LEFT2,

        /**
         * Turns two direction steps clockwise; acts as {@link #U_TURN} on square cells and is not supported
         * on triangle cells.
         */
        RIGHT2,

        /**
         * Keeps the current direction; not supported on triangle cells.
         */
        NONE,

        /**
         * Reverses the current direction; supported on all cell shapes.
         */
        U_TURN

    }

}
