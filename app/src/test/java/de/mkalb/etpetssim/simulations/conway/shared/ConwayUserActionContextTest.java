package de.mkalb.etpetssim.simulations.conway.shared;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import org.junit.jupiter.api.Test;

import java.util.*;

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
                () -> () -> Map.of(new GridOffset(0, 0), ConwayEntity.ALIVE),
                (_, _) -> true);
        ConwayUserActionContext.PlacePattern context = new ConwayUserActionContext.PlacePattern(patternChoice);

        assertSame(patternChoice, context.patternChoice());
    }

}
