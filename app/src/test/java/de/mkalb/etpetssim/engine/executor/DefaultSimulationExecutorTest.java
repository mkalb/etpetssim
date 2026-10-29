package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DefaultSimulationExecutorTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
            GridSize.square(GridSize.MIN_SIZE));

    private static SparseGridModel<TestEntity> createModel() {
        return new SparseGridModel<>(STRUCTURE, TestEntity.EMPTY);
    }

    private static DefaultSimulationExecutor<TestEntity, SparseGridModel<TestEntity>, List<Integer>> createExecutor(
            List<Integer> context) {
        SparseGridModel<TestEntity> model = createModel();
        return new DefaultSimulationExecutor<>(
                (stepIndex, stepContext) -> stepContext.add(stepIndex),
                () -> model,
                (_, _, _) -> false,
                context);
    }

    @Test
    void testCurrentGridModel() {
        SparseGridModel<TestEntity> firstModel = createModel();
        SparseGridModel<TestEntity> secondModel = createModel();
        AtomicReference<SparseGridModel<TestEntity>> modelReference = new AtomicReference<>(firstModel);
        DefaultSimulationExecutor<TestEntity, SparseGridModel<TestEntity>, List<Integer>> executor =
                new DefaultSimulationExecutor<>((_, _) -> {
                }, modelReference::get, (_, _, _) -> false, new ArrayList<>());
        SparseGridModel<TestEntity> modelBeforeChange = executor.currentGridModel();

        modelReference.set(secondModel);

        assertAll(
                () -> assertSame(firstModel, modelBeforeChange),
                () -> assertSame(secondModel, executor.currentGridModel())
        );
    }

    @Test
    void testStepCount() {
        DefaultSimulationExecutor<TestEntity, SparseGridModel<TestEntity>, List<Integer>> executor =
                createExecutor(new ArrayList<>());
        int initialStepCount = executor.stepCount();

        executor.executeStep();
        executor.executeStep();

        assertAll(
                () -> assertEquals(0, initialStepCount),
                () -> assertEquals(2, executor.stepCount())
        );
    }

    @Test
    void testIsFinished() {
        SparseGridModel<TestEntity> model = createModel();
        List<Integer> context = new ArrayList<>();
        List<Integer> recordedStepCounts = new ArrayList<>();
        DefaultSimulationExecutor<TestEntity, SparseGridModel<TestEntity>, List<Integer>> executor =
                new DefaultSimulationExecutor<>((_, _) -> {
                }, () -> model, (conditionModel, stepCount, conditionContext) -> {
                    assertSame(model, conditionModel);
                    assertSame(context, conditionContext);
                    recordedStepCounts.add(stepCount);
                    return stepCount >= 1;
                }, context);
        boolean finishedBeforeStep = executor.isFinished();

        executor.executeStep();

        assertAll(
                () -> assertFalse(finishedBeforeStep),
                () -> assertTrue(executor.isFinished()),
                () -> assertEquals(List.of(0, 1), recordedStepCounts)
        );
    }

    @Test
    void testExecuteStep() {
        List<Integer> context = new ArrayList<>();
        DefaultSimulationExecutor<TestEntity, SparseGridModel<TestEntity>, List<Integer>> executor =
                createExecutor(context);

        executor.executeStep();
        executor.executeStep();
        executor.executeStep();

        assertEquals(List.of(0, 1, 2), context);
    }

    private enum TestEntity implements GridEntity {
        EMPTY;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
