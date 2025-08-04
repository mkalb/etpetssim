package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class ConwayConfigView extends AbstractConfigView<ConwayConfig, ConwayConfigViewModel> {

    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT = "conway.config.alivepercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP = "conway.config.alivepercent.tooltip";

    public ConwayConfigView(ConwayConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane();

        // --- Initialization Group ---
        var alivePercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.alivePercentProperty(),
                AppLocalization.getText(CONWAY_CONFIG_ALIVE_PERCENT),
                AppLocalization.getText(CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), alivePercentControl);

        // --- Rules Group ---
        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES));

        return createConfigMainBox(structurePane, initPane, rulesPane);
    }

    private TitledPane createStructurePane() {
        var cellShapeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.cellShapeProperty(),
                viewModel.cellShapeProperty().displayNameProvider(),
                AppLocalization.getText(CellShape.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridEdgeBehaviorControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.gridEdgeBehaviorProperty(),
                viewModel.gridEdgeBehaviorProperty().displayNameProvider(),
                AppLocalization.getText(GridEdgeBehavior.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridWidthControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.gridWidthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_WIDTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP, viewModel.gridWidthProperty().min(), viewModel.gridWidthProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var gridHeightControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.gridHeightProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_HEIGHT),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_HEIGHT_TOOLTIP, viewModel.gridHeightProperty().min(), viewModel.gridHeightProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var cellEdgeLengthControl = FXComponentFactory.createLabeledIntSlider(
                viewModel.cellEdgeLengthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, viewModel.cellEdgeLengthProperty().min(), viewModel.cellEdgeLengthProperty().max()),
                FXStyleClasses.CONFIG_SLIDER
        );

        return createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                cellShapeControl,
                gridEdgeBehaviorControl,
                gridWidthControl,
                gridHeightControl,
                cellEdgeLengthControl
        );
    }

}