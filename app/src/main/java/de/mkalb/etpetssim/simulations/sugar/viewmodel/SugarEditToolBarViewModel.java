package de.mkalb.etpetssim.simulations.sugar.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.sugar.shared.SugarAddSugarLevel;
import de.mkalb.etpetssim.ui.InputChoiceProperty;

/**
 * Edit tool bar ViewModel of Sugarscape with the level of added sugar.
 */
public final class SugarEditToolBarViewModel {

    private final InputChoiceProperty<SugarAddSugarLevel> selectedAddSugarLevel = InputChoiceProperty.ofEnum(
            SugarAddSugarLevel.LOW,
            SugarAddSugarLevel.class,
            level -> AppLocalization.getText(level.labelKey()));

    public InputChoiceProperty<SugarAddSugarLevel> selectedAddSugarLevelProperty() {
        return selectedAddSugarLevel;
    }

    public SugarAddSugarLevel getSelectedAddSugarLevel() {
        return selectedAddSugarLevel.getValue();
    }

}
