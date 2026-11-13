package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.core.view.SimulationLocalizationKeys;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.layout.Region;

/**
 * Config view of Rebounding Entities.
 */
public final class ReboundingConfigView extends AbstractConfigView<ReboundingConfig, ReboundingConfigViewModel> {

    // Initialization
    private static final String REBOUNDING_CONFIG_VERTICAL_WALLS = "rebounding.config.verticalwalls";
    private static final String REBOUNDING_CONFIG_VERTICAL_WALLS_TOOLTIP = "rebounding.config.verticalwalls.tooltip";
    private static final String REBOUNDING_CONFIG_REBOUNDER_PERCENT = "rebounding.config.rebounderpercent";
    private static final String REBOUNDING_CONFIG_REBOUNDER_PERCENT_TOOLTIP =
            "rebounding.config.rebounderpercent.tooltip";

    public ReboundingConfigView(ReboundingConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        // Structure
        var structurePane = createStructurePane();
        // Layout
        var layoutPane = createLayoutPane();

        // Initialization
        var seedControl = createSeedControl();
        var verticalWallsControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.verticalWallsProperty(),
                AppLocalization.getText(REBOUNDING_CONFIG_VERTICAL_WALLS),
                formatIntRangeTooltip(REBOUNDING_CONFIG_VERTICAL_WALLS_TOOLTIP, viewModel.verticalWallsProperty()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var rebounderPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.rebounderPercentProperty(),
                AppLocalization.getText(REBOUNDING_CONFIG_REBOUNDER_PERCENT),
                formatPercentRangeTooltip(REBOUNDING_CONFIG_REBOUNDER_PERCENT_TOOLTIP,
                        viewModel.rebounderPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );

        var initializationPane = createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                seedControl, verticalWallsControl, rebounderPercentControl);

        // Rules
        var neighborhoodModeControl = createNeighborhoodModeControl(viewModel.neighborhoodModeProperty());

        var rulesPane = createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_RULES),
                neighborhoodModeControl);

        return createConfigMainBox(structurePane, layoutPane, initializationPane, rulesPane);
    }

}
