package de.mkalb.etpetssim.wator;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.util.*;

public final class WaTorModel {

    public static final int MIN_SIZE = 64;

    private static final int INITIAL_SPEED = 10; // Tenth of a second
    private static final int INITIAL_TIME_COUNTER = 0;
    private static final int INITIAL_X_SIZE = 256;
    private static final int INITIAL_Y_SIZE = 128;
    private static final int INITIAL_CELL_LENGTH = 2;
    private static final int INITIAL_FISH_NUMBER = 512;
    private static final int INITIAL_SHARK_NUMBER = 256;

    private static final List<Integer> CELL_LENGTH_CHOICES = List.of(1, 2, 4, 8);

    private final IntegerProperty speedProperty = new SimpleIntegerProperty(INITIAL_SPEED); // Tenth of a second
    private final LongProperty timeCounterProperty = new SimpleLongProperty(INITIAL_TIME_COUNTER);
    private final IntegerProperty xSizeProperty = new SimpleIntegerProperty(INITIAL_X_SIZE);
    private final IntegerProperty ySizeProperty = new SimpleIntegerProperty(INITIAL_Y_SIZE);
    private final IntegerProperty cellLengthProperty = new SimpleIntegerProperty(INITIAL_CELL_LENGTH);
    private final IntegerProperty fishNumberProperty = new SimpleIntegerProperty(INITIAL_FISH_NUMBER);
    private final IntegerProperty sharkNumberProperty = new SimpleIntegerProperty(INITIAL_SHARK_NUMBER);

    public WaTorModel() {
    }

    public IntegerProperty speedProperty() {
        return speedProperty;
    }

    public double speedAsTenthOfASecond() {
        return speedProperty.get();
    }

    public Duration speedAsDuration() {
        return Duration.millis(speedProperty.get() * 100);
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

    public IntegerProperty xSizeProperty() {
        return xSizeProperty;
    }

    public int xSize() {
        return xSizeProperty.get();
    }

    public IntegerProperty ySizeProperty() {
        return ySizeProperty;
    }

    public int ySize() {
        return ySizeProperty.get();
    }

    public IntegerProperty cellLengthProperty() {
        return cellLengthProperty;
    }

    public Property<Integer> cellLengthPropertyAsObject() {
        return cellLengthProperty.asObject();
    }

    public int cellLength() {
        return cellLengthProperty.get();
    }

    public ObservableList<Integer> cellLengthChoices() {
        return javafx.collections.FXCollections.observableList(CELL_LENGTH_CHOICES);
    }

    public IntegerProperty fishNumberProperty() {
        return fishNumberProperty;
    }

    public int fishNumber() {
        return fishNumberProperty.get();
    }

    public IntegerProperty sharkNumberProperty() {
        return sharkNumberProperty;
    }

    public int sharkNumber() {
        return sharkNumberProperty.get();
    }

}
