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

@SuppressWarnings("SpellCheckingInspection")
public final class SugarConfigView
        extends AbstractConfigView<SugarConfig, SugarConfigViewModel> {

    // Initialization
    private static final String SUGAR_CONFIG_AGENT_PERCENT = "sugar.config.agentpercent";
    private static final String SUGAR_CONFIG_AGENT_PERCENT_TOOLTIP = "sugar.config.agentpercent.tooltip";
    private static final String SUGAR_CONFIG_SUGAR_PEAKS = "sugar.config.sugarpeaks";
    private static final String SUGAR_CONFIG_SUGAR_PEAKS_TOOLTIP = "sugar.config.sugarpeaks.tooltip";
    private static final String SUGAR_CONFIG_SUGAR_RADIUS_LIMIT = "sugar.config.sugarradiuslimit";
    private static final String SUGAR_CONFIG_SUGAR_RADIUS_LIMIT_TOOLTIP = "sugar.config.sugarradiuslimit.tooltip";
    private static final String SUGAR_CONFIG_MAX_SUGAR_AMOUNT = "sugar.config.maxsugaramount";
    private static final String SUGAR_CONFIG_MAX_SUGAR_AMOUNT_TOOLTIP = "sugar.config.maxsugaramount.tooltip";
    private static final String SUGAR_CONFIG_AGENT_INITIAL_ENERGY = "sugar.config.agentinitialenergy";
    private static final String SUGAR_CONFIG_AGENT_INITIAL_ENERGY_TOOLTIP = "sugar.config.agentinitialenergy.tooltip";

    // Rules
    private static final String SUGAR_CONFIG_SUGAR_REGENERATION_RATE = "sugar.config.sugarregenerationrate";
    private static final String SUGAR_CONFIG_SUGAR_REGENERATION_RATE_TOOLTIP = "sugar.config.sugarregenerationrate.tooltip";
    private static final String SUGAR_CONFIG_AGENT_METABOLISM_RATE = "sugar.config.agentmetabolismrate";
    private static final String SUGAR_CONFIG_AGENT_METABOLISM_RATE_TOOLTIP = "sugar.config.agentmetabolismrate.tooltip";
    private static final String SUGAR_CONFIG_AGENT_VISION_RANGE = "sugar.config.agentvisionrange";
    private static final String SUGAR_CONFIG_AGENT_VISION_RANGE_TOOLTIP = "sugar.config.agentvisionrange.tooltip";
    private static final String SUGAR_CONFIG_AGENT_MAX_AGE = "sugar.config.agentmaxage";
    private static final String SUGAR_CONFIG_AGENT_MAX_AGE_TOOLTIP = "sugar.config.agentmaxage.tooltip";

    public SugarConfigView(SugarConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        // --- Initialization Group ---
        var seedControl = createSeedControl();
        var agentPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.agentPercentProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_PERCENT),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );
        var sugarPeaksControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sugarPeaksProperty(),
                AppLocalization.getText(SUGAR_CONFIG_SUGAR_PEAKS),
                AppLocalization.getFormattedText(SUGAR_CONFIG_SUGAR_PEAKS_TOOLTIP, viewModel.sugarPeaksProperty().min(), viewModel.sugarPeaksProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var sugarRadiusLimitControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sugarRadiusLimitProperty(),
                AppLocalization.getText(SUGAR_CONFIG_SUGAR_RADIUS_LIMIT),
                AppLocalization.getFormattedText(SUGAR_CONFIG_SUGAR_RADIUS_LIMIT_TOOLTIP, viewModel.sugarRadiusLimitProperty().min(), viewModel.sugarRadiusLimitProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var maxSugarAmountControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.maxSugarAmountProperty(),
                AppLocalization.getText(SUGAR_CONFIG_MAX_SUGAR_AMOUNT),
                AppLocalization.getFormattedText(SUGAR_CONFIG_MAX_SUGAR_AMOUNT_TOOLTIP, viewModel.maxSugarAmountProperty().min(), viewModel.maxSugarAmountProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var agentInitialEnergyControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.agentInitialEnergyProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_INITIAL_ENERGY),
                AppLocalization.getFormattedText(SUGAR_CONFIG_AGENT_INITIAL_ENERGY_TOOLTIP, viewModel.agentInitialEnergyProperty().min(), viewModel.agentInitialEnergyProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true,
                seedControl, agentPercentControl, sugarPeaksControl, sugarRadiusLimitControl, maxSugarAmountControl, agentInitialEnergyControl);

        // --- Rules Group ---
        var sugarRegenerationRateControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sugarRegenerationRateProperty(),
                AppLocalization.getText(SUGAR_CONFIG_SUGAR_REGENERATION_RATE),
                AppLocalization.getFormattedText(SUGAR_CONFIG_SUGAR_REGENERATION_RATE_TOOLTIP, viewModel.sugarRegenerationRateProperty().min(), viewModel.sugarRegenerationRateProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var agentMetabolismRateControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.agentMetabolismRateProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_METABOLISM_RATE),
                AppLocalization.getFormattedText(SUGAR_CONFIG_AGENT_METABOLISM_RATE_TOOLTIP, viewModel.agentMetabolismRateProperty().min(), viewModel.agentMetabolismRateProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var agentVisionRangeControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.agentVisionRangeProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_VISION_RANGE),
                AppLocalization.getFormattedText(SUGAR_CONFIG_AGENT_VISION_RANGE_TOOLTIP, viewModel.agentVisionRangeProperty().min(), viewModel.agentVisionRangeProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var agentMaxAgeControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.agentMaxAgeProperty(),
                AppLocalization.getText(SUGAR_CONFIG_AGENT_MAX_AGE),
                AppLocalization.getFormattedText(SUGAR_CONFIG_AGENT_MAX_AGE_TOOLTIP, viewModel.agentMaxAgeProperty().min(), viewModel.agentMaxAgeProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                sugarRegenerationRateControl, agentMetabolismRateControl, agentVisionRangeControl, agentMaxAgeControl);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}
