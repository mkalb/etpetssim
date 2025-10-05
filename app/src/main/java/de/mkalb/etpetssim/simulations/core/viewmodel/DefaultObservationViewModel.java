package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

public final class DefaultObservationViewModel<
        ENT extends GridEntity,
        STA extends SimulationStatistics>
        extends AbstractObservationViewModel<STA> {

    private final ObjectProperty<@Nullable GridCell<ENT>> selectedGridCell = new SimpleObjectProperty<>();

    public DefaultObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

    void bindSelectedGridCellProperty(ObjectProperty<@Nullable GridCell<ENT>> property) {
        selectedGridCell.bind(property);
    }

    public ObjectProperty<@Nullable GridCell<ENT>> selectedGridCellProperty() {
        return selectedGridCell;
    }

}
