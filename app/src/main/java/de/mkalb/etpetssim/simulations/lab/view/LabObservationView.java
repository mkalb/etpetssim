package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public final class LabObservationView
        extends AbstractObservationView<LabStatistics, DefaultObservationViewModel<LabEntity, LabStatistics>> {

    public LabObservationView(DefaultObservationViewModel<LabEntity, LabStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        Label coordinateLabel = new Label();

        String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
        StringBinding coordinateDisplayBinding = Bindings.createStringBinding(
                () -> {
                    var coordinate = viewModel.lastClickedCoordinateProperty().get();
                    return (coordinate != null) ? coordinate.toDisplayString() : valueUnknown;
                },
                viewModel.lastClickedCoordinateProperty()
        );
        coordinateLabel.textProperty().bind(coordinateDisplayBinding);

        String[] nameKeys = {AppLocalizationKeys.OBSERVATION_COORDINATE};
        Label[] valueLabels = {coordinateLabel};
        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
    }

}
