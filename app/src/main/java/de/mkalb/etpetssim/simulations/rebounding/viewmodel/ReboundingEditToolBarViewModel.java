package de.mkalb.etpetssim.simulations.rebounding.viewmodel;

import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import javafx.beans.property.*;
import javafx.collections.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

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
        List<CompassDirection> directions = switch (config.cellShape()) {
            case HEXAGON -> CellNeighborhoods.HEXAGON_DIRECTION_RING;
            case SQUARE -> (config.neighborhoodMode() == NeighborhoodMode.EDGES_AND_VERTICES)
                    ? CellNeighborhoods.SQUARE_EDGES_AND_VERTICES_DIRECTION_RING
                    : CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING;
            case TRIANGLE ->
                    throw new IllegalStateException("Unsupported cell shape for Rebounding: " + config.cellShape());
        };
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
