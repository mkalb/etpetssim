package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.*;

public final class ConwayConfigView
        extends AbstractConfigView<ConwayConfig, ConwayConfigViewModel> {

    // Initialization
    private static final String CONWAY_CONFIG_ALIVE_PERCENT = "conway.config.alivepercent";
    private static final String CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP = "conway.config.alivepercent.tooltip";

    // Rules - Presets
    private static final String CONWAY_CONFIG_PRESET_HEXAGON = "conway.config.preset.hexagon";
    private static final String CONWAY_CONFIG_PRESET_HEXAGON_TOOLTIP = "conway.config.preset.hexagon.tooltip";
    private static final String CONWAY_CONFIG_PRESET_SQUARE = "conway.config.preset.square";
    private static final String CONWAY_CONFIG_PRESET_SQUARE_TOOLTIP = "conway.config.preset.square.tooltip";
    private static final String CONWAY_CONFIG_PRESET_TRIANGLE = "conway.config.preset.triangle";
    private static final String CONWAY_CONFIG_PRESET_TRIANGLE_TOOLTIP = "conway.config.preset.triangle.tooltip";

    // Rules
    private static final String CONWAY_CONFIG_RULES = "conway.config.rules";
    private static final String CONWAY_CONFIG_RULES_INPUT = "conway.config.rules.input";
    private static final String CONWAY_CONFIG_RULES_INPUT_CLEAR_TOOLTIP = "conway.config.rules.input.clear.tooltip";
    private static final String CONWAY_CONFIG_RULES_INPUT_PROMPT = "conway.config.rules.input.prompt";
    private static final String CONWAY_CONFIG_RULES_INPUT_TOOLTIP = "conway.config.rules.input.tooltip";
    private static final String CONWAY_CONFIG_RULES_TOOLTIP = "conway.config.rules.tooltip";
    private static final String CONWAY_CONFIG_RULES_SURVIVE = "conway.config.rules.survive";
    private static final String CONWAY_CONFIG_RULES_BIRTH = "conway.config.rules.birth";

    public ConwayConfigView(ConwayConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        // Structure
        var structurePane = createStructurePane(true);
        // Layout
        var layoutPane = createLayoutPane(true);

        // Initialization
        var seedControl = createSeedControl();
        var alivePercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.alivePercentProperty(),
                AppLocalization.getText(CONWAY_CONFIG_ALIVE_PERCENT),
                formatPercentRangeTooltip(CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP, viewModel.alivePercentProperty()),
                FXStyleClasses.CONFIG_SLIDER
        );

        var initializationPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION), true,
                seedControl, alivePercentControl);

        // Rules
        var presetTriangleControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.ruleProperty().presetTriangleProperty(),
                viewModel.ruleProperty().presetTriangleProperty().displayNameProvider(),
                AppLocalization.getText(CONWAY_CONFIG_PRESET_TRIANGLE),
                AppLocalization.getText(CONWAY_CONFIG_PRESET_TRIANGLE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
        var presetSquareControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.ruleProperty().presetSquareProperty(),
                viewModel.ruleProperty().presetSquareProperty().displayNameProvider(),
                AppLocalization.getText(CONWAY_CONFIG_PRESET_SQUARE),
                AppLocalization.getText(CONWAY_CONFIG_PRESET_SQUARE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
        var presetHexagonControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.ruleProperty().presetHexagonProperty(),
                viewModel.ruleProperty().presetHexagonProperty().displayNameProvider(),
                AppLocalization.getText(CONWAY_CONFIG_PRESET_HEXAGON),
                AppLocalization.getText(CONWAY_CONFIG_PRESET_HEXAGON_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );

        setupPresetControlBindings(presetTriangleControl.label(), presetTriangleControl.controlRegion(), CellShape.TRIANGLE);
        setupPresetControlBindings(presetSquareControl.label(), presetSquareControl.controlRegion(), CellShape.SQUARE);
        setupPresetControlBindings(presetHexagonControl.label(), presetHexagonControl.controlRegion(), CellShape.HEXAGON);

        var inputControl = FXComponentFactory.createLabeledStringTextBox(
                viewModel.ruleProperty().stringProperty(),
                viewModel.ruleProperty().labelProperty(),
                AppLocalization.getText(CONWAY_CONFIG_RULES_INPUT),
                AppLocalization.getText(CONWAY_CONFIG_RULES_INPUT_PROMPT),
                AppLocalization.getText(CONWAY_CONFIG_RULES_INPUT_TOOLTIP),
                AppLocalization.getText(CONWAY_CONFIG_RULES_INPUT_CLEAR_TOOLTIP),
                FXStyleClasses.CONFIG_TEXTBOX
        );

        var rulesControl = createTransitionRulesControl(viewModel.getSurviveProperties(),
                viewModel.getBirthProperties(),
                viewModel.transitionRulesProperty(),
                viewModel.maxNeighborCountProperty(),
                AppLocalization.getText(CONWAY_CONFIG_RULES),
                AppLocalization.getText(CONWAY_CONFIG_RULES_TOOLTIP));

        var rulesPane = createConfigTitledPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES), true,
                presetTriangleControl, presetSquareControl, presetHexagonControl, inputControl, rulesControl);

        return createConfigMainBox(structurePane, layoutPane, initializationPane, rulesPane);
    }

    private Label createRulesLabel(String text, Tooltip tooltipValue, HPos pos, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setTooltip(tooltipValue);
        GridPane.setHalignment(label, pos);

        return label;
    }

    private FXComponentFactory.LabeledControl<GridPane> createTransitionRulesControl(List<BooleanProperty> surviveProperties,
                                                                                     List<BooleanProperty> birthProperties,
                                                                                     ObjectProperty<ConwayTransitionRules> conwayRulesProperty,
                                                                                     IntegerProperty maxNeighborCountProperty,
                                                                                     String labelFormatString,
                                                                                     String tooltipFormatString) {
        Label label = new Label();
        label.textProperty().bind(Bindings.createStringBinding(
                () -> String.format(labelFormatString, conwayRulesProperty.get().toDisplayString()),
                conwayRulesProperty
        ));
        Tooltip tooltip = new Tooltip(tooltipFormatString);
        tooltip.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format(tooltipFormatString, maxNeighborCountProperty.get()),
                        maxNeighborCountProperty
                )
        );
        label.setTooltip(tooltip);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add(ConwayStyleClasses.CONWAY_TRANSITIONRULES_GRIDPANE);

        gridPane.add(createRulesLabel("", tooltip, HPos.LEFT, ConwayStyleClasses.CONWAY_TRANSITIONRULES_LABEL), 0, 0);
        gridPane.add(createRulesLabel(AppLocalization.getText(CONWAY_CONFIG_RULES_SURVIVE), tooltip, HPos.LEFT, ConwayStyleClasses.CONWAY_TRANSITIONRULES_LABEL), 0, 1);
        gridPane.add(createRulesLabel(AppLocalization.getText(CONWAY_CONFIG_RULES_BIRTH), tooltip, HPos.LEFT, ConwayStyleClasses.CONWAY_TRANSITIONRULES_LABEL), 0, 2);

        // Add digit labels (row 0)
        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            int index = i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT;
            Label digitLabel = createRulesLabel(String.valueOf(i), tooltip, HPos.CENTER, ConwayStyleClasses.CONWAY_TRANSITIONRULES_DIGIT_LABEL);
            gridPane.add(digitLabel, 1 + index, 0);
            GridPane.setHalignment(digitLabel, HPos.CENTER);
        }

        // Add Survive checkboxes (row 1)
        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            int index = i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT;
            var surviveCheckBox = new javafx.scene.control.CheckBox();
            surviveCheckBox.selectedProperty().bindBidirectional(surviveProperties.get(index));
            surviveCheckBox.disableProperty().bind(
                    viewModel.maxNeighborCountProperty().lessThan(i)
            );
            surviveCheckBox.setTooltip(tooltip);
            surviveCheckBox.getStyleClass().add(FXStyleClasses.CONFIG_CHECKBOX);
            gridPane.add(surviveCheckBox, 1 + index, 1);
            GridPane.setHalignment(surviveCheckBox, HPos.CENTER);
        }

        // Add Born checkboxes (row 2)
        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            int index = i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT;
            var bornCheckBox = new javafx.scene.control.CheckBox();
            bornCheckBox.selectedProperty().bindBidirectional(birthProperties.get(index));
            if (index == 0) { // It is not possible to be born without a neighbor.
                bornCheckBox.setDisable(true);
            } else {
                bornCheckBox.disableProperty().bind(
                        viewModel.maxNeighborCountProperty().lessThan(i)
                );
            }
            bornCheckBox.setTooltip(tooltip);
            bornCheckBox.getStyleClass().add(FXStyleClasses.CONFIG_CHECKBOX);
            gridPane.add(bornCheckBox, 1 + index, 2);
            GridPane.setHalignment(bornCheckBox, HPos.CENTER);
        }

        return new FXComponentFactory.LabeledControl<>(label, gridPane);
    }

    private void setupPresetControlBindings(Label label, Region controlRegion, CellShape shape) {
        label.visibleProperty().bind(viewModel.cellShapeProperty().property().isEqualTo(shape));
        label.managedProperty().bind(label.visibleProperty());
        controlRegion.visibleProperty().bind(viewModel.cellShapeProperty().property().isEqualTo(shape));
        controlRegion.managedProperty().bind(controlRegion.visibleProperty());
    }

}
