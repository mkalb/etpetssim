package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayObservationViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class ConwayObservationView {

    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAYSLIFE_OBSERVATION_TOTAL_CELLS = "conwayslife.observation.cells.total";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAYSLIFE_OBSERVATION_ALIVE_CELLS = "conwayslife.observation.cells.alive";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAYSLIFE_OBSERVATION_MAX_ALIVE_CELLS = "conwayslife.observation.cells.maxalive";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAYSLIFE_OBSERVATION_DEAD_CELLS = "conwayslife.observation.cells.dead";

    private final ConwayObservationViewModel viewModel;

    private final Label stepLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label aliveCellsLabel = new Label();
    private final Label deadCellsLabel = new Label();
    private final Label maxAliveCellsLabel = new Label();

    public ConwayObservationView(ConwayObservationViewModel viewModel) {
        this.viewModel = viewModel;
    }

    Region buildRegion() {
        updateObservationLabels();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.getStyleClass().add(FXStyleClasses.OBSERVATION_GRID);

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                CONWAYSLIFE_OBSERVATION_TOTAL_CELLS,
                CONWAYSLIFE_OBSERVATION_ALIVE_CELLS,
                CONWAYSLIFE_OBSERVATION_MAX_ALIVE_CELLS,
                CONWAYSLIFE_OBSERVATION_DEAD_CELLS
        };
        Label[] valueLabels = {stepLabel, totalCellsLabel, aliveCellsLabel, maxAliveCellsLabel, deadCellsLabel};

        for (int i = 0; i < nameKeys.length; i++) {
            Label nameLabel = new Label(AppLocalization.getText(nameKeys[i]));
            nameLabel.getStyleClass().add(FXStyleClasses.OBSERVATION_NAME_LABEL);
            valueLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_VALUE_LABEL);

            grid.add(nameLabel, 0, i);
            grid.add(valueLabels[i], 1, i);
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