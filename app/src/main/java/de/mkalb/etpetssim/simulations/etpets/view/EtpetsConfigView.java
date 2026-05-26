package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsConfig;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.layout.Region;

public final class EtpetsConfigView
        extends AbstractConfigView<EtpetsConfig, EtpetsConfigViewModel> {

    private static final String ETPETS_CONFIG_ROCK_PERCENT = "etpets.config.rockpercent";
    private static final String ETPETS_CONFIG_ROCK_PERCENT_TOOLTIP = "etpets.config.rockpercent.tooltip";
    private static final String ETPETS_CONFIG_WATER_PERCENT = "etpets.config.waterpercent";
    private static final String ETPETS_CONFIG_WATER_PERCENT_TOOLTIP = "etpets.config.waterpercent.tooltip";
    private static final String ETPETS_CONFIG_PLANT_PERCENT = "etpets.config.plantpercent";
    private static final String ETPETS_CONFIG_PLANT_PERCENT_TOOLTIP = "etpets.config.plantpercent.tooltip";
    private static final String ETPETS_CONFIG_INSECT_PERCENT = "etpets.config.insectpercent";
    private static final String ETPETS_CONFIG_INSECT_PERCENT_TOOLTIP = "etpets.config.insectpercent.tooltip";
    private static final String ETPETS_CONFIG_PET_COUNT = "etpets.config.petcount";
    private static final String ETPETS_CONFIG_PET_COUNT_TOOLTIP = "etpets.config.petcount.tooltip";

    public EtpetsConfigView(EtpetsConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        // Structure
        var structurePane = createStructurePane(true);
        // Layout
        var layoutPane = createLayoutPane(true);

        // Initialization
        var seedControl = createSeedControl();
        var rockPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.rockPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_ROCK_PERCENT),
                formatPercentRangeTooltip(ETPETS_CONFIG_ROCK_PERCENT_TOOLTIP, viewModel.rockPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );
        var waterPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.waterPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_WATER_PERCENT),
                formatPercentRangeTooltip(ETPETS_CONFIG_WATER_PERCENT_TOOLTIP, viewModel.waterPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );
        var plantPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.plantPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_PLANT_PERCENT),
                formatPercentRangeTooltip(ETPETS_CONFIG_PLANT_PERCENT_TOOLTIP, viewModel.plantPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );
        var insectPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.insectPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_INSECT_PERCENT),
                formatPercentRangeTooltip(ETPETS_CONFIG_INSECT_PERCENT_TOOLTIP, viewModel.insectPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );
        var petCountControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.petCountProperty(),
                AppLocalization.getText(ETPETS_CONFIG_PET_COUNT),
                AppLocalization.getFormattedText(ETPETS_CONFIG_PET_COUNT_TOOLTIP,
                        viewModel.petCountProperty().min(),
                        viewModel.petCountProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var initializationPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), true,
                seedControl, rockPercentControl, waterPercentControl, plantPercentControl, insectPercentControl, petCountControl);

        // Rules
        var rulesPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES), true);

        return createConfigMainBox(structurePane, layoutPane, initializationPane, rulesPane);
    }

}
