package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonDirectionOptions;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonUserActionContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
