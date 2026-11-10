package de.mkalb.etpetssim.simulations.snake.viewmodel;

import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategy;
import de.mkalb.etpetssim.ui.InputChoiceProperty;

/**
 * Edit tool bar ViewModel of Snake with the move strategy of new snakes.
 */
public final class SnakeEditToolBarViewModel {

    private final InputChoiceProperty<SnakeMoveStrategy> selectedStrategy = InputChoiceProperty.ofList(
            SnakeMoveStrategies.strategies().getFirst(),
            SnakeMoveStrategies.strategies(),
            SnakeMoveStrategy::displayName);

    public InputChoiceProperty<SnakeMoveStrategy> selectedStrategyProperty() {
        return selectedStrategy;
    }

    public SnakeMoveStrategy getSelectedStrategy() {
        return selectedStrategy.getValue();
    }

}
