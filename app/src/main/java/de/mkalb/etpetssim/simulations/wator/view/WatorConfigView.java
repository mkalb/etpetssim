package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
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
    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_SHARK_BIRTH_ENERGY = "wator.config.sharkbirthenergy";
    @SuppressWarnings("SpellCheckingInspection")
    static final String WATOR_CONFIG_SHARK_BIRTH_ENERGY_TOOLTIP = "wator.config.sharkbirthenergy.tooltip";

    public WatorConfigView(WatorConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);

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

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true,
                fishPercentControl, sharkPercentControl);

        // --- Rules Group ---
        var neighborhoodModeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.neighborhoodModeProperty(),
                viewModel.neighborhoodModeProperty().displayNameProvider(),
                AppLocalization.getText(NeighborhoodMode.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_NEIGHBORHOOD_MODE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var sharkBirthEnergyControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.sharkBirthEnergyProperty(),
                AppLocalization.getText(WATOR_CONFIG_SHARK_BIRTH_ENERGY),
                AppLocalization.getFormattedText(WATOR_CONFIG_SHARK_BIRTH_ENERGY_TOOLTIP, viewModel.sharkBirthEnergyProperty().min(), viewModel.sharkBirthEnergyProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                neighborhoodModeControl, sharkBirthEnergyControl);

        return createConfigMainBox(structurePane, initPane, rulesPane);
    }

}