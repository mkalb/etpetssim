package de.mkalb.etpetssim.ui;

import javafx.scene.paint.*;

import java.util.*;

/**
 * Utility class for creating various JavaFX {@link Paint} objects such as gradients and solid colors.
 * <p>
 * This class provides static factory methods to simplify the creation of common paint patterns
 * for JavaFX components. It cannot be instantiated.
 */
@SuppressWarnings({"MagicNumber", "SpellCheckingInspection"})
public final class FXPaintFactory {

    public static final Color BACKGROUND_COLOR = Color.web("#1a2233");
    public static final Color BORDER_COLOR = Color.web("#00eaff");

    /**
     * Private constructor to prevent instantiation.
     */
    private FXPaintFactory() {
    }

    /**
     * Returns a map of brightness variants for a base color.
     * <p>
     * For each integer value in the range {@code [minInclusive, maxInclusive]}, assigns a color variant based on brightness adjustment.
     * All values within a group share the same color.
     * The brightness adjustment is distributed linearly from the base color (factor 1.0) up to {@code 1.0 + maxFactorDelta}.
     * If {@code maxFactorDelta} is positive, colors are brightened; if negative, colors are darkened.
     * The number of groups determines how many distinct brightness levels are used.
     * The distribution ensures all values are covered, and groups are as evenly sized as possible.
     *
     * @param baseColor      the base color to adjust
     * @param minInclusive   the lower bound (inclusive)
     * @param maxInclusive   the upper bound (inclusive)
     * @param groupCount     the number of brightness groups
     * @param maxFactorDelta the total brightness change (positive to brighten, negative to darken)
     * @return a map from integer value to color variant
     * @throws IllegalArgumentException if the range is invalid, groupCount is less than 1,
     * or maxFactorDelta is outside the range [-1.0, 10.0]
     */
    public static Map<Integer, Color> getBrightnessVariantsMap(
            Color baseColor,
            int minInclusive,
            int maxInclusive,
            int groupCount,
            double maxFactorDelta) {
        int range = (maxInclusive - minInclusive) + 1;
        if (range < 1) {
            throw new IllegalArgumentException("Invalid range: " + minInclusive + " to " + maxInclusive);
        }
        if (groupCount < 1) {
            throw new IllegalArgumentException("Group count must be at least 1: " + groupCount);
        }
        if (Double.isNaN(maxFactorDelta) || (maxFactorDelta < -1.0d) || (maxFactorDelta > 10.0d)) {
            throw new IllegalArgumentException("maxFactorDelta must be in the range [-1.0, 10.0]: " + maxFactorDelta);
        }

        int baseGroupSize = Math.max(1, range / groupCount);
        int remainder = range % groupCount;
        double brightnessStep = (groupCount > 1) ? (maxFactorDelta / (groupCount - 1)) : 0.0d;

        Map<Integer, Color> map = HashMap.newHashMap(range);
        int groupStart = minInclusive;
        for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
            double groupIndexFactor = 1.0d + (groupIndex * brightnessStep);
            Color groupColor = adjustBrightness(baseColor, groupIndexFactor);

            int currentGroupSize = baseGroupSize + ((groupIndex < remainder) ? 1 : 0);
            int groupEnd = Math.min((groupStart + currentGroupSize) - 1, maxInclusive);

            for (int value = groupStart; value <= groupEnd; value++) {
                map.put(value, groupColor);
            }
            groupStart += currentGroupSize;
        }

        return map;
    }

    /**
     * Returns a new {@link Color} with adjusted brightness.
     * <p>
     * A factor greater than 1.0 brightens the color, less than 1.0 darkens it.
     *
     * @param color  the base color to adjust
     * @param factor the brightness factor
     * @return a new color with adjusted brightness
     */
    public static Color adjustBrightness(Color color, double factor) {
        double r = Math.max(Math.min(color.getRed() * factor, 1.0d), 0.0d);
        double g = Math.max(Math.min(color.getGreen() * factor, 1.0d), 0.0d);
        double b = Math.max(Math.min(color.getBlue() * factor, 1.0d), 0.0d);
        return new Color(r, g, b, color.getOpacity());
    }

    /**
     * Creates a horizontal linear gradient from left to right.
     *
     * @param leftColor  the color at the left edge
     * @param rightColor the color at the right edge
     * @return a horizontal LinearGradient
     */
    public static Paint createHorizontalGradient(Color leftColor, Color rightColor) {
        return new LinearGradient(
                0, 0, 1, 0, // Horizontal.
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, leftColor),
                new Stop(1, rightColor)
        );
    }

    /**
     * Creates a vertical linear gradient from top to bottom.
     *
     * @param topColor    the color at the top edge
     * @param bottomColor the color at the bottom edge
     * @return a vertical LinearGradient
     */
    public static Paint createVerticalGradient(Color topColor, Color bottomColor) {
        return new LinearGradient(
                0, 0, 0, 1, // Vertical.
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, topColor),
                new Stop(1, bottomColor)
        );
    }

    /**
     * Creates a radial gradient from the center to the edge.
     *
     * @param centerColor the color at the center of the gradient
     * @param edgeColor   the color at the edge of the gradient
     * @return a RadialGradient from centerColor to edgeColor
     */
    public static Paint createRadialGradient(Color centerColor, Color edgeColor) {
        return new RadialGradient(
                0, 0, // Focus angle, focus distance.
                0.5, 0.5, // Center X, center Y (relative).
                0.5, // Radius (relative).
                true, // Proportional.
                CycleMethod.NO_CYCLE,
                new Stop(0, centerColor),
                new Stop(1, edgeColor)
        );
    }

    /**
     * Returns a new {@link Color} with the same RGB values as the given base color,
     * but with the specified alpha (opacity).
     * <p>
     * The red, green, and blue components are preserved, while the opacity is set to {@code alpha}.
     *
     * @param baseColor the base color whose RGB values are used
     * @param alpha     the alpha value (opacity) in the range [0.0, 1.0]
     * @return a new Color with the specified alpha
     * @throws IllegalArgumentException if {@code alpha} is not in the range [0.0, 1.0]
     */
    public static Color adjustColorAlpha(Color baseColor, double alpha) {
        if ((alpha < 0.0) || (alpha > 1.0)) {
            throw new IllegalArgumentException("Alpha must be in the range [0.0, 1.0]");
        }
        return new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                alpha
        );
    }

}
