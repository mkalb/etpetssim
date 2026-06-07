package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingConfigViewModel;
import de.mkalb.etpetssim.ui.*;
import javafx.scene.layout.Region;

public final class ReboundingConfigView
        extends AbstractConfigView<ReboundingConfig, ReboundingConfigViewModel> {

    // Initialization
    private static final String REBOUNDING_CONFIG_VERTICAL_WALLS = "rebounding.config.verticalwalls";
    private static final String REBOUNDING_CONFIG_VERTICAL_WALLS_TOOLTIP = "rebounding.config.verticalwalls.tooltip";
    private static final String REBOUNDING_CONFIG_MOVING_ENTITY_PERCENT = "rebounding.config.movingentitypercent";
    private static final String REBOUNDING_CONFIG_MOVING_ENTITY_PERCENT_TOOLTIP = "rebounding.config.movingentitypercent.tooltip";

    public ReboundingConfigView(ReboundingConfigViewModel viewModel) {
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
        var verticalWallsControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.verticalWallsProperty(),
                AppLocalization.getText(REBOUNDING_CONFIG_VERTICAL_WALLS),
                formatIntRangeTooltip(REBOUNDING_CONFIG_VERTICAL_WALLS_TOOLTIP, viewModel.verticalWallsProperty()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var movingEntityPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.movingEntityPercentProperty(),
                AppLocalization.getText(REBOUNDING_CONFIG_MOVING_ENTITY_PERCENT),
                formatPercentRangeTooltip(REBOUNDING_CONFIG_MOVING_ENTITY_PERCENT_TOOLTIP, viewModel.movingEntityPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );

        var initializationPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), true,
                seedControl, verticalWallsControl, movingEntityPercentControl);

        // Rules
        var neighborhoodModeControl = createNeighborhoodModeControl(viewModel.neighborhoodModeProperty());

        var rulesPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES), true,
                neighborhoodModeControl);

        return createConfigMainBox(structurePane, layoutPane, initializationPane, rulesPane);
    }

}
