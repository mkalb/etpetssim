package de.mkalb.etpetssim.simulations.sugar.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class AgentTest {

    @Test
    void testToDisplayString() {
        Agent agent = new Agent(10, 2);

        assertEquals("[AGENT *2 E=10]", agent.toDisplayString());
    }

}
