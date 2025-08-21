package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayRules;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

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
    private static final ConwayRules DEFAULT_RULES = ConwayRules.of(Set.of(2, 3), Set.of(3));

    private static final double ALIVE_PERCENT_INITIAL = 0.15d;
    private static final double ALIVE_PERCENT_MAX = 1.0d;
    private static final double ALIVE_PERCENT_MIN = 0.0d;

    private final InputDoubleProperty alivePercent = InputDoubleProperty.of(
            ALIVE_PERCENT_INITIAL,
            ALIVE_PERCENT_MIN,
            ALIVE_PERCENT_MAX);
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode =
            InputEnumProperty.of(NEIGHBORHOOD_MODE_INITIAL, NeighborhoodMode.class,
                    e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final List<BooleanProperty> surviveProperties = new ArrayList<>();
    private final List<BooleanProperty> bornProperties = new ArrayList<>();

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, STRUCTURE_SETTINGS);

        for (int i = ConwayRules.MIN_NEIGHBOR_COUNT; i <= ConwayRules.MAX_NEIGHBOR_COUNT; i++) {
            surviveProperties.add(new SimpleBooleanProperty(DEFAULT_RULES.surviveCounts().contains(i)));
            bornProperties.add(new SimpleBooleanProperty(DEFAULT_RULES.birthCounts().contains(i)));
        }

        // HEXAGON: 23/34
        // SQUARE: 23/3
        // TRIANGLE: 45/456
    }

    @Override
    public ConwayConfig getConfig() {
        SortedSet<Integer> surviveCounts = new TreeSet<>();
        SortedSet<Integer> birthCounts = new TreeSet<>();
        for (int i = ConwayRules.MIN_NEIGHBOR_COUNT; i <= ConwayRules.MAX_NEIGHBOR_COUNT; i++) {
            if (surviveProperties.get(i - ConwayRules.MIN_NEIGHBOR_COUNT).get()) {
                surviveCounts.add(i);
            }
            if (bornProperties.get(i - ConwayRules.MIN_NEIGHBOR_COUNT).get()) {
                birthCounts.add(i);
            }
        }
        return new ConwayConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                alivePercent.getValue(),
                neighborhoodMode.getValue(),
                new ConwayRules(surviveCounts, birthCounts)
        );
    }

    public InputDoubleProperty alivePercentProperty() {
        return alivePercent;
    }

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

    public List<BooleanProperty> getSurviveProperties() {
        return surviveProperties;
    }

    public List<BooleanProperty> getBornProperties() {
        return bornProperties;
    }

}
