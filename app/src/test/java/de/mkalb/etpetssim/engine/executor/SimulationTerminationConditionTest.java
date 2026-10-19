package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

final class SimulationTerminationConditionTest {

    private static final GridStructure STRUCTURE = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), GridSize.square(GridSize.MIN_SIZE));
    private static final SparseGridModel<TestEntity> MODEL = new SparseGridModel<>(STRUCTURE, TestEntity.EMPTY);
    private static final String CONTEXT = "context";

    private static boolean isFinished(SimulationTerminationCondition<TestEntity, SparseGridModel<TestEntity>, String> condition) {
        return condition.isFinished(MODEL, 3, CONTEXT);
    }

    private static SimulationTerminationCondition<TestEntity, SparseGridModel<TestEntity>, String> always() {
        return (model, stepCount, context) -> true;
    }

    private static SimulationTerminationCondition<TestEntity, SparseGridModel<TestEntity>, String> never() {
        return (model, stepCount, context) -> false;
    }

    private static SimulationTerminationCondition<TestEntity, SparseGridModel<TestEntity>, String> matchingArguments() {
        return (model, stepCount, context) -> (model == MODEL) && (stepCount == 3) && CONTEXT.equals(context);
    }

    @Test
    void testAnd() {
        assertAll(
                () -> assertTrue(isFinished(always().and(always()))),
                () -> assertFalse(isFinished(always().and(never()))),
                () -> assertFalse(isFinished(never().and(always()))),
                () -> assertFalse(isFinished(never().and(never())))
        );
    }

    @Test
    void testAndShortCircuits() {
        AtomicInteger calls = new AtomicInteger();

        boolean result = isFinished(never().and((model, stepCount, context) -> calls.incrementAndGet() > 0));

        assertAll(
                () -> assertFalse(result),
                () -> assertEquals(0, calls.get())
        );
    }

    @Test
    void testAndPassesArguments() {
        assertTrue(isFinished(matchingArguments().and(matchingArguments())));
    }

    @Test
    void testOr() {
        assertAll(
                () -> assertTrue(isFinished(always().or(always()))),
                () -> assertTrue(isFinished(always().or(never()))),
                () -> assertTrue(isFinished(never().or(always()))),
                () -> assertFalse(isFinished(never().or(never())))
        );
    }

    @Test
    void testOrShortCircuits() {
        AtomicInteger calls = new AtomicInteger();

        boolean result = isFinished(always().or((model, stepCount, context) -> calls.incrementAndGet() > 0));

        assertAll(
                () -> assertTrue(result),
                () -> assertEquals(0, calls.get())
        );
    }

    @Test
    void testOrPassesArguments() {
        assertTrue(isFinished(never().or(matchingArguments())));
    }

    @Test
    void testNegate() {
        assertAll(
                () -> assertFalse(isFinished(always().negate())),
                () -> assertTrue(isFinished(never().negate()))
        );
    }

    @Test
    void testNegatePassesArguments() {
        assertFalse(isFinished(matchingArguments().negate()));
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
