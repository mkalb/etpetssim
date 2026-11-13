package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.core.view.SimulationLocalizationKeys;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.layout.Region;

/**
 * Config view of the Simulation Lab.
 */
public final class LabConfigView extends AbstractConfigView<LabConfig, LabConfigViewModel> {

    // Layout
    private static final String LAB_CONFIG_COLOR_TOOLTIP = "lab.config.color.tooltip";

    // Initialization
    private static final String LAB_CONFIG_HIGHLIGHTED_PERCENT = "lab.config.highlightedpercent";
    private static final String LAB_CONFIG_HIGHLIGHTED_PERCENT_TOOLTIP = "lab.config.highlightedpercent.tooltip";

    public LabConfigView(LabConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        // Structure
        var structurePane = createStructurePane();

        // Layout
        var colorModeControl = FXComponentFactory.createLabeledChoiceRadioButtons(viewModel.colorModeProperty(),
                FXComponentFactory.createHBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                AppLocalization.getText(LabColorMode.labelResourceKey()),
                AppLocalization.getText(LAB_CONFIG_COLOR_TOOLTIP),
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        var layoutPane = createLayoutPane(colorModeControl);

        // Initialization
        var seedControl = createSeedControl();
        var highlightedPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.highlightedPercentProperty(),
                AppLocalization.getText(LAB_CONFIG_HIGHLIGHTED_PERCENT),
                formatPercentRangeTooltip(LAB_CONFIG_HIGHLIGHTED_PERCENT_TOOLTIP,
                        viewModel.highlightedPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );

        var initializationPane = createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                seedControl, highlightedPercentControl);

        // Rules
        var neighborhoodModeControl = createNeighborhoodModeControl(viewModel.neighborhoodModeProperty());

        var rulesPane = createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_RULES),
                neighborhoodModeControl);

        return createConfigMainBox(structurePane, layoutPane, initializationPane, rulesPane);
    }

}
