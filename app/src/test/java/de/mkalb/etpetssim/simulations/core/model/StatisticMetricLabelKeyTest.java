package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Keep single-threaded because AppLogger holds shared static state.
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class StatisticMetricLabelKeyTest {

    private Set<String> enKeys = Set.of();
    private Set<String> deKeys = Set.of();

    private static Set<String> loadMainPropertyKeys(String fileName) throws IOException {
        Properties props = new Properties();
        File file = new File("src/main/resources/i18n/" + fileName);
        try (var reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
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

    // --- Helper ---

    @BeforeAll
    void setUpBeforeAll() throws IOException {
        AppLogger.initializeForTesting();
        enKeys = loadMainPropertyKeys("messages_en_US.properties");
        deKeys = loadMainPropertyKeys("messages_de_DE.properties");
    }

    // --- LabelKey resolution tests ---

    @Test
    void testAllLabelKeysResolveInEnglishBundle() {
        for (String labelKey : allLabelKeys()) {
            assertTrue(enKeys.contains(labelKey),
                    "Missing en_US key: " + labelKey);
        }
    }

    @Test
    void testAllLabelKeysResolveInGermanBundle() {
        for (String labelKey : allLabelKeys()) {
            assertTrue(deKeys.contains(labelKey),
                    "Missing de_DE key: " + labelKey);
        }
    }

}
