package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SynchronousStepRunnerTest {

    private static SparseGridModel<TestEntity> createModel() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(
                new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                        GridSize.square(GridSize.MIN_SIZE)),
                TestEntity.EMPTY);
        model.setEntity(new GridCoordinate(1, 1), TestEntity.AGENT);
        return model;
    }

    private static void moveAgentsRight(ReadableGridModel<TestEntity> currentGridModel,
                                        WritableGridModel<TestEntity> nextGridModel,
                                        int stepIndex,
                                        List<Integer> context) {
        assertAll(
                () -> assertEquals(currentGridModel.structure(), nextGridModel.structure()),
                () -> assertEquals(TestEntity.EMPTY, nextGridModel.defaultEntity()),
                () -> assertTrue(nextGridModel.nonDefaultCoordinates().isEmpty())
        );
        for (GridCoordinate coordinate : currentGridModel.nonDefaultCoordinates()) {
            nextGridModel.setEntity(coordinate.offset(1, 0), currentGridModel.getEntity(coordinate));
        }
        context.add(stepIndex);
    }

    @Test
    void testConstructor() {
        SparseGridModel<TestEntity> model = createModel();

        SynchronousStepRunner<TestEntity, List<Integer>> runner =
                new SynchronousStepRunner<>(model, SynchronousStepRunnerTest::moveAgentsRight);

        assertSame(model, runner.currentGridModel());
    }

    @Test
    void testPerformStep() {
        SparseGridModel<TestEntity> initialModel = createModel();
        List<Integer> context = new ArrayList<>();
        SynchronousStepRunner<TestEntity, List<Integer>> runner =
                new SynchronousStepRunner<>(initialModel, SynchronousStepRunnerTest::moveAgentsRight);

        runner.performStep(0, context);
        WritableGridModel<TestEntity> modelAfterFirstStep = runner.currentGridModel();
        Set<GridCoordinate> coordinatesAfterFirstStep = Set.copyOf(modelAfterFirstStep.nonDefaultCoordinates());
        runner.performStep(1, context);

        assertAll(
                () -> assertNotSame(initialModel, modelAfterFirstStep),
                () -> assertEquals(Set.of(new GridCoordinate(2, 1)), coordinatesAfterFirstStep),
                () -> assertSame(initialModel, runner.currentGridModel()),
                () -> assertEquals(Set.of(new GridCoordinate(3, 1)), runner.currentGridModel().nonDefaultCoordinates()),
                () -> assertTrue(modelAfterFirstStep.nonDefaultCoordinates().isEmpty()),
                () -> assertEquals(List.of(0, 1), context)
        );
    }

    private enum TestEntity implements GridEntity {
        EMPTY,
        AGENT;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
