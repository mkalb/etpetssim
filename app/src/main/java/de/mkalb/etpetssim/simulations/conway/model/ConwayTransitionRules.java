package de.mkalb.etpetssim.simulations.conway.model;

import java.util.*;

/**
 * Represents the transition rule set for Conway-style cellular automata.
 * <p>
 * The rules are defined by two sets:
 * <ul>
 *   <li>{@code surviveCounts}: neighbor counts for which a living cell survives</li>
 *   <li>{@code birthCounts}: neighbor counts for which a dead cell becomes alive</li>
 * </ul>
 * Both sets must contain non-negative integers and are stored in sorted order for display and logic purposes.
 * <p>
 * This record provides utility methods for rule evaluation and display formatting.
 *
 * @param surviveCounts the sorted set of neighbor counts for cell survival
 * @param birthCounts   the sorted set of neighbor counts for cell birth
 */
public record ConwayTransitionRules(
        SortedSet<Integer> surviveCounts,
        SortedSet<Integer> birthCounts) {

    /**
     * The minimum allowed neighbor count for Conway rules.
     * <p>
     * This value is used to validate rule sets and display formats.
     */
    public static final int MIN_NEIGHBOR_COUNT = 1;

    /**
     * The maximum allowed neighbor count for Conway rules.
     * <p>
     * This value ensures that all neighbor counts can be represented unambiguously in the display string format.
     */
    public static final int MAX_NEIGHBOR_COUNT = 9;

    /**
     * Constructs a new {@code ConwayTransitionRules} record with the specified survival and birth neighbor counts.
     * <p>
     * Both sets must be non-empty and all values must be within the allowed range from
     * {@link #MIN_NEIGHBOR_COUNT} to {@link #MAX_NEIGHBOR_COUNT}.
     *
     * @param surviveCounts the sorted set of neighbor counts for cell survival
     * @param birthCounts   the sorted set of neighbor counts for cell birth
     * @throws IllegalArgumentException if either set contains out-of-range values
     */
    public ConwayTransitionRules(SortedSet<Integer> surviveCounts,
                                 SortedSet<Integer> birthCounts) {
        for (int n : surviveCounts) {
            if ((n < MIN_NEIGHBOR_COUNT) || (n > MAX_NEIGHBOR_COUNT)) {
                throw new IllegalArgumentException("surviveCounts value out of range: " + n);
            }
        }
        for (int n : birthCounts) {
            if ((n < MIN_NEIGHBOR_COUNT) || (n > MAX_NEIGHBOR_COUNT)) {
                throw new IllegalArgumentException("birthCounts value out of range: " + n);
            }
        }
        this.surviveCounts = Collections.unmodifiableSortedSet(surviveCounts);
        this.birthCounts = Collections.unmodifiableSortedSet(birthCounts);
    }

    /**
     * Creates a new {@code ConwayTransitionRules} instance from two collections of neighbor counts.
     * <p>
     * The collections are converted to sorted sets internally.
     *
     * @param surviveCounts the collection of neighbor counts for cell survival
     * @param birthCounts   the collection of neighbor counts for cell birth
     * @return a new {@code ConwayTransitionRules} instance with the specified rules
     * @throws IllegalArgumentException if either set contains out-of-range values
     */
    public static ConwayTransitionRules of(Collection<Integer> surviveCounts, Collection<Integer> birthCounts) {
        return new ConwayTransitionRules(new java.util.TreeSet<>(surviveCounts), new java.util.TreeSet<>(birthCounts));
    }

    /**
     * Creates a new {@code ConwayTransitionRules} instance from a display string.
     * <p>
     * The input string must be in the format {@code surviveCounts/birthCounts},
     * where each part is a sequence of digits representing allowed neighbor counts.
     * <br>
     * Example: {@code 23/3} or {@code 1257/1357}
     * <p>
     * The numbers before the slash represent the neighbor counts for which a cell survives.
     * The numbers after the slash represent the neighbor counts for which a cell is born.
     *
     * @param displayString the string representation of Conway rules
     * @return a new {@code ConwayTransitionRules} instance parsed from the string
     * @throws IllegalArgumentException if the format is invalid or contains out-of-range values
     */
    public static ConwayTransitionRules of(String displayString) {
        if (!displayString.contains("/")) {
            throw new IllegalArgumentException("Invalid format: must contain '/'");
        }
        String[] parts = displayString.split("/", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format: must have one '/'");
        }
        java.util.SortedSet<Integer> survive = new java.util.TreeSet<>();
        java.util.SortedSet<Integer> birth = new java.util.TreeSet<>();
        for (char c : parts[0].toCharArray()) {
            if (Character.isDigit(c)) {
                survive.add(Character.getNumericValue(c));
            }
        }
        for (char c : parts[1].toCharArray()) {
            if (Character.isDigit(c)) {
                birth.add(Character.getNumericValue(c));
            }
        }
        return new ConwayTransitionRules(survive, birth);
    }

    /**
     * Checks if a cell should survive based on the number of alive neighbors.
     *
     * @param aliveNeighbors the number of alive neighboring cells
     * @return {@code true} if the cell survives, {@code false} otherwise
     */
    public boolean shouldSurvive(int aliveNeighbors) {
        return surviveCounts.contains(aliveNeighbors);
    }

    /**
     * Checks if a cell should be born based on the number of alive neighbors.
     *
     * @param aliveNeighbors the number of alive neighboring cells
     * @return {@code true} if the cell is born, {@code false} otherwise
     */
    public boolean shouldBeBorn(int aliveNeighbors) {
        return birthCounts.contains(aliveNeighbors);
    }

    /**
     * Returns a short, human-readable string representation of the Conway rules.
     * <p>
     * Format: {@code surviveCounts/birthCounts}
     * <br>
     * Example: {@code 23/3} or {@code 1257/1357}
     * <p>
     * The numbers before the slash represent the neighbor counts for which a cell survives.
     * The numbers after the slash represent the neighbor counts for which a cell is born.
     *
     * @return a concise display string for these Conway rules
     */
    public String toDisplayString() {
        String survive = surviveCounts.stream()
                                      .map(String::valueOf)
                                      .collect(java.util.stream.Collectors.joining());
        String birth = birthCounts.stream()
                                  .map(String::valueOf)
                                  .collect(java.util.stream.Collectors.joining());
        return survive + "/" + birth;
    }

}
