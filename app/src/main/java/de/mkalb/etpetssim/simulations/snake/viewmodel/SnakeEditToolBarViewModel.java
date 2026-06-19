package de.mkalb.etpetssim.simulations.snake.viewmodel;

import de.mkalb.etpetssim.simulations.snake.model.strategy.*;
import de.mkalb.etpetssim.ui.InputChoiceProperty;

import java.util.*;

public final class SnakeEditToolBarViewModel {

    private final InputChoiceProperty<SnakeMoveStrategy> selectedStrategy;

    public SnakeEditToolBarViewModel() {
        List<SnakeMoveStrategy> availableStrategies = List.copyOf(SnakeMoveStrategies.strategiesForConfig());
        if (availableStrategies.isEmpty()) {
            throw new IllegalStateException("Snake move strategy list must not be empty.");
        }
        selectedStrategy = InputChoiceProperty.ofList(
                availableStrategies.getFirst(),
                availableStrategies,
                SnakeMoveStrategy::toString);
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

