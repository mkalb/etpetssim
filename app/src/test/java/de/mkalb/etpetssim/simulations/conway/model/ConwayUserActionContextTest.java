package de.mkalb.etpetssim.simulations.conway.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ConwayUserActionContextTest {

    @Test
    void testPermittedSubclasses() {
        assertArrayEquals(
                new Class<?>[]{
                        ConwayUserActionContext.FixedAction.class,
                        ConwayUserActionContext.PlacePattern.class
                },
                ConwayUserActionContext.class.getPermittedSubclasses());
    }

    @Test
    void testFixedActionDeclarationOrder() {
        assertArrayEquals(
                new ConwayUserActionContext.FixedAction[]{
                        ConwayUserActionContext.FixedAction.CLEAR_GRID,
                        ConwayUserActionContext.FixedAction.TOGGLE_CELL
                },
                ConwayUserActionContext.FixedAction.values());
    }

    @Test
    void testPlacePatternStoresPatternChoice() {
        ConwayPatternChoice patternChoice = new ConwayPatternChoice(
                "test.block",
                "test.pattern.block",
                ConwayPatterns::block,
                config -> true);
        ConwayUserActionContext.PlacePattern context = new ConwayUserActionContext.PlacePattern(patternChoice);

        assertSame(patternChoice, context.patternChoice());
    }

}
