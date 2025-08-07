package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabObservationViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractObservationView;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class LabObservationView
        extends AbstractObservationView<LabStatistics, LabObservationViewModel> {

    public LabObservationView(LabObservationViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add(FXStyleClasses.OBSERVATION_GRID);

        Label[] nameLabels = {
                new Label("Coordinate:")
        };

        String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);

        Label coordinateLabel = new Label(valueUnknown);
        StringBinding coordinateDisplayBinding = Bindings.createStringBinding(
                () -> {
                    GridCoordinate coord = viewModel.getLastClickedCoordinate();
                    return (coord != null) ? coord.toDisplayString() : valueUnknown;
                },
                viewModel.lastClickedCoordinateProperty()
        );
        coordinateLabel.textProperty().bind(coordinateDisplayBinding);

        Label[] valueLabels = {
                coordinateLabel
        };

        for (int i = 0; i < nameLabels.length; i++) {
            nameLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_NAME_LABEL);
            valueLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_VALUE_LABEL);

            grid.add(nameLabels[i], 0, i);
            grid.add(valueLabels[i], 1, i);

            GridPane.setHalignment(nameLabels[i], HPos.LEFT);
            GridPane.setHalignment(valueLabels[i], HPos.RIGHT);
        }

        return grid;
    }

}
