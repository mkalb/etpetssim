package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Content of the start screen, which {@link EtpetssimApplication} shows below its header.
 *
 * <p>The start screen shows one button per {@link SimulationType} that is shown on the start screen, followed by the
 * language selection. It only reports the selections to its callbacks and holds no state.
 */
final class StartScreen {

    private StartScreen() {
    }

    /**
     * Creates the region of the start screen.
     *
     * @param simulationSelection receives the simulation type of a pressed button
     * @param languageSelection   receives a selected locale that differs from the active one
     * @return a new region of the start screen
     */
    static Region createRegion(Consumer<SimulationType> simulationSelection,
                               Consumer<CountryLocale> languageSelection) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add(FXStyleClasses.START_CONTROL_VBOX);

        Label titleLabel = FXComponentFactory.createLabel(AppLocalization.getText(SimulationType.labelResourceKey()),
                FXStyleClasses.START_TITLE_LABEL);
        vbox.getChildren().add(titleLabel);

        for (SimulationType type : SimulationType.values()) {
            if (!type.isShownOnStartScreen()) {
                continue;
            }

            Button button = new Button(type.title());
            button.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
            type.subtitle().ifPresent(subtitle -> button.setTooltip(new Tooltip(subtitle)));
            button.setOnAction(_ -> simulationSelection.accept(type));

            vbox.getChildren().add(button);
        }

        var languageControl = FXComponentFactory.createLabeledChoiceComboBox(
                createLanguageChoiceProperty(languageSelection),
                AppLocalization.getText(AppLocalizationKeys.START_LANGUAGE_LABEL),
                AppLocalization.getText(AppLocalizationKeys.START_LANGUAGE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX);
        vbox.getChildren().addAll(languageControl.label(), languageControl.controlRegion());

        return vbox;
    }

    private static InputChoiceProperty<CountryLocale> createLanguageChoiceProperty(
            Consumer<CountryLocale> languageSelection) {
        Locale activeLocale = AppLocalization.locale();
        CountryLocale activeCountryLocale = AppLocalization.supportedLocales().stream()
                .filter(countryLocale -> countryLocale.countryLocale().equals(activeLocale))
                .findFirst()
                .orElseThrow();
        InputChoiceProperty<CountryLocale> languageProperty = InputChoiceProperty.ofList(activeCountryLocale,
                AppLocalization.supportedLocales(),
                countryLocale -> countryLocale.displayName() + " [" + countryLocale.localeCode() + "]");
        languageProperty.property().addListener((_, _, newLocale) -> languageSelection.accept(newLocale));
        return languageProperty;
    }

}
