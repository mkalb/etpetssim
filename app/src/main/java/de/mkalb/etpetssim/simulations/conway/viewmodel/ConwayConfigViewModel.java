package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;

import java.util.*;

public final class ConwayConfigViewModel
        extends AbstractConfigViewModel<ConwayConfig> {

    private static final GridStructureSettings STRUCTURE_SETTINGS = new GridStructureSettings(
            CellShape.SQUARE,
            GridEdgeBehavior.WRAP_XY,
            List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY),
            200,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            100,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            4,
            1,
            48);
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_AND_VERTICES;
    private static final ConwayTransitionRules DEFAULT_TRANSITION_RULES = ConwayTransitionRules.of(Set.of(2, 3), Set.of(3));

    private static final double ALIVE_PERCENT_INITIAL = 0.15d;
    private static final double ALIVE_PERCENT_MAX = 1.0d;
    private static final double ALIVE_PERCENT_MIN = 0.0d;

    private final InputDoubleProperty alivePercent = InputDoubleProperty.of(
            ALIVE_PERCENT_INITIAL,
            ALIVE_PERCENT_MIN,
            ALIVE_PERCENT_MAX);

    private final List<BooleanProperty> surviveProperties = new ArrayList<>();
    private final List<BooleanProperty> birthProperties = new ArrayList<>();
    private final ObjectProperty<ConwayTransitionRules> transitionRulesProperty = new SimpleObjectProperty<>(DEFAULT_TRANSITION_RULES);

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, STRUCTURE_SETTINGS);

        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            surviveProperties.add(new SimpleBooleanProperty(DEFAULT_TRANSITION_RULES.surviveCounts().contains(i)));
            birthProperties.add(new SimpleBooleanProperty(DEFAULT_TRANSITION_RULES.birthCounts().contains(i)));
        }

        ChangeListener<Boolean> updateListener = (_, _, _) -> updateConwayRules();
        surviveProperties.forEach(p -> p.addListener(updateListener));
        birthProperties.forEach(p -> p.addListener(updateListener));

        // HEXAGON: 23/34
        // SQUARE: 23/3
        // TRIANGLE: 45/456
    }

    private void updateConwayRules() {
        SortedSet<Integer> surviveCounts = new TreeSet<>();
        SortedSet<Integer> birthCounts = new TreeSet<>();
        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            if (surviveProperties.get(i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT).get()) {
                surviveCounts.add(i);
            }
            if (birthProperties.get(i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT).get()) {
                birthCounts.add(i);
            }
        }
        transitionRulesProperty.set(new ConwayTransitionRules(surviveCounts, birthCounts));
    }

    @Override
    public ConwayConfig getConfig() {
        return new ConwayConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                alivePercent.getValue(),
                NEIGHBORHOOD_MODE_INITIAL,
                transitionRulesProperty().get()
        );
    }

    public InputDoubleProperty alivePercentProperty() {
        return alivePercent;
    }

    public List<BooleanProperty> getSurviveProperties() {
        return surviveProperties;
    }

    public List<BooleanProperty> getBirthProperties() {
        return birthProperties;
    }

    public ObjectProperty<ConwayTransitionRules> transitionRulesProperty() {
        return transitionRulesProperty;
    }

}
