package de.mkalb.etpetssim.simulations.core.shared;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class SimulationNotificationTypeTest {

    @Test
    void testResourceKey() {
        assertKeysInAllBundles(
                Arrays.stream(SimulationNotificationType.values()).map(SimulationNotificationType::resourceKey).toList());
    }

}
