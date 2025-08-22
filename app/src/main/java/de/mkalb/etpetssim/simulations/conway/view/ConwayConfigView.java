package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.*;

public final class ConwayConfigView
        extends AbstractConfigView<ConwayConfig, ConwayConfigViewModel> {

    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT = "conway.config.alivepercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP = "conway.config.alivepercent.tooltip";
    static final String CONWAY_CONFIG_RULES = "conway.config.rules";
    static final String CONWAY_CONFIG_RULES_TOOLTIP = "conway.config.rules.tooltip";
    static final String CONWAY_CONFIG_RULES_SURVIVE = "conway.config.rules.survive";
    static final String CONWAY_CONFIG_RULES_BIRTH = "conway.config.rules.birth";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CONWAY_TRANSITIONRULES_GRIDPANE = "conway-transitionrules-gridpane";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CONWAY_TRANSITIONRULES_LABEL = "conway-transitionrules-label";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CONWAY_TRANSITIONRULES_DIGIT_LABEL = "conway-transitionrules-digit-label";

    public ConwayConfigView(ConwayConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);

        // --- Initialization Group ---
        var alivePercentControl = FXComponentFactory.createLabeledPercentSlider(
                viewModel.alivePercentProperty(),
                AppLocalization.getText(CONWAY_CONFIG_ALIVE_PERCENT),
                AppLocalization.getText(CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true, alivePercentControl);

        // --- Rules Group ---
        var rulesControl = createTransitionRulesControl(viewModel.getSurviveProperties(),
                viewModel.getBirthProperties(),
                viewModel.transitionRulesProperty(),
                AppLocalization.getText(CONWAY_CONFIG_RULES),
                AppLocalization.getText(CONWAY_CONFIG_RULES_TOOLTIP));

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true, rulesControl);

        return createConfigMainBox(structurePane, initPane, rulesPane);
    }

    private Label createRulesLabel(String text, Tooltip tooltipValue, HPos pos, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setTooltip(tooltipValue);
        GridPane.setHalignment(label, pos);

        return label;
    }

    private FXComponentFactory.LabeledControl<GridPane> createTransitionRulesControl(List<BooleanProperty> surviveProperties,
                                                                                     List<BooleanProperty> bornProperties,
                                                                                     ObjectProperty<ConwayTransitionRules> conwayRulesProperty,
                                                                                     String labelFormatString,
                                                                                     String tooltip) {
        Label label = new Label();
        label.textProperty().bind(Bindings.createStringBinding(
                () -> String.format(labelFormatString, conwayRulesProperty.get().toDisplayString()),
                conwayRulesProperty
        ));
        Tooltip tooltipValue = new Tooltip(tooltip);
        label.setTooltip(tooltipValue);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add(CONWAY_TRANSITIONRULES_GRIDPANE);

        gridPane.add(createRulesLabel("", tooltipValue, HPos.LEFT, CONWAY_TRANSITIONRULES_LABEL), 0, 0);
        gridPane.add(createRulesLabel(AppLocalization.getText(CONWAY_CONFIG_RULES_SURVIVE), tooltipValue, HPos.LEFT, CONWAY_TRANSITIONRULES_LABEL), 0, 1);
        gridPane.add(createRulesLabel(AppLocalization.getText(CONWAY_CONFIG_RULES_BIRTH), tooltipValue, HPos.LEFT, CONWAY_TRANSITIONRULES_LABEL), 0, 2);

        // Add digit labels (row 0)
        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            int index = i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT;
            Label digitLabel = createRulesLabel(String.valueOf(i), tooltipValue, HPos.CENTER, CONWAY_TRANSITIONRULES_DIGIT_LABEL);
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
            surviveCheckBox.setTooltip(tooltipValue);
            surviveCheckBox.getStyleClass().add(FXStyleClasses.CONFIG_CHECKBOX);
            gridPane.add(surviveCheckBox, 1 + index, 1);
            GridPane.setHalignment(surviveCheckBox, HPos.CENTER);
        }

        // Add Born checkboxes (row 2)
        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            int index = i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT;
            var bornCheckBox = new javafx.scene.control.CheckBox();
            bornCheckBox.selectedProperty().bindBidirectional(bornProperties.get(index));
            bornCheckBox.disableProperty().bind(
                    viewModel.maxNeighborCountProperty().lessThan(i)
            );
            bornCheckBox.setTooltip(tooltipValue);
            bornCheckBox.getStyleClass().add(FXStyleClasses.CONFIG_CHECKBOX);
            gridPane.add(bornCheckBox, 1 + index, 2);
            GridPane.setHalignment(bornCheckBox, HPos.CENTER);
        }

        return new FXComponentFactory.LabeledControl<>(label, gridPane);
    }

}