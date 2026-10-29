package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SimulationUserActionDescriptorTest {

    private static final String TOOL_ID_SAMPLE = "sample.tool";
    private static final String LABEL_KEY_SAMPLE = "sample.toolbar.tool";
    private static final String TOOLTIP_KEY_SAMPLE = "sample.toolbar.tool.tooltip";

    private static SimulationUserActionDescriptor<NoUserActionContext> createDescriptor(String toolId,
                                                                                        String labelKey,
                                                                                        String tooltipKey) {
        return new SimulationUserActionDescriptor<>(
                toolId,
                SimulationUserActionScope.GLOBAL,
                labelKey,
                tooltipKey,
                Optional::empty);
    }

    @Test
    void testRecord() {
        Supplier<Optional<NoUserActionContext>> contextResolver = Optional::empty;

        SimulationUserActionDescriptor<NoUserActionContext> descriptor = new SimulationUserActionDescriptor<>(
                TOOL_ID_SAMPLE,
                SimulationUserActionScope.CELL_SELECTED,
                LABEL_KEY_SAMPLE,
                TOOLTIP_KEY_SAMPLE,
                contextResolver);

        assertAll(
                () -> assertEquals(TOOL_ID_SAMPLE, descriptor.toolId()),
                () -> assertEquals(SimulationUserActionScope.CELL_SELECTED, descriptor.scope()),
                () -> assertEquals(LABEL_KEY_SAMPLE, descriptor.labelKey()),
                () -> assertEquals(TOOLTIP_KEY_SAMPLE, descriptor.tooltipKey()),
                () -> assertSame(contextResolver, descriptor.contextResolver())
        );
    }

    @Test
    void testConstructorWithContext() {
        SimulationUserActionDescriptor<NoUserActionContext> descriptor = new SimulationUserActionDescriptor<>(
                TOOL_ID_SAMPLE,
                NoUserActionContext.NO_CONTEXT,
                SimulationUserActionScope.GLOBAL,
                LABEL_KEY_SAMPLE,
                TOOLTIP_KEY_SAMPLE);

        assertAll(
                () -> assertEquals(TOOL_ID_SAMPLE, descriptor.toolId()),
                () -> assertEquals(SimulationUserActionScope.GLOBAL, descriptor.scope()),
                () -> assertEquals(LABEL_KEY_SAMPLE, descriptor.labelKey()),
                () -> assertEquals(TOOLTIP_KEY_SAMPLE, descriptor.tooltipKey()),
                () -> assertEquals(Optional.of(NoUserActionContext.NO_CONTEXT), descriptor.resolveContext())
        );
    }

    @Test
    void testConstructorRejectsBlankToolId() {
        assertThrows(IllegalArgumentException.class,
                () -> createDescriptor(" ", LABEL_KEY_SAMPLE, TOOLTIP_KEY_SAMPLE));
    }

    @Test
    void testConstructorRejectsSelectToolId() {
        assertThrows(IllegalArgumentException.class,
                () -> createDescriptor(SimulationUserActionDescriptor.SELECT_TOOL_ID, LABEL_KEY_SAMPLE,
                        TOOLTIP_KEY_SAMPLE));
    }

    @Test
    void testConstructorRejectsBlankLabelKey() {
        assertThrows(IllegalArgumentException.class,
                () -> createDescriptor(TOOL_ID_SAMPLE, " ", TOOLTIP_KEY_SAMPLE));
    }

    @Test
    void testConstructorRejectsBlankTooltipKey() {
        assertThrows(IllegalArgumentException.class,
                () -> createDescriptor(TOOL_ID_SAMPLE, LABEL_KEY_SAMPLE, " "));
    }

    @Test
    void testResolveContext() {
        AtomicReference<Optional<NoUserActionContext>> currentContext = new AtomicReference<>(Optional.empty());
        SimulationUserActionDescriptor<NoUserActionContext> descriptor = new SimulationUserActionDescriptor<>(
                TOOL_ID_SAMPLE,
                SimulationUserActionScope.CELL_SELECTED,
                LABEL_KEY_SAMPLE,
                TOOLTIP_KEY_SAMPLE,
                currentContext::get);

        Optional<NoUserActionContext> contextBefore = descriptor.resolveContext();
        currentContext.set(Optional.of(NoUserActionContext.NO_CONTEXT));
        Optional<NoUserActionContext> contextAfter = descriptor.resolveContext();

        assertAll(
                () -> assertTrue(contextBefore.isEmpty()),
                () -> assertEquals(Optional.of(NoUserActionContext.NO_CONTEXT), contextAfter)
        );
    }

}

