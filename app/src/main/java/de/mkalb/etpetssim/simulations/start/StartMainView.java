package de.mkalb.etpetssim.simulations.start;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;
import de.mkalb.etpetssim.core.AppSettings;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.simulations.core.view.SimulationControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * Main view for the start screen of the Extraterrestrial Pets Simulation application.
 * <p>Implements {@link SimulationMainView} and {@link SimulationControlView} and builds a vertical list of
 * buttons for each {@link SimulationType} that is configured to be shown on the start screen. Implemented
 * simulations are clickable and switch the stage to the selected type; unimplemented ones are disabled.
 * The class encapsulates both the view and minimal control behavior; no dedicated ViewModel is required.
 */
public final class StartMainView implements SimulationMainView, SimulationControlView {

    private static final String START_LANGUAGE_LABEL = "start.language.label";
    private static final String START_LANGUAGE_TOOLTIP = "start.language.tooltip";

    private final Stage stage;
    private final BiConsumer<Stage, SimulationType> stageUpdater;

    /**
     * Creates a new start screen main view.
     *
     * @param stage        the primary stage hosting the UI
     * @param stageUpdater a callback that switches the stage to a different simulation type
     */
    public StartMainView(Stage stage,
                         BiConsumer<Stage, SimulationType> stageUpdater) {
        this.stage = stage;
        this.stageUpdater = stageUpdater;
    }

    @Override
    public Region buildMainRegion() {
        return buildControlRegion();
    }

    @Override
    public SimulationTerminationHandle shutdownSimulation() {
        return SimulationTerminationHandle.completed();
    }

    @Override
    public Region buildControlRegion() {
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
            if (type.isImplemented()) {
                type.subtitle().ifPresent(subtitle -> button.setTooltip(new Tooltip(subtitle)));
                button.setOnAction(_ -> stageUpdater.accept(stage, type));
            } else {
                button.setDisable(true);
            }

            vbox.getChildren().add(button);
        }

        var languageControl = FXComponentFactory.createLabeledChoiceComboBox(
                createLanguageChoiceProperty(),
                AppLocalization.getText(START_LANGUAGE_LABEL),
                AppLocalization.getText(START_LANGUAGE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX);
        vbox.getChildren().addAll(languageControl.label(), languageControl.controlRegion());

        return vbox;
    }

    @Override
    public void setStepCount(int stepCount) {
        // Do nothing
    }

    private InputChoiceProperty<CountryLocale> createLanguageChoiceProperty() {
        Locale activeLocale = AppLocalization.locale();
        CountryLocale activeCountryLocale = AppLocalization.supportedLocales().stream()
                .filter(countryLocale -> countryLocale.countryLocale().equals(activeLocale))
                .findFirst()
                .orElseThrow();
        InputChoiceProperty<CountryLocale> languageProperty = InputChoiceProperty.ofList(activeCountryLocale,
                AppLocalization.supportedLocales(),
                countryLocale -> countryLocale.displayName() + " [" + countryLocale.localeCode() + "]");
        languageProperty.property().addListener((_, _, newLocale) -> handleLanguageSelection(newLocale));
        return languageProperty;
    }

    /**
     * Activates the selected locale, saves it as application setting, and rebuilds the start screen in the new
     * language.
     *
     * @param countryLocale the selected locale, which differs from the active one
     */
    private void handleLanguageSelection(CountryLocale countryLocale) {
        AppLocalization.setLocale(countryLocale);
        Locale.setDefault(AppLocalization.locale());
        AppSettings.writeLocale(countryLocale);
        stageUpdater.accept(stage, SimulationType.STARTSCREEN);
    }

}
