package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabConfigViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class LabConfigView extends AbstractConfigView<LabConfig, LabConfigViewModel> {

    public LabConfigView(LabConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane();
        TitledPane layoutPane = createLayoutPane();

        return createConfigMainBox(structurePane, layoutPane);
    }

    private TitledPane createStructurePane() {
        var cellShapeControl = FXComponentBuilder.createLabeledEnumComboBox(
                viewModel.cellShapeProperty(),
                viewModel.cellShapeProperty().displayNameProvider(),
                AppLocalization.getText(CellShape.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridEdgeBehaviorControl = FXComponentBuilder.createLabeledEnumComboBox(
                viewModel.gridEdgeBehaviorProperty(),
                viewModel.gridEdgeBehaviorProperty().displayNameProvider(),
                AppLocalization.getText(GridEdgeBehavior.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridWidthControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridWidthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_WIDTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP, viewModel.gridWidthProperty().min(), viewModel.gridWidthProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var gridHeightControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridHeightProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_HEIGHT),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_HEIGHT_TOOLTIP, viewModel.gridHeightProperty().min(), viewModel.gridHeightProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var cellEdgeLengthControl = FXComponentBuilder.createLabeledIntSlider(
                viewModel.cellEdgeLengthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, viewModel.cellEdgeLengthProperty().min(), viewModel.cellEdgeLengthProperty().max()),
                FXStyleClasses.CONFIG_SLIDER
        );

        return createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                false,
                cellShapeControl,
                gridEdgeBehaviorControl,
                gridWidthControl,
                gridHeightControl,
                cellEdgeLengthControl
        );
    }

    private TitledPane createLayoutPane() {
        var colorModeControl = FXComponentBuilder.createLabeledEnumRadioButtons(viewModel.colorModeProperty(),
                viewModel.colorModeProperty().displayNameProvider(),
                FXComponentBuilder.createHBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                "Color Mode:", // TODO Label text
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        var renderingModeControl = FXComponentBuilder.createLabeledEnumRadioButtons(viewModel.renderingModeProperty(),
                viewModel.renderingModeProperty().displayNameProvider(),
                FXComponentBuilder.createVBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                "Rendering Mode:", // TODO Label text
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_RADIOBUTTON
        );

        var strokeModeControl = FXComponentBuilder.createLabeledEnumCheckBox(viewModel.strokeModeProperty(),
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
