package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class ConwayConfigView
        extends AbstractConfigView<ConwayConfig, ConwayConfigViewModel> {

    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT = "conway.config.alivepercent";
    @SuppressWarnings("SpellCheckingInspection")
    static final String CONWAY_CONFIG_ALIVE_PERCENT_TOOLTIP = "conway.config.alivepercent.tooltip";

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
        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true);

        return createConfigMainBox(structurePane, initPane, rulesPane);
    }

}