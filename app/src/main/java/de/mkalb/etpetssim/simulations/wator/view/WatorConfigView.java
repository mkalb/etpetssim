package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class WatorConfigView extends AbstractConfigView<WatorConfigViewModel> {

    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_FISH_PERCENT = "wator.config.alivepercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_FISH_PERCENT_TOOLTIP = "wator.config.alivepercent.tooltip";

    public WatorConfigView(WatorConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildRegion() {
        TitledPane structurePane = createStructurePane();

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

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), fishPercentControl, sharkPercentControl);

        // --- Rules Group ---
        var sharkBirthEnergyControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.sharkBirthEnergyProperty(),
                "Shark Birth Energy", //  AppLocalization.getText(AppLocalizationKeys.CONFIG_SHARK_BIRTH_ENERGY),
                "", // AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_SHARK_BIRTH_ENERGY_TOOLTIP, viewModel.sharkBirthEnergyProperty().min(), viewModel.sharkBirthEnergyProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES), sharkBirthEnergyControl);

        return createConfigMainBox(structurePane, initPane, rulesPane);
    }

    private TitledPane createStructurePane() {
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

        return createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                cellShapeControl,
                gridEdgeBehaviorControl,
                gridWidthControl,
                gridHeightControl,
                cellEdgeLengthControl
        );
    }

}