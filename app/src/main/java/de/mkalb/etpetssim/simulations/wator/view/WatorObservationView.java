package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorEntity;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class WatorObservationView
        extends AbstractObservationView<WatorStatistics, DefaultObservationViewModel<WatorEntity, WatorStatistics>> {

    static final String WATOR_OBSERVATION_TOTAL_CELLS = "wator.observation.cells.total";
    static final String WATOR_OBSERVATION_MAX_FISH_CELLS = "wator.observation.cells.maxfish";
    static final String WATOR_OBSERVATION_MAX_SHARK_CELLS = "wator.observation.cells.maxshark";
    static final String WATOR_OBSERVATION_MIN_FISH_CELLS = "wator.observation.cells.minfish";
    static final String WATOR_OBSERVATION_MIN_SHARK_CELLS = "wator.observation.cells.minshark";
    static final String WATOR_OBSERVATION_FISH_CELLS = "wator.observation.cells.fish";
    static final String WATOR_OBSERVATION_SHARK_CELLS = "wator.observation.cells.shark";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label maxFishCellsLabel = new Label();
    private final Label maxSharkCellsLabel = new Label();
    private final Label minFishCellsLabel = new Label();
    private final Label minSharkCellsLabel = new Label();
    private final Label fishCellsLabel = new Label();
    private final Label sharkCellsLabel = new Label();

    private @Nullable NumberFormat intFormat;

    public WatorObservationView(DefaultObservationViewModel<WatorEntity, WatorStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                WATOR_OBSERVATION_TOTAL_CELLS,
                WATOR_OBSERVATION_MAX_FISH_CELLS,
                WATOR_OBSERVATION_MAX_SHARK_CELLS,
                WATOR_OBSERVATION_MIN_FISH_CELLS,
                WATOR_OBSERVATION_MIN_SHARK_CELLS,
                WATOR_OBSERVATION_FISH_CELLS,
                WATOR_OBSERVATION_SHARK_CELLS
        };
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                maxFishCellsLabel,
                maxSharkCellsLabel,
                minFishCellsLabel,
                minSharkCellsLabel,
                fishCellsLabel,
                sharkCellsLabel
        };

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<WatorStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            stepCountLabel.setText(intFormat.format(statistics.get().getStepCount()));
            totalCellsLabel.setText(intFormat.format(statistics.get().getTotalCells()));
            maxFishCellsLabel.setText(intFormat.format(statistics.get().getMaxFishCells()));
            maxSharkCellsLabel.setText(intFormat.format(statistics.get().getMaxSharkCells()));
            minFishCellsLabel.setText(intFormat.format(statistics.get().getMinFishCells()));
            minSharkCellsLabel.setText(intFormat.format(statistics.get().getMinSharkCells()));
            fishCellsLabel.setText(intFormat.format(statistics.get().getFishCells()));
            sharkCellsLabel.setText(intFormat.format(statistics.get().getSharkCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            maxFishCellsLabel.setText(valueUnknown);
            maxSharkCellsLabel.setText(valueUnknown);
            minFishCellsLabel.setText(valueUnknown);
            minSharkCellsLabel.setText(valueUnknown);
            fishCellsLabel.setText(valueUnknown);
            sharkCellsLabel.setText(valueUnknown);

        }
    }

}