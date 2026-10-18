package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class AgentEntityTest {

    @Test
    void testIsTerrain() {
        assertFalse(NoAgent.NO_AGENT.isTerrain());
    }

    @Test
    void testIsResource() {
        assertFalse(NoAgent.NO_AGENT.isResource());
    }

    @Test
    void testId() {
        assertEquals(0, NoAgent.NO_AGENT.id());
    }

}
