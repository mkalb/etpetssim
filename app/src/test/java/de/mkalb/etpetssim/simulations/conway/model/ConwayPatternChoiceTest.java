package de.mkalb.etpetssim.simulations.conway.model;

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

    private static ConwayConfig createConfig(CellShape cellShape) {
        return new ConwayConfig(
                cellShape,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                ConwayConstraints.TRANSITION_RULES_DEFAULT
        );
    }

    private static Supplier<GridPattern<ConwayEntity>> createPatternSupplier(Map<GridOffset, ConwayEntity> offsetMap) {
        return () -> () -> offsetMap;
    }

    // --- Construction tests ---

    @Test
    void testConstructorWithValidArguments() {
        Supplier<GridPattern<ConwayEntity>> supplier = createPatternSupplier(
                Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE));
        Predicate<ConwayConfig> predicate = _ -> true;

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
                        _ -> true));

        assertEquals("choiceId must not be blank", exception.getMessage());
    }

    @Test
    void testConstructorRejectsBlankLabelKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new ConwayPatternChoice(
                        CHOICE_ID_SAMPLE,
                        " ",
                        createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                        _ -> true));

        assertEquals("labelKey must not be blank", exception.getMessage());
    }

    @Test
    void testConstructorRejectsChoiceIdMatchingLabelKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new ConwayPatternChoice(
                        LABEL_KEY_SAMPLE,
                        LABEL_KEY_SAMPLE,
                        createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                        _ -> true));

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
                        _ -> true));

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
                        _ -> true));

        assertNotNull(exception);
    }

    // --- Pattern tests ---

    @Test
    void testPatternReturnsNormalizedPattern() {
        ConwayPatternChoice choice = new ConwayPatternChoice(
                CHOICE_ID_SAMPLE,
                LABEL_KEY_SAMPLE,
                createPatternSupplier(Map.of(
                        new GridOffset(2, 3), ConwayEntity.ALIVE,
                        new GridOffset(3, 4), ConwayEntity.DEAD)),
                _ -> true);

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
                _ -> true);

        choice.pattern();
        choice.pattern();

        assertEquals(2, invocationCount.get());
    }

    // --- Availability tests ---

    @Test
    void testAvailableForDelegatesToPredicate() {
        AtomicInteger invocationCount = new AtomicInteger();
        ConwayConfig[] lastSeenConfig = {createConfig(CellShape.HEXAGON)};
        ConwayPatternChoice choice = new ConwayPatternChoice(
                CHOICE_ID_SAMPLE,
                LABEL_KEY_SAMPLE,
                createPatternSupplier(Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE)),
                config -> {
                    invocationCount.incrementAndGet();
                    lastSeenConfig[0] = config;
                    return config.cellShape() == CellShape.SQUARE;
                });
        ConwayConfig squareConfig = createConfig(CellShape.SQUARE);
        ConwayConfig triangleConfig = createConfig(CellShape.TRIANGLE);

        assertAll(
                () -> assertTrue(choice.availableFor(squareConfig)),
                () -> assertFalse(choice.availableFor(triangleConfig)),
                () -> assertEquals(2, invocationCount.get()),
                () -> assertSame(triangleConfig, lastSeenConfig[0])
        );
    }

}

