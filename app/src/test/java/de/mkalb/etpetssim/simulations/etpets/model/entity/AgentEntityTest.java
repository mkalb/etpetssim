package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class AgentEntityTest {

    @Test
    void testId() {
        assertEquals(0, NoAgent.NO_AGENT.id());
    }

}
