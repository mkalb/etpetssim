package de.mkalb.etpetssim.simulations.etpets.shared;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class EtpetsResourceChoiceTest {

    @Test
    void testLabelKey() {
        assertKeysInAllBundles(Arrays.stream(EtpetsResourceChoice.values()).map(EtpetsResourceChoice::labelKey).toList());
    }

}
