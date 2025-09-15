package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class AbstractConfigView<CON extends SimulationConfig, VM extends AbstractConfigViewModel<CON>>
        implements SimulationConfigView {

    protected final VM viewModel;

    protected AbstractConfigView(VM viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildConfigRegion();

    protected final Region createConfigMainBox(TitledPane... titledPanes) {
        HBox configHBox = new HBox(titledPanes);
        configHBox.getStyleClass().add(FXStyleClasses.CONFIG_HBOX);

        ScrollPane configScrollPane = new ScrollPane();
        configScrollPane.getStyleClass().add(FXStyleClasses.CONFIG_SCROLLPANE);
        configScrollPane.setContent(configHBox);
        configScrollPane.setFitToHeight(false);
        configScrollPane.setFitToWidth(false);
        configScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        configScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        configScrollPane.setPannable(false);

        return configScrollPane;
    }

    @SafeVarargs
    protected final TitledPane createConfigTitledPane(String title, boolean configControlsEnabledOnlyIfSimulationCanStart,
                                                      FXComponentFactory.LabeledControl<? extends Region>... content) {
        VBox box = new VBox();
        for (FXComponentFactory.LabeledControl<? extends Region> labeledControl : content) {
            box.getChildren().addAll(labeledControl.label(), labeledControl.controlRegion());
            if (configControlsEnabledOnlyIfSimulationCanStart) {
                labeledControl.controlRegion().disableProperty().bind(Bindings.createBooleanBinding(
                        () -> viewModel.getSimulationState().cannotStart(),
                        viewModel.simulationStateProperty()
                ));
            }
        }
        box.getStyleClass().add(FXStyleClasses.CONFIG_VBOX);

        TitledPane pane = new TitledPane(title, box);
        pane.setCollapsible(content.length > 0);
        pane.setExpanded(content.length > 0);
        pane.setDisable(content.length == 0);
        pane.getStyleClass().add(FXStyleClasses.CONFIG_TITLEDPANE);
        return pane;
    }

    protected final TitledPane createStructurePane(boolean configControlsEnabledOnlyIfSimulationCanStart) {
        var cellShapeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.cellShapeProperty(),
                viewModel.cellShapeProperty().displayNameProvider(),
                AppLocalization.getText(CellShape.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_SHAPE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridEdgeBehaviorControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.gridEdgeBehaviorProperty(),
                viewModel.gridEdgeBehaviorProperty().displayNameProvider(),
                AppLocalization.getText(GridEdgeBehavior.labelResourceKey()),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_EDGE_BEHAVIOR_TOOLTIP),
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

        return createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                configControlsEnabledOnlyIfSimulationCanStart,
                cellShapeControl,
                gridEdgeBehaviorControl,
                gridWidthControl,
                gridHeightControl
        );
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    protected final TitledPane createLayoutPane(boolean configControlsEnabledOnlyIfSimulationCanStart,
                                                FXComponentFactory.LabeledControl<? extends Region>... additionalContent) {
        var cellEdgeLengthControl = FXComponentFactory.createLabeledIntSlider(
                viewModel.cellEdgeLengthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, viewModel.cellEdgeLengthProperty().min(), viewModel.cellEdgeLengthProperty().max()),
                FXStyleClasses.CONFIG_SLIDER
        );

        // Only add cellDisplayModeControl if there are multiple valid values
        FXComponentFactory.LabeledControl<? extends Region> cellDisplayModeControl = null;
        if (viewModel.cellDisplayModeProperty().hasMultipleValidValues()) {
            cellDisplayModeControl = FXComponentFactory.createLabeledEnumComboBox(
                    viewModel.cellDisplayModeProperty(),
                    viewModel.cellDisplayModeProperty().displayNameProvider(),
                    AppLocalization.getText(CellDisplayMode.labelResourceKey()),
                    AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_EDGE_BEHAVIOR_TOOLTIP),
                    FXStyleClasses.CONFIG_COMBOBOX
            );
        }

        int baseControls = (cellDisplayModeControl != null) ? 2 : 1;
        var content = new FXComponentFactory.LabeledControl[baseControls + additionalContent.length];
        content[0] = cellEdgeLengthControl;
        if (cellDisplayModeControl != null) {
            content[1] = cellDisplayModeControl;
        }
        if (additionalContent.length > 0) {
            System.arraycopy(additionalContent, 0, content, baseControls, additionalContent.length);
        }

        return createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_LAYOUT),
                configControlsEnabledOnlyIfSimulationCanStart,
                content
        );
    }

}
