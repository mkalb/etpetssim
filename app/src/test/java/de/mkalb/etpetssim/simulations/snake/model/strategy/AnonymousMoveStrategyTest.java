package de.mkalb.etpetssim.simulations.snake.model.strategy;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class AnonymousMoveStrategyTest {

    @Test
    void testDisplayName() {
        AnonymousMoveStrategy strategy = _ -> Optional.empty();

        assertEquals(strategy.toString(), strategy.displayName());
    }

}
