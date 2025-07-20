package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
import de.mkalb.etpetssim.ui.FXComponentBuilder.LabeledControl;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class ConwayConfigView {

    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT = "conway.config.alivepercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP = "conway.config.alivepercent.tooltip";

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
        box.getStyleClass().add(FXStyleClasses.CONFIG_VBOX);

        TitledPane pane = new TitledPane(title, box);
        pane.setCollapsible(content.length > 0);
        pane.setExpanded(content.length > 0);
        pane.setDisable(content.length == 0);
        pane.getStyleClass().add(FXStyleClasses.CONFIG_TITLEDPANE);
        return pane;
    }

    Region buildRegion() {
        // TODO Optimize min/max values, ...

        // --- Structure/Layout Group ---
        var widthControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridWidthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_WIDTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP, viewModel.gridWidthProperty().min(), viewModel.gridWidthProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var heightControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridHeightProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_HEIGHT),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_HEIGHT_TOOLTIP, viewModel.gridHeightProperty().min(), viewModel.gridHeightProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var sliderControl = FXComponentBuilder.createLabeledIntSlider(viewModel.getCellEdgeLengthMin(), viewModel.getCellEdgeLengthMax(),
                viewModel.cellEdgeLengthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, viewModel.getCellEdgeLengthMin(), viewModel.getCellEdgeLengthMax()),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane structurePane = createConfigPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                widthControl,
                heightControl,
                sliderControl
        );

        // --- Initialization Group ---
        var percentControl = FXComponentBuilder.createLabeledPercentSlider(
                viewModel.alivePercentProperty(),
                AppLocalization.getText(CONWAY_CONFIG_ALIVE_PERCENT),
                AppLocalization.getText(CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), percentControl);

        // --- Rules Group ---
        TitledPane rulesPane = createConfigPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES));

        // --- Main Layout as Columns ---
        HBox mainBox = new HBox(structurePane, initPane, rulesPane);
        mainBox.getStyleClass().add(FXStyleClasses.CONFIG_HBOX);

        return mainBox;
    }

}