package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayPatternChoice;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConwayStepLogicTest {

    private static final ConwayTransitionRules CONWAYS_LIFE_RULES = ConwayConstraints.TRANSITION_RULES_DEFAULT;
    private static final String GOSPER_GLIDER_GUN_ID = "conway.gosperglidergun";
    private static final int GOSPER_GLIDER_GUN_PERIOD = 30;

    private static ConwayConfig createConfig(CellShape cellShape,
                                             GridEdgeBehavior gridEdgeBehavior,
                                             int gridWidth,
                                             int gridHeight,
                                             ConwayTransitionRules transitionRules) {
        return new ConwayConfig(
                cellShape,
                gridEdgeBehavior,
                gridWidth,
                gridHeight,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                0.0d,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                transitionRules
        );
    }

    private static ConwayConfig createConfig(GridEdgeBehavior gridEdgeBehavior) {
        return createConfig(CellShape.SQUARE, gridEdgeBehavior, GridSize.MIN_SIZE, GridSize.MIN_SIZE,
                CONWAYS_LIFE_RULES);
    }

    private static Stream<Arguments> topologies() {
        return Arrays.stream(CellShape.values())
                .flatMap(cellShape -> ConwayConstraints.GRID_EDGE_BEHAVIOR_VALUES.stream()
                        .map(gridEdgeBehavior -> Arguments.of(cellShape, gridEdgeBehavior)));
    }

    private static WritableGridModel<ConwayEntity> createGridModel(ConwayConfig config,
                                                                   Set<GridCoordinate> aliveCoordinates) {
        var gridModel = new SparseGridModel<>(config.createGridStructure(), ConwayEntity.DEAD);
        aliveCoordinates.forEach(coordinate -> gridModel.setEntity(coordinate, ConwayEntity.ALIVE));
        return gridModel;
    }

    private static ReadableGridModel<ConwayEntity> performStep(ConwayConfig config,
                                                               ReadableGridModel<ConwayEntity> currentGridModel,
                                                               ConwayStatistics statistics) {
        var nextGridModel = new SparseGridModel<>(currentGridModel.structure(), ConwayEntity.DEAD);
        new ConwayStepLogic(currentGridModel.structure(), config)
                .performSynchronousStep(currentGridModel, nextGridModel, 0, statistics);
        return nextGridModel;
    }

    private static Set<GridCoordinate> coordinates(int... xyPairs) {
        Set<GridCoordinate> coordinates = new HashSet<>();
        for (int i = 0; i < xyPairs.length; i += 2) {
            coordinates.add(new GridCoordinate(xyPairs[i], xyPairs[i + 1]));
        }
        return coordinates;
    }

    private static int countAliveNeighbors(GridCoordinate coordinate, ReadableGridModel<ConwayEntity> gridModel) {
        return (int) CellNeighborhoods.neighborEdgeBehaviorResults(coordinate,
                        ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT, gridModel.structure())
                .stream()
                .filter(result -> result.action().isWithinGrid())
                .filter(result -> gridModel.getEntity(result.mapped()).isAlive())
                .count();
    }

    private static void assertCellCounts(int aliveCells, int changedCells, ConwayStatistics statistics) {
        assertAll(
                () -> assertEquals(aliveCells, statistics.aliveCells()),
                () -> assertEquals(statistics.totalCells() - aliveCells, statistics.deadCells()),
                () -> assertEquals(changedCells, statistics.changedCells())
        );
    }

    @Test
    void testPerformSynchronousStep() {
        ConwayConfig config = createConfig(GridEdgeBehavior.BLOCK_XY);
        var horizontalBlinker = coordinates(2, 3, 3, 3, 4, 3);
        var verticalBlinker = coordinates(3, 2, 3, 3, 3, 4);
        var statistics = new ConwayStatistics(config.createGridStructure());

        var nextGridModel = performStep(config, createGridModel(config, horizontalBlinker), statistics);

        // The center survives with 2 neighbors, the ends die with 1, and two cells are born with 3.
        assertAll(
                () -> assertEquals(verticalBlinker, nextGridModel.nonDefaultCoordinates()),
                () -> assertCellCounts(3, 4, statistics)
        );
    }

    @Test
    void testPerformSynchronousStepReturnsOscillatorToStartAfterPeriod() {
        ConwayConfig config = createConfig(GridEdgeBehavior.BLOCK_XY);
        var toad = coordinates(3, 3, 4, 3, 5, 3, 2, 4, 3, 4, 4, 4);
        var statistics = new ConwayStatistics(config.createGridStructure());

        var firstGridModel = performStep(config, createGridModel(config, toad), statistics);
        var secondGridModel = performStep(config, firstGridModel, statistics);

        assertAll(
                () -> assertEquals(toad, secondGridModel.nonDefaultCoordinates()),
                () -> assertCellCounts(6, 8, statistics)
        );
    }

    @Test
    void testPerformSynchronousStepKeepsStillLife() {
        ConwayConfig config = createConfig(GridEdgeBehavior.BLOCK_XY);
        var block = coordinates(3, 3, 4, 3, 3, 4, 4, 4);
        var statistics = new ConwayStatistics(config.createGridStructure());

        var nextGridModel = performStep(config, createGridModel(config, block), statistics);

        assertAll(
                () -> assertEquals(block, nextGridModel.nonDefaultCoordinates()),
                () -> assertCellCounts(4, 0, statistics)
        );
    }

    @Test
    void testPerformSynchronousStepKillsIsolatedCell() {
        ConwayConfig config = createConfig(GridEdgeBehavior.BLOCK_XY);
        var statistics = new ConwayStatistics(config.createGridStructure());

        var nextGridModel = performStep(config, createGridModel(config, coordinates(3, 3)), statistics);

        assertAll(
                () -> assertTrue(nextGridModel.nonDefaultCoordinates().isEmpty()),
                () -> assertCellCounts(0, 1, statistics)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "BLOCK_XY, 2, DEAD",
            "WRAP_XY,  3, ALIVE"
    })
    void testPerformSynchronousStepBearsCellAcrossGridEdgeOnlyWhenWrapping(GridEdgeBehavior gridEdgeBehavior,
                                                                          int expectedAliveCells,
                                                                          ConwayEntity expectedEntity) {
        ConwayConfig config = createConfig(gridEdgeBehavior);
        var verticalBlinkerAtLeftEdge = coordinates(0, 2, 0, 3, 0, 4);
        var oppositeEdgeCoordinate = new GridCoordinate(GridSize.MIN_SIZE - 1, 3);
        var statistics = new ConwayStatistics(config.createGridStructure());

        var nextGridModel = performStep(config, createGridModel(config, verticalBlinkerAtLeftEdge), statistics);

        assertAll(
                () -> assertEquals(expectedEntity, nextGridModel.getEntity(oppositeEdgeCoordinate)),
                () -> assertEquals(expectedAliveCells, statistics.aliveCells())
        );
    }

    @ParameterizedTest
    @MethodSource("topologies")
    void testPerformSynchronousStepAppliesRulesForAllTopologies(CellShape cellShape,
                                                               GridEdgeBehavior gridEdgeBehavior) {
        ConwayConfig config = createConfig(cellShape, gridEdgeBehavior, GridSize.MIN_SIZE, GridSize.MIN_SIZE,
                ConwayTransitionRules.parse("2345/234"));
        var random = new Random(1L);
        Set<GridCoordinate> aliveCoordinates = new HashSet<>();
        for (GridCoordinate coordinate : config.createGridStructure().coordinatesList()) {
            if (random.nextDouble() < 0.4d) {
                aliveCoordinates.add(coordinate);
            }
        }
        var currentGridModel = createGridModel(config, aliveCoordinates);
        var statistics = new ConwayStatistics(currentGridModel.structure());

        var nextGridModel = performStep(config, currentGridModel, statistics);

        Map<GridCoordinate, ConwayEntity> expectedEntities = new HashMap<>();
        Map<GridCoordinate, ConwayEntity> actualEntities = new HashMap<>();
        int changedCells = 0;
        for (GridCoordinate coordinate : currentGridModel.structure().coordinatesList()) {
            boolean alive = currentGridModel.getEntity(coordinate).isAlive();
            int aliveNeighbors = countAliveNeighbors(coordinate, currentGridModel);
            boolean nextAlive = alive
                    ? config.transitionRules().shouldSurvive(aliveNeighbors)
                    : config.transitionRules().shouldBeBorn(aliveNeighbors);
            expectedEntities.put(coordinate, nextAlive ? ConwayEntity.ALIVE : ConwayEntity.DEAD);
            actualEntities.put(coordinate, nextGridModel.getEntity(coordinate));
            if (alive != nextAlive) {
                changedCells++;
            }
        }
        int expectedAliveCells = (int) expectedEntities.values().stream().filter(ConwayEntity::isAlive).count();
        int expectedChangedCells = changedCells;
        assertAll(
                () -> assertEquals(expectedEntities, actualEntities),
                () -> assertCellCounts(expectedAliveCells, expectedChangedCells, statistics)
        );
    }

    @Test
    void testPerformSynchronousStepEmitsGliderFromGosperGliderGun() {
        ConwayConfig config = createConfig(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY, 48, 24, CONWAYS_LIFE_RULES);
        ConwayPatternChoice gunChoice = ConwayPatterns.choices().stream()
                .filter(choice -> choice.choiceId().equals(GOSPER_GLIDER_GUN_ID))
                .findFirst()
                .orElseThrow();
        Set<GridCoordinate> gun = gunChoice.pattern().offsetMap().entrySet().stream()
                .filter(entry -> entry.getValue().isAlive())
                .map(Map.Entry::getKey)
                .map(offset -> new GridCoordinate(offset.dx(), offset.dy()))
                .collect(Collectors.toSet());
        var statistics = new ConwayStatistics(config.createGridStructure());

        ReadableGridModel<ConwayEntity> gridModel = createGridModel(config, gun);
        for (int step = 0; step < GOSPER_GLIDER_GUN_PERIOD; step++) {
            gridModel = performStep(config, gridModel, statistics);
        }

        Set<GridCoordinate> aliveAfterPeriod = gridModel.nonDefaultCoordinates();
        assertAll(
                () -> assertTrue(aliveAfterPeriod.containsAll(gun), "The gun should return to its start"),
                () -> assertEquals(gun.size() + 5, aliveAfterPeriod.size(), "The gun should emit one glider")
        );
    }

}
