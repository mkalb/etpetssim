package de.mkalb.etpetssim.simulations.conway.shared;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Represents the transition rule set for Conway-style cellular automata.
 *
 * <p>The rules are defined by two sets:
 * <ul>
 *   <li>{@code surviveCounts}: neighbor counts for which a living cell survives</li>
 *   <li>{@code birthCounts}: neighbor counts for which a dead cell becomes alive</li>
 * </ul>
 *
 * <p>Both sets are stored as unmodifiable copies in ascending order, regardless of the order of the given sets.
 *
 * @param surviveCounts the sorted set of neighbor counts for cell survival
 * @param birthCounts   the sorted set of neighbor counts for cell birth
 */
public record ConwayTransitionRules(
        SortedSet<Integer> surviveCounts,
        SortedSet<Integer> birthCounts) {

    /**
     * The minimum allowed neighbor count for Conway rules.
     */
    public static final int MIN_NEIGHBOR_COUNT = 0;

    /**
     * The maximum allowed neighbor count for Conway rules.
     *
     * <p>This value ensures that all neighbor counts can be represented unambiguously in the display string format.
     */
    public static final int MAX_NEIGHBOR_COUNT = 9;

    /**
     * Creates transition rules with unmodifiable copies of both sets in ascending order.
     *
     * @throws IllegalArgumentException if either set contains a value less than {@link #MIN_NEIGHBOR_COUNT} or greater
     *                                  than {@link #MAX_NEIGHBOR_COUNT}
     */
    public ConwayTransitionRules {
        checkNeighborCounts("surviveCounts", surviveCounts);
        checkNeighborCounts("birthCounts", birthCounts);
        surviveCounts = copyInAscendingOrder(surviveCounts);
        birthCounts = copyInAscendingOrder(birthCounts);
    }

    /**
     * Creates a new {@code ConwayTransitionRules} instance from two collections of neighbor counts.
     *
     * <p>The collections are converted to sorted sets internally.
     *
     * @param surviveCounts the collection of neighbor counts for cell survival
     * @param birthCounts   the collection of neighbor counts for cell birth
     * @return a new {@code ConwayTransitionRules} instance with the specified rules
     * @throws IllegalArgumentException if either collection contains out-of-range values
     */
    public static ConwayTransitionRules of(Collection<Integer> surviveCounts, Collection<Integer> birthCounts) {
        return new ConwayTransitionRules(new TreeSet<>(surviveCounts), new TreeSet<>(birthCounts));
    }

    /**
     * Parses a display string in S/B notation into a new {@code ConwayTransitionRules} instance.
     *
     * <p>S/B notation (also called <em>traditional notation</em>) is one of two common rule string formats for
     * Conway-style cellular automata. It places survival counts before the slash and birth counts after the slash.
     *
     * <p>The input string must be in the format {@code S/B}, where each part is a sequence of digits representing
     * allowed neighbor counts. Non-digit characters other than the slash separator are silently ignored during
     * parsing. Examples: {@code 23/3} (Conway's Game of Life) or {@code 1257/1357}.
     *
     * <p>The alternative <em>B/S notation</em> ({@code B3/S23}) is not supported by this method.
     *
     * @param displayString the S/B rule string to parse
     * @return a new {@code ConwayTransitionRules} instance parsed from the string
     * @throws IllegalArgumentException if the string does not contain exactly one slash
     * @see #toDisplayString()
     */
    public static ConwayTransitionRules parse(String displayString) {
        if (!displayString.contains("/")) {
            throw new IllegalArgumentException("Invalid format: must contain '/'");
        }
        String[] parts = displayString.split("/", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format: must have one '/'");
        }
        SortedSet<Integer> survive = new TreeSet<>();
        SortedSet<Integer> birth = new TreeSet<>();
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

    private static void checkNeighborCounts(String componentName, Collection<Integer> neighborCounts) {
        for (int n : neighborCounts) {
            if ((n < MIN_NEIGHBOR_COUNT) || (n > MAX_NEIGHBOR_COUNT)) {
                throw new IllegalArgumentException(componentName + " value out of range: " + n);
            }
        }
    }

    private static SortedSet<Integer> copyInAscendingOrder(Collection<Integer> neighborCounts) {
        return Collections.unmodifiableSortedSet(new TreeSet<>(neighborCounts));
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
     * Returns a short, human-readable string representation of these Conway rules in <em>S/B notation</em> (also
     * called <em>traditional notation</em>).
     *
     * <p>S/B notation is one of two common rule string formats for Conway-style cellular automata. Survival counts
     * appear before the slash, birth counts after. The alternative format, <em>B/S notation</em> (e.g.,
     * {@code B3/S23}), reverses this order and adds letter prefixes.
     *
     * <p>Format: {@code S/B}, survival neighbor counts followed by {@code /} followed by birth neighbor counts, each
     * part as a sorted sequence of single digits. Examples: {@code 23/3} (Conway's Game of Life) or {@code 1257/1357}.
     *
     * <p>The returned string can be parsed back by {@link #parse(String)}.
     *
     * @return a concise S/B notation rule string for these Conway rules
     * @see #parse(String)
     */
    public String toDisplayString() {
        String survive = surviveCounts.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
        String birth = birthCounts.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
        return survive + "/" + birth;
    }

}

