package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;

final class StatisticsMetricLabelKeyTest {

    private static List<String> allLabelKeys() {
        return Stream.<List<? extends StatisticsMetric<?>>>of(
                        ConwayStatistics.metrics(),
                        EtpetsStatistics.metrics(),
                        ForestStatistics.metrics(),
                        LangtonStatistics.metrics(),
                        ReboundingStatistics.metrics(),
                        SnakeStatistics.metrics(),
                        SugarStatistics.metrics(),
                        WatorStatistics.metrics()
                ).flatMap(List::stream)
                .map(StatisticsMetric::labelKey)
                .toList();
    }

    @Test
    void testLabelKeysExistInAllBundles() {
        assertKeysInAllBundles(allLabelKeys());
    }

}
