package de.mkalb.etpetssim.ui;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class CellDimensionTest {

    private static final double EDGE_LENGTH = 10.0d;
    private static final double WIDTH = 20.0d;
    private static final double HEIGHT = 30.0d;
    private static final double HALF_EDGE_LENGTH = EDGE_LENGTH / 2.0d;
    private static final double HALF_WIDTH = WIDTH / 2.0d;
    private static final double HALF_HEIGHT = HEIGHT / 2.0d;
    private static final double INNER_RADIUS = 8.0d;
    private static final double OUTER_RADIUS = 12.0d;
    private static final double COLUMN_WIDTH = 18.0d;
    private static final double ROW_HEIGHT = 24.0d;
    private static final double TOP_LEFT_X = 3.5d;
    private static final double TOP_LEFT_Y = 4.5d;
    private static final double INVALID_ZERO = 0.0d;
    private static final double INVALID_NEGATIVE = -1.0d;
    private static final double HALF_FACTOR = 2.0d;
    private static final double HALF_DIMENSION_OFFSET = 0.25d;
    private static final double DISPLAY_EDGE_LENGTH = 1_234.5d;
    private static final double DISPLAY_WIDTH = 2_345.6d;
    private static final double DISPLAY_HEIGHT = 3_456.7d;

    private Locale defaultLocale;

    @BeforeEach
    void setUpBeforeEach() {
        defaultLocale = Locale.getDefault();
    }

    @AfterEach
    void tearDownAfterEach() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    void testRecord() {
        CellDimension dimension = createValidCellDimension();

        assertAll(
                () -> assertEquals(EDGE_LENGTH, dimension.edgeLength()),
                () -> assertEquals(WIDTH, dimension.width()),
                () -> assertEquals(HEIGHT, dimension.height()),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.halfEdgeLength()),
                () -> assertEquals(HALF_WIDTH, dimension.halfWidth()),
                () -> assertEquals(HALF_HEIGHT, dimension.halfHeight()),
                () -> assertEquals(INNER_RADIUS, dimension.innerRadius()),
                () -> assertEquals(OUTER_RADIUS, dimension.outerRadius()),
                () -> assertEquals(COLUMN_WIDTH, dimension.columnWidth()),
                () -> assertEquals(ROW_HEIGHT, dimension.rowHeight()),
                () -> assertTrue(dimension.toString().contains("edgeLength=" + EDGE_LENGTH)),
                () -> assertTrue(dimension.toString().contains("width=" + WIDTH)),
                () -> assertTrue(dimension.toString().contains("height=" + HEIGHT))
        );
    }

    @Test
    void testConstructorRejectsNonPositiveRequiredDimensions() {
        assertAll(
                () -> assertPositiveDimensionValidationFailure(() -> createCellDimension(
                        INVALID_ZERO, WIDTH, HEIGHT, HALF_EDGE_LENGTH, HALF_WIDTH, HALF_HEIGHT,
                        INNER_RADIUS, OUTER_RADIUS, COLUMN_WIDTH, ROW_HEIGHT)),
                () -> assertPositiveDimensionValidationFailure(() -> createCellDimension(
                        EDGE_LENGTH, INVALID_NEGATIVE, HEIGHT, HALF_EDGE_LENGTH, HALF_WIDTH, HALF_HEIGHT,
                        INNER_RADIUS, OUTER_RADIUS, COLUMN_WIDTH, ROW_HEIGHT)),
                () -> assertPositiveDimensionValidationFailure(() -> createCellDimension(
                        EDGE_LENGTH, WIDTH, INVALID_ZERO, HALF_EDGE_LENGTH, HALF_WIDTH, HALF_HEIGHT,
                        INNER_RADIUS, OUTER_RADIUS, COLUMN_WIDTH, ROW_HEIGHT)),
                () -> assertPositiveDimensionValidationFailure(() -> createCellDimension(
                        EDGE_LENGTH, WIDTH, HEIGHT, HALF_EDGE_LENGTH, HALF_WIDTH, HALF_HEIGHT,
                        INVALID_NEGATIVE, OUTER_RADIUS, COLUMN_WIDTH, ROW_HEIGHT)),
                () -> assertPositiveDimensionValidationFailure(() -> createCellDimension(
                        EDGE_LENGTH, WIDTH, HEIGHT, HALF_EDGE_LENGTH, HALF_WIDTH, HALF_HEIGHT,
                        INNER_RADIUS, INVALID_ZERO, COLUMN_WIDTH, ROW_HEIGHT)),
                () -> assertPositiveDimensionValidationFailure(() -> createCellDimension(
                        EDGE_LENGTH, WIDTH, HEIGHT, HALF_EDGE_LENGTH, HALF_WIDTH, HALF_HEIGHT,
                        INNER_RADIUS, OUTER_RADIUS, INVALID_NEGATIVE, ROW_HEIGHT)),
                () -> assertPositiveDimensionValidationFailure(() -> createCellDimension(
                        EDGE_LENGTH, WIDTH, HEIGHT, HALF_EDGE_LENGTH, HALF_WIDTH, HALF_HEIGHT,
                        INNER_RADIUS, OUTER_RADIUS, COLUMN_WIDTH, INVALID_ZERO))
        );
    }

    @Test
    void testConstructorRejectsMismatchedHalfDimensions() {
        assertAll(
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> createCellDimension(
                            EDGE_LENGTH,
                            WIDTH,
                            HEIGHT,
                            HALF_EDGE_LENGTH + HALF_DIMENSION_OFFSET,
                            HALF_WIDTH,
                            HALF_HEIGHT,
                            INNER_RADIUS,
                            OUTER_RADIUS,
                            COLUMN_WIDTH,
                            ROW_HEIGHT));
                    assertTrue(exception.getMessage().contains("Half dimensions must be half of the full dimensions."));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> createCellDimension(
                            EDGE_LENGTH,
                            WIDTH,
                            HEIGHT,
                            HALF_EDGE_LENGTH,
                            HALF_WIDTH + HALF_DIMENSION_OFFSET,
                            HALF_HEIGHT,
                            INNER_RADIUS,
                            OUTER_RADIUS,
                            COLUMN_WIDTH,
                            ROW_HEIGHT));
                    assertTrue(exception.getMessage().contains("Half dimensions must be half of the full dimensions."));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> createCellDimension(
                            EDGE_LENGTH,
                            WIDTH,
                            HEIGHT,
                            HALF_EDGE_LENGTH,
                            HALF_WIDTH,
                            HALF_HEIGHT + HALF_DIMENSION_OFFSET,
                            INNER_RADIUS,
                            OUTER_RADIUS,
                            COLUMN_WIDTH,
                            ROW_HEIGHT));
                    assertTrue(exception.getMessage().contains("Half dimensions must be half of the full dimensions."));
                }
        );
    }

    @Test
    void testConstructorRejectsOuterRadiusNotGreaterThanInnerRadius() {
        assertAll(
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> createCellDimension(
                            EDGE_LENGTH,
                            WIDTH,
                            HEIGHT,
                            HALF_EDGE_LENGTH,
                            HALF_WIDTH,
                            HALF_HEIGHT,
                            INNER_RADIUS,
                            INNER_RADIUS,
                            COLUMN_WIDTH,
                            ROW_HEIGHT));
                    assertTrue(exception.getMessage().contains("Outer radius must be greater than inner radius."));
                },
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> createCellDimension(
                            EDGE_LENGTH,
                            WIDTH,
                            HEIGHT,
                            HALF_EDGE_LENGTH,
                            HALF_WIDTH,
                            HALF_HEIGHT,
                            INNER_RADIUS,
                            INNER_RADIUS - HALF_DIMENSION_OFFSET,
                            COLUMN_WIDTH,
                            ROW_HEIGHT));
                    assertTrue(exception.getMessage().contains("Outer radius must be greater than inner radius."));
                }
        );
    }

    @Test
    void testBoundingBoxDimension() {
        CellDimension dimension = createValidCellDimension();

        assertEquals(new Dimension2D(WIDTH, HEIGHT), dimension.boundingBoxDimension());
    }

    @Test
    void testBoundingBoxAtCoordinates() {
        CellDimension dimension = createValidCellDimension();

        assertEquals(new Rectangle2D(TOP_LEFT_X, TOP_LEFT_Y, WIDTH, HEIGHT),
                dimension.boundingBoxAt(TOP_LEFT_X, TOP_LEFT_Y));
    }

    @Test
    void testBoundingBoxAtPoint() {
        CellDimension dimension = createValidCellDimension();
        Point2D topLeftPoint = new Point2D(TOP_LEFT_X, TOP_LEFT_Y);

        assertEquals(new Rectangle2D(TOP_LEFT_X, TOP_LEFT_Y, WIDTH, HEIGHT), dimension.boundingBoxAt(topLeftPoint));
    }

    @Test
    void testToDisplayStringUsesUsLocaleFormatting() {
        Locale.setDefault(Locale.GERMANY);
        CellDimension dimension = createCellDimension(
                DISPLAY_EDGE_LENGTH,
                DISPLAY_WIDTH,
                DISPLAY_HEIGHT,
                DISPLAY_EDGE_LENGTH / HALF_FACTOR,
                DISPLAY_WIDTH / HALF_FACTOR,
                DISPLAY_HEIGHT / HALF_FACTOR,
                INNER_RADIUS,
                OUTER_RADIUS + DISPLAY_EDGE_LENGTH,
                COLUMN_WIDTH,
                ROW_HEIGHT);

        assertEquals("[1234.5, 2345.6 × 3456.7]", dimension.toDisplayString());
    }

    private void assertPositiveDimensionValidationFailure(Executable executable) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
        assertTrue(exception.getMessage().contains("All dimensions must be positive."));
    }

    private CellDimension createValidCellDimension() {
        return createCellDimension(
                EDGE_LENGTH,
                WIDTH,
                HEIGHT,
                HALF_EDGE_LENGTH,
                HALF_WIDTH,
                HALF_HEIGHT,
                INNER_RADIUS,
                OUTER_RADIUS,
                COLUMN_WIDTH,
                ROW_HEIGHT);
    }

    private CellDimension createCellDimension(
            double edgeLength,
            double width,
            double height,
            double halfEdgeLength,
            double halfWidth,
            double halfHeight,
            double innerRadius,
            double outerRadius,
            double columnWidth,
            double rowHeight) {
        return new CellDimension(
                edgeLength,
                width,
                height,
                halfEdgeLength,
                halfWidth,
                halfHeight,
                innerRadius,
                outerRadius,
                columnWidth,
                rowHeight);
    }

}

