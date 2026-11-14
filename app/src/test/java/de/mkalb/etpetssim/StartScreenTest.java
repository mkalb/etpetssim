package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

@Execution(ExecutionMode.SAME_THREAD)
final class StartScreenTest {

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static <T> T findChild(Region region, Class<T> childType, Predicate<T> filter) {
        return assertInstanceOf(VBox.class, region).getChildren()
                .stream()
                .filter(childType::isInstance)
                .map(childType::cast)
                .filter(filter)
                .findFirst()
                .orElseThrow();
    }

    @Test
    void testCreateRegionReportsSimulationSelection() {
        FXTestSupport.runAndWait(() -> {
            AtomicReference<SimulationType> selectedType = new AtomicReference<>();
            AtomicReference<CountryLocale> selectedLocale = new AtomicReference<>();
            SimulationType expectedType = Arrays.stream(SimulationType.values())
                    .filter(SimulationType::isShownOnStartScreen)
                    .findFirst()
                    .orElseThrow();

            Region region = StartScreen.createRegion(selectedType::set, selectedLocale::set);
            findChild(region, Button.class, button -> button.getText().equals(expectedType.title())).fire();

            assertAll(
                    () -> assertEquals(expectedType, selectedType.get()),
                    () -> assertNull(selectedLocale.get())
            );
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testCreateRegionReportsLanguageSelection() {
        FXTestSupport.runAndWait(() -> {
            AtomicReference<SimulationType> selectedType = new AtomicReference<>();
            AtomicReference<CountryLocale> selectedLocale = new AtomicReference<>();
            CountryLocale expectedLocale = AppLocalization.supportedLocales().stream()
                    .filter(countryLocale -> !countryLocale.countryLocale().equals(AppLocalization.locale()))
                    .findFirst()
                    .orElseThrow();

            Region region = StartScreen.createRegion(selectedType::set, selectedLocale::set);
            ComboBox<CountryLocale> comboBox = findChild(region, ComboBox.class, _ -> true);
            comboBox.setValue(expectedLocale);

            assertAll(
                    () -> assertEquals(expectedLocale, selectedLocale.get()),
                    () -> assertNull(selectedType.get())
            );
        });
    }

}
