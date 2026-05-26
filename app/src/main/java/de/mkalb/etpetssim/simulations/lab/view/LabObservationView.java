package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

public final class LabObservationView
        extends AbstractObservationView<LabStatistics, DefaultObservationViewModel<LabEntity, LabStatistics>> {

    private @Nullable Region selectedCellRegion;

    public LabObservationView(DefaultObservationViewModel<LabEntity, LabStatistics> viewModel,
                              GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.lastClickedCoordinateProperty().addListener((_, _, newCoordinate) ->
                updateSelectedCellSectionVisibility(newCoordinate != null));
    }

    private void updateSelectedCellSectionVisibility(boolean visible) {
        if (selectedCellRegion != null) {
            selectedCellRegion.setManaged(visible);
            selectedCellRegion.setVisible(visible);
        }
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

        selectedCellRegion = createObservationGrid(
                new String[]{
                        AppLocalizationKeys.OBSERVATION_COORDINATE
                },
                new Label[]{
                        coordinateLabel
                }
        );
        updateSelectedCellSectionVisibility(viewModel.lastClickedCoordinateProperty().get() != null);
        return createObservationScrollPane(selectedCellRegion);
    }

    @Override
    protected void updateObservationLabels() {
    }

}
