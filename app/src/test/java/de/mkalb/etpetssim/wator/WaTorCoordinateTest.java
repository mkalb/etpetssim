package de.mkalb.etpetssim.wator;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WaTorCoordinateTest {

    @Test
    void gridIndex() {
        assertEquals(0, new WaTorCoordinate(0, 0).gridIndex(3));
        assertEquals(1, new WaTorCoordinate(1, 0).gridIndex(3));
        assertEquals(2, new WaTorCoordinate(2, 0).gridIndex(3));
        assertEquals(3, new WaTorCoordinate(0, 1).gridIndex(3));
        assertEquals(4, new WaTorCoordinate(1, 1).gridIndex(3));
        assertEquals(5, new WaTorCoordinate(2, 1).gridIndex(3));
        assertEquals(6, new WaTorCoordinate(0, 2).gridIndex(3));
        assertEquals(7, new WaTorCoordinate(1, 2).gridIndex(3));
        assertEquals(8, new WaTorCoordinate(2, 2).gridIndex(3));
    }

    @Test
    void topCoordinate() {
        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(0, 0).topCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(0, 1).topCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 1), new WaTorCoordinate(0, 2).topCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 2), new WaTorCoordinate(0, 3).topCoordinate(5, 4));

        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(4, 0).topCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(4, 1).topCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 1), new WaTorCoordinate(4, 2).topCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 2), new WaTorCoordinate(4, 3).topCoordinate(5, 4));
    }

    @Test
    void bottomCoordinate() {
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(0, 3).bottomCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 1), new WaTorCoordinate(0, 0).bottomCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 2), new WaTorCoordinate(0, 1).bottomCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(0, 2).bottomCoordinate(5, 4));

        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(4, 3).bottomCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 1), new WaTorCoordinate(4, 0).bottomCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 2), new WaTorCoordinate(4, 1).bottomCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(4, 2).bottomCoordinate(5, 4));
    }

    @Test
    void leftCoordinate() {
        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(0, 0).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(1, 0).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(1, 0), new WaTorCoordinate(2, 0).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(2, 0), new WaTorCoordinate(3, 0).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(3, 0), new WaTorCoordinate(4, 0).leftCoordinate(5, 4));

        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(0, 3).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(1, 3).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(1, 3), new WaTorCoordinate(2, 3).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(2, 3), new WaTorCoordinate(3, 3).leftCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(3, 3), new WaTorCoordinate(4, 3).leftCoordinate(5, 4));
    }

    @Test
    void rightCoordinate() {
        assertEquals(new WaTorCoordinate(0, 0), new WaTorCoordinate(4, 0).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(1, 0), new WaTorCoordinate(0, 0).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(2, 0), new WaTorCoordinate(1, 0).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(3, 0), new WaTorCoordinate(2, 0).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 0), new WaTorCoordinate(3, 0).rightCoordinate(5, 4));

        assertEquals(new WaTorCoordinate(0, 3), new WaTorCoordinate(4, 3).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(1, 3), new WaTorCoordinate(0, 3).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(2, 3), new WaTorCoordinate(1, 3).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(3, 3), new WaTorCoordinate(2, 3).rightCoordinate(5, 4));
        assertEquals(new WaTorCoordinate(4, 3), new WaTorCoordinate(3, 3).rightCoordinate(5, 4));
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