package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final class ForestStepLogicTest {

    private static final GridCoordinate BURNING_COORDINATE = new GridCoordinate(3, 3);
    private static final GridCoordinate EDGE_NEIGHBOR_COORDINATE = new GridCoordinate(4, 3);
    private static final GridCoordinate VERTEX_NEIGHBOR_COORDINATE = new GridCoordinate(4, 4);
    private static final GridCoordinate DISTANT_COORDINATE = new GridCoordinate(6, 6);

    private static ForestConfig createConfig(CellShape cellShape,
                                             GridEdgeBehavior gridEdgeBehavior,
                                             NeighborhoodMode neighborhoodMode,
                                             double treeGrowthProbability,
                                             double lightningIgnitionProbability) {
        return new ForestConfig(
                cellShape,
                gridEdgeBehavior,
                GridSize.MIN_SIZE,
                GridSize.MIN_SIZE,
                ForestConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ForestConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                0.0d,
                neighborhoodMode,
                treeGrowthProbability,
                lightningIgnitionProbability
        );
    }

    private static ForestConfig createConfig(double treeGrowthProbability, double lightningIgnitionProbability) {
        return createConfig(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, NeighborhoodMode.EDGES_ONLY,
                treeGrowthProbability, lightningIgnitionProbability);
    }

    private static Stream<Arguments> topologies() {
        return Arrays.stream(CellShape.values())
                .flatMap(cellShape -> Arrays.stream(NeighborhoodMode.values())
                        .flatMap(neighborhoodMode -> ForestConstraints.GRID_EDGE_BEHAVIOR_VALUES.stream()
                                .map(gridEdgeBehavior -> Arguments.of(cellShape, neighborhoodMode,
                                        gridEdgeBehavior))));
    }

    private static boolean hasBurningNeighbor(GridCoordinate coordinate,
                                              ReadableGridModel<ForestEntity> gridModel,
                                              NeighborhoodMode neighborhoodMode) {
        return CellNeighborhoods.neighborEdgeBehaviorResults(coordinate, neighborhoodMode, gridModel.structure())
                .stream()
                .filter(result -> result.action().isWithinGrid())
                .map(EdgeBehaviorResult::mapped)
                .anyMatch(neighbor -> gridModel.getEntity(neighbor).isBurning());
    }

    private static WritableGridModel<ForestEntity> createGridModel(ForestConfig config) {
        return new ArrayGridModel<>(config.createGridStructure(), ForestEntity.EMPTY);
    }

    private static ReadableGridModel<ForestEntity> performStep(ForestConfig config,
                                                               ReadableGridModel<ForestEntity> currentGridModel,
                                                               ForestStatistics statistics) {
        var nextGridModel = createGridModel(config);
        new ForestStepLogic(currentGridModel.structure(), config, new Random(1L))
                .performSynchronousStep(currentGridModel, nextGridModel, 0, statistics);
        return nextGridModel;
    }

    private static void assertCellCounts(int treeCells, int burningCells, ForestStatistics statistics) {
        assertAll(
                () -> assertEquals(treeCells, statistics.treeCells()),
                () -> assertEquals(burningCells, statistics.burningCells()),
                () -> assertEquals(statistics.totalCells() - treeCells - burningCells, statistics.emptyCells())
        );
    }

    @Test
    void testPerformSynchronousStep() {
        ForestConfig config = createConfig(0.0d, 0.0d);
        var currentGridModel = createGridModel(config);
        currentGridModel.setEntity(BURNING_COORDINATE, ForestEntity.BURNING);
        currentGridModel.setEntity(EDGE_NEIGHBOR_COORDINATE, ForestEntity.TREE);
        currentGridModel.setEntity(DISTANT_COORDINATE, ForestEntity.TREE);
        var statistics = new ForestStatistics(currentGridModel.structure());

        var nextGridModel = performStep(config, currentGridModel, statistics);

        assertAll(
                () -> assertSame(ForestEntity.EMPTY, nextGridModel.getEntity(BURNING_COORDINATE)),
                () -> assertSame(ForestEntity.BURNING, nextGridModel.getEntity(EDGE_NEIGHBOR_COORDINATE)),
                () -> assertSame(ForestEntity.TREE, nextGridModel.getEntity(DISTANT_COORDINATE)),
                () -> assertCellCounts(1, 1, statistics)
        );
    }

    @Test
    void testPerformSynchronousStepGrowsTreesWithGrowthProbabilityOne() {
        ForestConfig config = createConfig(1.0d, 0.0d);
        var currentGridModel = createGridModel(config);
        currentGridModel.setEntity(BURNING_COORDINATE, ForestEntity.BURNING);
        var statistics = new ForestStatistics(currentGridModel.structure());

        var nextGridModel = performStep(config, currentGridModel, statistics);

        assertAll(
                () -> assertSame(ForestEntity.EMPTY, nextGridModel.getEntity(BURNING_COORDINATE)),
                () -> assertEquals(statistics.totalCells() - 1, nextGridModel.countEntities(ForestEntity::isTree)),
                () -> assertCellCounts(statistics.totalCells() - 1, 0, statistics)
        );
    }

    @Test
    void testPerformSynchronousStepIgnitesTreesWithLightningProbabilityOne() {
        ForestConfig config = createConfig(0.0d, 1.0d);
        var currentGridModel = createGridModel(config);
        currentGridModel.setEntity(EDGE_NEIGHBOR_COORDINATE, ForestEntity.TREE);
        currentGridModel.setEntity(DISTANT_COORDINATE, ForestEntity.TREE);
        var statistics = new ForestStatistics(currentGridModel.structure());

        var nextGridModel = performStep(config, currentGridModel, statistics);

        assertAll(
                () -> assertSame(ForestEntity.BURNING, nextGridModel.getEntity(EDGE_NEIGHBOR_COORDINATE)),
                () -> assertSame(ForestEntity.BURNING, nextGridModel.getEntity(DISTANT_COORDINATE)),
                () -> assertCellCounts(0, 2, statistics)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "EDGES_ONLY,         TREE",
            "EDGES_AND_VERTICES, BURNING"
    })
    void testPerformSynchronousStepSpreadsFireDiagonallyOnlyWithVertices(NeighborhoodMode neighborhoodMode,
                                                                         ForestEntity expectedEntity) {
        ForestConfig config = createConfig(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, neighborhoodMode, 0.0d, 0.0d);
        var currentGridModel = createGridModel(config);
        currentGridModel.setEntity(BURNING_COORDINATE, ForestEntity.BURNING);
        currentGridModel.setEntity(VERTEX_NEIGHBOR_COORDINATE, ForestEntity.TREE);
        var statistics = new ForestStatistics(currentGridModel.structure());

        var nextGridModel = performStep(config, currentGridModel, statistics);

        assertSame(expectedEntity, nextGridModel.getEntity(VERTEX_NEIGHBOR_COORDINATE));
    }

    @ParameterizedTest
    @CsvSource({
            "BLOCK_XY, TREE",
            "WRAP_XY,  BURNING"
    })
    void testPerformSynchronousStepSpreadsFireAcrossGridEdgeOnlyWhenWrapping(GridEdgeBehavior gridEdgeBehavior,
                                                                             ForestEntity expectedEntity) {
        ForestConfig config = createConfig(CellShape.SQUARE, gridEdgeBehavior, NeighborhoodMode.EDGES_ONLY, 0.0d,
                0.0d);
        var currentGridModel = createGridModel(config);
        GridCoordinate oppositeEdgeCoordinate = new GridCoordinate(GridSize.MIN_SIZE - 1, 0);
        currentGridModel.setEntity(new GridCoordinate(0, 0), ForestEntity.BURNING);
        currentGridModel.setEntity(oppositeEdgeCoordinate, ForestEntity.TREE);
        var statistics = new ForestStatistics(currentGridModel.structure());

        var nextGridModel = performStep(config, currentGridModel, statistics);

        assertSame(expectedEntity, nextGridModel.getEntity(oppositeEdgeCoordinate));
    }

    @ParameterizedTest
    @MethodSource("topologies")
    void testPerformSynchronousStepIgnitesTreesWithBurningNeighborForAllTopologies(CellShape cellShape,
                                                                                   NeighborhoodMode neighborhoodMode,
                                                                                   GridEdgeBehavior gridEdgeBehavior) {
        ForestConfig config = createConfig(cellShape, gridEdgeBehavior, neighborhoodMode, 0.0d, 0.0d);
        var currentGridModel = createGridModel(config);
        var random = new Random(1L);
        for (GridCoordinate coordinate : currentGridModel.structure().coordinatesList()) {
            double value = random.nextDouble();
            if (value < 0.1d) {
                currentGridModel.setEntity(coordinate, ForestEntity.BURNING);
            } else if (value < 0.7d) {
                currentGridModel.setEntity(coordinate, ForestEntity.TREE);
            }
        }
        var statistics = new ForestStatistics(currentGridModel.structure());

        var nextGridModel = performStep(config, currentGridModel, statistics);

        Map<GridCoordinate, ForestEntity> expectedEntities = new HashMap<>();
        Map<GridCoordinate, ForestEntity> actualEntities = new HashMap<>();
        for (GridCoordinate coordinate : currentGridModel.filteredCoordinates(ForestEntity::isTree)) {
            expectedEntities.put(coordinate, hasBurningNeighbor(coordinate, currentGridModel, neighborhoodMode)
                    ? ForestEntity.BURNING : ForestEntity.TREE);
            actualEntities.put(coordinate, nextGridModel.getEntity(coordinate));
        }
        assertEquals(expectedEntities, actualEntities);
    }

}
