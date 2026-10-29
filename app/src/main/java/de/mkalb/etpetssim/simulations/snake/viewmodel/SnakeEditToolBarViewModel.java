package de.mkalb.etpetssim.simulations.snake.viewmodel;

import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategy;
import de.mkalb.etpetssim.ui.InputChoiceProperty;

import java.util.List;

public final class SnakeEditToolBarViewModel {

    private final InputChoiceProperty<SnakeMoveStrategy> selectedStrategy;

    public SnakeEditToolBarViewModel() {
        List<SnakeMoveStrategy> availableStrategies = SnakeMoveStrategies.strategies();
        if (availableStrategies.isEmpty()) {
            throw new IllegalStateException("Snake move strategy list must not be empty.");
        }
        selectedStrategy = InputChoiceProperty.ofList(
                availableStrategies.getFirst(),
                availableStrategies,
                SnakeMoveStrategy::displayName);
    }

    public List<SnakeMoveStrategy> availableStrategies() {
        return selectedStrategy.validValues();
    }

    public InputChoiceProperty<SnakeMoveStrategy> selectedStrategyProperty() {
        return selectedStrategy;
    }

    public SnakeMoveStrategy getSelectedStrategy() {
        return selectedStrategy.getValue();
    }

    public void setSelectedStrategy(SnakeMoveStrategy strategy) {
        selectedStrategy.setValue(strategy);
    }

}

