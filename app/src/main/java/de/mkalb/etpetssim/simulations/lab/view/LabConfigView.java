package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabConfigViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
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
        TitledPane layoutPane = createLayoutPane();

        return createConfigMainBox(structurePane, layoutPane);
    }

    private TitledPane createLayoutPane() {
        var colorModeControl = FXComponentFactory.createLabeledEnumRadioButtons(viewModel.colorModeProperty(),
                viewModel.colorModeProperty().displayNameProvider(),
                FXComponentFactory.createHBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                "Color Mode:", // TODO Label text
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        var renderingModeControl = FXComponentFactory.createLabeledEnumRadioButtons(viewModel.renderingModeProperty(),
                viewModel.renderingModeProperty().displayNameProvider(),
                FXComponentFactory.createVBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                "Rendering Mode:", // TODO Label text
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        var strokeModeControl = FXComponentFactory.createLabeledEnumCheckBox(viewModel.strokeModeProperty(),
                LabConfig.StrokeMode.CENTERED,
                LabConfig.StrokeMode.NONE,
                "Stroke Mode:", // TODO Label text
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_CHECKBOX
        );

        return createConfigTitledPane(
                "Layout",
                false,
                colorModeControl,
                renderingModeControl,
                strokeModeControl
        );
    }

}
