package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AbstractConfigViewModelTest {

    private static final String SEED_DEFAULT = "seed";
    private static final String MESSAGE_FIRST = "first";
    private static final String MESSAGE_SECOND = "second";

    private static AbstractConfigViewModel.CommonConfigConstraints createConstraints() {
        return new AbstractConfigViewModel.CommonConfigConstraints(
                CellShape.HEXAGON, List.of(CellShape.SQUARE, CellShape.HEXAGON),
                GridEdgeBehavior.BLOCK_XY, List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY),
                40, 10, 100, 2,
                30, 10, 80, 5,
                12, 4, 20,
                CellDisplayMode.SHAPE, List.of(CellDisplayMode.values()),
                SEED_DEFAULT);
    }

    private static TestConfigViewModel createViewModel() {
        return new TestConfigViewModel(new SimpleObjectProperty<>(SimulationState.READY));
    }

    @Test
    void testConstructor() {
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.READY);

        TestConfigViewModel viewModel = new TestConfigViewModel(simulationState);

        assertAll(
                () -> assertSame(simulationState, viewModel.simulationStateProperty()),
                () -> assertEquals(CellShape.HEXAGON, viewModel.cellShapeProperty().getValue()),
                () -> assertEquals(List.of(CellShape.SQUARE, CellShape.HEXAGON),
                        viewModel.cellShapeProperty().validValues()),
                () -> assertEquals(GridEdgeBehavior.BLOCK_XY, viewModel.gridEdgeBehaviorProperty().getValue()),
                () -> assertEquals(List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY),
                        viewModel.gridEdgeBehaviorProperty().validValues()),
                () -> assertEquals(40, viewModel.gridWidthProperty().getValue()),
                () -> assertEquals(10, viewModel.gridWidthProperty().min()),
                () -> assertEquals(100, viewModel.gridWidthProperty().max()),
                () -> assertEquals(2, viewModel.gridWidthProperty().step()),
                () -> assertEquals(30, viewModel.gridHeightProperty().getValue()),
                () -> assertEquals(10, viewModel.gridHeightProperty().min()),
                () -> assertEquals(80, viewModel.gridHeightProperty().max()),
                () -> assertEquals(5, viewModel.gridHeightProperty().step()),
                () -> assertEquals(12.0d, viewModel.cellEdgeLengthProperty().getValue(), 0.0d),
                () -> assertEquals(4, viewModel.cellEdgeLengthProperty().min()),
                () -> assertEquals(20, viewModel.cellEdgeLengthProperty().max()),
                () -> assertEquals(CellDisplayMode.SHAPE, viewModel.cellDisplayModeProperty().getValue()),
                () -> assertEquals(List.of(CellDisplayMode.values()),
                        viewModel.cellDisplayModeProperty().validValues()),
                () -> assertEquals(SEED_DEFAULT, viewModel.seedProperty().stringProperty().get()),
                () -> assertFalse(viewModel.hasConfigValidationIssuesProperty().get()),
                () -> assertEquals("", viewModel.configValidationMessageProperty().get())
        );
    }

    @Test
    void testCommonConfigConstraintsRecord() {
        List<CellShape> cellShapes = new ArrayList<>(List.of(CellShape.SQUARE, CellShape.HEXAGON));
        List<GridEdgeBehavior> gridEdgeBehaviors = new ArrayList<>(List.of(GridEdgeBehavior.values()));
        List<CellDisplayMode> cellDisplayModes = new ArrayList<>(List.of(CellDisplayMode.values()));

        AbstractConfigViewModel.CommonConfigConstraints constraints =
                new AbstractConfigViewModel.CommonConfigConstraints(
                        CellShape.SQUARE, cellShapes,
                        GridEdgeBehavior.values()[0], gridEdgeBehaviors,
                        40, 10, 100, 2,
                        30, 10, 80, 5,
                        12, 4, 20,
                        CellDisplayMode.values()[0], cellDisplayModes,
                        "seed");
        cellShapes.clear();
        gridEdgeBehaviors.clear();
        cellDisplayModes.clear();

        assertAll(
                () -> assertEquals(CellShape.SQUARE, constraints.cellShapeDefault()),
                () -> assertEquals(List.of(CellShape.SQUARE, CellShape.HEXAGON), constraints.cellShapeValues()),
                () -> assertEquals(GridEdgeBehavior.values()[0], constraints.gridEdgeBehaviorDefault()),
                () -> assertEquals(List.of(GridEdgeBehavior.values()), constraints.gridEdgeBehaviorValues()),
                () -> assertEquals(40, constraints.gridWidthDefault()),
                () -> assertEquals(10, constraints.gridWidthMin()),
                () -> assertEquals(100, constraints.gridWidthMax()),
                () -> assertEquals(2, constraints.gridWidthStep()),
                () -> assertEquals(30, constraints.gridHeightDefault()),
                () -> assertEquals(10, constraints.gridHeightMin()),
                () -> assertEquals(80, constraints.gridHeightMax()),
                () -> assertEquals(5, constraints.gridHeightStep()),
                () -> assertEquals(12, constraints.cellEdgeLengthDefault()),
                () -> assertEquals(4, constraints.cellEdgeLengthMin()),
                () -> assertEquals(20, constraints.cellEdgeLengthMax()),
                () -> assertEquals(CellDisplayMode.values()[0], constraints.cellDisplayModeDefault()),
                () -> assertEquals(List.of(CellDisplayMode.values()), constraints.cellDisplayModeValues()),
                () -> assertEquals("seed", constraints.seedDefault()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> constraints.cellShapeValues().add(CellShape.TRIANGLE)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> constraints.gridEdgeBehaviorValues().clear()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> constraints.cellDisplayModeValues().clear())
        );
    }

    @Test
    void testGetSimulationState() {
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.READY);
        TestConfigViewModel viewModel = new TestConfigViewModel(simulationState);

        simulationState.set(SimulationState.RUNNING_TIMED);

        assertEquals(SimulationState.RUNNING_TIMED, viewModel.getSimulationState());
    }

    @Test
    void testRegisterConfigValidationRule() {
        TestConfigViewModel viewModel = createViewModel();
        BooleanProperty violated = new SimpleBooleanProperty(true);

        viewModel.registerConfigValidationRule(violated, () -> MESSAGE_FIRST);
        boolean issuesAfterRegistration = viewModel.hasConfigValidationIssuesProperty().get();
        String messageAfterRegistration = viewModel.configValidationMessageProperty().get();
        violated.set(false);

        assertAll(
                () -> assertTrue(issuesAfterRegistration),
                () -> assertEquals(MESSAGE_FIRST, messageAfterRegistration),
                () -> assertFalse(viewModel.hasConfigValidationIssuesProperty().get()),
                () -> assertEquals("", viewModel.configValidationMessageProperty().get())
        );
    }

    @Test
    void testRegisterConfigValidationRuleJoinsMessagesInRegistrationOrder() {
        TestConfigViewModel viewModel = createViewModel();
        BooleanProperty firstViolated = new SimpleBooleanProperty(false);
        BooleanProperty secondViolated = new SimpleBooleanProperty(false);
        viewModel.registerConfigValidationRule(firstViolated, () -> MESSAGE_FIRST);
        viewModel.registerConfigValidationRule(secondViolated, () -> MESSAGE_SECOND);

        secondViolated.set(true);
        firstViolated.set(true);

        assertAll(
                () -> assertTrue(viewModel.hasConfigValidationIssuesProperty().get()),
                () -> assertEquals(MESSAGE_FIRST + System.lineSeparator() + MESSAGE_SECOND,
                        viewModel.configValidationMessageProperty().get())
        );
    }

    @Test
    void testRegisterConfigValidationRuleReadsMessageOnUpdate() {
        TestConfigViewModel viewModel = createViewModel();
        BooleanProperty violated = new SimpleBooleanProperty(false);
        AtomicReference<String> message = new AtomicReference<>(MESSAGE_FIRST);
        viewModel.registerConfigValidationRule(violated, message::get);

        message.set(MESSAGE_SECOND);
        violated.set(true);

        assertEquals(MESSAGE_SECOND, viewModel.configValidationMessageProperty().get());
    }

    private static final class TestConfigViewModel extends AbstractConfigViewModel<ConwayConfig> {

        TestConfigViewModel(ObjectProperty<SimulationState> simulationState) {
            super(simulationState, createConstraints());
        }

        @NonNull
        @Override
        public ConwayConfig createConfig() {
            throw new UnsupportedOperationException();
        }

    }

}
