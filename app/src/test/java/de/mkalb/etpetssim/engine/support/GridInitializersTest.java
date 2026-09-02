package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
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
    void testCancellationAwareFillRandomPercentMatchesLegacySeededPlacement() {
        SparseGridModel<TestEntity> legacyModel = createModel();
        SparseGridModel<TestEntity> cancellationAwareModel = createModel();

        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.25d, TestEntity.EMPTY, new Random(42L))
                        .initialize(legacyModel);
        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.25d, TestEntity.EMPTY, new Random(42L), () -> {})
                        .initialize(cancellationAwareModel);

        assertEquals(entities(legacyModel), entities(cancellationAwareModel));
    }

    @Test
    void testCancellationAwareFillRandomPercentMatchesLegacySeededPlacementAtRoundingBoundary() {
        GridStructure roundingBoundaryStructure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(10, 11));
        SparseGridModel<TestEntity> legacyModel = new SparseGridModel<>(roundingBoundaryStructure, TestEntity.EMPTY);
        SparseGridModel<TestEntity> cancellationAwareModel = new SparseGridModel<>(roundingBoundaryStructure,
                TestEntity.EMPTY);

        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.95d, TestEntity.EMPTY, new Random(42L))
                        .initialize(legacyModel);
        GridInitializers.fillRandomPercent(() -> TestEntity.FILLED, 0.95d, TestEntity.EMPTY, new Random(42L), () -> {})
                        .initialize(cancellationAwareModel);

        assertEquals(entities(legacyModel), entities(cancellationAwareModel));
    }

    @Test
    void testPlaceShuffledCountChecksCancellationDuringShuffle() {
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
        FILLED;

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
