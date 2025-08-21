package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.wator.model.WatorConfig;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class WatorConfigView
        extends AbstractConfigView<WatorConfig, WatorConfigViewModel> {

    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_FISH_PERCENT = "wator.config.fishpercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_FISH_PERCENT_TOOLTIP = "wator.config.fishpercent.tooltip";
    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_SHARK_PERCENT = "wator.config.sharkpercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_SHARK_PERCENT_TOOLTIP = "wator.config.sharkpercent.tooltip";

    public WatorConfigView(WatorConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane();

        // --- Initialization Group ---
        var fishPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.fishPercentProperty(),
                AppLocalization.getText(WATOR_CONFIG_FISH_PERCENT),
                AppLocalization.getText(WATOR_CONFIG_FISH_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var sharkPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.sharkPercentProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_PERCENT),
                AppLocalization.getText(WATOR_CONFIG_SHARK_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), fishPercentControl, sharkPercentControl);

        // --- Rules Group ---
        var sharkBirthEnergyControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkBirthEnergyProperty(),
                "Shark Birth Energy", //  AppLocalization.getText(AppLocalizationKeys.CONFIG_SHARK_BIRTH_ENERGY),
                "", // AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_SHARK_BIRTH_ENERGY_TOOLTIP, viewModel.sharkBirthEnergyProperty().min(), viewModel.sharkBirthEnergyProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES), sharkBirthEnergyControl);

        return createConfigMainBox(structurePane, initPane, rulesPane);
    }

    private TitledPane createStructurePane() {
        var cellShapeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.cellShapeProperty(),
                viewModel.cellShapeProperty().displayNameProvider(),
                AppLocalization.getText(CellShape.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_SHAPE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridEdgeBehaviorControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.gridEdgeBehaviorProperty(),
                viewModel.gridEdgeBehaviorProperty().displayNameProvider(),
                AppLocalization.getText(GridEdgeBehavior.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_EDGE_BEHAVIOR_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridWidthControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.gridWidthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_WIDTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP, viewModel.gridWidthProperty().min(), viewModel.gridWidthProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var gridHeightControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.gridHeightProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_HEIGHT),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_HEIGHT_TOOLTIP, viewModel.gridHeightProperty().min(), viewModel.gridHeightProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var cellEdgeLengthControl = FXComponentFactory.createLabeledIntSlider(
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