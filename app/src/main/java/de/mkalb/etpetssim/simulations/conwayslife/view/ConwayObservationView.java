package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

/**
 * Displays observation data (step and alive cell count) for Conway's Game of Life.
 * Used as a UI component in the simulation view.
 *
 * @see ConwayView
 */
final class ConwayObservationView {

    private final ConwayViewModel viewModel;
    private final Label stepLabel = new Label();
    private final Label aliveCellsLabel = new Label();

    /**
     * Creates a new observation view for the given view model.
     *
     * @param viewModel the Conway view model providing simulation data
     */
    ConwayObservationView(ConwayViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Builds and returns the UI region displaying the observation data.
     *
     * @return the observation region as a {@link Region}
     */
    Region buildObservationRegion() {
        updateObservationLabels();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.getStyleClass().add("observation-grid");

        Label[] nameLabels = {new Label("Step:"), new Label("Alive:")};
        Label[] valueLabels = {stepLabel, aliveCellsLabel};

        for (int i = 0; i < nameLabels.length; i++) {
            grid.add(nameLabels[i], 0, i);
            grid.add(valueLabels[i], 1, i);
        }

        return grid;
    }

    /**
     * Updates the displayed observation values (step and alive cell count).
     */
    void updateObservationLabels() {
        if (viewModel.getSimulationState() == SimulationState.READY) {
            stepLabel.setText("-");
            aliveCellsLabel.setText("-");
            return;
        }
        long step = viewModel.getCurrentStep();
        ReadableGridModel<ConwayEntity> model = viewModel.getCurrentModel();
        long aliveCount = model.count(cell -> cell.entity().isAlive());

        stepLabel.setText(Long.toString(step));
        aliveCellsLabel.setText(Long.toString(aliveCount));
    }

}