package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

final class NoUserActionContextTest {

    @Test
    void testEnumValues() {
        assertArrayEquals(
                new NoUserActionContext[]{NoUserActionContext.NO_CONTEXT},
                NoUserActionContext.values());
    }

}
