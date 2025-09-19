package de.mkalb.etpetssim.simulations.langton.model;

import java.util.*;

public record LangtonMovementRules(
        List<AntTurn> turns) {

    public static final int MIN_RULE_COUNT = 2;

    public LangtonMovementRules(List<AntTurn> turns) {
        if (turns.size() < MIN_RULE_COUNT) {
            throw new IllegalArgumentException("At least " + MIN_RULE_COUNT + " rules required, got: " + turns.size());
        }
        this.turns = List.copyOf(turns);
    }

    public static LangtonMovementRules fromString(String ruleString) {
        List<AntTurn> result = new ArrayList<>();
        for (int i = 0; i < ruleString.length(); ) {
            char c = ruleString.charAt(i);
            switch (c) {
                case 'L' -> {
                    if (((i + 1) < ruleString.length()) && (ruleString.charAt(i + 1) == '2')) {
                        result.add(AntTurn.LEFT2);
                        i += 2;
                    } else {
                        result.add(AntTurn.LEFT);
                        i++;
                    }
                }
                case 'R' -> {
                    if (((i + 1) < ruleString.length()) && (ruleString.charAt(i + 1) == '2')) {
                        result.add(AntTurn.RIGHT2);
                        i += 2;
                    } else {
                        result.add(AntTurn.RIGHT);
                        i++;
                    }
                }
                case 'N' -> {
                    result.add(AntTurn.NONE);
                    i++;
                }
                default -> throw new IllegalArgumentException("Unknown rule character: " + c);
            }
        }
        return new LangtonMovementRules(result);
    }

    public AntTurn getTurnForState(int state) {
        return turns.get(state % turns.size());
    }

    public int getColorCount() {
        return turns.size();
    }

    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        for (AntTurn turn : turns) {
            sb.append(switch (turn) {
                case LEFT -> "L";
                case RIGHT -> "R";
                case NONE -> "N";
                case LEFT2 -> "L2";
                case RIGHT2 -> "R2";
            });
        }
        return sb.toString();
    }

    public enum AntTurn {
        LEFT, RIGHT, NONE, LEFT2, RIGHT2
    }

}