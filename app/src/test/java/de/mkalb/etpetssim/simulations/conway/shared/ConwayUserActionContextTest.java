package de.mkalb.etpetssim.simulations.conway.shared;

import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertSame;

final class ConwayUserActionContextTest {

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
