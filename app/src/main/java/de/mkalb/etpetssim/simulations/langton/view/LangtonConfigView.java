package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class LangtonConfigView
        extends AbstractConfigView<LangtonConfig, LangtonConfigViewModel> {

    // Rules
    private static final String LANGTON_CONFIG_PRESET_TRIANGLE = "langton.config.preset.triangle";
    private static final String LANGTON_CONFIG_PRESET_TRIANGLE_TOOLTIP = "langton.config.preset.triangle.tooltip";
    private static final String LANGTON_CONFIG_PRESET_SQUARE = "langton.config.preset.square";
    private static final String LANGTON_CONFIG_PRESET_SQUARE_TOOLTIP = "langton.config.preset.square.tooltip";
    private static final String LANGTON_CONFIG_PRESET_HEXAGON = "langton.config.preset.hexagon";
    private static final String LANGTON_CONFIG_PRESET_HEXAGON_TOOLTIP = "langton.config.preset.hexagon.tooltip";
    private static final String LANGTON_CONFIG_RULE = "langton.config.rule";
    private static final String LANGTON_CONFIG_RULE_PROMPT = "langton.config.rule.prompt";
    private static final String LANGTON_CONFIG_RULE_TOOLTIP = "langton.config.rule.tooltip";
    private static final String LANGTON_CONFIG_RULE_CLEAR_TOOLTIP = "langton.config.rule.clear.tooltip";

    public LangtonConfigView(LangtonConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        // --- Rules Group ---
        var presetTriangleControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.ruleProperty().presetTriangleProperty(),
                viewModel.ruleProperty().presetTriangleProperty().displayNameProvider(),
                AppLocalization.getText(LANGTON_CONFIG_PRESET_TRIANGLE),
                AppLocalization.getText(LANGTON_CONFIG_PRESET_TRIANGLE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
        var presetSquareControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.ruleProperty().presetSquareProperty(),
                viewModel.ruleProperty().presetSquareProperty().displayNameProvider(),
                AppLocalization.getText(LANGTON_CONFIG_PRESET_SQUARE),
                AppLocalization.getText(LANGTON_CONFIG_PRESET_SQUARE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
        var presetHexagonControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.ruleProperty().presetHexagonProperty(),
                viewModel.ruleProperty().presetHexagonProperty().displayNameProvider(),
                AppLocalization.getText(LANGTON_CONFIG_PRESET_HEXAGON),
                AppLocalization.getText(LANGTON_CONFIG_PRESET_HEXAGON_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        setupPresetControlBindings(presetTriangleControl.label(), presetTriangleControl.controlRegion(), CellShape.TRIANGLE);
        setupPresetControlBindings(presetSquareControl.label(), presetSquareControl.controlRegion(), CellShape.SQUARE);
        setupPresetControlBindings(presetHexagonControl.label(), presetHexagonControl.controlRegion(), CellShape.HEXAGON);

        var ruleControl = FXComponentFactory.createLabeledStringTextBox(
                viewModel.ruleProperty().stringProperty(),
                viewModel.ruleProperty().labelProperty(),
                AppLocalization.getText(LANGTON_CONFIG_RULE),
                AppLocalization.getText(LANGTON_CONFIG_RULE_PROMPT),
                AppLocalization.getText(LANGTON_CONFIG_RULE_TOOLTIP),
                AppLocalization.getText(LANGTON_CONFIG_RULE_CLEAR_TOOLTIP),
                FXStyleClasses.CONFIG_TEXTBOX
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                presetTriangleControl, presetSquareControl, presetHexagonControl,
                ruleControl);

        return createConfigMainBox(structurePane, layoutPane, rulesPane);
    }

    private void setupPresetControlBindings(Label label, Region controlRegion, CellShape shape) {
        label.visibleProperty().bind(viewModel.cellShapeProperty().property().isEqualTo(shape));
        label.managedProperty().bind(label.visibleProperty());
        controlRegion.visibleProperty().bind(viewModel.cellShapeProperty().property().isEqualTo(shape));
        controlRegion.managedProperty().bind(controlRegion.visibleProperty());
    }

}
