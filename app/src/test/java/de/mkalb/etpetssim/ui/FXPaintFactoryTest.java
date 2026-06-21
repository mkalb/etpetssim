package de.mkalb.etpetssim.ui;

import javafx.scene.paint.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class FXPaintFactoryTest {

    private static final double DELTA = 0.000_001d;
    private static final Color BASE_COLOR = Color.color(0.2d, 0.4d, 0.6d, 0.8d);

    private static void assertColorEquals(Color expected, Color actual) {
        assertAll(
                () -> assertEquals(expected.getRed(), actual.getRed(), DELTA),
                () -> assertEquals(expected.getGreen(), actual.getGreen(), DELTA),
                () -> assertEquals(expected.getBlue(), actual.getBlue(), DELTA),
                () -> assertEquals(expected.getOpacity(), actual.getOpacity(), DELTA)
        );
    }

    @Test
    void testComputeBrightnessVariantsMapCoversRangeAndUsesEffectiveGroupCount() {
        Map<Integer, Color> variants = FXPaintFactory.computeBrightnessVariantsMap(BASE_COLOR, 1, 3, 5, 1.0d);

        assertAll(
                () -> assertEquals(Set.of(1, 2, 3), variants.keySet()),
                () -> assertColorEquals(BASE_COLOR, variants.get(1)),
                () -> assertColorEquals(FXPaintFactory.adjustBrightness(BASE_COLOR, 1.5d), variants.get(2)),
                () -> assertColorEquals(FXPaintFactory.adjustBrightness(BASE_COLOR, 2.0d), variants.get(3))
        );
    }

    @Test
    void testComputeBrightnessVariantsMapRejectsInvalidArguments() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.computeBrightnessVariantsMap(BASE_COLOR, 3, 1, 1, 0.0d)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.computeBrightnessVariantsMap(BASE_COLOR, 1, 3, 0, 0.0d)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.computeBrightnessVariantsMap(BASE_COLOR, 1, 3, 1, Double.NaN)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.computeBrightnessVariantsMap(BASE_COLOR, 1, 3, 1, -1.1d)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.computeBrightnessVariantsMap(BASE_COLOR, 1, 3, 1, 10.1d))
        );
    }

    @Test
    void testAdjustBrightnessClampsComponentsAndPreservesOpacity() {
        Color brightened = FXPaintFactory.adjustBrightness(BASE_COLOR, 2.0d);
        Color darkened = FXPaintFactory.adjustBrightness(BASE_COLOR, 0.5d);

        assertAll(
                () -> assertEquals(0.4d, brightened.getRed(), DELTA),
                () -> assertEquals(0.8d, brightened.getGreen(), DELTA),
                () -> assertEquals(1.0d, brightened.getBlue(), DELTA),
                () -> assertEquals(BASE_COLOR.getOpacity(), brightened.getOpacity(), DELTA),
                () -> assertEquals(0.1d, darkened.getRed(), DELTA),
                () -> assertEquals(0.2d, darkened.getGreen(), DELTA),
                () -> assertEquals(0.3d, darkened.getBlue(), DELTA),
                () -> assertEquals(BASE_COLOR.getOpacity(), darkened.getOpacity(), DELTA)
        );
    }

    @Test
    void testAdjustBrightnessRejectsNonFiniteFactor() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.adjustBrightness(BASE_COLOR, Double.NaN)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.adjustBrightness(BASE_COLOR, Double.POSITIVE_INFINITY))
        );
    }

    @Test
    void testCreateGradientsUseExpectedStopsAndDirection() {
        LinearGradient horizontal = (LinearGradient) FXPaintFactory.createHorizontalGradient(Color.RED, Color.BLUE);
        LinearGradient vertical = (LinearGradient) FXPaintFactory.createVerticalGradient(Color.RED, Color.BLUE);
        RadialGradient radial = (RadialGradient) FXPaintFactory.createRadialGradient(Color.RED, Color.BLUE);

        assertAll(
                () -> assertEquals(0.0d, horizontal.getStartX(), DELTA),
                () -> assertEquals(1.0d, horizontal.getEndX(), DELTA),
                () -> assertEquals(0.0d, vertical.getStartY(), DELTA),
                () -> assertEquals(1.0d, vertical.getEndY(), DELTA),
                () -> assertEquals(0.5d, radial.getCenterX(), DELTA),
                () -> assertEquals(0.5d, radial.getCenterY(), DELTA),
                () -> assertEquals(List.of(new Stop(0, Color.RED), new Stop(1, Color.BLUE)), horizontal.getStops()),
                () -> assertEquals(List.of(new Stop(0, Color.RED), new Stop(1, Color.BLUE)), vertical.getStops()),
                () -> assertEquals(List.of(new Stop(0, Color.RED), new Stop(1, Color.BLUE)), radial.getStops())
        );
    }

    @Test
    void testAdjustColorAlphaPreservesRgbAndRejectsInvalidAlpha() {
        Color adjusted = FXPaintFactory.adjustColorAlpha(BASE_COLOR, 0.25d);

        assertAll(
                () -> assertEquals(BASE_COLOR.getRed(), adjusted.getRed(), DELTA),
                () -> assertEquals(BASE_COLOR.getGreen(), adjusted.getGreen(), DELTA),
                () -> assertEquals(BASE_COLOR.getBlue(), adjusted.getBlue(), DELTA),
                () -> assertEquals(0.25d, adjusted.getOpacity(), DELTA),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.adjustColorAlpha(BASE_COLOR, -0.1d)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.adjustColorAlpha(BASE_COLOR, 1.1d)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> FXPaintFactory.adjustColorAlpha(BASE_COLOR, Double.NaN))
        );
    }

}
