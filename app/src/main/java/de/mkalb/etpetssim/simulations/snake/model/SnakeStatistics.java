package de.mkalb.etpetssim.simulations.snake.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationStatistics;

public final class SnakeStatistics
        extends AbstractTimedSimulationStatistics {

    public SnakeStatistics(int totalCells) {
        super(totalCells);
    }

    public void update(int newStepCount,
                       StepTimingStatistics newStepTimingStatistics) {
        updateCommon(newStepCount, newStepTimingStatistics);
    }

    @Override
    public String toString() {
        return "SnakeStatistics{" +
                baseToString() +
                '}';
    }

}
