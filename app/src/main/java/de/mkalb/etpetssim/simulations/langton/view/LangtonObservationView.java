package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.*;

public final class LangtonObservationView
        extends
        AbstractObservationView<LangtonStatistics, DefaultObservationViewModel<LangtonEntity, LangtonStatistics>> {

    private static final String LANGTON_OBSERVATION_TOTAL_CELLS = "langton.observation.cells.total";
    private static final String LANGTON_OBSERVATION_ANT_CELLS = "langton.observation.cells.ant";
    private static final String LANGTON_OBSERVATION_VISITED_CELLS = "langton.observation.cells.visited";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label antCellsLabel = new Label();
    private final Label visitedCellsLabel = new Label();

    public LangtonObservationView(DefaultObservationViewModel<LangtonEntity, LangtonStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                LANGTON_OBSERVATION_TOTAL_CELLS,
                LANGTON_OBSERVATION_ANT_CELLS,
                LANGTON_OBSERVATION_VISITED_CELLS};
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                antCellsLabel,
                visitedCellsLabel};

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<LangtonStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            LangtonStatistics current = statistics.get();
            stepCountLabel.setText(integerFormat().format(current.getStepCount()));
            totalCellsLabel.setText(integerFormat().format(current.getTotalCells()));
            antCellsLabel.setText(integerFormat().format(current.getAntCells()));
            visitedCellsLabel.setText(integerFormat().format(current.getVisitedCells()));
        } else {
            setUnknownValues(stepCountLabel, totalCellsLabel, antCellsLabel, visitedCellsLabel);
        }
    }

}