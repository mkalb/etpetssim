package de.mkalb.etpetssim.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class GridSizeTest {

    @Test
    void testValidConstruction() {
        GridSize size = new GridSize(32, 64);
        assertEquals(32, size.width());
        assertEquals(64, size.height());
    }

    @Test
    void testMinAndMaxSizeAreValid() {
        assertDoesNotThrow(() -> new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE));
        assertDoesNotThrow(() -> new GridSize(GridSize.MAX_SIZE, GridSize.MAX_SIZE));
    }

    @Test
    void testNegativeSizeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new GridSize(-1, 8));
        assertThrows(IllegalArgumentException.class, () -> new GridSize(8, -1));
        assertThrows(IllegalArgumentException.class, () -> new GridSize(-1, -1));
    }

    @Test
    void testTooSmallSizeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new GridSize(7, 8));
        assertThrows(IllegalArgumentException.class, () -> new GridSize(8, 7));
        assertThrows(IllegalArgumentException.class, () -> new GridSize(7, 7));
    }

    @Test
    void testTooLargeSizeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new GridSize(20_000, 16));
        assertThrows(IllegalArgumentException.class, () -> new GridSize(16, 20_000));
        assertThrows(IllegalArgumentException.class, () -> new GridSize(20_000, 20_000));
    }

    @Test
    void testArea() {
        GridSize size = new GridSize(20, 30);
        assertEquals(20 * 30, size.area());
    }

    @Test
    void testPerimeter() {
        GridSize size = new GridSize(20, 30);
        assertEquals(2 * (20 + 30), size.perimeter());
    }

    @Test
    void testIsSquare() {
        assertTrue(GridSize.square(32).isSquare());
        assertFalse(new GridSize(32, 64).isSquare());
    }

    @Test
    void testIsLandscape() {
        assertTrue(new GridSize(64, 32).isLandscape());
        assertFalse(new GridSize(32, 64).isLandscape());
    }

    @Test
    void testIsPortrait() {
        assertTrue(new GridSize(32, 64).isPortrait());
        assertFalse(new GridSize(64, 32).isPortrait());
    }

    @Test
    void testAspectRatio() {
        assertEquals(1.0d, new GridSize(32, 32).aspectRatio(), 0.00001d);
        assertEquals(1.0d, new GridSize(64, 64).aspectRatio(), 0.00001d);
        assertEquals(2.0d, new GridSize(64, 32).aspectRatio(), 0.00001d);
        assertEquals(0.5d, new GridSize(32, 64).aspectRatio(), 0.00001d);
    }

    @Test
    void testToDisplayString() {
        assertEquals("32 × 64", new GridSize(32, 64).toDisplayString());
        assertEquals("1024 × 2048", new GridSize(1_024, 2_048).toDisplayString());
        // Boundary values
        assertEquals("8 × 8", new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE).toDisplayString());
        assertEquals("16384 × 16384", new GridSize(GridSize.MAX_SIZE, GridSize.MAX_SIZE).toDisplayString());
        // Square and rectangle
        assertEquals("16 × 16", GridSize.square(16).toDisplayString());
        assertEquals("16 × 8", new GridSize(16, 8).toDisplayString());
        assertEquals("8 × 16", new GridSize(8, 16).toDisplayString());
    }

    @Test
    void testSquareFactoryMethod() {
        GridSize square = GridSize.square(64);
        assertEquals(64, square.width());
        assertEquals(64, square.height());
        assertTrue(square.isSquare());
    }

    @Test
    void testIsInvalidSize() {
        assertFalse(GridSize.isInvalidSize(15));
        assertTrue(GridSize.isInvalidSize(20_000));
        assertFalse(GridSize.isInvalidSize(32));
    }

}
