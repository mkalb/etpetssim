package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.ALIVE_PERCENT_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.ALIVE_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.ALIVE_PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_DISPLAY_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_EDGE_LENGTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_EDGE_LENGTH_MAX;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_EDGE_LENGTH_MIN;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_SHAPE_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_EDGE_BEHAVIOR_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_HEIGHT_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_HEIGHT_MAX;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_HEIGHT_MIN;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_HEIGHT_STEP;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_WIDTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_WIDTH_MAX;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_WIDTH_MIN;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.GRID_WIDTH_STEP;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.SEED_DEFAULT;
import static de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints.TRANSITION_RULES_DEFAULT;

public final class ConwayConfigViewModel
        extends AbstractConfigViewModel<ConwayConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CELL_SHAPE_DEFAULT,
            CELL_SHAPE_VALUES,
            GRID_EDGE_BEHAVIOR_DEFAULT,
            GRID_EDGE_BEHAVIOR_VALUES,
            GRID_WIDTH_DEFAULT,
            GRID_WIDTH_MIN,
            GRID_WIDTH_MAX,
            GRID_WIDTH_STEP,
            GRID_HEIGHT_DEFAULT,
            GRID_HEIGHT_MIN,
            GRID_HEIGHT_MAX,
            GRID_HEIGHT_STEP,
            CELL_EDGE_LENGTH_DEFAULT,
            CELL_EDGE_LENGTH_MIN,
            CELL_EDGE_LENGTH_MAX,
            CELL_DISPLAY_MODE_DEFAULT,
            CELL_DISPLAY_MODE_VALUES,
            SEED_DEFAULT
    );

    // Rules
    private final ConwayRuleProperty ruleProperty = new ConwayRuleProperty();

    // Initialization
    private final InputDoubleProperty alivePercent = InputDoubleProperty.of(
            ALIVE_PERCENT_DEFAULT,
            ALIVE_PERCENT_MIN,
            ALIVE_PERCENT_MAX);

    // Rules
    private final List<BooleanProperty> surviveProperties = new ArrayList<>();
    private final List<BooleanProperty> birthProperties = new ArrayList<>();
    private final ObjectProperty<ConwayTransitionRules> transitionRules = new SimpleObjectProperty<>(TRANSITION_RULES_DEFAULT);
    private final IntegerProperty maxNeighborCount = new SimpleIntegerProperty(0);

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);

        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            surviveProperties.add(new SimpleBooleanProperty(TRANSITION_RULES_DEFAULT.surviveCounts().contains(i)));
            birthProperties.add(new SimpleBooleanProperty(TRANSITION_RULES_DEFAULT.birthCounts().contains(i)));
        }

        ChangeListener<Boolean> updateListener = (_, _, _) -> updateTransitionRules();
        surviveProperties.forEach(p -> p.addListener(updateListener));
        birthProperties.forEach(p -> p.addListener(updateListener));

        ruleProperty.setRulesChangedListener(this::updateCheckboxesFromRules);

        maxNeighborCountProperty().addListener((_, _, _) -> disableUnusedNeighborProperties());

        cellShapeProperty().property().addListener((_, _, _) -> updateMaxNeighborCount());

        updateMaxNeighborCount();
    }

    private void updateMaxNeighborCount() {
        int maxNeighbors = Math.min(ConwayTransitionRules.MAX_NEIGHBOR_COUNT,
                CellNeighborhoods.maxNeighborCount(
                        NEIGHBORHOOD_MODE_DEFAULT,
                        cellShapeProperty().property().getValue()
                ));
        maxNeighborCount.set(maxNeighbors);
    }

    private void disableUnusedNeighborProperties() {
        int maxNeighbors = maxNeighborCount.get();
        if (maxNeighbors < ConwayTransitionRules.MAX_NEIGHBOR_COUNT) {
            for (int i = maxNeighbors + 1; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
                surviveProperties.get(i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT).set(false);
                birthProperties.get(i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT).set(false);
            }
        }
    }

    private void updateTransitionRules() {
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
        transitionRules.set(new ConwayTransitionRules(surviveCounts, birthCounts));
    }

    private void updateCheckboxesFromRules(ConwayTransitionRules rules) {
        for (int i = ConwayTransitionRules.MIN_NEIGHBOR_COUNT; i <= ConwayTransitionRules.MAX_NEIGHBOR_COUNT; i++) {
            int index = i - ConwayTransitionRules.MIN_NEIGHBOR_COUNT;
            surviveProperties.get(index).set(rules.surviveCounts().contains(i));
            birthProperties.get(index).set(rules.birthCounts().contains(i));
        }
    }

    @Override
    public ConwayConfig createConfig() {
        return new ConwayConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                alivePercent.getValue(),
                NEIGHBORHOOD_MODE_DEFAULT,
                transitionRulesProperty().getValue()
        );
    }

    public InputDoubleProperty alivePercentProperty() {
        return alivePercent;
    }

    public ConwayRuleProperty ruleProperty() {
        return ruleProperty;
    }

    public List<BooleanProperty> getSurviveProperties() {
        return surviveProperties;
    }

    public List<BooleanProperty> getBirthProperties() {
        return birthProperties;
    }

    public ObjectProperty<ConwayTransitionRules> transitionRulesProperty() {
        return transitionRules;
    }

    public IntegerProperty maxNeighborCountProperty() {
        return maxNeighborCount;
    }

}
