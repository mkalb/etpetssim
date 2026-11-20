package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class GridInitializersTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(64, 64));

    private static SparseGridModel<TestEntity> createModel() {
        return new SparseGridModel<>(STRUCTURE, TestEntity.EMPTY);
    }

    private static SparseGridModel<TestEntity> createSmallModel() {
        return createModel(8, 8);
    }

    private static SparseGridModel<TestEntity> createModel(int width, int height) {
        return new SparseGridModel<>(new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(width, height)),
                TestEntity.EMPTY);
    }

    private static long count(SparseGridModel<TestEntity> model, TestEntity entity) {
        return entities(model).stream().filter(entity::equals).count();
    }

    private static List<TestEntity> entities(SparseGridModel<TestEntity> model) {
        return model.structure().coordinatesList().stream().map(model::getEntity).toList();
    }

    private static List<TestEntity> expectedEntities(int cellCount, double probability, TestEntity entity) {
        Random random = new Random(42L);
        List<TestEntity> expected = new ArrayList<>(cellCount);
        for (int index = 0; index < cellCount; index++) {
            expected.add((random.nextDouble() < probability) ? entity : TestEntity.EMPTY);
        }
        return expected;
    }

    private static List<TestEntity> entitiesAfterFillWithProbability(double probability) {
        SparseGridModel<TestEntity> model = createSmallModel();
        GridInitializers.fillWithProbability(probability, () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L),
                () -> {}).initialize(model);
        return entities(model);
    }

    private static long countFilledAfterFillWithPercent(double percent) {
        SparseGridModel<TestEntity> model = createModel();
        GridInitializers.fillWithPercent(percent, () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L),
                () -> {}).initialize(model);
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
    void testFillConstant() {
        SparseGridModel<TestEntity> model = createSmallModel();
        model.setEntity(new GridCoordinate(1, 1), TestEntity.OTHER);

        GridInitializers.fillConstant(TestEntity.FILLED).initialize(model);

        assertEquals(64, count(model, TestEntity.FILLED));
    }

    @Test
    void testFillFromSupplier() {
        SparseGridModel<TestEntity> model = createSmallModel();
        AtomicInteger calls = new AtomicInteger();

        GridInitializers.fillFromSupplier(() -> {
            calls.incrementAndGet();
            return TestEntity.FILLED;
        }).initialize(model);

        assertAll(
                () -> assertEquals(64, count(model, TestEntity.FILLED)),
                () -> assertEquals(64, calls.get())
        );
    }

    @Test
    void testFillWithProbability() {
        assertAll(
                () -> assertEquals(expectedEntities(64, 0.3d, TestEntity.FILLED),
                        entitiesAfterFillWithProbability(0.3d)),
                () -> assertEquals(Collections.nCopies(64, TestEntity.EMPTY), entitiesAfterFillWithProbability(0.0d)),
                () -> assertEquals(Collections.nCopies(64, TestEntity.FILLED), entitiesAfterFillWithProbability(1.0d))
        );
    }

    @Test
    void testFillWithProbabilityRejectsProbabilityOutOfRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> GridInitializers.fillWithProbability(
                        -0.1d, () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L), () -> {})),
                () -> assertThrows(IllegalArgumentException.class, () -> GridInitializers.fillWithProbability(
                        1.1d, () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L), () -> {}))
        );
    }

    @Test
    void testFillWithProbabilityRunsCheckBeforeFirstCell() {
        SparseGridModel<TestEntity> model = createSmallModel();
        model.setEntity(new GridCoordinate(1, 1), TestEntity.OTHER);

        assertThrows(CancellationException.class, () -> GridInitializers.fillWithProbability(0.5d,
                () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L), () -> {
                    throw new CancellationException();
                }).initialize(model));
        assertAll(
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(1, 1))),
                () -> assertEquals(63, count(model, TestEntity.EMPTY))
        );
    }

    @Test
    void testFillWithPercent() {
        assertAll(
                () -> assertEquals(0, countFilledAfterFillWithPercent(0.0d)),
                () -> assertEquals(1_024, countFilledAfterFillWithPercent(0.25d)),
                () -> assertEquals(3_686, countFilledAfterFillWithPercent(0.9d)),
                () -> assertEquals(4_096, countFilledAfterFillWithPercent(1.0d))
        );
    }

    @Test
    void testFillWithPercentRoundsEntityCount() {
        SparseGridModel<TestEntity> model = createModel(10, 11);

        GridInitializers.fillWithPercent(0.95d, () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L), () -> {})
                .initialize(model);

        assertEquals(105, count(model, TestEntity.FILLED));
    }

    @Test
    void testFillWithPercentCallsSupplierOncePerEntity() {
        SparseGridModel<TestEntity> model = createSmallModel();
        AtomicInteger calls = new AtomicInteger();

        GridInitializers.fillWithPercent(0.9d, () -> {
            calls.incrementAndGet();
            return TestEntity.FILLED;
        }, TestEntity.EMPTY, new Random(42L), () -> {}).initialize(model);

        assertAll(
                () -> assertEquals(58, count(model, TestEntity.FILLED)),
                () -> assertEquals(58, calls.get())
        );
    }

    @Test
    void testFillWithPercentRejectsPercentOutOfRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> GridInitializers.fillWithPercent(
                        -0.1d, () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L), () -> {})),
                () -> assertThrows(IllegalArgumentException.class, () -> GridInitializers.fillWithPercent(
                        1.1d, () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L), () -> {}))
        );
    }

    @Test
    void testFillWithPercentRunsCheckBeforeFirstCell() {
        SparseGridModel<TestEntity> model = createSmallModel();
        model.setEntity(new GridCoordinate(1, 1), TestEntity.OTHER);

        assertThrows(CancellationException.class, () -> GridInitializers.fillWithPercent(0.5d,
                () -> TestEntity.FILLED, TestEntity.EMPTY, new Random(42L), () -> {
                    throw new CancellationException();
                }).initialize(model));
        assertAll(
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(1, 1))),
                () -> assertEquals(63, count(model, TestEntity.EMPTY))
        );
    }

    @Test
    void testPlaceCells() {
        SparseGridModel<TestEntity> model = createModel();
        AtomicInteger checks = new AtomicInteger();
        List<GridCell<TestEntity>> cells = List.of(
                new GridCell<>(new GridCoordinate(0, 0), TestEntity.FILLED),
                new GridCell<>(new GridCoordinate(3, 2), TestEntity.OTHER));

        GridInitializers.placeCells(cells, checks::incrementAndGet).initialize(model);

        assertAll(
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(0, 0))),
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(3, 2))),
                () -> assertEquals(4_094, count(model, TestEntity.EMPTY)),
                () -> assertEquals(1, checks.get())
        );
    }

    @Test
    void testPlaceCellsRejectsCoordinateOutsideGrid() {
        SparseGridModel<TestEntity> model = createSmallModel();
        List<GridCell<TestEntity>> cells = List.of(new GridCell<>(new GridCoordinate(8, 0), TestEntity.FILLED));

        assertThrows(IndexOutOfBoundsException.class,
                () -> GridInitializers.placeCells(cells, () -> {}).initialize(model));
    }

    @Test
    void testPlaceBorder() {
        SparseGridModel<TestEntity> model = createSmallModel();

        GridInitializers.placeBorder(TestEntity.FILLED).initialize(model);

        assertAll(
                () -> assertEquals(28, count(model, TestEntity.FILLED)),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(0, 0))),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(7, 7))),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(7, 3))),
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(new GridCoordinate(1, 1))),
                () -> assertEquals(TestEntity.EMPTY, model.getEntity(new GridCoordinate(6, 6)))
        );
    }

    @Test
    void testPlacePattern() {
        SparseGridModel<TestEntity> model = createModel();
        GridPattern<TestEntity> pattern = GridPatterns.of(TestEntity.FILLED,
                List.of(new GridOffset(0, 0), new GridOffset(1, 2)));

        GridInitializers.placePattern(pattern, new GridCoordinate(5, 7)).initialize(model);

        assertAll(
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(5, 7))),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(6, 9))),
                () -> assertEquals(2, count(model, TestEntity.FILLED))
        );
    }

    @Test
    void testPlacePatternSkipsEntriesOutsideGrid() {
        SparseGridModel<TestEntity> model = createModel();
        GridPattern<TestEntity> pattern = GridPatterns.of(TestEntity.FILLED,
                List.of(new GridOffset(0, 0), new GridOffset(-1, -1), new GridOffset(1, 0), new GridOffset(0, 1)));

        GridInitializers.placePattern(pattern, new GridCoordinate(63, 63)).initialize(model);

        assertAll(
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(63, 63))),
                () -> assertEquals(TestEntity.FILLED, model.getEntity(new GridCoordinate(62, 62))),
                () -> assertEquals(2, count(model, TestEntity.FILLED))
        );
    }

    @Test
    void testPlacePatternOverwritesExistingEntities() {
        SparseGridModel<TestEntity> model = createModel();
        GridCoordinate target = new GridCoordinate(10, 10);
        model.setEntity(target, TestEntity.OTHER);

        GridInitializers.placePattern(GridPatterns.of(TestEntity.FILLED, List.of(new GridOffset(0, 0))), target)
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
    void testPlaceAtRandomCells() {
        SparseGridModel<TestEntity> model = createSmallModel();
        model.setEntity(new GridCoordinate(2, 2), TestEntity.OTHER);
        List<TestEntity> entities = Collections.nCopies(63, TestEntity.FILLED);

        GridInitializers.placeAtRandomCells(entities, TestEntity.EMPTY::equals, new Random(42L), () -> {})
                .initialize(model);

        assertAll(
                () -> assertEquals(63, count(model, TestEntity.FILLED)),
                () -> assertEquals(TestEntity.OTHER, model.getEntity(new GridCoordinate(2, 2)))
        );
    }

    @Test
    void testPlaceAtRandomCellsRejectsTooFewCells() {
        SparseGridModel<TestEntity> model = createSmallModel();
        List<TestEntity> entities = Collections.nCopies(65, TestEntity.FILLED);

        assertThrows(IllegalStateException.class, () -> GridInitializers.placeAtRandomCells(
                entities, TestEntity.EMPTY::equals, new Random(42L), () -> {}).initialize(model));
        assertEquals(64, count(model, TestEntity.EMPTY));
    }

    @Test
    void testPlaceAtRandomCellsRunsCheckBeforeFirstCell() {
        SparseGridModel<TestEntity> model = createSmallModel();
        List<TestEntity> entities = Collections.nCopies(10, TestEntity.FILLED);

        assertThrows(CancellationException.class, () -> GridInitializers.placeAtRandomCells(
                entities, TestEntity.EMPTY::equals, new Random(42L), () -> {
                    throw new CancellationException();
                }).initialize(model));
        assertEquals(64, count(model, TestEntity.EMPTY));
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

}
