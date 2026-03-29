package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
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
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        // --- Initialization Group ---
        var seedControl = createSeedControl();
        var verticalWallsControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.verticalWallsProperty(),
                AppLocalization.getText(REBOUNDING_CONFIG_VERTICAL_WALLS),
                AppLocalization.getFormattedText(REBOUNDING_CONFIG_VERTICAL_WALLS_TOOLTIP, viewModel.verticalWallsProperty().min(), viewModel.verticalWallsProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var movingEntityPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.movingEntityPercentProperty(),
                AppLocalization.getText(REBOUNDING_CONFIG_MOVING_ENTITY_PERCENT),
                AppLocalization.getText(REBOUNDING_CONFIG_MOVING_ENTITY_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true,
                seedControl, verticalWallsControl, movingEntityPercentControl);

        // --- Rules Group ---
        var neighborhoodModeControl = createNeighborhoodModeControl(viewModel.neighborhoodModeProperty());
        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                neighborhoodModeControl);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}
