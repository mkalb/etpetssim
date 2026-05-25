package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.layout.Region;

public final class LabConfigView
        extends AbstractConfigView<LabConfig, LabConfigViewModel> {

    // Layout
    private static final String LAB_CONFIG_COLOR = "lab.config.color";
    private static final String LAB_CONFIG_COLOR_TOOLTIP = "lab.config.color.tooltip";

    public LabConfigView(LabConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        // Structure
        var structurePane = createStructurePane(false);

        // Layout
        var colorModeControl = FXComponentFactory.createLabeledEnumRadioButtons(viewModel.colorModeProperty(),
                viewModel.colorModeProperty().displayNameProvider(),
                FXComponentFactory.createHBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                AppLocalization.getText(LAB_CONFIG_COLOR),
                AppLocalization.getText(LAB_CONFIG_COLOR_TOOLTIP),
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        var layoutPane = createLayoutPane(false, colorModeControl);

        // Initialization
        var seedControl = createSeedControl();

        var initializationPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), false,
                seedControl);

        // Rules
        var neighborhoodModeControl = createNeighborhoodModeControl(viewModel.neighborhoodModeProperty());

        var rulesPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES), false,
                neighborhoodModeControl);

        return createConfigMainBox(structurePane, layoutPane, initializationPane, rulesPane);
    }

}
