package de.mkalb.etpetssim.simulations.snake.model.strategy;

import java.util.*;

public interface SnakeMoveStrategy {

    Optional<MoveDecision> decideMove(MoveContext context);

    String name();

}
