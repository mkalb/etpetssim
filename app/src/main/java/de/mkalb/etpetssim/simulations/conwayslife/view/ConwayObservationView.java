package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayObservationViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class ConwayObservationView {

    private final ConwayObservationViewModel viewModel;

    private final Label stepLabel = new Label();
    private final Label cellCountLabel = new Label();
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

        Label[] nameLabels = {
                new Label("Step:"),
                new Label("Total cells:"),
                new Label("Alive:"),
                new Label("Max alive:"),
                new Label("Dead:")
        };
        Label[] valueLabels = {stepLabel, cellCountLabel, aliveCellsLabel, maxAliveCellsLabel, deadCellsLabel};

        for (int i = 0; i < nameLabels.length; i++) {
            nameLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_NAME_LABEL);
            valueLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_VALUE_LABEL);

            grid.add(nameLabels[i], 0, i);
            grid.add(valueLabels[i], 1, i);
        }

        return grid;
    }

    void updateObservationLabels() {
        if (viewModel.getSimulationState() == SimulationState.READY) {
            stepLabel.setText("-");
            cellCountLabel.setText("-");
            aliveCellsLabel.setText("-");
            maxAliveCellsLabel.setText("-");
            deadCellsLabel.setText("-");
            return;
        }
        ConwayStatistics statistics = viewModel.getStatistics();

        stepLabel.setText(Long.toString(statistics.getStep()));
        cellCountLabel.setText(Long.toString(statistics.getTotalCells()));
        aliveCellsLabel.setText(Long.toString(statistics.getAliveCells()));
        maxAliveCellsLabel.setText(Long.toString(statistics.getMaxAliveCells()));
        deadCellsLabel.setText(Long.toString(statistics.getDeadCells()));
    }

}