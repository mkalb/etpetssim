package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class LabConfigView
        extends AbstractConfigView<LabConfig, LabConfigViewModel> {

    public LabConfigView(LabConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(false);

        // Layout
        var colorModeControl = FXComponentFactory.createLabeledEnumRadioButtons(viewModel.colorModeProperty(),
                viewModel.colorModeProperty().displayNameProvider(),
                FXComponentFactory.createHBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                "Color:",
                "Draw in color or black and white.",
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        TitledPane layoutPane = createLayoutPane(false, colorModeControl);

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
        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                false,
                seedControl);

        // --- Rules Group ---
        var neighborhoodModeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.neighborhoodModeProperty(),
                viewModel.neighborhoodModeProperty().displayNameProvider(),
                AppLocalization.getText(NeighborhoodMode.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_NEIGHBORHOOD_MODE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                false,
                neighborhoodModeControl);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}
