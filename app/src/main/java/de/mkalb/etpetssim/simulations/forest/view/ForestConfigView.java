package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.forest.model.ForestConfig;
import de.mkalb.etpetssim.simulations.forest.viewmodel.ForestConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class ForestConfigView
        extends AbstractConfigView<ForestConfig, ForestConfigViewModel> {

    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_CONFIG_TREE_DENSITY = "forest.config.treedensity";
    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_CONFIG_TREE_DENSITY_TOOLTIP = "forest.config.treedensity.tooltip";
    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_CONFIG_TREE_GROWTH_PROBABILITY = "forest.config.treegrowthprobability";
    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_CONFIG_TREE_GROWTH_PROBABILITY_TOOLTIP = "forest.config.treegrowthprobability.tooltip";
    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_CONFIG_LIGHTNING_IGNITION_PROBABILITY = "forest.config.lightningignitionprobability";
    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_CONFIG_LIGHTNING_IGNITION_PROBABILITY_TOOLTIP = "forest.config.lightningignitionprobability.tooltip";

    public ForestConfigView(ForestConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        // --- Initialization Group ---
        var seedControl = FXComponentFactory.createLabeledStringTextBox(
                viewModel.seedProperty().stringProperty(),
                viewModel.seedProperty().labelProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED_PROMPT),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED_TOOLTIP),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED_CLEAR_TOOLTIP),
                FXStyleClasses.CONFIG_TEXTBOX
        );
        var treeDensityControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.treeDensityProperty(),
                AppLocalization.getText(FOREST_CONFIG_TREE_DENSITY),
                AppLocalization.getText(FOREST_CONFIG_TREE_DENSITY_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true, seedControl, treeDensityControl);

        // --- Rules Group ---
        var neighborhoodModeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.neighborhoodModeProperty(),
                viewModel.neighborhoodModeProperty().displayNameProvider(),
                AppLocalization.getText(NeighborhoodMode.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_NEIGHBORHOOD_MODE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
        var treeGrowthProbabilityControl = FXComponentFactory.createLabeledDoubleSlider(
                viewModel.treeGrowthProbabilityProperty(),
                ForestConfigViewModel.TREE_GROWTH_DECIMALS,
                AppLocalization.getText(FOREST_CONFIG_TREE_GROWTH_PROBABILITY),
                AppLocalization.getText(FOREST_CONFIG_TREE_GROWTH_PROBABILITY_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var lightningIgnitionProbabilityControl = FXComponentFactory.createLabeledDoubleSlider(
                viewModel.lightningIgnitionProbabilityProperty(),
                ForestConfigViewModel.LIGHTNING_IGNITION_DECIMALS,
                AppLocalization.getText(FOREST_CONFIG_LIGHTNING_IGNITION_PROBABILITY),
                AppLocalization.getText(FOREST_CONFIG_LIGHTNING_IGNITION_PROBABILITY_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                neighborhoodModeControl,
                treeGrowthProbabilityControl,
                lightningIgnitionProbabilityControl);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}