package de.mkalb.etpetssim.wator;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.util.*;

public final class WaTorConfigModel {

    public static final int MIN_SIZE = 32;
    public static final int MAX_SIZE = 256;

    private static final int INITIAL_SPEED = 25; // hundredth of a second
    private static final int INITIAL_X_SIZE = 256;
    private static final int INITIAL_Y_SIZE = 128;
    private static final int INITIAL_CELL_LENGTH = 4;
    private static final int INITIAL_FISH_NUMBER = 128;
    private static final int INITIAL_SHARK_NUMBER = 32;

    private static final List<Integer> CELL_LENGTH_CHOICES = List.of(1, 2, 4, 8, 16);

    private final IntegerProperty speedProperty = new SimpleIntegerProperty(INITIAL_SPEED); // hundredth of a second
    private final IntegerProperty xSizeProperty = new SimpleIntegerProperty(INITIAL_X_SIZE);
    private final IntegerProperty ySizeProperty = new SimpleIntegerProperty(INITIAL_Y_SIZE);
    private final Property<Integer> cellLengthProperty = new SimpleObjectProperty<>(INITIAL_CELL_LENGTH);
    private final IntegerProperty fishNumberProperty = new SimpleIntegerProperty(INITIAL_FISH_NUMBER);
    private final IntegerProperty sharkNumberProperty = new SimpleIntegerProperty(INITIAL_SHARK_NUMBER);

    public WaTorConfigModel() {
    }

    public IntegerProperty speedProperty() {
        return speedProperty;
    }

    public double speedAsHundredthOfASecond() {
        return speedProperty.get();
    }

    public Duration speedAsDuration() {
        return Duration.millis(speedProperty.get() * 10);
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

    public Property<Integer> cellLengthProperty() {
        return cellLengthProperty;
    }

    public int cellLength() {
        return cellLengthProperty.getValue();
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
