package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EtpetsDeterminismTest {

    @Test
    void testCompareCoordinates() {
        assertAll(
                () -> assertTrue(EtpetsDeterminism.compareCoordinates(new GridCoordinate(1, 9),
                        new GridCoordinate(2, 0)) < 0),
                () -> assertTrue(EtpetsDeterminism.compareCoordinates(new GridCoordinate(2, 1),
                        new GridCoordinate(2, 0)) > 0),
                () -> assertEquals(0, EtpetsDeterminism.compareCoordinates(new GridCoordinate(2, 1),
                        new GridCoordinate(2, 1)))
        );
    }

}
