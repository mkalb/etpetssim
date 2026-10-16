package de.mkalb.etpetssim.simulations.wator.shared;

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

}
