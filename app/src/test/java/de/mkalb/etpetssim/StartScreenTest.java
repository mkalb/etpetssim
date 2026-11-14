package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.control.Button;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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
    void testCreateRegion() {
        FXTestSupport.runAndWait(() -> {
            AtomicReference<SimulationType> selectedType = new AtomicReference<>();
            SimulationType expectedType = Arrays.stream(SimulationType.values())
                    .filter(type -> type.category().isPresent())
                    .findFirst()
                    .orElseThrow();

            Region region = StartScreen.createRegion(selectedType::set);
            findChild(region, Button.class, button -> button.getText().equals(expectedType.title())).fire();

            assertEquals(expectedType, selectedType.get());
        });
    }

}
