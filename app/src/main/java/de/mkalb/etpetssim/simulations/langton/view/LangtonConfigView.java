package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonConfigViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class LangtonConfigView
        extends AbstractConfigView<LangtonConfig, LangtonConfigViewModel> {

    static final String LANGTON_CONFIG_RULE = "langton.config.rule";
    static final String LANGTON_CONFIG_RULE_PROMPT = "langton.config.rule.prompt";
    static final String LANGTON_CONFIG_RULE_TOOLTIP = "langton.config.rule.tooltip";
    static final String LANGTON_CONFIG_RULE_CLEAR_TOOLTIP = "langton.config.rule.clear.tooltip";

    public LangtonConfigView(LangtonConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        // --- Rules Group ---
        var presetControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.ruleProperty().presetProperty(),
                viewModel.ruleProperty().presetProperty().displayNameProvider(),
                "", // TODO KEY
                "", // TODO KEY
                FXStyleClasses.CONFIG_COMBOBOX
        );

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
                true, presetControl, ruleControl);

        return createConfigMainBox(structurePane, layoutPane, rulesPane);
    }

}