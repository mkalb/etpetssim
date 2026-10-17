package de.mkalb.etpetssim.simulations.conway.shared;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayPatternChoiceTest {

    private static final String CHOICE_ID_SAMPLE = "conway.sample";
    private static final String LABEL_KEY_SAMPLE = "conway.pattern.sample";

    private static Supplier<GridPattern<ConwayEntity>> createPatternSupplier(Map<GridOffset, ConwayEntity> offsetMap) {
        return () -> () -> offsetMap;
    }

    @Test
    void testConstructorAcceptsValidArguments() {
        Supplier<GridPattern<ConwayEntity>> supplier = createPatternSupplier(
                Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE));
        BiPredicate<CellShape, ConwayTransitionRules> predicate = (_, _) -> true;

        ConwayPatternChoice choice = new ConwayPatternChoice(
                CHOICE_ID_SAMPLE,
                LABEL_KEY_SAMPLE,
                supplier,
                predicate);

        assertAll(
                () -> assertEquals(CHOICE_ID_SAMPLE, choice.choiceId()),
                () -> assertEquals(LABEL_KEY_SAMPLE, choice.labelKey()),
                () -> assertSame(supplier, choice.patternSupplier()),
                () -> assertSame(predicate, choice.availabilityRule())
        );
    }

    @Test
    void testConstructorRejectsBlankChoiceId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new ConwayPatternChoice(
                        " ",
                        LABEL_KEY_SAMPLE,
                        createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                        (_, _) -> true));

        assertEquals("choiceId must not be blank", exception.getMessage());
    }

    @Test
    void testConstructorRejectsBlankLabelKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new ConwayPatternChoice(
                        CHOICE_ID_SAMPLE,
                        " ",
                        createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                        (_, _) -> true));

        assertEquals("labelKey must not be blank", exception.getMessage());
    }

    @Test
    void testConstructorRejectsChoiceIdMatchingLabelKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new ConwayPatternChoice(
                        LABEL_KEY_SAMPLE,
                        LABEL_KEY_SAMPLE,
                        createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                        (_, _) -> true));

        assertEquals("choiceId must differ from labelKey", exception.getMessage());
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testConstructorRejectsNullChoiceId() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new ConwayPatternChoice(
                        null,
                        LABEL_KEY_SAMPLE,
                        createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                        (_, _) -> true));

        assertNotNull(exception);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testConstructorRejectsNullLabelKey() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new ConwayPatternChoice(
                        CHOICE_ID_SAMPLE,
                        null,
                        createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                        (_, _) -> true));

        assertNotNull(exception);
    }

    @Test
    void testPatternReturnsNormalizedPattern() {
        ConwayPatternChoice choice = new ConwayPatternChoice(
                CHOICE_ID_SAMPLE,
                LABEL_KEY_SAMPLE,
                createPatternSupplier(Map.of(
                        new GridOffset(2, 3), ConwayEntity.ALIVE,
                        new GridOffset(3, 4), ConwayEntity.DEAD)),
                (_, _) -> true);

        GridPattern<ConwayEntity> pattern = choice.pattern();
        Map<GridOffset, ConwayEntity> offsetMap = pattern.offsetMap();

        assertAll(
                () -> assertTrue(pattern.isTopLeftAtOrigin(), "Pattern should be normalized to the origin"),
                () -> assertEquals(2, pattern.width()),
                () -> assertEquals(2, pattern.height()),
                () -> assertEquals(2, pattern.size()),
                () -> assertEquals(ConwayEntity.ALIVE, offsetMap.get(new GridOffset(0, 0))),
                () -> assertEquals(ConwayEntity.DEAD, offsetMap.get(new GridOffset(1, 1))),
                () -> assertFalse(offsetMap.containsKey(new GridOffset(2, 3)))
        );
    }

    @Test
    void testPatternCallsSupplierForEachInvocation() {
        AtomicInteger invocationCount = new AtomicInteger();
        ConwayPatternChoice choice = new ConwayPatternChoice(
                CHOICE_ID_SAMPLE,
                LABEL_KEY_SAMPLE,
                () -> {
                    invocationCount.incrementAndGet();
                    return () -> Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE);
                },
                (_, _) -> true);

        choice.pattern();
        choice.pattern();

        assertEquals(2, invocationCount.get());
    }

    @Test
    void testIsAvailableForDelegatesToPredicate() {
        AtomicInteger invocationCount = new AtomicInteger();
        CellShape[] lastSeenCellShape = {CellShape.HEXAGON};
        ConwayTransitionRules[] lastSeenTransitionRules = {null};
        ConwayPatternChoice choice = new ConwayPatternChoice(
                CHOICE_ID_SAMPLE,
                LABEL_KEY_SAMPLE,
                createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                (cellShape, transitionRules) -> {
                    invocationCount.incrementAndGet();
                    lastSeenCellShape[0] = cellShape;
                    lastSeenTransitionRules[0] = transitionRules;
                    return cellShape == CellShape.SQUARE;
                });
        ConwayTransitionRules firstRules = ConwayTransitionRules.of(Set.of(2, 3), Set.of(3));
        ConwayTransitionRules secondRules = ConwayTransitionRules.of(Set.of(4, 5, 6), Set.of(4));

        assertAll(
                () -> assertTrue(choice.isAvailableFor(CellShape.SQUARE, firstRules)),
                () -> assertFalse(choice.isAvailableFor(CellShape.TRIANGLE, secondRules)),
                () -> assertEquals(2, invocationCount.get()),
                () -> assertSame(CellShape.TRIANGLE, lastSeenCellShape[0]),
                () -> assertSame(secondRules, lastSeenTransitionRules[0])
        );
    }

}

