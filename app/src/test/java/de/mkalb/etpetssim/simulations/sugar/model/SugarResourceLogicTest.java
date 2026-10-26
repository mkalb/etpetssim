package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;
import org.junit.jupiter.api.Test;

import static de.mkalb.etpetssim.simulations.sugar.model.SugarLogicTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

final class SugarResourceLogicTest {

    private static final int REGENERATION_RATE = 2;

    @Test
    void testApply() {
        SugarConfig config = createConfig(SugarConstraints.AGENT_VISION_RANGE_DEFAULT, REGENERATION_RATE);
        SugarGridModel model = createGridModel(config);
        Sugar growingSugar = new Sugar(10, 3);
        Sugar fullSugar = new Sugar(10, 9);
        model.resourceModel().setEntity(new GridCoordinate(0, 0), growingSugar);
        model.resourceModel().setEntity(new GridCoordinate(1, 0), fullSugar);

        SugarResourceLogic.apply(model, config);

        assertAll(
                () -> assertEquals(3 + REGENERATION_RATE, growingSugar.currentAmount()),
                () -> assertEquals(10, fullSugar.currentAmount())
        );
    }

}
