package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.*;

/**
 * UI-facing descriptor for a simulation user action.
 *
 * @param <CTX>      simulation-specific action context type
 * @param context    action context forwarded to model-side action application
 * @param scope      action scope that determines UI behavior
 * @param labelKey   localization key for the control label
 * @param tooltipKey localization key for the control tooltip
 */
public record SimulationUserActionDescriptor<CTX extends SimulationUserActionContext>(
        CTX context,
        SimulationUserActionScope scope,
        String labelKey,
        String tooltipKey) {

    public SimulationUserActionDescriptor {
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
        if (tooltipKey.isBlank()) {
            throw new IllegalArgumentException("tooltipKey must not be blank");
        }
    }

}
