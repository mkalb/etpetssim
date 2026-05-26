package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;

import java.util.*;
import java.util.function.*;

/**
 * Base implementation for configuration view models.
 */
public abstract class AbstractConfigViewModel<CON extends SimulationConfig>
        implements SimulationConfigViewModel<CON> {

    private final List<ConfigValidationRule> configValidationRules = new ArrayList<>();
    private final ReadOnlyBooleanWrapper hasConfigValidationIssues = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyStringWrapper configValidationMessage = new ReadOnlyStringWrapper("");

    private final ReadOnlyObjectProperty<SimulationState> simulationState;

    private final InputEnumProperty<CellShape> cellShape;
    private final InputEnumProperty<GridEdgeBehavior> gridEdgeBehavior;
    private final InputIntegerProperty gridWidth;
    private final InputIntegerProperty gridHeight;
    private final InputDoublePropertyIntRange cellEdgeLength;
    private final InputEnumProperty<CellDisplayMode> cellDisplayMode;
    private final SeedProperty seed;

    protected AbstractConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState,
                                      CommonConfigSettings commonConfigSettings) {
        this.simulationState = simulationState;

        cellShape = InputEnumProperty.of(
                commonConfigSettings.cellShapeInitial(),
                commonConfigSettings.cellShapeValues(),
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        gridEdgeBehavior = InputEnumProperty.of(
                commonConfigSettings.gridEdgeBehaviorInitial(),
                commonConfigSettings.gridEdgeBehaviorValues(),
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        gridWidth = InputIntegerProperty.of(
                commonConfigSettings.gridWidthInitial(),
                commonConfigSettings.gridWidthMin(),
                commonConfigSettings.gridWidthMax(),
                commonConfigSettings.gridWidthStep());
        gridHeight = InputIntegerProperty.of(
                commonConfigSettings.gridHeightInitial(),
                commonConfigSettings.gridHeightMin(),
                commonConfigSettings.gridHeightMax(),
                commonConfigSettings.gridHeightStep());
        cellEdgeLength = InputDoublePropertyIntRange.of(
                commonConfigSettings.cellEdgeLengthInitial(),
                commonConfigSettings.cellEdgeLengthMin(),
                commonConfigSettings.cellEdgeLengthMax());
        cellDisplayMode = InputEnumProperty.of(
                commonConfigSettings.cellDisplayModeInitial(),
                commonConfigSettings.cellDisplayModeValues(),
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        seed = new SeedProperty(commonConfigSettings.seedInitial());
    }

    @Override
    public final ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public final SimulationState getSimulationState() {
        return simulationState.get();
    }

    /**
     * Exposes the editable cell-shape input.
     *
     * @return cell-shape input property wrapper
     */
    public final InputEnumProperty<CellShape> cellShapeProperty() {
        return cellShape;
    }

    /**
     * Exposes the editable grid-edge behavior input.
     *
     * @return grid-edge behavior input property wrapper
     */
    public final InputEnumProperty<GridEdgeBehavior> gridEdgeBehaviorProperty() {
        return gridEdgeBehavior;
    }

    /**
     * Exposes the editable grid width input.
     *
     * @return grid-width input property wrapper
     */
    public final InputIntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    /**
     * Exposes the editable grid height input.
     *
     * @return grid-height input property wrapper
     */
    public final InputIntegerProperty gridHeightProperty() {
        return gridHeight;
    }

    /**
     * Exposes the editable cell edge length input.
     *
     * @return cell-edge-length input property wrapper
     */
    public final InputDoublePropertyIntRange cellEdgeLengthProperty() {
        return cellEdgeLength;
    }

    /**
     * Exposes the editable cell display mode input.
     *
     * @return cell-display-mode input property wrapper
     */
    public final InputEnumProperty<CellDisplayMode> cellDisplayModeProperty() {
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

    /**
     * Registers a UI-facing configuration validation rule.
     *
     * @param violatedCondition condition that evaluates to {@code true} when the rule is violated
     * @param messageProvider   provider for the localized violation message
     */
    protected final void addConfigValidationRule(ObservableBooleanValue violatedCondition,
                                                 Supplier<String> messageProvider) {
        configValidationRules.add(new ConfigValidationRule(violatedCondition, messageProvider));
        violatedCondition.addListener((_, _, _) -> updateConfigValidationState());
        updateConfigValidationState();
    }

    public final ReadOnlyBooleanProperty hasConfigValidationIssuesProperty() {
        return hasConfigValidationIssues.getReadOnlyProperty();
    }

    public final boolean hasConfigValidationIssues() {
        return hasConfigValidationIssues.get();
    }

    public final ReadOnlyStringProperty configValidationMessageProperty() {
        return configValidationMessage.getReadOnlyProperty();
    }

    public final String getConfigValidationMessage() {
        return configValidationMessage.get();
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
     * @param cellShapeInitial initial cell shape
     * @param cellShapeValues selectable cell shapes
     * @param gridEdgeBehaviorInitial initial grid edge behavior
     * @param gridEdgeBehaviorValues selectable grid edge behaviors
     * @param gridWidthInitial initial grid width
     * @param gridWidthMin minimum grid width
     * @param gridWidthMax maximum grid width
     * @param gridWidthStep grid width step size
     * @param gridHeightInitial initial grid height
     * @param gridHeightMin minimum grid height
     * @param gridHeightMax maximum grid height
     * @param gridHeightStep grid height step size
     * @param cellEdgeLengthInitial initial cell edge length
     * @param cellEdgeLengthMin minimum cell edge length
     * @param cellEdgeLengthMax maximum cell edge length
     * @param cellDisplayModeInitial initial cell display mode
     * @param cellDisplayModeValues selectable cell display modes
     * @param seedInitial initial seed text
     */
    public record CommonConfigSettings(
            CellShape cellShapeInitial,
            List<CellShape> cellShapeValues,
            GridEdgeBehavior gridEdgeBehaviorInitial,
            List<GridEdgeBehavior> gridEdgeBehaviorValues,
            int gridWidthInitial,
            int gridWidthMin,
            int gridWidthMax,
            int gridWidthStep,
            int gridHeightInitial,
            int gridHeightMin,
            int gridHeightMax,
            int gridHeightStep,
            int cellEdgeLengthInitial,
            int cellEdgeLengthMin,
            int cellEdgeLengthMax,
            CellDisplayMode cellDisplayModeInitial,
            List<CellDisplayMode> cellDisplayModeValues,
            String seedInitial
    ) {

        public CommonConfigSettings {
            cellShapeValues = List.copyOf(cellShapeValues);
            gridEdgeBehaviorValues = List.copyOf(gridEdgeBehaviorValues);
            cellDisplayModeValues = List.copyOf(cellDisplayModeValues);
        }

    }

}
