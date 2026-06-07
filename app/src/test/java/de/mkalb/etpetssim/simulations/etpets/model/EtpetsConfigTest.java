package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class EtpetsConfigTest {

    private static EtpetsConfig createConfig(CellShape cellShape,
                                             GridEdgeBehavior gridEdgeBehavior,
                                             CellDisplayMode cellDisplayMode,
                                             double rockPercent,
                                             double waterPercent,
                                             double plantPercent,
                                             double insectPercent,
                                             int petCount,
                                             NeighborhoodMode neighborhoodMode) {
        return new EtpetsConfig(
                cellShape,
                gridEdgeBehavior,
                EtpetsConstraints.GRID_WIDTH_DEFAULT,
                EtpetsConstraints.GRID_HEIGHT_DEFAULT,
                EtpetsConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                1L,
                rockPercent,
                waterPercent,
                plantPercent,
                insectPercent,
                petCount,
                neighborhoodMode
        );
    }

    private static EtpetsConfig createConfig(CellShape cellShape,
                                             GridEdgeBehavior gridEdgeBehavior,
                                             double rockPercent,
                                             double waterPercent,
                                             double plantPercent,
                                             double insectPercent,
                                             int petCount,
                                             NeighborhoodMode neighborhoodMode) {
        return createConfig(
                cellShape,
                gridEdgeBehavior,
                EtpetsConstraints.CELL_DISPLAY_MODE_DEFAULT,
                rockPercent,
                waterPercent,
                plantPercent,
                insectPercent,
                petCount,
                neighborhoodMode);
    }

    @Test
    void testIsValidWithDefaultConstraints() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedCellShape() {
        EtpetsConfig config = createConfig(
                CellShape.SQUARE,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedGridEdgeBehavior() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                GridEdgeBehavior.WRAP_XY,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                NeighborhoodMode.EDGES_AND_VERTICES);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedCellDisplayMode() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.CIRCLE,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsRockPercentOutsideRange() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.PERCENT_MAX + 0.1d,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsPetCountOutsideRange() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_MAX + 1,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsTotalPercentAboveLimit() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                0.20d,
                0.20d,
                0.10d,
                0.01d,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsTotalPercentAtLimit() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                0.20d,
                0.20d,
                0.05d,
                0.05d,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidAcceptsTotalPercentBelowLimit() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

}
