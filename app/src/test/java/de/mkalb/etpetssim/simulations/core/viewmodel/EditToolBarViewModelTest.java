package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EditToolBarViewModelTest {

    private static final String TOOL_ID_CELL = "test.cell";
    private static final String TOOL_ID_GLOBAL = "test.global";
    private static final String TOOL_ID_UNKNOWN = "test.unknown";

    private static final SimulationUserActionDescriptor<NoUserActionContext> CELL_DESCRIPTOR =
            createDescriptor(TOOL_ID_CELL, SimulationUserActionScope.SELECTED_CELL);
    private static final SimulationUserActionDescriptor<NoUserActionContext> CELL_DUPLICATE_DESCRIPTOR =
            createDescriptor(TOOL_ID_CELL, SimulationUserActionScope.SELECTED_CELL);
    private static final SimulationUserActionDescriptor<NoUserActionContext> GLOBAL_DESCRIPTOR =
            createDescriptor(TOOL_ID_GLOBAL, SimulationUserActionScope.GLOBAL);
    private static final List<SimulationUserActionDescriptor<NoUserActionContext>> DESCRIPTORS =
            List.of(GLOBAL_DESCRIPTOR, CELL_DESCRIPTOR, CELL_DUPLICATE_DESCRIPTOR);

    private static SimulationUserActionDescriptor<NoUserActionContext> createDescriptor(
            String toolId, SimulationUserActionScope scope) {
        return new SimulationUserActionDescriptor<>(toolId, NoUserActionContext.NO_CONTEXT, scope,
                "test.toolbar.tool", "test.toolbar.tool.tooltip");
    }

    private static EditToolBarViewModel<NoUserActionContext> createViewModelWithSelectedTool(String toolId) {
        var viewModel = new EditToolBarViewModel<NoUserActionContext>();
        viewModel.selectedUserActionToolIdProperty().set(toolId);
        return viewModel;
    }

    @Test
    void testConstructor() {
        var viewModel = new EditToolBarViewModel<NoUserActionContext>();

        assertAll(
                () -> assertFalse(viewModel.isEditModeActive()),
                () -> assertFalse(viewModel.editModeActiveProperty().get()),
                () -> assertEquals(SimulationUserActionDescriptor.SELECT_TOOL_ID,
                        viewModel.selectedUserActionToolIdProperty().get())
        );
    }

    @Test
    void testIsEditModeActive() {
        var viewModel = new EditToolBarViewModel<NoUserActionContext>();

        viewModel.editModeActiveProperty().set(true);

        assertTrue(viewModel.isEditModeActive());
    }

    @Test
    void testResetToSelectMode() {
        var viewModel = createViewModelWithSelectedTool(TOOL_ID_CELL);
        viewModel.editModeActiveProperty().set(true);

        viewModel.resetToSelectMode();

        assertAll(
                () -> assertFalse(viewModel.isEditModeActive()),
                () -> assertEquals(SimulationUserActionDescriptor.SELECT_TOOL_ID,
                        viewModel.selectedUserActionToolIdProperty().get())
        );
    }

    @Test
    void testFindSelectedCellActionDescriptor() {
        var viewModel = createViewModelWithSelectedTool(TOOL_ID_CELL);

        var descriptor = viewModel.findSelectedCellActionDescriptor(DESCRIPTORS);

        assertSame(CELL_DESCRIPTOR, descriptor.orElseThrow());
    }

    @Test
    void testFindSelectedCellActionDescriptorIgnoresGlobalScope() {
        var viewModel = createViewModelWithSelectedTool(TOOL_ID_GLOBAL);

        assertEquals(Optional.empty(), viewModel.findSelectedCellActionDescriptor(DESCRIPTORS));
    }

    @Test
    void testFindSelectedCellActionDescriptorReturnsEmptyForSelectTool() {
        var viewModel = new EditToolBarViewModel<NoUserActionContext>();

        assertEquals(Optional.empty(), viewModel.findSelectedCellActionDescriptor(DESCRIPTORS));
    }

    @Test
    void testFindSelectedCellActionDescriptorReturnsEmptyForUnknownToolId() {
        var viewModel = createViewModelWithSelectedTool(TOOL_ID_UNKNOWN);

        assertAll(
                () -> assertEquals(Optional.empty(), viewModel.findSelectedCellActionDescriptor(DESCRIPTORS)),
                () -> assertEquals(Optional.empty(), viewModel.findSelectedCellActionDescriptor(List.of()))
        );
    }

}
