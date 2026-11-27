package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetSquare;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationState;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.TRANSITION_RULES_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class ConwayConfigViewModelTest {

    private static ConwayConfigViewModel createViewModel(CellShape cellShape) {
        ConwayConfigViewModel viewModel = new ConwayConfigViewModel(
                new SimpleObjectProperty<>(SimulationState.READY));
        viewModel.cellShapeProperty().property().setValue(cellShape);
        return viewModel;
    }

    @ParameterizedTest
    @CsvSource({
            "TRIANGLE, 9",
            "SQUARE,   8",
            "HEXAGON,  6"
    })
    void testUpdateMaxNeighborCount(CellShape cellShape, int expectedMaxNeighborCount) {
        ConwayConfigViewModel viewModel = createViewModel(cellShape);

        assertEquals(expectedMaxNeighborCount, viewModel.maxNeighborCountProperty().get());
    }

    @ParameterizedTest
    @CsvSource({
            "23/36, SQUARE, 23/36",
            "B36/S23, SQUARE, 23/36",
            "8/8, SQUARE, 8/8",
            "6/6, HEXAGON, 6/6",
            "9/9, TRIANGLE, 9/9"
    })
    void testUpdateCheckboxesFromRules(String input, CellShape cellShape, String expectedRuleString) {
        ConwayConfigViewModel viewModel = createViewModel(cellShape);

        viewModel.ruleProperty().stringProperty().set(input);

        assertEquals(ConwayTransitionRules.parse(expectedRuleString), viewModel.transitionRulesProperty().get());
    }

    @ParameterizedTest
    @CsvSource({
            "23/03, SQUARE",
            "9/3, SQUARE",
            "B9/S23, SQUARE",
            "23/78, HEXAGON",
            "7/3, HEXAGON",
            "B0/S23, TRIANGLE"
    })
    void testUpdateCheckboxesFromRulesIgnoresCountsNotAllowedForCellShape(String input, CellShape cellShape) {
        ConwayConfigViewModel viewModel = createViewModel(cellShape);

        viewModel.ruleProperty().stringProperty().set(input);

        assertEquals(TRANSITION_RULES_DEFAULT, viewModel.transitionRulesProperty().get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"23", "B3/23", "2 3/3"})
    void testUpdateCheckboxesFromRulesIgnoresParsingErrors(String input) {
        ConwayConfigViewModel viewModel = createViewModel(CellShape.SQUARE);

        viewModel.ruleProperty().stringProperty().set(input);

        assertEquals(TRANSITION_RULES_DEFAULT, viewModel.transitionRulesProperty().get());
    }

    @Test
    void testUpdateCheckboxesFromRulesKeepsCheckboxesForInvalidCounts() {
        ConwayConfigViewModel viewModel = createViewModel(CellShape.HEXAGON);
        viewModel.ruleProperty().stringProperty().set("34/2");

        viewModel.ruleProperty().stringProperty().set("34/27");

        assertEquals(ConwayTransitionRules.parse("34/2"), viewModel.transitionRulesProperty().get());
    }

    @Test
    void testUpdateRuleText() {
        ConwayConfigViewModel viewModel = createViewModel(CellShape.SQUARE);

        viewModel.getBirthProperties().get(6).set(true);
        viewModel.getSurviveProperties().get(2).set(false);

        assertEquals("3/36", viewModel.ruleProperty().stringProperty().get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"B36/S23", "b36/s23", " 23/36 ", "223/36"})
    void testUpdateRuleTextKeepsTypedInput(String input) {
        ConwayConfigViewModel viewModel = createViewModel(CellShape.SQUARE);

        viewModel.ruleProperty().stringProperty().set(input);

        assertAll(
                () -> assertEquals(input, viewModel.ruleProperty().stringProperty().get()),
                () -> assertEquals(ConwayTransitionRules.parse("23/36"), viewModel.transitionRulesProperty().get())
        );
    }

    @Test
    void testUpdateRuleTextAfterCellShapeChange() {
        ConwayConfigViewModel viewModel = createViewModel(CellShape.SQUARE);
        viewModel.ruleProperty().stringProperty().set("B378/S238");

        viewModel.cellShapeProperty().property().setValue(CellShape.HEXAGON);

        assertAll(
                () -> assertEquals("23/3", viewModel.ruleProperty().stringProperty().get()),
                () -> assertEquals(ConwayTransitionRules.parse("23/3"), viewModel.transitionRulesProperty().get())
        );
    }

    @Test
    void testUpdateRuleTextForPresetSelection() {
        ConwayConfigViewModel viewModel = createViewModel(CellShape.SQUARE);

        viewModel.ruleProperty().presetSquareProperty().property().setValue(ConwayRulePresetSquare.HIGHLIFE);

        assertAll(
                () -> assertEquals(ConwayRulePresetSquare.HIGHLIFE.ruleString(),
                        viewModel.ruleProperty().stringProperty().get()),
                () -> assertEquals(ConwayTransitionRules.parse(ConwayRulePresetSquare.HIGHLIFE.ruleString()),
                        viewModel.transitionRulesProperty().get())
        );
    }

}
