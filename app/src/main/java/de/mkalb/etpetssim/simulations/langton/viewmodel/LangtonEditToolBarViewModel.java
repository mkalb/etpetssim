package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.shared.*;
import javafx.beans.property.*;
import javafx.collections.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LangtonEditToolBarViewModel {

    private final ObservableList<CompassDirection> availableDirections = FXCollections.observableArrayList();
    private final ObjectProperty<@Nullable CompassDirection> selectedDirection = new SimpleObjectProperty<>();

    public ObservableList<CompassDirection> availableDirections() {
        return availableDirections;
    }

    public ObjectProperty<@Nullable CompassDirection> selectedDirectionProperty() {
        return selectedDirection;
    }

    public @Nullable CompassDirection getSelectedDirection() {
        return selectedDirection.get();
    }

    public void setSelectedDirection(@Nullable CompassDirection direction) {
        if ((direction == null) || availableDirections.contains(direction)) {
            selectedDirection.set(direction);
        }
    }

    public void updateAvailableDirections(LangtonConfig config) {
        List<CompassDirection> directions = LangtonDirectionOptions.selectableInitialDirections(config.cellShape());
        availableDirections.setAll(directions);
        selectedDirection.set(resolveSelectedDirection(directions));
    }

    public Optional<LangtonUserActionContext.AddAnt> resolveSelectedAddAntContext(LangtonConfig config,
                                                                                  @Nullable GridCoordinate selectedCoordinate) {
        return LangtonDirectionOptions.resolveInitialDirection(config.cellShape(), selectedCoordinate, selectedDirection.get())
                                      .map(LangtonUserActionContext.AddAnt::new);
    }

    private @Nullable CompassDirection resolveSelectedDirection(List<CompassDirection> directions) {
        CompassDirection currentDirection = selectedDirection.get();
        if ((currentDirection != null) && directions.contains(currentDirection)) {
            return currentDirection;
        }
        return directions.isEmpty() ? null : directions.getFirst();
    }

}
