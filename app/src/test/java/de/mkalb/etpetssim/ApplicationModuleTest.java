package de.mkalb.etpetssim;

import org.junit.jupiter.api.Test;

import java.lang.module.ModuleDescriptor;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class ApplicationModuleTest {

    private static final Set<String> EXPECTED_REQUIRED_MODULES =
            Set.of("java.base", "java.logging", "javafx.controls", "org.jspecify");

    @Test
    void testModuleRequiresOnlyExpectedModules() {
        ModuleDescriptor descriptor = ApplicationModuleTest.class.getModule().getDescriptor();
        assertNotNull(descriptor, "Tests must run patched into the application module");

        Set<String> requiredModules = descriptor.requires().stream()
                .map(ModuleDescriptor.Requires::name)
                .collect(Collectors.toSet());

        assertEquals(EXPECTED_REQUIRED_MODULES, requiredModules);
    }

}
