package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import de.mkalb.etpetssim.ui.InputDoubleIntRangeProperty;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for building simulation configuration views.
 *
 * @param <CON> immutable simulation config type
 * @param <VM>  config view model type
 */
public abstract class AbstractConfigView<CON extends SimulationConfig, VM extends AbstractConfigViewModel<CON>> {

    protected final VM viewModel;

    protected AbstractConfigView(VM viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Builds the configuration UI region.
     *
     * @return root region for simulation configuration
     */
    public abstract Region buildConfigRegion();

    protected final Region createConfigMainBox(TitledPane... titledPanes) {
        HBox configHBox = new HBox(titledPanes);
        configHBox.getStyleClass().add(FXStyleClasses.CONFIG_HBOX);

        Label configValidationLabel = FXComponentFactory.createLabel("", FXStyleClasses.CONFIG_VALIDATION_LABEL);
        configValidationLabel.textProperty().bind(viewModel.configValidationMessageProperty());
        configValidationLabel.visibleProperty().bind(viewModel.hasConfigValidationIssuesProperty());
        configValidationLabel.managedProperty().bind(configValidationLabel.visibleProperty());
        configValidationLabel.setWrapText(true);

        VBox configContentVBox = FXComponentFactory.createVBox(FXStyleClasses.CONFIG_CONTENT_VBOX);
        configContentVBox.getChildren().addAll(configHBox, configValidationLabel);

        ScrollPane configScrollPane = new ScrollPane();
        configScrollPane.getStyleClass().add(FXStyleClasses.CONFIG_SCROLLPANE);
        configScrollPane.setContent(configContentVBox);
        configScrollPane.setFitToHeight(false);
        configScrollPane.setFitToWidth(false);
        configScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        configScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        configScrollPane.setPannable(false);

        return configScrollPane;
    }

    /**
     * Creates a titled config pane that stacks the labels and control regions of the given controls.
     *
     * <p>The control regions are disabled while the simulation state is not startable. A pane without controls is
     * disabled and not collapsible.
     *
     * @param title   the pane title
     * @param content the labeled controls in display order
     * @return the titled pane
     */
    @SafeVarargs
    protected final TitledPane createConfigTitledPane(String title,
                                                      FXComponentFactory.LabeledControl<? extends Region>... content) {
        VBox box = new VBox();
        for (FXComponentFactory.LabeledControl<? extends Region> labeledControl : content) {
            box.getChildren().addAll(labeledControl.label(), labeledControl.controlRegion());
            labeledControl.controlRegion().disableProperty().bind(Bindings.createBooleanBinding(
                    () -> !viewModel.getSimulationState().isStartable(),
                    viewModel.simulationStateProperty()
            ));
        }
        box.getStyleClass().add(FXStyleClasses.CONFIG_VBOX);

        TitledPane pane = new TitledPane(title, box);
        pane.setCollapsible(content.length > 0);
        pane.setExpanded(content.length > 0);
        pane.setDisable(content.length == 0);
        pane.getStyleClass().add(FXStyleClasses.CONFIG_TITLEDPANE);
        return pane;
    }

    /**
     * Binds the visibility of a label and its control region to the selected cell shape.
     * Both nodes are visible and managed only while the cell shape equals the given shape.
     *
     * @param label         the label to show or hide
     * @param controlRegion the control region to show or hide
     * @param cellShape     the cell shape for which both nodes are visible
     */
    protected final void bindVisibilityToCellShape(Label label, Region controlRegion, CellShape cellShape) {
        label.visibleProperty().bind(viewModel.cellShapeProperty().property().isEqualTo(cellShape));
        label.managedProperty().bind(label.visibleProperty());
        controlRegion.visibleProperty().bind(viewModel.cellShapeProperty().property().isEqualTo(cellShape));
        controlRegion.managedProperty().bind(controlRegion.visibleProperty());
    }

    protected final TitledPane createStructurePane() {
        List<FXComponentFactory.LabeledControl<? extends Region>> content = new ArrayList<>(4);

        if (viewModel.cellShapeProperty().hasMultipleValidValues()) {
            content.add(FXComponentFactory.createLabeledChoiceComboBox(
                    viewModel.cellShapeProperty(),
                    AppLocalization.getText(CellShape.labelResourceKey()),
                    AppLocalization.getText(SimulationLocalizationKeys.CONFIG_CELL_SHAPE_TOOLTIP),
                    FXStyleClasses.CONFIG_COMBOBOX
            ));
        }

        if (viewModel.gridEdgeBehaviorProperty().hasMultipleValidValues()) {
            content.add(FXComponentFactory.createLabeledChoiceComboBox(
                    viewModel.gridEdgeBehaviorProperty(),
                    AppLocalization.getText(GridEdgeBehavior.labelResourceKey()),
                    AppLocalization.getText(SimulationLocalizationKeys.CONFIG_GRID_EDGE_BEHAVIOR_TOOLTIP),
                    FXStyleClasses.CONFIG_COMBOBOX
            ));
        }

        content.add(FXComponentFactory.createLabeledIntSpinner(
                viewModel.gridWidthProperty(),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_GRID_WIDTH),
                formatIntRangeTooltip(SimulationLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP,
                        viewModel.gridWidthProperty()),
                FXStyleClasses.CONFIG_SPINNER
        ));

        content.add(FXComponentFactory.createLabeledIntSpinner(
                viewModel.gridHeightProperty(),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_GRID_HEIGHT),
                formatIntRangeTooltip(SimulationLocalizationKeys.CONFIG_GRID_HEIGHT_TOOLTIP,
                        viewModel.gridHeightProperty()),
                FXStyleClasses.CONFIG_SPINNER
        ));

        return createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                toLabeledControlArray(content)
        );
    }

    @SafeVarargs
    protected final TitledPane createLayoutPane(
            FXComponentFactory.LabeledControl<? extends Region>... additionalContent) {
        List<FXComponentFactory.LabeledControl<? extends Region>> content =
                new ArrayList<>(2 + additionalContent.length);

        content.add(FXComponentFactory.createLabeledIntSlider(
                viewModel.cellEdgeLengthProperty(),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_CELL_EDGE_LENGTH),
                formatIntRangeTooltip(SimulationLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP,
                        viewModel.cellEdgeLengthProperty()),
                FXStyleClasses.CONFIG_SLIDER
        ));

        if (viewModel.cellDisplayModeProperty().hasMultipleValidValues()) {
            content.add(FXComponentFactory.createLabeledChoiceComboBox(
                    viewModel.cellDisplayModeProperty(),
                    AppLocalization.getText(CellDisplayMode.labelResourceKey()),
                    AppLocalization.getText(SimulationLocalizationKeys.CONFIG_CELL_DISPLAY_MODE_TOOLTIP),
                    FXStyleClasses.CONFIG_COMBOBOX
            ));
        }

        Collections.addAll(content, additionalContent);

        return createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_LAYOUT),
                toLabeledControlArray(content)
        );
    }

    protected final FXComponentFactory.LabeledControl<? extends Region> createSeedControl() {
        return FXComponentFactory.createLabeledStringTextBoxWithAdopt(
                viewModel.seedProperty().stringProperty(),
                viewModel.seedProperty().labelProperty(),
                viewModel.seedProperty().labelProperty(),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_SEED),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_SEED_PROMPT),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_SEED_TOOLTIP),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_SEED_ADOPT_TOOLTIP),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_SEED_CLEAR_TOOLTIP),
                FXStyleClasses.CONFIG_TEXTBOX
        );
    }

    protected final FXComponentFactory.LabeledControl<? extends Region> createNeighborhoodModeControl(
            InputChoiceProperty<NeighborhoodMode> neighborhoodModeProperty) {
        return FXComponentFactory.createLabeledChoiceComboBox(
                neighborhoodModeProperty,
                AppLocalization.getText(NeighborhoodMode.labelResourceKey()),
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_NEIGHBORHOOD_MODE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
    }

    /**
     * Formats a range tooltip with the bounds of the property as whole percentages of the active locale.
     *
     * @param key      the resource key of the tooltip, which receives the bounds as {@code %1$s} and {@code %2$s}
     * @param property the property whose bounds are formatted
     * @return the formatted tooltip
     */
    protected final String formatPercentRangeTooltip(String key, InputDoubleProperty property) {
        return formatPercentRangeTooltip(key, property, 0);
    }

    /**
     * Formats a range tooltip with the bounds of the property as percentages of the active locale with at most
     * {@code decimalPlaces} decimal places (e.g., {@code 0.25%} with two decimal places, but {@code 2%} instead of
     * {@code 2.00%}).
     *
     * @param key           the resource key of the tooltip, which receives the bounds as {@code %1$s} and
     *                      {@code %2$s}
     * @param property      the property whose bounds are formatted
     * @param decimalPlaces the maximum number of decimal places of the percentages
     * @return the formatted tooltip
     */
    protected final String formatPercentRangeTooltip(String key, InputDoubleProperty property, int decimalPlaces) {
        return AppLocalization.getFormattedText(key,
                AppLocalization.formatPercent(property.min(), 0, decimalPlaces),
                AppLocalization.formatPercent(property.max(), 0, decimalPlaces)
        );
    }

    protected final String formatDoubleRangeTooltip(String key, InputDoubleProperty property) {
        return AppLocalization.getFormattedText(key,
                property.min(), property.max());
    }

    protected final String formatIntRangeTooltip(String key, InputIntegerProperty property) {
        return AppLocalization.getFormattedText(key,
                property.min(), property.max());
    }

    @SuppressWarnings("SameParameterValue")
    protected final String formatIntRangeTooltip(String key, InputDoubleIntRangeProperty property) {
        return AppLocalization.getFormattedText(key,
                property.min(), property.max());
    }

    @SuppressWarnings("unchecked")
    private FXComponentFactory.LabeledControl<? extends Region>[] toLabeledControlArray(
            List<FXComponentFactory.LabeledControl<? extends Region>> content) {
        return content.toArray(new FXComponentFactory.LabeledControl[0]);
    }

}
