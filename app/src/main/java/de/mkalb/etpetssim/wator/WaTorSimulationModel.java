package de.mkalb.etpetssim.wator;

import javafx.beans.property.*;

import java.time.Instant;

public final class WaTorSimulationModel {

    private static final int INITIAL_TIME_COUNTER = 0;
    private static final int INITIAL_FISH_NUMBER = 0;
    private static final int INITIAL_SHARK_NUMBER = 0;

    private final ObjectProperty<Instant> startTimeProperty = new SimpleObjectProperty<>(Instant.now());
    private final LongProperty timeCounterProperty = new SimpleLongProperty(INITIAL_TIME_COUNTER);
    private final IntegerProperty fishNumberProperty = new SimpleIntegerProperty(INITIAL_FISH_NUMBER);
    private final IntegerProperty sharkNumberProperty = new SimpleIntegerProperty(INITIAL_SHARK_NUMBER);

    public WaTorSimulationModel() {
    }

    public void reset() {
        startTimeProperty.setValue(Instant.now());
        timeCounterProperty.set(INITIAL_TIME_COUNTER);
        fishNumberProperty.set(INITIAL_FISH_NUMBER);
        sharkNumberProperty.set(INITIAL_SHARK_NUMBER);
    }

    public ObjectProperty<Instant> startTimeProperty() {
        return startTimeProperty;
    }

    public Instant startTime() {
        return startTimeProperty.getValue();
    }

    public LongProperty timeCounterProperty() {
        return timeCounterProperty;
    }

    public long timeCounter() {
        return timeCounterProperty.get();
    }

    public void incrementTimeCounter() {
        timeCounterProperty.set(timeCounterProperty.get() + 1);
    }

    public void resetTimeCounter() {
        timeCounterProperty.set(INITIAL_TIME_COUNTER);
    }

    public IntegerProperty fishNumberProperty() {
        return fishNumberProperty;
    }

    public int fishNumber() {
        return fishNumberProperty.get();
    }

    public void incrementFishNumber() {
        fishNumberProperty.set(fishNumberProperty.get() + 1);
    }

    public void decrementFishNumber() {
        fishNumberProperty.set(fishNumberProperty.get() - 1);
    }

    public IntegerProperty sharkNumberProperty() {
        return sharkNumberProperty;
    }

    public int sharkNumber() {
        return sharkNumberProperty.get();
    }

    public void incrementSharkNumber() {
        sharkNumberProperty.set(sharkNumberProperty.get() + 1);
    }

    public void decrementSharkNumber() {
        sharkNumberProperty.set(sharkNumberProperty.get() - 1);
    }

    public int combinedNumberOfCreatures() {
        return fishNumberProperty.get() + sharkNumberProperty.get();
    }

}