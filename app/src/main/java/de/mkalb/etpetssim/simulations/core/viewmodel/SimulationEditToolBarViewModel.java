package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.*;
import javafx.beans.property.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Holds shared edit-toolbar UI state for simulation main views.
 *
 * @param <CTX> simulation-specific action context type
 */
final class SimulationEditToolBarViewModel<CTX extends SimulationUserActionContext> {

    private final BooleanProperty editModeActive = new SimpleBooleanProperty(false);
    private final ObjectProperty<@Nullable SimulationUserActionDescriptor<CTX>> selectedUserActionDescriptor =
            new SimpleObjectProperty<>();

    BooleanProperty editModeActiveProperty() {
        return editModeActive;
    }

    ObjectProperty<@Nullable SimulationUserActionDescriptor<CTX>> selectedUserActionDescriptorProperty() {
        return selectedUserActionDescriptor;
    }

    boolean isEditModeActive() {
        return editModeActive.get();
    }

    void resetToSelectMode() {
        editModeActive.set(false);
        selectedUserActionDescriptor.set(null);
    }

    Optional<SimulationUserActionDescriptor<CTX>> getSelectedCellActionDescriptor() {
        var descriptor = selectedUserActionDescriptor.get();
        if ((descriptor == null) || (descriptor.scope() != SimulationUserActionScope.CELL_SELECTED)) {
            return Optional.empty();
        }
        return Optional.of(descriptor);
    }

}
