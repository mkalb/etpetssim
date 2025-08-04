package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class ConwayObservationView
        extends AbstractObservationView<DefaultObservationViewModel<ConwayStatistics>> {

    static final String CONWAY_OBSERVATION_TOTAL_CELLS = "conway.observation.cells.total";
    static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_OBSERVATION_MAX_ALIVE_CELLS = "conway.observation.cells.maxalive";
    static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";

    private final Label stepLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label aliveCellsLabel = new Label();
    private final Label deadCellsLabel = new Label();
    private final Label maxAliveCellsLabel = new Label();

    public ConwayObservationView(DefaultObservationViewModel<ConwayStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        GridPane grid = new GridPane();
        grid.getStyleClass().add(FXStyleClasses.OBSERVATION_GRID);

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                CONWAY_OBSERVATION_TOTAL_CELLS,
                CONWAY_OBSERVATION_ALIVE_CELLS,
                CONWAY_OBSERVATION_MAX_ALIVE_CELLS,
                CONWAY_OBSERVATION_DEAD_CELLS
        };
        Label[] valueLabels = {stepLabel, totalCellsLabel, aliveCellsLabel, maxAliveCellsLabel, deadCellsLabel};

        for (int i = 0; i < nameKeys.length; i++) {
            Label nameLabel = new Label(AppLocalization.getText(nameKeys[i]));
            nameLabel.getStyleClass().add(FXStyleClasses.OBSERVATION_NAME_LABEL);
            valueLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_VALUE_LABEL);

            grid.add(nameLabel, 0, i);
            grid.add(valueLabels[i], 1, i);

            GridPane.setHalignment(nameLabel, HPos.LEFT);
            GridPane.setHalignment(valueLabels[i], HPos.RIGHT);
        }

        return grid;
    }

    void updateObservationLabels() {
        if (viewModel.getSimulationState() == SimulationState.READY) {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            aliveCellsLabel.setText(valueUnknown);
            maxAliveCellsLabel.setText(valueUnknown);
            deadCellsLabel.setText(valueUnknown);
            return;
        }
        ConwayStatistics statistics = viewModel.getStatistics();

        stepLabel.setText(Long.toString(statistics.getStep()));
        totalCellsLabel.setText(Long.toString(statistics.getTotalCells()));
        aliveCellsLabel.setText(Long.toString(statistics.getAliveCells()));
        maxAliveCellsLabel.setText(Long.toString(statistics.getMaxAliveCells()));
        deadCellsLabel.setText(Long.toString(statistics.getDeadCells()));
    }

}