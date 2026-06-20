package de.mkalb.etpetssim.simulations.wator.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class WatorUserActionContextTest {

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new WatorUserActionContext[]{
                        WatorUserActionContext.ADD_FISH,
                        WatorUserActionContext.ADD_SHARK,
                        WatorUserActionContext.REMOVE_CREATURE
                },
                WatorUserActionContext.values());
    }

    @Test
    void testValuesImplementSimulationUserActionContext() {
        for (WatorUserActionContext context : WatorUserActionContext.values()) {
            assertInstanceOf(SimulationUserActionContext.class, context);
        }
    }

}
