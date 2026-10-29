package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Optional;

/**
 * Holds shared edit-toolbar UI state for simulation main views.
 *
 * @param <CTX> simulation-specific action context type
 */
final class EditToolBarViewModel<CTX extends SimulationUserActionContext> {

    private final BooleanProperty editModeActive = new SimpleBooleanProperty(false);
    private final ObjectProperty<String> selectedUserActionToolId =
            new SimpleObjectProperty<>(SimulationUserActionDescriptor.SELECT_TOOL_ID);

    BooleanProperty editModeActiveProperty() {
        return editModeActive;
    }

    ObjectProperty<String> selectedUserActionToolIdProperty() {
        return selectedUserActionToolId;
    }

    boolean isEditModeActive() {
        return editModeActive.get();
    }

    void resetToSelectMode() {
        editModeActive.set(false);
        selectedUserActionToolId.set(SimulationUserActionDescriptor.SELECT_TOOL_ID);
    }

    Optional<SimulationUserActionDescriptor<CTX>> findSelectedUserActionDescriptor(List<SimulationUserActionDescriptor<CTX>> descriptors) {
        return descriptors.stream()
                .filter(descriptor -> selectedUserActionToolId.get().equals(descriptor.toolId()))
                .findFirst();
    }

    Optional<SimulationUserActionDescriptor<CTX>> findSelectedCellActionDescriptor(List<SimulationUserActionDescriptor<CTX>> descriptors) {
        return findSelectedUserActionDescriptor(descriptors)
                .filter(descriptor -> descriptor.scope() == SimulationUserActionScope.CELL_SELECTED);
    }

}
