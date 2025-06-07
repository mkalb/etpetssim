package de.mkalb.etpetssim.wator;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WaTorCoordinateTest {

    @Test
    void territoryIndex() {
        assertEquals(0, new WaTorCoordinate(0, 0).territoryIndex(3));
        assertEquals(1, new WaTorCoordinate(1, 0).territoryIndex(3));
        assertEquals(2, new WaTorCoordinate(2, 0).territoryIndex(3));
        assertEquals(3, new WaTorCoordinate(0, 1).territoryIndex(3));
        assertEquals(4, new WaTorCoordinate(1, 1).territoryIndex(3));
        assertEquals(5, new WaTorCoordinate(2, 1).territoryIndex(3));
        assertEquals(6, new WaTorCoordinate(0, 2).territoryIndex(3));
        assertEquals(7, new WaTorCoordinate(1, 2).territoryIndex(3));
        assertEquals(8, new WaTorCoordinate(2, 2).territoryIndex(3));
    }

    @Test
    void top() {
        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(0, 0).top(5, 4));
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(0, 1).top(5, 4));
        assertEquals(new WaTorCoordinate(0, 1), new WaTorCoordinate(0, 2).top(5, 4));
        assertEquals(new WaTorCoordinate(0, 2), new WaTorCoordinate(0, 3).top(5, 4));

        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(4, 0).top(5, 4));
        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(4, 1).top(5, 4));
        assertEquals(new WaTorCoordinate(4, 1), new WaTorCoordinate(4, 2).top(5, 4));
        assertEquals(new WaTorCoordinate(4, 2), new WaTorCoordinate(4, 3).top(5, 4));
    }

    @Test
    void bottom() {
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(0, 3).bottom(5, 4));
        assertEquals(new WaTorCoordinate(0, 1), new WaTorCoordinate(0, 0).bottom(5, 4));
        assertEquals(new WaTorCoordinate(0, 2), new WaTorCoordinate(0, 1).bottom(5, 4));
        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(0, 2).bottom(5, 4));

        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(4, 3).bottom(5, 4));
        assertEquals(new WaTorCoordinate(4, 1), new WaTorCoordinate(4, 0).bottom(5, 4));
        assertEquals(new WaTorCoordinate(4, 2), new WaTorCoordinate(4, 1).bottom(5, 4));
        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(4, 2).bottom(5, 4));
    }

    @Test
    void left() {
        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(0, 0).left(5, 4));
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(1, 0).left(5, 4));
        assertEquals(new WaTorCoordinate(1, 0), new WaTorCoordinate(2, 0).left(5, 4));
        assertEquals(new WaTorCoordinate(2, 0), new WaTorCoordinate(3, 0).left(5, 4));
        assertEquals(new WaTorCoordinate(3, 0), new WaTorCoordinate(4, 0).left(5, 4));

        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(0, 3).left(5, 4));
        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(1, 3).left(5, 4));
        assertEquals(new WaTorCoordinate(1, 3), new WaTorCoordinate(2, 3).left(5, 4));
        assertEquals(new WaTorCoordinate(2, 3), new WaTorCoordinate(3, 3).left(5, 4));
        assertEquals(new WaTorCoordinate(3, 3), new WaTorCoordinate(4, 3).left(5, 4));
    }

    @Test
    void right() {
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(4, 0).right(5, 4));
        assertEquals(new WaTorCoordinate(1, 0), new WaTorCoordinate(0, 0).right(5, 4));
        assertEquals(new WaTorCoordinate(2, 0), new WaTorCoordinate(1, 0).right(5, 4));
        assertEquals(new WaTorCoordinate(3, 0), new WaTorCoordinate(2, 0).right(5, 4));
        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(3, 0).right(5, 4));

        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(4, 3).right(5, 4));
        assertEquals(new WaTorCoordinate(1, 3), new WaTorCoordinate(0, 3).right(5, 4));
        assertEquals(new WaTorCoordinate(2, 3), new WaTorCoordinate(1, 3).right(5, 4));
        assertEquals(new WaTorCoordinate(3, 3), new WaTorCoordinate(2, 3).right(5, 4));
        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(3, 3).right(5, 4));
    }

    @Test
    void neighbors() {
        int xSize = 5;
        int ySize = 4;
        int x = 1;
        int y = 2;
        WaTorCoordinate coordinate = new WaTorCoordinate(x, y);
        List<WaTorCoordinate> neighbors = coordinate.neighbors(xSize, ySize);
        assertEquals(4, neighbors.size());
        assertTrue(neighbors.contains(new WaTorCoordinate(2, 2)));
        assertTrue(neighbors.contains(new WaTorCoordinate(1, 3)));
        assertTrue(neighbors.contains(new WaTorCoordinate(0, 2)));
        assertTrue(neighbors.contains(new WaTorCoordinate(1, 1)));
    }

}