package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.wator.model.WatorConfig;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class WatorConfigView
        extends AbstractConfigView<WatorConfig, WatorConfigViewModel> {

    // Initialization
    static final String WATOR_CONFIG_FISH_PERCENT = "wator.config.fishpercent";
    static final String WATOR_CONFIG_FISH_PERCENT_TOOLTIP = "wator.config.fishpercent.tooltip";
    static final String WATOR_CONFIG_SHARK_PERCENT = "wator.config.sharkpercent";
    static final String WATOR_CONFIG_SHARK_PERCENT_TOOLTIP = "wator.config.sharkpercent.tooltip";

    // Rules
    static final String WATOR_CONFIG_FISH_MAX_AGE = "wator.config.fishmaxage";
    static final String WATOR_CONFIG_FISH_MAX_AGE_TOOLTIP = "wator.config.fishmaxage.tooltip";
    static final String WATOR_CONFIG_FISH_MIN_REPRODUCTION_AGE = "wator.config.fishminreproductionage";
    static final String WATOR_CONFIG_FISH_MIN_REPRODUCTION_AGE_TOOLTIP = "wator.config.fishminreproductionage.tooltip";
    static final String WATOR_CONFIG_FISH_MIN_REPRODUCTION_INTERVAL = "wator.config.fishminreproductioninterval";
    static final String WATOR_CONFIG_FISH_MIN_REPRODUCTION_INTERVAL_TOOLTIP = "wator.config.fishminreproductioninterval.tooltip";
    static final String WATOR_CONFIG_SHARK_MAX_AGE = "wator.config.sharkmaxage";
    static final String WATOR_CONFIG_SHARK_MAX_AGE_TOOLTIP = "wator.config.sharkmaxage.tooltip";
    static final String WATOR_CONFIG_SHARK_BIRTH_ENERGY = "wator.config.sharkbirthenergy";
    static final String WATOR_CONFIG_SHARK_BIRTH_ENERGY_TOOLTIP = "wator.config.sharkbirthenergy.tooltip";
    static final String WATOR_CONFIG_SHARK_ENERGY_LOSS_PER_STEP = "wator.config.sharkenergylossperstep";
    static final String WATOR_CONFIG_SHARK_ENERGY_LOSS_PER_STEP_TOOLTIP = "wator.config.sharkenergylossperstep.tooltip";
    static final String WATOR_CONFIG_SHARK_ENERGY_GAIN_PER_FISH = "wator.config.sharkenergygainperfish";
    static final String WATOR_CONFIG_SHARK_ENERGY_GAIN_PER_FISH_TOOLTIP = "wator.config.sharkenergygainperfish.tooltip";
    static final String WATOR_CONFIG_SHARK_MIN_REPRODUCTION_AGE = "wator.config.sharkminreproductionage";
    static final String WATOR_CONFIG_SHARK_MIN_REPRODUCTION_AGE_TOOLTIP = "wator.config.sharkminreproductionage.tooltip";
    static final String WATOR_CONFIG_SHARK_MIN_REPRODUCTION_ENERGY = "wator.config.sharkminreproductionenergy";
    static final String WATOR_CONFIG_SHARK_MIN_REPRODUCTION_ENERGY_TOOLTIP = "wator.config.sharkminreproductionenergy.tooltip";
    static final String WATOR_CONFIG_SHARK_MIN_REPRODUCTION_INTERVAL = "wator.config.sharkminreproductioninterval";
    static final String WATOR_CONFIG_SHARK_MIN_REPRODUCTION_INTERVAL_TOOLTIP = "wator.config.sharkminreproductioninterval.tooltip";

    static final String WATOR_CONFIG_TITLE_FISH_RULES = "wator.config.title.fishrules";
    static final String WATOR_CONFIG_TITLE_SHARK_RULES = "wator.config.title.sharkrules";

    public WatorConfigView(WatorConfigViewModel viewModel) {
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

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true,
                seedControl, fishPercentControl, sharkPercentControl);

        // --- Rules Group ---
        var neighborhoodModeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.neighborhoodModeProperty(),
                viewModel.neighborhoodModeProperty().displayNameProvider(),
                AppLocalization.getText(NeighborhoodMode.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_NEIGHBORHOOD_MODE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                neighborhoodModeControl);

        TitledPane fishRulesPane = createFishRulesPane();
        fishRulesPane.setExpanded(false);
        TitledPane sharkRulesPane = createSharkRulesPane();
        sharkRulesPane.setExpanded(false);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane, fishRulesPane, sharkRulesPane);
    }

    private TitledPane createFishRulesPane() {
        var fishMaxAgeControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.fishMaxAgeProperty(),
                AppLocalization.getText(WATOR_CONFIG_FISH_MAX_AGE),
                AppLocalization.getFormattedText(WATOR_CONFIG_FISH_MAX_AGE_TOOLTIP, viewModel.fishMaxAgeProperty().min(), viewModel.fishMaxAgeProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var fishMinReproductionAgeControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.fishMinReproductionAgeProperty(),
                AppLocalization.getText(WATOR_CONFIG_FISH_MIN_REPRODUCTION_AGE),
                AppLocalization.getFormattedText(WATOR_CONFIG_FISH_MIN_REPRODUCTION_AGE_TOOLTIP, viewModel.fishMinReproductionAgeProperty().min(), viewModel.fishMinReproductionAgeProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var fishMinReproductionIntervalControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.fishMinReproductionIntervalProperty(),
                AppLocalization.getText(WATOR_CONFIG_FISH_MIN_REPRODUCTION_INTERVAL),
                AppLocalization.getFormattedText(WATOR_CONFIG_FISH_MIN_REPRODUCTION_INTERVAL_TOOLTIP, viewModel.fishMinReproductionIntervalProperty().min(), viewModel.fishMinReproductionIntervalProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        return createConfigTitledPane(AppLocalization.getText(WATOR_CONFIG_TITLE_FISH_RULES),
                true,
                fishMaxAgeControl,
                fishMinReproductionAgeControl,
                fishMinReproductionIntervalControl);
    }

    private TitledPane createSharkRulesPane() {
        var sharkMaxAgeControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkMaxAgeProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_MAX_AGE),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_MAX_AGE_TOOLTIP, viewModel.sharkMaxAgeProperty().min(), viewModel.sharkMaxAgeProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var sharkBirthEnergyControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkBirthEnergyProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_BIRTH_ENERGY),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_BIRTH_ENERGY_TOOLTIP, viewModel.sharkBirthEnergyProperty().min(), viewModel.sharkBirthEnergyProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var sharkEnergyLossPerStepControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkEnergyLossPerStepProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_ENERGY_LOSS_PER_STEP),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_ENERGY_LOSS_PER_STEP_TOOLTIP, viewModel.sharkEnergyLossPerStepProperty().min(), viewModel.sharkEnergyLossPerStepProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var sharkEnergyGainPerFishControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkEnergyGainPerFishProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_ENERGY_GAIN_PER_FISH),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_ENERGY_GAIN_PER_FISH_TOOLTIP, viewModel.sharkEnergyGainPerFishProperty().min(), viewModel.sharkEnergyGainPerFishProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var sharkMinReproductionAgeControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkMinReproductionAgeProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_MIN_REPRODUCTION_AGE),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_MIN_REPRODUCTION_AGE_TOOLTIP, viewModel.sharkMinReproductionAgeProperty().min(), viewModel.sharkMinReproductionAgeProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var sharkMinReproductionEnergyControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkMinReproductionEnergyProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_MIN_REPRODUCTION_ENERGY),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_MIN_REPRODUCTION_ENERGY_TOOLTIP, viewModel.sharkMinReproductionEnergyProperty().min(), viewModel.sharkMinReproductionEnergyProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var sharkMinReproductionIntervalControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkMinReproductionIntervalProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_MIN_REPRODUCTION_INTERVAL),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_MIN_REPRODUCTION_INTERVAL_TOOLTIP, viewModel.sharkMinReproductionIntervalProperty().min(), viewModel.sharkMinReproductionIntervalProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        return createConfigTitledPane(AppLocalization.getText(WATOR_CONFIG_TITLE_SHARK_RULES),
                true,
                sharkMaxAgeControl,
                sharkBirthEnergyControl,
                sharkEnergyLossPerStepControl,
                sharkEnergyGainPerFishControl,
                sharkMinReproductionAgeControl,
                sharkMinReproductionEnergyControl,
                sharkMinReproductionIntervalControl);
    }

}