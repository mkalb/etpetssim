package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.core.view.SimulationLocalizationKeys;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.shared.LabBaseLayer;
import de.mkalb.etpetssim.simulations.lab.shared.LabCellText;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabDistribution;
import de.mkalb.etpetssim.simulations.lab.shared.LabGridModelType;
import de.mkalb.etpetssim.simulations.lab.shared.LabPattern;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.layout.Region;

import java.util.function.Predicate;

/**
 * Config view of the Simulation Lab.
 */
public final class LabConfigView extends AbstractConfigView<LabConfig, LabConfigViewModel> {

    // Structure
    private static final String LAB_CONFIG_GRID_MODEL_TYPE_TOOLTIP = "lab.config.gridmodeltype.tooltip";

    // Layout
    private static final String LAB_CONFIG_BASE_LAYER_TOOLTIP = "lab.config.baselayer.tooltip";
    private static final String LAB_CONFIG_CELL_TEXT_TOOLTIP = "lab.config.celltext.tooltip";
    private static final String LAB_CONFIG_COLOR_TOOLTIP = "lab.config.color.tooltip";

    // Initialization
    private static final String LAB_CONFIG_DISTRIBUTION_TOOLTIP = "lab.config.distribution.tooltip";
    private static final String LAB_CONFIG_HIGHLIGHTED_PERCENT = "lab.config.highlightedpercent";
    private static final String LAB_CONFIG_HIGHLIGHTED_PERCENT_TOOLTIP = "lab.config.highlightedpercent.tooltip";
    private static final String LAB_CONFIG_PATTERN_TOOLTIP = "lab.config.pattern.tooltip";

    public LabConfigView(LabConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        // Structure
        var gridModelTypeControl = FXComponentFactory.createLabeledChoiceComboBox(
                viewModel.gridModelTypeProperty(),
                AppLocalization.getText(LabGridModelType.labelResourceKey()),
                AppLocalization.getText(LAB_CONFIG_GRID_MODEL_TYPE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var structurePane = createStructurePane(gridModelTypeControl);

        // Layout
        var colorModeControl = FXComponentFactory.createLabeledChoiceRadioButtons(viewModel.colorModeProperty(),
                FXComponentFactory.createHBox(FXStyleClasses.CONFIG_RADIOBUTTON_BOX),
                AppLocalization.getText(LabColorMode.labelResourceKey()),
                AppLocalization.getText(LAB_CONFIG_COLOR_TOOLTIP),
                FXStyleClasses.CONFIG_RADIOBUTTON
        );
        var baseLayerControl = FXComponentFactory.createLabeledChoiceComboBox(
                viewModel.baseLayerProperty(),
                AppLocalization.getText(LabBaseLayer.labelResourceKey()),
                AppLocalization.getText(LAB_CONFIG_BASE_LAYER_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var cellTextControl = FXComponentFactory.createLabeledChoiceCheckBox(
                viewModel.cellTextProperty(),
                LabCellText.SHOWN,
                LabCellText.HIDDEN,
                AppLocalization.getText(LabCellText.labelResourceKey()),
                AppLocalization.getText(LAB_CONFIG_CELL_TEXT_TOOLTIP),
                FXStyleClasses.CONFIG_CHECKBOX
        );

        var layoutPane = createLayoutPane(colorModeControl, baseLayerControl, cellTextControl);

        // Initialization
        var seedControl = createSeedControl();
        var distributionControl = FXComponentFactory.createLabeledChoiceComboBox(
                viewModel.distributionProperty(),
                AppLocalization.getText(LabDistribution.labelResourceKey()),
                AppLocalization.getText(LAB_CONFIG_DISTRIBUTION_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
        var highlightedPercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.highlightedPercentProperty(),
                AppLocalization.getText(LAB_CONFIG_HIGHLIGHTED_PERCENT),
                formatPercentRangeTooltip(LAB_CONFIG_HIGHLIGHTED_PERCENT_TOOLTIP,
                        viewModel.highlightedPercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );
        var patternControl = FXComponentFactory.createLabeledChoiceComboBox(
                viewModel.patternProperty(),
                AppLocalization.getText(LabPattern.labelResourceKey()),
                AppLocalization.getText(LAB_CONFIG_PATTERN_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var initializationPane = createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                seedControl, distributionControl, highlightedPercentControl, patternControl);
        bindDisabledWhileUnused(highlightedPercentControl,
                createDistributionDoesNotUseBinding(LabDistribution::usesHighlightedPercent));
        bindDisabledWhileUnused(patternControl, createDistributionDoesNotUseBinding(LabDistribution::usesPattern));

        // Rules
        var neighborhoodModeControl = createNeighborhoodModeControl(viewModel.neighborhoodModeProperty());

        var rulesPane = createConfigTitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_RULES),
                neighborhoodModeControl);

        return createConfigMainBox(structurePane, layoutPane, initializationPane, rulesPane);
    }

    private BooleanBinding createDistributionDoesNotUseBinding(Predicate<LabDistribution> usedBy) {
        return Bindings.createBooleanBinding(
                () -> !usedBy.test(viewModel.distributionProperty().getValue()),
                viewModel.distributionProperty().property());
    }

}
