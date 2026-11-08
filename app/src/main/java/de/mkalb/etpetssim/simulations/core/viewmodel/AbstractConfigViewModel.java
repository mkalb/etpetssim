package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import de.mkalb.etpetssim.ui.InputDoubleIntRangeProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableBooleanValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base implementation for configuration view models.
 *
 * @param <CON> immutable simulation config type
 */
public abstract class AbstractConfigViewModel<CON extends SimulationConfig> implements SimulationConfigViewModel<CON> {

    private final List<ConfigValidationRule> configValidationRules = new ArrayList<>();
    private final ReadOnlyBooleanWrapper hasConfigValidationIssues = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyStringWrapper configValidationMessage = new ReadOnlyStringWrapper("");

    private final ReadOnlyObjectProperty<SimulationState> simulationState;

    private final InputChoiceProperty<CellShape> cellShape;
    private final InputChoiceProperty<GridEdgeBehavior> gridEdgeBehavior;
    private final InputIntegerProperty gridWidth;
    private final InputIntegerProperty gridHeight;
    private final InputDoubleIntRangeProperty cellEdgeLength;
    private final InputChoiceProperty<CellDisplayMode> cellDisplayMode;
    private final SeedProperty seed;

    protected AbstractConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState,
                                      CommonConfigSettings commonConfigSettings) {
        this.simulationState = simulationState;

        cellShape = InputChoiceProperty.ofList(
                commonConfigSettings.cellShapeDefault(),
                commonConfigSettings.cellShapeValues(),
                e -> AppLocalization.getText(e.resourceKey()));
        gridEdgeBehavior = InputChoiceProperty.ofList(
                commonConfigSettings.gridEdgeBehaviorDefault(),
                commonConfigSettings.gridEdgeBehaviorValues(),
                e -> AppLocalization.getText(e.resourceKey()));
        gridWidth = InputIntegerProperty.of(
                commonConfigSettings.gridWidthDefault(),
                commonConfigSettings.gridWidthMin(),
                commonConfigSettings.gridWidthMax(),
                commonConfigSettings.gridWidthStep());
        gridHeight = InputIntegerProperty.of(
                commonConfigSettings.gridHeightDefault(),
                commonConfigSettings.gridHeightMin(),
                commonConfigSettings.gridHeightMax(),
                commonConfigSettings.gridHeightStep());
        cellEdgeLength = InputDoubleIntRangeProperty.of(
                commonConfigSettings.cellEdgeLengthDefault(),
                commonConfigSettings.cellEdgeLengthMin(),
                commonConfigSettings.cellEdgeLengthMax());
        cellDisplayMode = InputChoiceProperty.ofList(
                commonConfigSettings.cellDisplayModeDefault(),
                commonConfigSettings.cellDisplayModeValues(),
                e -> AppLocalization.getText(e.resourceKey()));
        seed = new SeedProperty(commonConfigSettings.seedDefault());
    }

    @Override
    public final ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public final SimulationState getSimulationState() {
        return simulationState.get();
    }

    public final InputChoiceProperty<CellShape> cellShapeProperty() {
        return cellShape;
    }

    public final InputChoiceProperty<GridEdgeBehavior> gridEdgeBehaviorProperty() {
        return gridEdgeBehavior;
    }

    public final InputIntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    public final InputIntegerProperty gridHeightProperty() {
        return gridHeight;
    }

    public final InputDoubleIntRangeProperty cellEdgeLengthProperty() {
        return cellEdgeLength;
    }

    public final InputChoiceProperty<CellDisplayMode> cellDisplayModeProperty() {
        return cellDisplayMode;
    }

    /**
     * Exposes the editable simulation-seed input and computed label.
     *
     * @return seed input wrapper
     */
    public final SeedProperty seedProperty() {
        return seed;
    }

    public final ReadOnlyBooleanProperty hasConfigValidationIssuesProperty() {
        return hasConfigValidationIssues.getReadOnlyProperty();
    }

    public final ReadOnlyStringProperty configValidationMessageProperty() {
        return configValidationMessage.getReadOnlyProperty();
    }

    /**
     * Registers a UI-facing configuration validation rule.
     *
     * @param violatedCondition condition that evaluates to {@code true} when the rule is violated
     * @param messageProvider   provider for the localized violation message
     */
    protected final void registerConfigValidationRule(ObservableBooleanValue violatedCondition,
                                                      Supplier<String> messageProvider) {
        configValidationRules.add(new ConfigValidationRule(violatedCondition, messageProvider));
        violatedCondition.addListener((_, _, _) -> updateConfigValidationState());
        updateConfigValidationState();
    }

    private void updateConfigValidationState() {
        List<String> messages = configValidationRules.stream()
                .filter(rule -> rule.violatedCondition().get())
                .map(rule -> rule.messageProvider().get())
                .toList();
        hasConfigValidationIssues.set(!messages.isEmpty());
        configValidationMessage.set(String.join(System.lineSeparator(), messages));
    }

    private record ConfigValidationRule(ObservableBooleanValue violatedCondition,
                                        Supplier<String> messageProvider) {
    }

    /**
     * Shared defaults and ranges used to initialize common config controls.
     *
     * @param cellShapeDefault        default cell shape
     * @param cellShapeValues         selectable cell shapes
     * @param gridEdgeBehaviorDefault default grid edge behavior
     * @param gridEdgeBehaviorValues  selectable grid edge behaviors
     * @param gridWidthDefault        default grid width
     * @param gridWidthMin            minimum grid width
     * @param gridWidthMax            maximum grid width
     * @param gridWidthStep           grid width step size
     * @param gridHeightDefault       default grid height
     * @param gridHeightMin           minimum grid height
     * @param gridHeightMax           maximum grid height
     * @param gridHeightStep          grid height step size
     * @param cellEdgeLengthDefault   default cell edge length
     * @param cellEdgeLengthMin       minimum cell edge length
     * @param cellEdgeLengthMax       maximum cell edge length
     * @param cellDisplayModeDefault  default cell display mode
     * @param cellDisplayModeValues   selectable cell display modes
     * @param seedDefault             default seed text
     */
    public record CommonConfigSettings(
            CellShape cellShapeDefault,
            List<CellShape> cellShapeValues,
            GridEdgeBehavior gridEdgeBehaviorDefault,
            List<GridEdgeBehavior> gridEdgeBehaviorValues,
            int gridWidthDefault,
            int gridWidthMin,
            int gridWidthMax,
            int gridWidthStep,
            int gridHeightDefault,
            int gridHeightMin,
            int gridHeightMax,
            int gridHeightStep,
            int cellEdgeLengthDefault,
            int cellEdgeLengthMin,
            int cellEdgeLengthMax,
            CellDisplayMode cellDisplayModeDefault,
            List<CellDisplayMode> cellDisplayModeValues,
            String seedDefault
    ) {

        public CommonConfigSettings {
            cellShapeValues = List.copyOf(cellShapeValues);
            gridEdgeBehaviorValues = List.copyOf(gridEdgeBehaviorValues);
            cellDisplayModeValues = List.copyOf(cellDisplayModeValues);
        }

    }

}
