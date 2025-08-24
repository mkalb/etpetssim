package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.*;

public final class WatorObservationView
        extends AbstractObservationView<WatorStatistics, DefaultObservationViewModel<WatorStatistics>> {

    static final String WATOR_OBSERVATION_TOTAL_CELLS = "wator.observation.cells.total";
    static final String WATOR_OBSERVATION_FISH_CELLS = "wator.observation.cells.fish";
    static final String WATOR_OBSERVATION_SHARK_CELLS = "wator.observation.cells.shark";

    private final Label stepCountLabel = new Label();
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
        Label[] valueLabels = {stepCountLabel, totalCellsLabel, fishCellsLabel, sharkCellsLabel};

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<WatorStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            stepCountLabel.setText(Long.toString(statistics.get().getStepCount()));
            totalCellsLabel.setText(Long.toString(statistics.get().getTotalCells()));
            fishCellsLabel.setText(Long.toString(statistics.get().getFishCells()));
            sharkCellsLabel.setText(Long.toString(statistics.get().getSharkCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            fishCellsLabel.setText(valueUnknown);
            sharkCellsLabel.setText(valueUnknown);

        }
    }

}