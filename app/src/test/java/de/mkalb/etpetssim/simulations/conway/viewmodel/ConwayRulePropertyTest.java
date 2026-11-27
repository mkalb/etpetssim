package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetHexagon;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetSquare;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetTriangle;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ConwayRulePropertyTest {

    private static List<ConwayTransitionRules> recordRules(ConwayRuleProperty ruleProperty) {
        List<ConwayTransitionRules> recordedRules = new ArrayList<>();
        ruleProperty.setRulesChangedListener(recordedRules::add);
        return recordedRules;
    }

    @Test
    void testConstructor() {
        ConwayRuleProperty ruleProperty = new ConwayRuleProperty();

        assertAll(
                () -> assertEquals("", ruleProperty.stringProperty().get()),
                () -> assertEquals("", ruleProperty.labelProperty().get()),
                () -> assertEquals(ConwayRulePresetTriangle.EMPTY,
                        ruleProperty.presetTriangleProperty().property().getValue()),
                () -> assertEquals(ConwayRulePresetSquare.EMPTY,
                        ruleProperty.presetSquareProperty().property().getValue()),
                () -> assertEquals(ConwayRulePresetHexagon.EMPTY,
                        ruleProperty.presetHexagonProperty().property().getValue())
        );
    }

    @Test
    void testSetRulesChangedListener() {
        ConwayRuleProperty ruleProperty = new ConwayRuleProperty();
        List<ConwayTransitionRules> recordedRules = recordRules(ruleProperty);

        ruleProperty.stringProperty().set("23/3");
        ruleProperty.stringProperty().set("B36/S23");

        assertEquals(List.of(ConwayTransitionRules.parse("23/3"), ConwayTransitionRules.parse("23/36")),
                recordedRules);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "23", "B3/23", "2 3/3"})
    void testSetRulesChangedListenerIgnoresBlankAndInvalidInput(String input) {
        ConwayRuleProperty ruleProperty = new ConwayRuleProperty();
        ruleProperty.stringProperty().set("1/1");
        List<ConwayTransitionRules> recordedRules = recordRules(ruleProperty);

        ruleProperty.stringProperty().set(input);

        assertTrue(recordedRules.isEmpty());
    }

    @Test
    void testSetRulesChangedListenerReplacesPreviousListener() {
        ConwayRuleProperty ruleProperty = new ConwayRuleProperty();
        List<ConwayTransitionRules> firstRecordedRules = recordRules(ruleProperty);
        List<ConwayTransitionRules> secondRecordedRules = recordRules(ruleProperty);

        ruleProperty.stringProperty().set("23/3");

        assertAll(
                () -> assertTrue(firstRecordedRules.isEmpty()),
                () -> assertEquals(List.of(ConwayTransitionRules.parse("23/3")), secondRecordedRules)
        );
    }

    @Test
    void testPresetProperties() {
        ConwayRuleProperty ruleProperty = new ConwayRuleProperty();
        List<ConwayTransitionRules> recordedRules = recordRules(ruleProperty);

        ruleProperty.presetSquareProperty().property().setValue(ConwayRulePresetSquare.HIGHLIFE);
        String squareRuleString = ruleProperty.stringProperty().get();
        ruleProperty.presetHexagonProperty().property().setValue(ConwayRulePresetHexagon.HEX_LIFE);
        String hexagonRuleString = ruleProperty.stringProperty().get();
        ruleProperty.presetTriangleProperty().property().setValue(ConwayRulePresetTriangle.TRI_25_3);
        String triangleRuleString = ruleProperty.stringProperty().get();

        assertAll(
                () -> assertEquals(ConwayRulePresetSquare.HIGHLIFE.ruleString(), squareRuleString),
                () -> assertEquals(ConwayRulePresetHexagon.HEX_LIFE.ruleString(), hexagonRuleString),
                () -> assertEquals(ConwayRulePresetTriangle.TRI_25_3.ruleString(), triangleRuleString),
                () -> assertEquals(List.of(
                        ConwayTransitionRules.parse(squareRuleString),
                        ConwayTransitionRules.parse(hexagonRuleString),
                        ConwayTransitionRules.parse(triangleRuleString)), recordedRules)
        );
    }

}
