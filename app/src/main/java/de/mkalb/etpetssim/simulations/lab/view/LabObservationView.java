package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class LabObservationView
        extends AbstractObservationView<LabStatistics, DefaultObservationViewModel<LabEntity, LabStatistics>> {

    public LabObservationView(DefaultObservationViewModel<LabEntity, LabStatistics> viewModel) {
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
                    var coordinate = viewModel.lastClickedCoordinateProperty().get();
                    return (coordinate != null) ? coordinate.toDisplayString() : valueUnknown;
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

    @Override
    protected void updateObservationLabels() {
    }

}
