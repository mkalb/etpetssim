package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.sugar.model.SugarConfig;
import de.mkalb.etpetssim.simulations.sugar.viewmodel.SugarConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class SugarConfigView
        extends AbstractConfigView<SugarConfig, SugarConfigViewModel> {

    // Initialization
    static final String SUGAR_CONFIG_SUGAR_PERCENT = "sugar.config.sugarpercent";
    static final String SUGAR_CONFIG_SUGAR_PERCENT_TOOLTIP = "sugar.config.sugarpercent.tooltip";
    static final String SUGAR_CONFIG_AGENT_PERCENT = "sugar.config.agentpercent";
    static final String SUGAR_CONFIG_AGENT_PERCENT_TOOLTIP = "sugar.config.agentpercent.tooltip";
    static final String SUGAR_CONFIG_MAX_SUGAR_AMOUNT = "sugar.config.maxsugaramount";
    static final String SUGAR_CONFIG_MAX_SUGAR_AMOUNT_TOOLTIP = "sugar.config.maxsugaramount.tooltip";
    static final String SUGAR_CONFIG_AGENT_INITIAL_ENERGY = "sugar.config.agentinitialenergy";
    static final String SUGAR_CONFIG_AGENT_INITIAL_ENERGY_TOOLTIP = "sugar.config.agentinitialenergy.tooltip";
    // Rules
    static final String SUGAR_CONFIG_SUGAR_REGENERATION_RATE = "sugar.config.sugarregenerationrate";
    static final String SUGAR_CONFIG_SUGAR_REGENERATION_RATE_TOOLTIP = "sugar.config.sugarregenerationrate.tooltip";
    static final String SUGAR_CONFIG_AGENT_METABOLISM_RATE = "sugar.config.agentmetabolismrate";
    static final String SUGAR_CONFIG_AGENT_METABOLISM_RATE_TOOLTIP = "sugar.config.agentmetabolismrate.tooltip";
    static final String SUGAR_CONFIG_AGENT_VISION_RANGE = "sugar.config.agentvisionrange";
    static final String SUGAR_CONFIG_AGENT_VISION_RANGE_TOOLTIP = "sugar.config.agentvisionrange.tooltip";

    public SugarConfigView(SugarConfigViewModel viewModel) {
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
        var sugarPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.sugarPercentProperty(),
                AppLocalization.getText(SUGAR_CONFIG_SUGAR_PERCENT),
                AppLocalization.getText(SUGAR_CONFIG_SUGAR_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var agentPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.agentPercentProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_PERCENT),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var maxSugarAmountControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.maxSugarAmountProperty(),
                AppLocalization.getText(SUGAR_CONFIG_MAX_SUGAR_AMOUNT),
                AppLocalization.getText(SUGAR_CONFIG_MAX_SUGAR_AMOUNT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var agentInitialEnergyControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.agentInitialEnergyProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_INITIAL_ENERGY),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_INITIAL_ENERGY_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true,
                seedControl, sugarPercentControl, agentPercentControl, maxSugarAmountControl, agentInitialEnergyControl);

        // --- Rules Group ---
        var sugarRegenerationRateControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sugarRegenerationRateProperty(),
                AppLocalization.getText(SUGAR_CONFIG_SUGAR_REGENERATION_RATE),
                AppLocalization.getText(SUGAR_CONFIG_SUGAR_REGENERATION_RATE_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var agentMetabolismRateControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.agentMetabolismRateProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_METABOLISM_RATE),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_METABOLISM_RATE_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var agentVisionRangeControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.agentVisionRangeProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_VISION_RANGE),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_VISION_RANGE_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                sugarRegenerationRateControl, agentMetabolismRateControl, agentVisionRangeControl);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}
