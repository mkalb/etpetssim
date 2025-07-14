package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayViewModel;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

final class ConwayConfigView {

    private final ConwayViewModel viewModel;

    ConwayConfigView(ConwayViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private TitledPane createConfigPane(String title, Node... content) {
        VBox box = new VBox(content);
        box.getStyleClass().add("config-vbox");
        TitledPane pane = new TitledPane(title, box);
        pane.setCollapsible(true);
        pane.setExpanded(false);
        pane.disableProperty().bind(
                viewModel.simulationStateProperty().isNotEqualTo(SimulationState.READY)
        );
        pane.getStyleClass().add("config-titled-pane");
        return pane;
    }

    Region buildConfigRegion() {
        // --- Structure/Layout Group ---
        Label widthLabel = new Label("Grid Width:");
        Spinner<Integer> widthSpinner = new Spinner<>(8, 16_384, viewModel.getGridWidth(), 2);
        widthLabel.setLabelFor(widthSpinner);
        widthSpinner.getStyleClass().add("config-spinner");
        widthSpinner.setTooltip(new Tooltip("Set the width of the grid (8 - 16384)"));

        Label heightLabel = new Label("Grid Height:");
        Spinner<Integer> heightSpinner = new Spinner<>(8, 16_384, viewModel.getGridHeight(), 2);
        heightLabel.setLabelFor(heightSpinner);
        heightSpinner.getStyleClass().add("config-spinner");
        heightSpinner.setTooltip(new Tooltip("Set the height of the grid (8 - 16384)"));

        Label cellLabel = new Label("Cell Edge Length:");
        Slider cellSlider = new Slider(5, 50, viewModel.getCellEdgeLength());
        cellLabel.setLabelFor(cellSlider);
        cellSlider.setShowTickLabels(true);
        cellSlider.setShowTickMarks(true);
        cellSlider.getStyleClass().add("config-slider");
        cellSlider.setTooltip(new Tooltip("Adjust the edge length of each cell (5 - 50)"));
        cellSlider.valueProperty().bindBidirectional(viewModel.cellEdgeLengthProperty());

        widthSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.gridWidthProperty().asObject());
        heightSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.gridHeightProperty().asObject());

        TitledPane structurePane = createConfigPane(
                "Grid Structure",
                widthLabel, widthSpinner,
                heightLabel, heightSpinner,
                cellLabel, cellSlider
        );

        // --- Initialization Group ---
        Label percentLabel = new Label("Alive %:");
        Slider percentSlider = new Slider(0.0, 1.0, viewModel.getAlivePercent());
        percentLabel.setLabelFor(percentSlider);
        percentSlider.setShowTickLabels(true);
        percentSlider.setShowTickMarks(true);
        percentSlider.setMajorTickUnit(0.1);
        percentSlider.setMinorTickCount(4);
        percentSlider.setBlockIncrement(0.01);
        percentSlider.getStyleClass().add("config-slider");
        percentSlider.setTooltip(new Tooltip("Set the initial percentage of alive cells (0% - 100%)"));
        percentSlider.valueProperty().bindBidirectional(viewModel.alivePercentProperty());

        TitledPane initPane = createConfigPane("Initialization", percentLabel, percentSlider);

        // --- Rules Group ---
        Label rulesLabel = new Label("Rules: (coming soon)");
        TitledPane rulesPane = createConfigPane("Rules", rulesLabel);

        // --- Main Layout as Columns ---
        HBox mainBox = new HBox(structurePane, initPane, rulesPane);
        mainBox.getStyleClass().add("config-hbox");

        return mainBox;
    }

}