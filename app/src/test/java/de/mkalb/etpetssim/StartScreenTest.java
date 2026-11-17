package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.SimulationCategory;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private static List<Node> groupNodes(Region region) {
        return assertInstanceOf(VBox.class, assertInstanceOf(ScrollPane.class, region).getContent()).getChildren();
    }

    @Test
    void testCreateRegion() {
        FXTestSupport.runAndWait(() -> {
            List<SimulationType> selectedTypes = new ArrayList<>();
            List<SimulationType> expectedTypes = List.of(
                    SimulationType.ET_PETS,
                    SimulationType.WATOR,
                    SimulationType.SUGARSCAPE,
                    SimulationType.LANGTONS_ANT,
                    SimulationType.SNAKE,
                    SimulationType.REBOUNDING_ENTITIES,
                    SimulationType.CONWAYS_LIFE,
                    SimulationType.FOREST_FIRE);

            Region region = StartScreen.createRegion(selectedTypes::add);
            List<Button> cards = groupNodes(region).stream()
                    .filter(TilePane.class::isInstance)
                    .flatMap(node -> ((Pane) node).getChildren().stream())
                    .map(Button.class::cast)
                    .toList();
            for (Button card : cards) {
                card.fire();
            }

            assertEquals(expectedTypes, selectedTypes);
        });
    }

    @Test
    void testCreateRegionShowsCategoryHeadings() {
        FXTestSupport.runAndWait(() -> {
            List<String> expectedHeadings = List.of(
                    AppLocalization.getText(SimulationCategory.AGENT_AND_MOVEMENT_BASED_MODEL.resourceKey()),
                    AppLocalization.getText(SimulationCategory.CELLULAR_AUTOMATON.resourceKey()));

            List<String> headings = groupNodes(StartScreen.createRegion(_ -> {})).stream()
                    .filter(Label.class::isInstance)
                    .map(node -> ((Labeled) node).getText())
                    .toList();

            assertEquals(expectedHeadings, headings);
        });
    }

}
