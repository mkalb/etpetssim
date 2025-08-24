package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public final class ConwayObservationView
        extends AbstractObservationView<ConwayStatistics, DefaultObservationViewModel<ConwayStatistics>> {

    static final String CONWAY_OBSERVATION_TOTAL_CELLS = "conway.observation.cells.total";
    static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_OBSERVATION_MAX_ALIVE_CELLS = "conway.observation.cells.maxalive";
    static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";
    static final String CONWAY_OBSERVATION_CHANGED_CELLS = "conway.observation.cells.changed";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label aliveCellsLabel = new Label();
    private final Label deadCellsLabel = new Label();
    private final Label maxAliveCellsLabel = new Label();
    private final Label changedCellsLabel = new Label();

    public ConwayObservationView(DefaultObservationViewModel<ConwayStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                CONWAY_OBSERVATION_TOTAL_CELLS,
                CONWAY_OBSERVATION_ALIVE_CELLS,
                CONWAY_OBSERVATION_MAX_ALIVE_CELLS,
                CONWAY_OBSERVATION_DEAD_CELLS,
                CONWAY_OBSERVATION_CHANGED_CELLS
        };
        Label[] valueLabels = {stepCountLabel, totalCellsLabel, aliveCellsLabel, maxAliveCellsLabel, deadCellsLabel, changedCellsLabel};

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        if (viewModel.getSimulationState() == SimulationState.INITIAL) {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            aliveCellsLabel.setText(valueUnknown);
            maxAliveCellsLabel.setText(valueUnknown);
            deadCellsLabel.setText(valueUnknown);
            changedCellsLabel.setText(valueUnknown);
            return;
        }
        ConwayStatistics statistics = viewModel.getStatistics();

        stepCountLabel.setText(Integer.toString(statistics.getStepCount()));
        totalCellsLabel.setText(Integer.toString(statistics.getTotalCells()));
        aliveCellsLabel.setText(Integer.toString(statistics.getAliveCells()));
        maxAliveCellsLabel.setText(Integer.toString(statistics.getMaxAliveCells()));
        deadCellsLabel.setText(Integer.toString(statistics.getDeadCells()));
        changedCellsLabel.setText(Integer.toString(statistics.getChangedCells()));
    }

}