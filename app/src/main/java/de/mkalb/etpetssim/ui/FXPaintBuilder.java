package de.mkalb.etpetssim.ui;

import javafx.scene.paint.*;

import java.util.*;

/**
 * Utility class for creating various JavaFX {@link Paint} objects such as gradients and solid colors.
 * <p>
 * This class provides static factory methods to simplify the creation of common paint patterns
 * for JavaFX components. It cannot be instantiated.
 */
@SuppressWarnings("MagicNumber")
public final class FXPaintBuilder {

    /**
     * Private constructor to prevent instantiation.
     */
    private FXPaintBuilder() {
    }

    /**
     * Returns a map of brightness variants for a base color.
     * <p>
     * For each integer value in the range [min, max], assigns a color variant based on the brightness adjustment.
     * All values within a step group share the same color.
     *
     * @param baseColor  the base color to adjust
     * @param min        the lower bound (inclusive)
     * @param max        the upper bound (inclusive)
     * @param step       the step size for grouping values
     * @param brighten   true to brighten, false to darken
     * @param factorStep the brightness factor per step (e.g. 0.08)
     * @return a map from integer value to color variant
     */
    public static Map<Integer, Color> getBrightnessVariantsMap(
            Color baseColor,
            int min,
            int max,
            int step,
            boolean brighten,
            double factorStep) {
        int range = max - min;
        double steps = range / (double) step;
        double invRange = 1.0 / range;
        int direction = brighten ? 1 : -1;
        double factorStepTotal = factorStep * steps;
        double stepFactor = direction * invRange * factorStepTotal;

        Map<Integer, Color> map = HashMap.newHashMap(range + 1);
        for (int s = min; s <= max; s += step) {
            double factor = 1.0 + ((s - min) * stepFactor);
            Color color = adjustBrightness(baseColor, factor);
            int end = Math.min((s + step) - 1, max);
            for (int i = s; i <= end; i++) {
                map.put(i, color);
            }
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
        double r = Math.min(color.getRed() * factor, 1.0);
        double g = Math.min(color.getGreen() * factor, 1.0);
        double b = Math.min(color.getBlue() * factor, 1.0);
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
                0, 0, 1, 0, // horizontal
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
                0, 0, 0, 1, // vertical
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
                0, 0, // focusAngle, focusDistance
                0.5, 0.5, // centerX, centerY (relative)
                0.5, // radius (relative)
                true, // proportional
                CycleMethod.NO_CYCLE,
                new Stop(0, centerColor),
                new Stop(1, edgeColor)
        );
    }

    /**
     * Creates a new {@link Color} with the same RGB values as the given base color,
     * but with the specified alpha (opacity).
     *
     * @param baseColor the base color to use for RGB values
     * @param alpha     the alpha value (opacity) in the range [0.0, 1.0]
     * @return a new Color with the specified alpha
     * @throws IllegalArgumentException if alpha is not in the range [0.0, 1.0]
     */
    public static Color createColorWithAlpha(Color baseColor, double alpha) {
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
