package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EtpetsConfigTest {

    private static EtpetsConfig createConfig(CellShape cellShape,
                                             GridEdgeBehavior gridEdgeBehavior,
                                             CellDisplayMode cellDisplayMode,
                                             int rockPercent,
                                             int waterPercent,
                                             int plantPercent,
                                             int insectPercent,
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
                                             int rockPercent,
                                             int waterPercent,
                                             int plantPercent,
                                             int insectPercent,
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
                EtpetsConstraints.PERCENT_MAX + 1,
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
    void testIsValidRejectsObstaclePercentAboveLimit() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.OBSTACLE_PERCENT_MAX,
                EtpetsConstraints.PERCENT_STEP,
                EtpetsConstraints.PLANT_PERCENT_DEFAULT,
                EtpetsConstraints.INSECT_PERCENT_DEFAULT,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsResourcePercentAboveLimit() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.ROCK_PERCENT_DEFAULT,
                EtpetsConstraints.WATER_PERCENT_DEFAULT,
                EtpetsConstraints.PERCENT_MAX,
                EtpetsConstraints.PERCENT_STEP,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsCombinedBoundaryValues() {
        EtpetsConfig config = createConfig(
                EtpetsConstraints.CELL_SHAPE_DEFAULT,
                EtpetsConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                EtpetsConstraints.OBSTACLE_PERCENT_MAX,
                EtpetsConstraints.PERCENT_MIN,
                EtpetsConstraints.PERCENT_MAX,
                EtpetsConstraints.PERCENT_MIN,
                EtpetsConstraints.PET_COUNT_DEFAULT,
                EtpetsConstraints.NEIGHBORHOOD_MODE_DEFAULT);

        assertTrue(config.isValid());
    }

}
