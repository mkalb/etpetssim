package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayConfigTest {

    private static ConwayConfig createConfig(CellShape cellShape,
                                             GridEdgeBehavior gridEdgeBehavior,
                                             CellDisplayMode cellDisplayMode,
                                             double alivePercent,
                                             NeighborhoodMode neighborhoodMode,
                                             ConwayTransitionRules transitionRules) {
        return new ConwayConfig(
                cellShape,
                gridEdgeBehavior,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                cellDisplayMode,
                1L,
                alivePercent,
                neighborhoodMode,
                transitionRules
        );
    }

    private static ConwayConfig createConfig(CellShape cellShape,
                                             double alivePercent,
                                             NeighborhoodMode neighborhoodMode,
                                             ConwayTransitionRules transitionRules) {
        return createConfig(
                cellShape,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                alivePercent,
                neighborhoodMode,
                transitionRules);
    }

    @Test
    void testIsValidWithDefaultConstraints() {
        ConwayConfig config = createConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT);

        assertTrue(config.isValid());
    }

    @Test
    void testIsValidRejectsAlivePercentOutsideRange() {
        ConwayConfig config = createConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                Math.nextUp(ConwayConstraints.ALIVE_PERCENT_MAX),
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        ConwayConfig config = createConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                NeighborhoodMode.EDGES_ONLY,
                ConwayConstraints.TRANSITION_RULES_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        ConwayConfig config = createConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                GridEdgeBehavior.ABSORB_XY,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        ConwayConfig config = createConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                CellDisplayMode.EMOJI,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT);

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsBirthRuleWithoutNeighbors() {
        ConwayConfig config = createConfig(
                ConwayConstraints.CELL_SHAPE_DEFAULT,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayTransitionRules.of(
                        ConwayConstraints.TRANSITION_RULES_DEFAULT.surviveCounts(),
                        Set.of(ConwayTransitionRules.MIN_NEIGHBOR_COUNT)));

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidRejectsTransitionRulesAboveShapeNeighborLimit() {
        int invalidBirthCount = CellNeighborhoods.maxNeighborCount(
                CellShape.HEXAGON,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT) + 1;

        ConwayConfig config = createConfig(
                CellShape.HEXAGON,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayTransitionRules.of(
                        ConwayConstraints.TRANSITION_RULES_DEFAULT.surviveCounts(),
                        Set.of(invalidBirthCount)));

        assertFalse(config.isValid());
    }

    @Test
    void testIsValidAcceptsHexagonRulesWithinShapeNeighborLimit() {
        ConwayConfig config = createConfig(
                CellShape.HEXAGON,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayTransitionRules.of(Set.of(2, 3), Set.of(3, 4)));

        assertTrue(config.isValid());
    }

}
