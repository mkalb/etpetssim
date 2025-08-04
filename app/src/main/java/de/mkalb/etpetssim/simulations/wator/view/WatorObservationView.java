package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public final class WatorObservationView extends AbstractObservationView<DefaultObservationViewModel<WatorStatistics>> {

    static final String WATOR_OBSERVATION_TOTAL_CELLS = "wator.observation.cells.total";
    static final String WATOR_OBSERVATION_FISH_CELLS = "wator.observation.cells.fish";
    static final String WATOR_OBSERVATION_SHARK_CELLS = "wator.observation.cells.shark";

    private final Label stepLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label fishCellsLabel = new Label();
    private final Label sharkCellsLabel = new Label();

    public WatorObservationView(DefaultObservationViewModel<WatorStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                WATOR_OBSERVATION_TOTAL_CELLS,
                WATOR_OBSERVATION_FISH_CELLS,
                WATOR_OBSERVATION_SHARK_CELLS
        };
        Label[] valueLabels = {stepLabel, totalCellsLabel, fishCellsLabel, sharkCellsLabel};

        return createObservationGrid(nameKeys, valueLabels);
    }

    void updateObservationLabels() {
        if (viewModel.getSimulationState() == SimulationState.READY) {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            fishCellsLabel.setText(valueUnknown);
            sharkCellsLabel.setText(valueUnknown);
            return;
        }
        WatorStatistics statistics = viewModel.getStatistics();

        stepLabel.setText(Long.toString(statistics.getStep()));
        totalCellsLabel.setText(Long.toString(statistics.getTotalCells()));
        fishCellsLabel.setText(Long.toString(statistics.getFishCells()));
        sharkCellsLabel.setText(Long.toString(statistics.getSharkCells()));
    }

}