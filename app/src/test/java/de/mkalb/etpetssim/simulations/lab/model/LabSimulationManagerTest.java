package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.shared.LabDistribution;
import de.mkalb.etpetssim.simulations.lab.shared.LabGridModelType;
import de.mkalb.etpetssim.simulations.lab.shared.LabPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LabSimulationManagerTest {

    private static final int GRID_WIDTH = 16;
    private static final int GRID_HEIGHT = 8;
    private static final int GRID_AREA = GRID_WIDTH * GRID_HEIGHT;
    private static final double HIGHLIGHTED_PERCENT = 0.25d;
    private static final int HIGHLIGHTED_COUNT = 32;

    private static LabConfig createConfig(LabGridModelType gridModelType, LabDistribution distribution,
                                          LabPattern pattern, long seed) {
        return new LabConfig(
                CellShape.SQUARE,
                GridEdgeBehavior.BLOCK_XY,
                GRID_WIDTH,
                GRID_HEIGHT,
                gridModelType,
                LabConstraints.CELL_EDGE_LENGTH_DEFAULT,
                LabConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LabConstraints.COLOR_MODE_DEFAULT,
                LabConstraints.BASE_LAYER_DEFAULT,
                seed,
                distribution,
                HIGHLIGHTED_PERCENT,
                pattern,
                LabConstraints.NEIGHBORHOOD_MODE_DEFAULT
        );
    }

    private static LabSimulationManager createManager(LabGridModelType gridModelType,
                                                      LabDistribution distribution) {
        return new LabSimulationManager(createConfig(gridModelType, distribution, LabConstraints.PATTERN_DEFAULT,
                1L));
    }

    private static LabSimulationManager createManager(LabDistribution distribution) {
        return createManager(LabConstraints.GRID_MODEL_TYPE_DEFAULT, distribution);
    }

    private static Set<GridCell<LabEntity>> highlightedCells(LabSimulationManager manager) {
        return Set.copyOf(manager.currentGridModel().nonDefaultCells());
    }

    private static void assertHighlightedCount(int expected, LabSimulationManager manager) {
        assertAll(
                () -> assertEquals(expected, highlightedCells(manager).size()),
                () -> assertEquals(expected, manager.statistics().highlightedCells()),
                () -> assertEquals(GRID_AREA - expected, manager.statistics().normalCells())
        );
    }

    @ParameterizedTest
    @EnumSource(value = LabDistribution.class, names = {"RANDOM_PERCENT", "RANDOM_CELLS"})
    void testConstructorPlacesExactShareForExactRandomDistributions(LabDistribution distribution) {
        assertHighlightedCount(HIGHLIGHTED_COUNT, createManager(distribution));
    }

    @ParameterizedTest
    @EnumSource(value = LabDistribution.class, names = {"RANDOM_PERCENT", "RANDOM_PROBABILITY", "RANDOM_CELLS"})
    void testConstructorIsReproducibleForRandomDistributions(LabDistribution distribution) {
        LabSimulationManager first = createManager(distribution);
        LabSimulationManager second = createManager(distribution);

        assertEquals(highlightedCells(first), highlightedCells(second));
    }

    @Test
    void testConstructorPlacesCellsWithProbabilityForRandomProbability() {
        LabSimulationManager manager = createManager(LabDistribution.RANDOM_PROBABILITY);

        int count = highlightedCells(manager).size();
        assertAll(
                () -> assertTrue((count > 0) && (count < GRID_AREA)),
                () -> assertHighlightedCount(count, manager)
        );
    }

    @Test
    void testConstructorPlacesGridEdgeForBorder() {
        LabSimulationManager manager = createManager(LabDistribution.BORDER);

        assertAll(
                () -> assertHighlightedCount((2 * (GRID_WIDTH + GRID_HEIGHT)) - 4, manager),
                () -> assertTrue(highlightedCells(manager).stream()
                        .map(GridCell::coordinate)
                        .allMatch(c -> (c.x() == 0) || (c.y() == 0)
                                || (c.x() == (GRID_WIDTH - 1)) || (c.y() == (GRID_HEIGHT - 1))))
        );
    }

    @ParameterizedTest
    @EnumSource(LabPattern.class)
    void testConstructorPlacesPatternForPattern(LabPattern pattern) {
        LabSimulationManager manager = new LabSimulationManager(
                createConfig(LabConstraints.GRID_MODEL_TYPE_DEFAULT, LabDistribution.PATTERN, pattern, 1L));

        assertHighlightedCount(LabPatterns.createPattern(pattern, new GridSize(GRID_WIDTH, GRID_HEIGHT)).size(),
                manager);
    }

    @Test
    void testConstructorCreatesGridModelOfSelectedType() {
        assertAll(
                () -> assertInstanceOf(SparseGridModel.class,
                        createManager(LabGridModelType.SPARSE, LabDistribution.BORDER).currentGridModel()),
                () -> assertInstanceOf(ArrayGridModel.class,
                        createManager(LabGridModelType.ARRAY, LabDistribution.BORDER).currentGridModel())
        );
    }

    @ParameterizedTest
    @EnumSource(LabDistribution.class)
    void testConstructorPlacesSameCellsForEachGridModelType(LabDistribution distribution) {
        LabSimulationManager sparse = createManager(LabGridModelType.SPARSE, distribution);
        LabSimulationManager array = createManager(LabGridModelType.ARRAY, distribution);

        assertAll(
                () -> assertEquals(highlightedCells(sparse), highlightedCells(array)),
                () -> assertEquals(sparse.statistics().highlightedCells(), array.statistics().highlightedCells())
        );
    }

}
