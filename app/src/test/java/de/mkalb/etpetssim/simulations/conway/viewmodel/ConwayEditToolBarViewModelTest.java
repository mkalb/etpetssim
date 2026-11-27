package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints;
import de.mkalb.etpetssim.simulations.conway.model.ConwayPatterns;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayPatternChoice;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetSquare;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayUserActionContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConwayEditToolBarViewModelTest {

    private static ConwayConfig createConfig(CellShape cellShape, ConwayTransitionRules transitionRules) {
        return new ConwayConfig(
                cellShape,
                ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                ConwayConstraints.GRID_WIDTH_DEFAULT,
                ConwayConstraints.GRID_HEIGHT_DEFAULT,
                ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT,
                ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                ConwayConstraints.ALIVE_PERCENT_DEFAULT,
                ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                transitionRules
        );
    }

    private static ConwayConfig createConfig(CellShape cellShape) {
        return createConfig(cellShape, ConwayConstraints.TRANSITION_RULES_DEFAULT);
    }

    private static ConwayEditToolBarViewModel createViewModel(ConwayConfig config) {
        ConwayEditToolBarViewModel viewModel = new ConwayEditToolBarViewModel();
        viewModel.updateAvailablePatternChoices(config);
        return viewModel;
    }

    @Test
    void testSetSelectedPatternChoice() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.SQUARE));
        ConwayPatternChoice lastChoice = viewModel.availablePatternChoices().getLast();

        viewModel.setSelectedPatternChoice(lastChoice);

        assertSame(lastChoice, viewModel.getSelectedPatternChoice());
    }

    @Test
    void testSetSelectedPatternChoiceAcceptsNull() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.SQUARE));

        viewModel.setSelectedPatternChoice(null);

        assertNull(viewModel.getSelectedPatternChoice());
    }

    @Test
    void testSetSelectedPatternChoiceIgnoresUnavailableChoice() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.SQUARE));
        ConwayPatternChoice selectedChoice = viewModel.getSelectedPatternChoice();
        ConwayPatternChoice unavailableChoice = ConwayPatterns.choices().stream()
                .filter(choice -> !viewModel.availablePatternChoices().contains(choice))
                .findFirst()
                .orElseThrow();

        viewModel.setSelectedPatternChoice(unavailableChoice);

        assertSame(selectedChoice, viewModel.getSelectedPatternChoice());
    }

    @Test
    void testUpdateAvailablePatternChoices() {
        ConwayConfig config = createConfig(CellShape.SQUARE);

        ConwayEditToolBarViewModel viewModel = createViewModel(config);

        assertAll(
                () -> assertEquals(ConwayPatterns.availableChoices(config), viewModel.availablePatternChoices()),
                () -> assertSame(viewModel.availablePatternChoices().getFirst(), viewModel.getSelectedPatternChoice())
        );
    }

    @Test
    void testUpdateAvailablePatternChoicesClearsChoicesForUnsupportedConfig() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.SQUARE));

        viewModel.updateAvailablePatternChoices(createConfig(CellShape.HEXAGON));

        assertAll(
                () -> assertTrue(viewModel.availablePatternChoices().isEmpty()),
                () -> assertNull(viewModel.getSelectedPatternChoice())
        );
    }

    @Test
    void testUpdateAvailablePatternChoicesReplacesChoicesForOtherRules() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.SQUARE));
        ConwayConfig highlifeConfig = createConfig(CellShape.SQUARE,
                ConwayTransitionRules.parse(ConwayRulePresetSquare.HIGHLIFE.ruleString()));

        viewModel.updateAvailablePatternChoices(highlifeConfig);

        assertAll(
                () -> assertEquals(ConwayPatterns.availableChoices(highlifeConfig),
                        viewModel.availablePatternChoices()),
                () -> assertSame(viewModel.availablePatternChoices().getFirst(), viewModel.getSelectedPatternChoice())
        );
    }

    @Test
    void testResolveSelectedPatternContext() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.SQUARE));

        Optional<ConwayUserActionContext.PlacePattern> context = viewModel.resolveSelectedPatternContext();

        assertEquals(Optional.of(new ConwayUserActionContext.PlacePattern(viewModel.getSelectedPatternChoice())),
                context);
    }

    @Test
    void testResolveSelectedPatternContextReturnsEmptyWithoutSelection() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.HEXAGON));

        assertTrue(viewModel.resolveSelectedPatternContext().isEmpty());
    }

    @Test
    void testResolveSelectedPatternContextReturnsEmptyForUnavailableSelection() {
        ConwayEditToolBarViewModel viewModel = createViewModel(createConfig(CellShape.SQUARE));
        ConwayPatternChoice unavailableChoice = ConwayPatterns.choices().stream()
                .filter(choice -> !viewModel.availablePatternChoices().contains(choice))
                .findFirst()
                .orElseThrow();
        viewModel.selectedPatternChoiceProperty().set(unavailableChoice);

        assertTrue(viewModel.resolveSelectedPatternContext().isEmpty());
    }

}
