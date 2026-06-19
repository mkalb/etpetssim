package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.*;
import javafx.beans.property.*;

import java.util.*;

/**
 * Holds shared edit-toolbar UI state for simulation main views.
 *
 * @param <CTX> simulation-specific action context type
 */
final class SimulationEditToolBarViewModel<CTX extends SimulationUserActionContext> {

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

    Optional<SimulationUserActionDescriptor<CTX>> getSelectedUserActionDescriptor(List<SimulationUserActionDescriptor<CTX>> descriptors) {
        return descriptors.stream()
                          .filter(descriptor -> selectedUserActionToolId.get().equals(descriptor.toolId()))
                          .findFirst();
    }

    Optional<SimulationUserActionDescriptor<CTX>> getSelectedCellActionDescriptor(List<SimulationUserActionDescriptor<CTX>> descriptors) {
        return getSelectedUserActionDescriptor(descriptors)
                .filter(descriptor -> descriptor.scope() == SimulationUserActionScope.CELL_SELECTED);
    }

}
