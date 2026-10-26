package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class GridInitializersTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(64, 64));

    private static SparseGridModel<TestEntity> createModel() {
        return new SparseGridModel<>(STRUCTURE, TestEntity.EMPTY);
    }

    private static SparseGridModel<TestEntity> createSmallModel() {
        return new SparseGridModel<>(new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(8, 8)), TestEntity.EMPTY);
    }

    private static long count(SparseGridModel<TestEntity> model, TestEntity entity) {
        return entities(model).stream().filter(entity::equals).count();
    }

    private static List<TestEntity> entities(SparseGridModel<TestEntity> model) {
        return model.structure().coordinatesList().stream().map(model::getEntity).toList();
    }

    private static long countFilledAfterFillRandomPercent(double percent) {
        SparseGridModel<TestEntity> model = createModel();
        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, percent, TestEntity.EMPTY, new Random(42L), () -> {})
                        .initialize(model);
        return count(model, TestEntity.FILLED);
    }

    @Test
    void testIdentity() {
        SparseGridModel<TestEntity> model = createModel();
        model.setEntity(new GridCoordinate(1, 1), TestEntity.FILLED);

        GridInitializers.<TestEntity>identity().initialize(model);

        assertAll(
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(1, 1))),
                () -> assertEquals(1, count(model, TestEntity.FILLED))
        );
    }

    @Test
    void testFromListWithCancellation() {
        SparseGridModel<TestEntity> model = createModel();
        AtomicInteger checks = new AtomicInteger();
        List<GridCell<TestEntity>> cells = List.of(
                new GridCell<>(new GridCoordinate(0, 0), TestEntity.FILLED),
                new GridCell<>(new GridCoordinate(3, 2), TestEntity.OTHER));

        GridInitializers.fromList(cells, checks::incrementAndGet).initialize(model);

        assertAll(
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(0, 0))),
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(3, 2))),
                () -> assertEquals(2, entities(model).stream().filter(e -> e != TestEntity.EMPTY).count()),
                () -> assertEquals(1, checks.get())
        );
    }

    @Test
    void testPlacePatternOffsetsEntriesByAnchor() {
        SparseGridModel<TestEntity> model = createModel();
        GridPattern<TestEntity> pattern = GridPatterns.of(TestEntity.FILLED,
                List.of(new GridOffset(0, 0), new GridOffset(1, 2)));

        GridInitializers.placePattern(pattern, new GridCoordinate(5, 7)).initialize(model);

        assertAll(
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(5, 7))),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(6, 9))),
                () -> assertEquals(2, entities(model).stream().filter(TestEntity.FILLED::equals).count())
        );
    }

    @Test
    void testPlacePatternSkipsEntriesOutsideGrid() {
        SparseGridModel<TestEntity> model = createModel();
        GridPattern<TestEntity> pattern = GridPatterns.of(TestEntity.FILLED,
                List.of(new GridOffset(0, 0), new GridOffset(-1, -1), new GridOffset(1, 0), new GridOffset(0, 1)));

        assertDoesNotThrow(() -> GridInitializers.placePattern(pattern, new GridCoordinate(63, 63)).initialize(model));

        assertAll(
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(63, 63))),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(62, 62))),
                () -> assertEquals(2, entities(model).stream().filter(TestEntity.FILLED::equals).count())
        );
    }

    @Test
    void testPlacePatternOverwritesExistingEntities() {
        SparseGridModel<TestEntity> model = createModel();
        GridCoordinate target = new GridCoordinate(10, 10);
        model.setEntity(target, TestEntity.OTHER);

        GridInitializers.placePattern(GridPatterns.singleton(TestEntity.FILLED, new GridOffset(0, 0)), target)
                        .initialize(model);

        assertEquals(TestEntity.FILLED, model.getEntity(target));
    }

    @Test
    void testPlacePatternWithEmptyPatternLeavesModelUnchanged() {
        SparseGridModel<TestEntity> model = createModel();

        GridInitializers.<TestEntity>placePattern(GridPatterns.empty(), new GridCoordinate(5, 5)).initialize(model);

        assertTrue(entities(model).stream().allMatch(TestEntity.EMPTY::equals));
    }

    @Test
    void testPlaceAllAtRandomPositionsWithCancellation() {
        SparseGridModel<TestEntity> model = createSmallModel();
        model.setEntity(new GridCoordinate(2, 2), TestEntity.OTHER);
        List<TestEntity> entities = Collections.nCopies(63, TestEntity.FILLED);

        GridInitializers.placeAllAtRandomPositions(entities, TestEntity.EMPTY::equals, new Random(42L), () -> {})
                        .initialize(model);

        assertAll(
                () -> assertEquals(63, count(model, TestEntity.FILLED)),
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(2, 2)))
        );
    }

    @Test
    void testPlaceAllAtRandomPositionsWithCancellationRejectsTooFewCells() {
        SparseGridModel<TestEntity> model = createSmallModel();
        List<TestEntity> entities = Collections.nCopies(65, TestEntity.FILLED);

        assertThrows(IllegalStateException.class, () -> GridInitializers.placeAllAtRandomPositions(
                entities, TestEntity.EMPTY::equals, new Random(42L), () -> {}).initialize(model));
        assertEquals(64, count(model, TestEntity.EMPTY));
    }

    @Test
    void testPlaceRandomCount() {
        SparseGridModel<TestEntity> model = createModel();
        model.setEntity(new GridCoordinate(0, 0), TestEntity.OTHER);

        GridInitializers.placeRandomCount(10, () -> TestEntity.FILLED, TestEntity.EMPTY::equals, new Random(42L))
                        .initialize(model);

        assertAll(
                () -> assertEquals(10, count(model, TestEntity.FILLED)),
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(0, 0)))
        );
    }

    @Test
    void testPlaceRandomCountRejectsGridWithoutReplaceableCells() {
        SparseGridModel<TestEntity> model = createSmallModel();

        assertThrows(IllegalStateException.class, () -> GridInitializers.placeRandomCount(
                1, () -> TestEntity.FILLED, TestEntity.OTHER::equals, new Random(42L)).initialize(model));
    }

    @Test
    void testFillRandomPercentWithCancellation() {
        assertAll(
                () -> assertEquals(0, countFilledAfterFillRandomPercent(0.0d)),
                () -> assertEquals(1_024, countFilledAfterFillRandomPercent(0.25d)),
                () -> assertEquals(3_686, countFilledAfterFillRandomPercent(0.9d)),
                () -> assertEquals(4_096, countFilledAfterFillRandomPercent(1.0d))
        );
    }

    @Test
    void testFillRandomPercentWithCancellationMatchesSeededPlacement() {
        SparseGridModel<TestEntity> withoutCancellationModel = createModel();
        SparseGridModel<TestEntity> withCancellationModel = createModel();

        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.25d, TestEntity.EMPTY, new Random(42L))
                        .initialize(withoutCancellationModel);
        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.25d, TestEntity.EMPTY, new Random(42L), () -> {})
                        .initialize(withCancellationModel);

        assertEquals(entities(withoutCancellationModel), entities(withCancellationModel));
    }

    @Test
    void testFillRandomPercentWithCancellationMatchesAtRoundingBoundary() {
        GridStructure roundingBoundaryStructure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(10, 11));
        SparseGridModel<TestEntity> withoutCancellationModel = new SparseGridModel<>(roundingBoundaryStructure, TestEntity.EMPTY);
        SparseGridModel<TestEntity> withCancellationModel = new SparseGridModel<>(roundingBoundaryStructure,
                TestEntity.EMPTY);

        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.95d, TestEntity.EMPTY, new Random(42L))
                        .initialize(withoutCancellationModel);
        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.95d, TestEntity.EMPTY, new Random(42L), () -> {})
                        .initialize(withCancellationModel);

        assertEquals(entities(withoutCancellationModel), entities(withCancellationModel));
    }

    @Test
    void testPlaceShuffledCountWithCancellation() {
        SparseGridModel<TestEntity> model = createSmallModel();
        model.setEntity(new GridCoordinate(1, 1), TestEntity.OTHER);

        GridInitializers.placeShuffledCount(63, () -> TestEntity.FILLED, TestEntity.EMPTY::equals, new Random(42L),
                () -> {}).initialize(model);

        assertAll(
                () -> assertEquals(63, count(model, TestEntity.FILLED)),
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(1, 1)))
        );
    }

    @Test
    void testPlaceShuffledCountWithCancellationRejectsTooFewCells() {
        SparseGridModel<TestEntity> model = createSmallModel();
        model.setEntity(new GridCoordinate(1, 1), TestEntity.OTHER);

        assertThrows(IllegalStateException.class, () -> GridInitializers.placeShuffledCount(
                64, () -> TestEntity.FILLED, TestEntity.EMPTY::equals, new Random(42L), () -> {}).initialize(model));
    }

    @Test
    void testPlaceShuffledCountWithCancellationChecksDuringShuffle() {
        TrackingRandom random = new TrackingRandom();
        SparseGridModel<TestEntity> model = createModel();

        assertThrows(CancellationException.class, () ->
                GridInitializers.placeShuffledCount(1, () -> TestEntity.FILLED, TestEntity.EMPTY::equals, random,
                        () -> {
                            if (random.nextIntCalls() > 0) {
                                throw new CancellationException();
                            }
                        }).initialize(model));

        assertEquals(WorkCheckpoints.CANCELLATION_CHECK_INTERVAL, random.nextIntCalls());
    }

    private enum TestEntity implements GridEntity {
        EMPTY,
        FILLED,
        OTHER;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private static final class TrackingRandom extends Random {

        private int nextIntCalls;

        @Override
        public int nextInt(int bound) {
            nextIntCalls++;
            return super.nextInt(bound);
        }

        int nextIntCalls() {
            return nextIntCalls;
        }

    }

}
