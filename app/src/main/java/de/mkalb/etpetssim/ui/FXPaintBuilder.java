package de.mkalb.etpetssim.ui;

import javafx.scene.paint.*;

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
