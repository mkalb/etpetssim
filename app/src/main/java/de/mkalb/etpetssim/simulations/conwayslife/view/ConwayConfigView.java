package de.mkalb.etpetssim.simulations.conwayslife.view;

import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
import de.mkalb.etpetssim.ui.FXComponentBuilder.LabeledControl;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class ConwayConfigView {

    private final ConwayConfigViewModel viewModel;

    public ConwayConfigView(ConwayConfigViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private TitledPane createConfigPane(String title, LabeledControl... content) {
        VBox box = new VBox();
        for (LabeledControl labeledControl : content) {
            box.getChildren().addAll(labeledControl.label(), labeledControl.control());
            labeledControl.control().disableProperty().bind(
                    viewModel.simulationStateProperty().isNotEqualTo(SimulationState.READY)
            );
        }
        box.getStyleClass().add("config-vbox");

        TitledPane pane = new TitledPane(title, box);
        pane.setCollapsible(content.length > 0);
        pane.setExpanded(content.length > 0);
        pane.setDisable(content.length == 0);
        pane.getStyleClass().add("config-titled-pane");
        return pane;
    }

    Region buildConfigRegion() {
        // TODO Optimize (ResourceBundle, min/max values, ...

        // --- Structure/Layout Group ---
        var widthControl = FXComponentBuilder.createLabeledIntSpinner(
                GridSize.MIN_SIZE, 512, 4,
                viewModel.gridWidthProperty(),
                "Grid Width: %d",
                "Set the width of the grid (8 - 16384)",
                "config-spinner"
        );

        var heightControl = FXComponentBuilder.createLabeledIntSpinner(
                GridSize.MIN_SIZE, 512, 4,
                viewModel.gridHeightProperty(),
                "Grid Height: %d",
                "Set the height of the grid (8 - 16384)",
                "config-spinner"
        );

        var sliderControl = FXComponentBuilder.createLabeledIntSlider(1, 40,
                viewModel.cellEdgeLengthProperty(), "Cell Edge Length: %.0f", "Adjust the edge length of each cell (1 - 40)", "config-slider"
        );

        TitledPane structurePane = createConfigPane(
                "Grid Structure",
                widthControl,
                heightControl,
                sliderControl
        );

        // --- Initialization Group ---
        var percentControl = FXComponentBuilder.createLabeledPercentSlider(
                0.0d, 1.0d, viewModel.alivePercentProperty(),
                "Alive %%: %.0f%%",
                "Set the initial percentage of alive cells (0% - 100%)",
                "config-slider"
        );

        TitledPane initPane = createConfigPane("Initialization", percentControl);

        // --- Rules Group ---
        TitledPane rulesPane = createConfigPane("Rules");

        // --- Main Layout as Columns ---
        HBox mainBox = new HBox(structurePane, initPane, rulesPane);
        mainBox.getStyleClass().add("config-hbox");

        return mainBox;
    }

}