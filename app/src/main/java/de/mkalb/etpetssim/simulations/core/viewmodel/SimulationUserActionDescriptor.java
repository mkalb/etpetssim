package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * UI-facing descriptor for a simulation user action.
 *
 * <p>The descriptor holds metadata and context resolution only; it does not own JavaFX nodes. Simulation-specific
 * option controls are created by the simulation view. An empty resolved context means the action is not applied.
 *
 * @param <CTX>           simulation-specific action context type
 * @param toolId          stable tool identifier used for selection and matching
 * @param scope           action scope that determines UI behavior
 * @param labelKey        localization key for the control label
 * @param tooltipKey      localization key for the control tooltip
 * @param contextResolver apply-time resolver for the current action context
 */
public record SimulationUserActionDescriptor<CTX extends SimulationUserActionContext>(
        String toolId,
        SimulationUserActionScope scope,
        String labelKey,
        String tooltipKey,
        Supplier<Optional<CTX>> contextResolver) {

    /**
     * Reserved stable tool id for the built-in Select tool.
     */
    public static final String SELECT_TOOL_ID = "core.select";

    /**
     * Creates a descriptor with a fixed action context.
     *
     * @param toolId     stable tool identifier used for selection and matching
     * @param context    fixed action context forwarded to model-side action application
     * @param scope      action scope that determines UI behavior
     * @param labelKey   localization key for the control label
     * @param tooltipKey localization key for the control tooltip
     * @throws IllegalArgumentException if {@code toolId}, {@code labelKey}, or {@code tooltipKey} is blank, or if
     *                                  {@code toolId} is {@link #SELECT_TOOL_ID}
     */
    public SimulationUserActionDescriptor(String toolId,
                                          CTX context,
                                          SimulationUserActionScope scope,
                                          String labelKey,
                                          String tooltipKey) {
        this(toolId, scope, labelKey, tooltipKey, () -> Optional.of(context));
    }

    /**
     * Creates a descriptor with an apply-time context resolver.
     *
     * @throws IllegalArgumentException if {@code toolId}, {@code labelKey}, or {@code tooltipKey} is blank, or if
     *                                  {@code toolId} is {@link #SELECT_TOOL_ID}
     */
    public SimulationUserActionDescriptor {
        if (toolId.isBlank()) {
            throw new IllegalArgumentException("toolId must not be blank");
        }
        if (SELECT_TOOL_ID.equals(toolId)) {
            throw new IllegalArgumentException("toolId must not use the reserved Select id");
        }
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
        if (tooltipKey.isBlank()) {
            throw new IllegalArgumentException("tooltipKey must not be blank");
        }
    }

    /**
     * Resolves the current action context when the tool is applied.
     *
     * @return resolved context, or empty when no valid context is currently available
     */
    public Optional<CTX> resolveContext() {
        return contextResolver.get();
    }

}
