package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
import de.mkalb.etpetssim.ui.FXComponentBuilder.LabeledControl;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class WatorConfigView {

    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_FISH_PERCENT = "wator.config.alivepercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_FISH_PERCENT_TOOLTIP = "wator.config.alivepercent.tooltip";

    private final WatorConfigViewModel viewModel;

    public WatorConfigView(WatorConfigViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private TitledPane createConfigPane(String title, LabeledControl... content) {
        VBox box = new VBox();
        for (LabeledControl labeledControl : content) {
            box.getChildren().addAll(labeledControl.label(), labeledControl.controlRegion());
            labeledControl.controlRegion().disableProperty().bind(
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
        // --- Structure Group ---
        var cellShapeControl = FXComponentBuilder.createLabeledEnumComboBox(
                viewModel.cellShapeProperty(),
                viewModel.cellShapeProperty().displayNameProvider(),
                AppLocalization.getText(CellShape.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridEdgeBehaviorControl = FXComponentBuilder.createLabeledEnumComboBox(
                viewModel.gridEdgeBehaviorProperty(),
                viewModel.gridEdgeBehaviorProperty().displayNameProvider(),
                AppLocalization.getText(GridEdgeBehavior.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridWidthControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridWidthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_WIDTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP, viewModel.gridWidthProperty().min(), viewModel.gridWidthProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var gridHeightControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridHeightProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_HEIGHT),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_HEIGHT_TOOLTIP, viewModel.gridHeightProperty().min(), viewModel.gridHeightProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var cellEdgeLengthControl = FXComponentBuilder.createLabeledIntSlider(
                viewModel.cellEdgeLengthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, viewModel.cellEdgeLengthProperty().min(), viewModel.cellEdgeLengthProperty().max()),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane structurePane = createConfigPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                cellShapeControl,
                gridEdgeBehaviorControl,
                gridWidthControl,
                gridHeightControl,
                cellEdgeLengthControl
        );

        // --- Initialization Group ---
        var fishPercentControl = FXComponentBuilder.createLabeledPercentSlider(
                viewModel.fishPercentProperty(),
                "Fish", // AppLocalization.getText(WATOR_CONFIG_FISH_PERCENT),
                "", // AppLocalization.getText(WATOR_CONFIG_FISH_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var sharkPercentControl = FXComponentBuilder.createLabeledPercentSlider(
                viewModel.sharkPercentProperty(),
                "Shark", // AppLocalization.getText(WATOR_CONFIG_FISH_PERCENT),
                "", // AppLocalization.getText(WATOR_CONFIG_FISH_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), fishPercentControl, sharkPercentControl);

        // --- Rules Group ---
        var sharkBirthEnergyControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.sharkBirthEnergyProperty(),
                "Shark Birth Energy", //  AppLocalization.getText(AppLocalizationKeys.CONFIG_SHARK_BIRTH_ENERGY),
                "", // AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_SHARK_BIRTH_ENERGY_TOOLTIP, viewModel.sharkBirthEnergyProperty().min(), viewModel.sharkBirthEnergyProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        TitledPane rulesPane = createConfigPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES), sharkBirthEnergyControl);

        // --- Main Layout as Columns ---
        HBox mainBox = new HBox(structurePane, initPane, rulesPane);
        mainBox.getStyleClass().add(FXStyleClasses.CONFIG_HBOX);

        return mainBox;
    }

}