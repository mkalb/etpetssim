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
        pane.expandedProperty().bind(
                viewModel.simulationStateProperty().isEqualTo(SimulationState.READY)
        );
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
        widthSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.gridWidthProperty().asObject());

        Label heightLabel = new Label("Grid Height:");
        Spinner<Integer> heightSpinner = new Spinner<>(8, 16_384, viewModel.getGridHeight(), 2);
        heightSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.gridHeightProperty().asObject());

        Label cellLabel = new Label("Cell Edge Length:");
        Slider cellSlider = new Slider(5, 50, viewModel.getCellEdgeLength());
        cellSlider.setShowTickLabels(true);
        cellSlider.setShowTickMarks(true);
        cellSlider.valueProperty().bindBidirectional(viewModel.cellEdgeLengthProperty());

        TitledPane structurePane = createConfigPane("Grid Structure", widthLabel, widthSpinner, heightLabel, heightSpinner, cellLabel, cellSlider);

        // --- Initialization Group ---
        Label percentLabel = new Label("Alive %:");
        Slider percentSlider = new Slider(0.0, 1.0, viewModel.getAlivePercent());
        percentSlider.setShowTickLabels(true);
        percentSlider.setShowTickMarks(true);
        percentSlider.setMajorTickUnit(0.1);
        percentSlider.setMinorTickCount(4);
        percentSlider.setBlockIncrement(0.01);
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