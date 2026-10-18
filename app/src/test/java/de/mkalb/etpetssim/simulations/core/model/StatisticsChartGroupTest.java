package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

final class StatisticsChartGroupTest {

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new StatisticsChartGroup[]{
                        StatisticsChartGroup.NONE,
                        StatisticsChartGroup.PRIMARY,
                        StatisticsChartGroup.SECONDARY
                },
                StatisticsChartGroup.values()
        );
    }

}
