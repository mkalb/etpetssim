package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class GridInitializersTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(64, 64));

    private static SparseGridModel<TestEntity> createModel() {
        return new SparseGridModel<>(STRUCTURE, TestEntity.EMPTY);
    }

    private static List<TestEntity> entities(SparseGridModel<TestEntity> model) {
        return model.structure().coordinatesList().stream().map(model::getEntity).toList();
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
