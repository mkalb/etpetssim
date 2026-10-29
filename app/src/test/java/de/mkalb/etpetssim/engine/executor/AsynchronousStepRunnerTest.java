package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class AsynchronousStepRunnerTest {

    private static final Comparator<GridCell<TestEntity>> BY_X_DESCENDING =
            Comparator.comparingInt((GridCell<TestEntity> cell) -> cell.coordinate().x()).reversed();

    private static SparseGridModel<TestEntity> createModel() {
        SparseGridModel<TestEntity> model = new SparseGridModel<>(
                new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                        GridSize.square(GridSize.MIN_SIZE)),
                TestEntity.EMPTY);
        model.setEntity(new GridCoordinate(1, 2), TestEntity.AGENT);
        model.setEntity(new GridCoordinate(3, 0), TestEntity.AGENT);
        model.setEntity(new GridCoordinate(2, 2), TestEntity.OBSTACLE);
        model.setEntity(new GridCoordinate(0, 5), TestEntity.AGENT);
        return model;
    }

    @Test
    void testPerformStep() {
        SparseGridModel<TestEntity> model = createModel();
        List<AgentCall> context = new ArrayList<>();
        AsynchronousStepRunner<TestEntity, List<AgentCall>> runner = new AsynchronousStepRunner<>(
                model, entity -> entity == TestEntity.AGENT, BY_X_DESCENDING,
                (agentCell, agentModel, stepIndex, agentContext) -> {
                    assertSame(model, agentModel);
                    agentContext.add(new AgentCall(agentCell.coordinate(), stepIndex));
                });

        runner.performStep(4, context);

        assertEquals(List.of(
                new AgentCall(new GridCoordinate(3, 0), 4),
                new AgentCall(new GridCoordinate(1, 2), 4),
                new AgentCall(new GridCoordinate(0, 5), 4)), context);
    }

    @Test
    void testPerformStepUsesAgentCellsSelectedAtStepStart() {
        SparseGridModel<TestEntity> model = createModel();
        GridCoordinate addedAgentCoordinate = new GridCoordinate(7, 7);
        List<GridCoordinate> context = new ArrayList<>();
        AsynchronousStepRunner<TestEntity, List<GridCoordinate>> runner = new AsynchronousStepRunner<>(
                model, entity -> entity == TestEntity.AGENT, BY_X_DESCENDING,
                (agentCell, agentModel, _, agentContext) -> {
                    agentModel.setEntity(addedAgentCoordinate, TestEntity.AGENT);
                    agentContext.add(agentCell.coordinate());
                });

        runner.performStep(0, context);

        assertAll(
                () -> assertEquals(3, context.size()),
                () -> assertFalse(context.contains(addedAgentCoordinate))
        );
    }

    @Test
    void testModel() {
        SparseGridModel<TestEntity> model = createModel();
        AsynchronousStepRunner<TestEntity, Object> runner = new AsynchronousStepRunner<>(
                model, entity -> entity == TestEntity.AGENT, BY_X_DESCENDING, (_, _, _, _) -> {
        });

        assertSame(model, runner.model());
    }

    private enum TestEntity implements GridEntity {
        EMPTY,
        AGENT,
        OBSTACLE;

        @NonNull
        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private record AgentCall(GridCoordinate coordinate, int stepIndex) {
    }

}
