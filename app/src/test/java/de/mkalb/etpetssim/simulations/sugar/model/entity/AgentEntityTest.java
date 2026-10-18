package de.mkalb.etpetssim.simulations.sugar.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

final class AgentEntityTest {

    @Test
    void testIsResource() {
        assertFalse(NoAgent.NO_AGENT.isResource());
    }

}
