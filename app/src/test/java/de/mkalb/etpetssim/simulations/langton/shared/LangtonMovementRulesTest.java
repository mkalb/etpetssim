package de.mkalb.etpetssim.simulations.langton.shared;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules.AntTurn;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LangtonMovementRulesTest {

    @Test
    void testRecord() {
        List<AntTurn> turns = new ArrayList<>(List.of(AntTurn.LEFT, AntTurn.RIGHT, AntTurn.NONE));

        LangtonMovementRules rules = new LangtonMovementRules(turns);
        turns.add(AntTurn.U_TURN);

        assertAll(
                () -> assertEquals(List.of(AntTurn.LEFT, AntTurn.RIGHT, AntTurn.NONE), rules.turns()),
                () -> assertThrows(UnsupportedOperationException.class, () -> rules.turns().add(AntTurn.LEFT)),
                () -> assertEquals(LangtonMovementRules.MIN_RULE_COUNT,
                        new LangtonMovementRules(Collections.nCopies(LangtonMovementRules.MIN_RULE_COUNT,
                                AntTurn.LEFT)).ruleCount()),
                () -> assertEquals(LangtonMovementRules.MAX_RULE_COUNT,
                        new LangtonMovementRules(Collections.nCopies(LangtonMovementRules.MAX_RULE_COUNT,
                                AntTurn.LEFT)).ruleCount())
        );
    }

    @Test
    void testConstructorRejectsTooFewTurns() {
        List<AntTurn> turns = Collections.nCopies(LangtonMovementRules.MIN_RULE_COUNT - 1, AntTurn.LEFT);
        assertThrows(IllegalArgumentException.class, () -> new LangtonMovementRules(turns));
    }

    @Test
    void testConstructorRejectsTooManyTurns() {
        List<AntTurn> turns = Collections.nCopies(LangtonMovementRules.MAX_RULE_COUNT + 1, AntTurn.LEFT);
        assertThrows(IllegalArgumentException.class, () -> new LangtonMovementRules(turns));
    }

    @Test
    void testParse() {
        assertEquals(List.of(AntTurn.LEFT2, AntTurn.RIGHT2, AntTurn.LEFT, AntTurn.RIGHT, AntTurn.NONE, AntTurn.U_TURN),
                LangtonMovementRules.parse("L2R2LRNU").turns());
    }

    @Test
    void testParseAcceptsAliases() {
        assertEquals(List.of(AntTurn.LEFT, AntTurn.RIGHT, AntTurn.NONE, AntTurn.NONE, AntTurn.U_TURN),
                LangtonMovementRules.parse(" l1r1cfb ").turns());
    }

    @Test
    void testParseRejectsUnknownCharacter() {
        assertThrows(IllegalArgumentException.class, () -> LangtonMovementRules.parse("LRX"));
    }

    @Test
    void testParseRejectsTooFewTurns() {
        assertThrows(IllegalArgumentException.class, () -> LangtonMovementRules.parse("L"));
    }

    @Test
    void testTurnForState() {
        LangtonMovementRules rules = LangtonMovementRules.parse("LRN");
        assertAll(
                () -> assertEquals(AntTurn.LEFT, rules.turnForState(0)),
                () -> assertEquals(AntTurn.RIGHT, rules.turnForState(1)),
                () -> assertEquals(AntTurn.NONE, rules.turnForState(2)),
                () -> assertEquals(AntTurn.LEFT, rules.turnForState(3)),
                () -> assertEquals(AntTurn.NONE, rules.turnForState(5))
        );
    }

    @Test
    void testRuleCount() {
        assertEquals(3, LangtonMovementRules.parse("L2RU").ruleCount());
    }

    @Test
    void testIsValidForCellShape() {
        LangtonMovementRules triangleRules = LangtonMovementRules.parse("LRU");
        LangtonMovementRules fullRules = LangtonMovementRules.parse("L2R2LRNU");
        assertAll(
                () -> assertTrue(triangleRules.isValidForCellShape(CellShape.TRIANGLE)),
                () -> assertTrue(triangleRules.isValidForCellShape(CellShape.SQUARE)),
                () -> assertTrue(triangleRules.isValidForCellShape(CellShape.HEXAGON)),
                () -> assertFalse(fullRules.isValidForCellShape(CellShape.TRIANGLE)),
                () -> assertFalse(LangtonMovementRules.parse("LN").isValidForCellShape(CellShape.TRIANGLE)),
                () -> assertFalse(LangtonMovementRules.parse("LR2").isValidForCellShape(CellShape.TRIANGLE)),
                () -> assertTrue(fullRules.isValidForCellShape(CellShape.SQUARE)),
                () -> assertTrue(fullRules.isValidForCellShape(CellShape.HEXAGON))
        );
    }

    @Test
    void testToDisplayString() {
        assertAll(
                () -> assertEquals("L2R2LRNU", LangtonMovementRules.parse("L2R2LRNU").toDisplayString()),
                () -> assertEquals("LRNNU", LangtonMovementRules.parse("l1r1cfb").toDisplayString())
        );
    }

}

