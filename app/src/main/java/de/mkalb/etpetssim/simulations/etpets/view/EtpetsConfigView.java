package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsConfig;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
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
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        var seedControl = createSeedControl();

        var rockPercentControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.rockPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_ROCK_PERCENT),
                AppLocalization.getText(ETPETS_CONFIG_ROCK_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SPINNER
        );
        var waterPercentControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.waterPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_WATER_PERCENT),
                AppLocalization.getText(ETPETS_CONFIG_WATER_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SPINNER
        );
        var plantPercentControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.plantPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_PLANT_PERCENT),
                AppLocalization.getText(ETPETS_CONFIG_PLANT_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SPINNER
        );
        var insectPercentControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.insectPercentProperty(),
                AppLocalization.getText(ETPETS_CONFIG_INSECT_PERCENT),
                AppLocalization.getText(ETPETS_CONFIG_INSECT_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SPINNER
        );
        var petCountControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.petCountProperty(),
                AppLocalization.getText(ETPETS_CONFIG_PET_COUNT),
                AppLocalization.getFormattedText(ETPETS_CONFIG_PET_COUNT_TOOLTIP,
                        viewModel.petCountProperty().min(),
                        viewModel.petCountProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        TitledPane initPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true,
                seedControl,
                rockPercentControl,
                waterPercentControl,
                plantPercentControl,
                insectPercentControl,
                petCountControl
        );

        TitledPane rulesPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true
        );

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}

