package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.model.StepTimingStatistics;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class AbstractTimedSimulationStatistics
        implements TimedSimulationStatistics {

    private final int totalCells;

    private int stepCount;
    private StepTimingStatistics stepTimingStatistics;

    protected AbstractTimedSimulationStatistics(int totalCells) {
        this.totalCells = totalCells;
        stepCount = 0;
        stepTimingStatistics = StepTimingStatistics.empty();
    }

    @Override
    public final int getStepCount() {
        return stepCount;
    }

    @Override
    public final int getTotalCells() {
        return totalCells;
    }

    @Override
    public final StepTimingStatistics stepTimingStatistics() {
        return stepTimingStatistics;
    }

    protected final void updateCommon(int newStepCount,
                                      StepTimingStatistics newStepTimingStatistics) {
        stepCount = newStepCount;
        stepTimingStatistics = newStepTimingStatistics;
    }

    protected final String baseToString() {
        return "totalCells=" + totalCells +
                ", stepCount=" + stepCount +
                ", stepTimingStatistics=" + stepTimingStatistics;
    }

}
