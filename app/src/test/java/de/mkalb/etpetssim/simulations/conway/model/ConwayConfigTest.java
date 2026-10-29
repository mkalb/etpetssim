package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Consumer;

import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.ALIVE_PERCENT_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.ALIVE_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.ALIVE_PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_SHAPE_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_HEIGHT_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_WIDTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.TRANSITION_RULES_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConwayConfigTest {

    private static boolean isValidWith(Consumer<ConfigValues> change) {
        ConfigValues values = new ConfigValues();
        change.accept(values);
        return values.toConfig().isValid();
    }

    @Test
    void testIsValidAcceptsDefaultConstraints() {
        assertTrue(isValidWith(_ -> {}));
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        assertTrue(isValidWith(v -> {
            v.cellShape = CellShape.TRIANGLE;
            v.gridEdgeBehavior = GridEdgeBehavior.BLOCK_XY;
            v.cellDisplayMode = CellDisplayMode.CIRCLE;
        }));
    }

    @Test
    void testIsValidAcceptsBoundaryValues() {
        assertAll(
                () -> assertTrue(isValidWith(v -> v.alivePercent = ALIVE_PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> v.alivePercent = ALIVE_PERCENT_MAX))
        );
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        assertFalse(isValidWith(v -> v.gridEdgeBehavior = GridEdgeBehavior.ABSORB_XY));
    }

    @Test
    void testIsValidRejectsUnsupportedCellDisplayMode() {
        assertFalse(isValidWith(v -> v.cellDisplayMode = CellDisplayMode.EMOJI));
    }

    @Test
    void testIsValidRejectsUnexpectedNeighborhoodMode() {
        assertFalse(isValidWith(v -> v.neighborhoodMode = NeighborhoodMode.EDGES_ONLY));
    }

    @Test
    void testIsValidRejectsAlivePercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.alivePercent = Math.nextDown(ALIVE_PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> v.alivePercent = Math.nextUp(ALIVE_PERCENT_MAX)))
        );
    }

    @Test
    void testIsValidRejectsBirthRuleWithoutNeighbors() {
        assertFalse(isValidWith(v -> v.transitionRules = ConwayTransitionRules.of(
                TRANSITION_RULES_DEFAULT.surviveCounts(),
                Set.of(ConwayTransitionRules.MIN_NEIGHBOR_COUNT))));
    }

    @Test
    void testIsValidAcceptsHexagonRulesWithinShapeNeighborLimit() {
        assertTrue(isValidWith(v -> {
            v.cellShape = CellShape.HEXAGON;
            v.transitionRules = ConwayTransitionRules.of(Set.of(2, 3), Set.of(3, 4));
        }));
    }

    @Test
    void testIsValidRejectsTransitionRulesAboveShapeNeighborLimit() {
        int invalidBirthCount = CellNeighborhoods.maxNeighborCount(NEIGHBORHOOD_MODE_DEFAULT, CellShape.HEXAGON) + 1;

        assertFalse(isValidWith(v -> {
            v.cellShape = CellShape.HEXAGON;
            v.transitionRules = ConwayTransitionRules.of(TRANSITION_RULES_DEFAULT.surviveCounts(),
                    Set.of(invalidBirthCount));
        }));
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        double alivePercent = ALIVE_PERCENT_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;
        ConwayTransitionRules transitionRules = TRANSITION_RULES_DEFAULT;

        ConwayConfig toConfig() {
            return new ConwayConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, alivePercent, neighborhoodMode, transitionRules);
        }

    }

}
