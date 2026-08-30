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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticMetricLabelKeyTest {

    private static Set<String> loadMainPropertyKeys(String fileName) throws IOException {
        String resourceName = "i18n/" + fileName;
        List<URL> mainResources = Collections.list(
                                                     StatisticMetricLabelKeyTest.class.getClassLoader().getResources(resourceName)
                                             ).stream()
                                             .filter(resource -> resource.toExternalForm().contains("/main/"))
                                             .toList();
        assertEquals(1, mainResources.size(), "Expected exactly one production bundle: " + fileName);

        Properties props = new Properties();
        try (var reader = new InputStreamReader(mainResources.getFirst().openStream(), StandardCharsets.UTF_8)) {
            props.load(reader);
        }
        return props.stringPropertyNames();
    }

    private static List<String> allLabelKeys() {
        return Stream.<List<? extends StatisticMetric<?>>>of(
                             ConwayStatistics.metrics(),
                             EtpetsStatistics.metrics(),
                             ForestStatistics.metrics(),
                             LangtonStatistics.metrics(),
                             ReboundingStatistics.metrics(),
                             SnakeStatistics.metrics(),
                             SugarStatistics.metrics(),
                             WatorStatistics.metrics()
                     ).flatMap(List::stream)
                     .map(StatisticMetric::labelKey)
                     .toList();
    }

    // --- LabelKey resolution tests ---

    @Test
    void testAllLabelKeysResolveInEnglishBundle() throws IOException {
        Set<String> enKeys = loadMainPropertyKeys("messages_en_US.properties");

        for (String labelKey : allLabelKeys()) {
            assertTrue(enKeys.contains(labelKey),
                    "Missing en_US key: " + labelKey);
        }
    }

    @Test
    void testAllLabelKeysResolveInGermanBundle() throws IOException {
        Set<String> deKeys = loadMainPropertyKeys("messages_de_DE.properties");

        for (String labelKey : allLabelKeys()) {
            assertTrue(deKeys.contains(labelKey),
                    "Missing de_DE key: " + labelKey);
        }
    }

}
