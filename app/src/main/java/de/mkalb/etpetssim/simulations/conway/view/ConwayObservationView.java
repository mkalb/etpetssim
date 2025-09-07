package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class ConwayObservationView
        extends AbstractObservationView<ConwayStatistics, DefaultObservationViewModel<ConwayEntity, ConwayStatistics>> {

    static final String CONWAY_OBSERVATION_TOTAL_CELLS = "conway.observation.cells.total";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_OBSERVATION_MAX_ALIVE_CELLS = "conway.observation.cells.maxalive";
    static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";
    static final String CONWAY_OBSERVATION_CHANGED_CELLS = "conway.observation.cells.changed";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label maxAliveCellsLabel = new Label();
    private final Label aliveCellsLabel = new Label();
    private final Label deadCellsLabel = new Label();
    private final Label changedCellsLabel = new Label();

    private @Nullable NumberFormat intFormat;

    public ConwayObservationView(DefaultObservationViewModel<ConwayEntity, ConwayStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                CONWAY_OBSERVATION_TOTAL_CELLS,
                CONWAY_OBSERVATION_MAX_ALIVE_CELLS,
                CONWAY_OBSERVATION_ALIVE_CELLS,
                CONWAY_OBSERVATION_DEAD_CELLS,
                CONWAY_OBSERVATION_CHANGED_CELLS};
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                maxAliveCellsLabel,
                aliveCellsLabel,
                deadCellsLabel,
                changedCellsLabel};

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ConwayStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            stepCountLabel.setText(intFormat.format(statistics.get().getStepCount()));
            totalCellsLabel.setText(intFormat.format(statistics.get().getTotalCells()));
            maxAliveCellsLabel.setText(intFormat.format(statistics.get().getMaxAliveCells()));
            aliveCellsLabel.setText(intFormat.format(statistics.get().getAliveCells()));
            deadCellsLabel.setText(intFormat.format(statistics.get().getDeadCells()));
            changedCellsLabel.setText(intFormat.format(statistics.get().getChangedCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            maxAliveCellsLabel.setText(valueUnknown);
            aliveCellsLabel.setText(valueUnknown);
            deadCellsLabel.setText(valueUnknown);
            changedCellsLabel.setText(valueUnknown);
        }
    }

}