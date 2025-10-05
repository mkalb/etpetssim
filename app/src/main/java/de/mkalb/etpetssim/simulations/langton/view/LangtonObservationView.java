package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class LangtonObservationView
        extends
        AbstractObservationView<LangtonStatistics, DefaultObservationViewModel<LangtonEntity, LangtonStatistics>> {

    static final String LANGTON_OBSERVATION_TOTAL_CELLS = "langton.observation.cells.total";
    static final String LANGTON_OBSERVATION_ANT_CELLS = "langton.observation.cells.ant";
    static final String LANGTON_OBSERVATION_VISITED_CELLS = "langton.observation.cells.visited";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label antCellsLabel = new Label();
    private final Label visitedCellsLabel = new Label();

    private @Nullable NumberFormat intFormat;

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

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<LangtonStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            stepCountLabel.setText(intFormat.format(statistics.get().getStepCount()));
            totalCellsLabel.setText(intFormat.format(statistics.get().getTotalCells()));
            antCellsLabel.setText(intFormat.format(statistics.get().getAntCells()));
            visitedCellsLabel.setText(intFormat.format(statistics.get().getVisitedCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            antCellsLabel.setText(valueUnknown);
            visitedCellsLabel.setText(valueUnknown);
        }
    }

}