package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class GridSizeTest {

    @Test
    void testRecord() {
        GridSize size = new GridSize(32, 64);
        GridSize minSize = new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE);
        GridSize maxSize = new GridSize(GridSize.MAX_SIZE, GridSize.MAX_SIZE);

        assertAll(
                () -> assertEquals(32, size.width()),
                () -> assertEquals(64, size.height()),
                () -> assertEquals(GridSize.MIN_SIZE, minSize.width()),
                () -> assertEquals(GridSize.MAX_SIZE, maxSize.height())
        );
    }

    @Test
    void testConstructorRejectsNegativeSize() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(-1, 8)),
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(8, -1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(-1, -1))
        );
    }

    @Test
    void testConstructorRejectsTooSmallSize() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(7, 8)),
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(8, 7)),
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(7, 7))
        );
    }

    @Test
    void testConstructorRejectsTooLargeSize() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(16_385, 16)),
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(16, 16_385)),
                () -> assertThrows(IllegalArgumentException.class, () -> new GridSize(20_000, 20_000))
        );
    }

    @Test
    void testIsInvalidSize() {
        assertAll(
                () -> assertFalse(GridSize.isInvalidSize(GridSize.MIN_SIZE)),
                () -> assertFalse(GridSize.isInvalidSize(15)),
                () -> assertFalse(GridSize.isInvalidSize(1_000)),
                () -> assertFalse(GridSize.isInvalidSize(GridSize.MAX_SIZE)),
                () -> assertTrue(GridSize.isInvalidSize(-1)),
                () -> assertTrue(GridSize.isInvalidSize(0)),
                () -> assertTrue(GridSize.isInvalidSize(GridSize.MIN_SIZE - 1)),
                () -> assertTrue(GridSize.isInvalidSize(GridSize.MAX_SIZE + 1))
        );
    }

    @Test
    void testSquare() {
        GridSize square = GridSize.square(64);
        assertAll(
                () -> assertEquals(64, square.width()),
                () -> assertEquals(64, square.height()),
                () -> assertThrows(IllegalArgumentException.class, () -> GridSize.square(GridSize.MIN_SIZE - 1))
        );
    }

    @Test
    void testPerimeter() {
        assertEquals(2 * (20 + 30), new GridSize(20, 30).perimeter());
    }

    @Test
    void testArea() {
        assertEquals(20 * 30, new GridSize(20, 30).area());
    }

    @Test
    void testIsSquare() {
        assertAll(
                () -> assertTrue(GridSize.square(32).isSquare()),
                () -> assertFalse(new GridSize(32, 64).isSquare())
        );
    }

    @Test
    void testIsLandscape() {
        assertAll(
                () -> assertTrue(new GridSize(64, 32).isLandscape()),
                () -> assertFalse(new GridSize(32, 64).isLandscape()),
                () -> assertFalse(GridSize.square(32).isLandscape())
        );
    }

    @Test
    void testIsPortrait() {
        assertAll(
                () -> assertTrue(new GridSize(32, 64).isPortrait()),
                () -> assertFalse(new GridSize(64, 32).isPortrait()),
                () -> assertFalse(GridSize.square(32).isPortrait())
        );
    }

    @SuppressWarnings("InsertLiteralUnderscores")
    @Test
    void testAspectRatio() {
        assertAll(
                () -> assertEquals(1.0d, new GridSize(32, 32).aspectRatio(), 0.00001d),
                () -> assertEquals(2.0d, new GridSize(64, 32).aspectRatio(), 0.00001d),
                () -> assertEquals(0.5d, new GridSize(32, 64).aspectRatio(), 0.00001d)
        );
    }

    @Test
    void testToDisplayString() {
        assertAll(
                () -> assertEquals("32 × 64", new GridSize(32, 64).toDisplayString()),
                () -> assertEquals("1024 × 2048", new GridSize(1_024, 2_048).toDisplayString()),
                () -> assertEquals("8 × 8", new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE).toDisplayString()),
                () -> assertEquals("16384 × 16384",
                        new GridSize(GridSize.MAX_SIZE, GridSize.MAX_SIZE).toDisplayString())
        );
    }

}
