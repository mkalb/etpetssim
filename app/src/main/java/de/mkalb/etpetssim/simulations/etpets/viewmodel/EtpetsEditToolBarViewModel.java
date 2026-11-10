package de.mkalb.etpetssim.simulations.etpets.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.etpets.shared.EtpetsResourceChoice;
import de.mkalb.etpetssim.simulations.etpets.shared.EtpetsTerrainChoice;
import de.mkalb.etpetssim.ui.InputChoiceProperty;

/**
 * Edit tool bar ViewModel of ET Pets with the terrain and the resource that the edit actions set.
 */
public final class EtpetsEditToolBarViewModel {

    private final InputChoiceProperty<EtpetsTerrainChoice> selectedTerrainChoice = InputChoiceProperty.ofEnum(
            EtpetsTerrainChoice.GROUND,
            EtpetsTerrainChoice.class,
            choice -> AppLocalization.getText(choice.labelKey()));
    private final InputChoiceProperty<EtpetsResourceChoice> selectedResourceChoice = InputChoiceProperty.ofEnum(
            EtpetsResourceChoice.NONE,
            EtpetsResourceChoice.class,
            choice -> AppLocalization.getText(choice.labelKey()));

    public InputChoiceProperty<EtpetsTerrainChoice> selectedTerrainChoiceProperty() {
        return selectedTerrainChoice;
    }

    public InputChoiceProperty<EtpetsResourceChoice> selectedResourceChoiceProperty() {
        return selectedResourceChoice;
    }

    public EtpetsTerrainChoice getSelectedTerrainChoice() {
        return selectedTerrainChoice.getValue();
    }

    public EtpetsResourceChoice getSelectedResourceChoice() {
        return selectedResourceChoice.getValue();
    }

}
