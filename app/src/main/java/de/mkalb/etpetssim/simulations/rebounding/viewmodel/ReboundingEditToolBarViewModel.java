package de.mkalb.etpetssim.simulations.rebounding.viewmodel;

import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Edit tool bar ViewModel of Rebounding with the direction of new rebounders.
 */
public final class ReboundingEditToolBarViewModel {

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

    public void updateAvailableDirections(ReboundingConfig config) {
        List<CompassDirection> directions = CellNeighborhoods.directionRing(config.neighborhoodMode(),
                config.cellShape());
        availableDirections.setAll(directions);
        selectedDirection.set(directions.isEmpty() ? null : directions.getFirst());
    }

    public Optional<ReboundingUserActionContext.AddRebounder> resolveSelectedAddRebounderContext() {
        CompassDirection direction = selectedDirection.get();
        if ((direction == null) || !availableDirections.contains(direction)) {
            return Optional.empty();
        }
        return Optional.of(new ReboundingUserActionContext.AddRebounder(direction));
    }

}
