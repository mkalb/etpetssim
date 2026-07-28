package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticExtremaTrackerTest {

    @Test
    void testUpdateRespectsExtremaModes() {
        StatisticExtremaTracker tracker = new StatisticExtremaTracker(List.of(
                new StatisticMetric<>("none", "none.key", _ -> 0.0d, StatisticExtremaMode.NONE),
                new StatisticMetric<>("min", "min.key", _ -> 0.0d, StatisticExtremaMode.MIN),
                new StatisticMetric<>("max", "max.key", _ -> 0.0d, StatisticExtremaMode.MAX),
                new StatisticMetric<>("both", "both.key", _ -> 0.0d, StatisticExtremaMode.MIN_AND_MAX)
        ));

        tracker.update(Map.of("none", 5.0d, "min", 5.0d, "max", 5.0d, "both", 5.0d));
        tracker.update(Map.of("none", 1.0d, "min", 1.0d, "max", 9.0d, "both", 9.0d));

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertFalse(extrema.minimumValues().containsKey("none")),
                () -> assertFalse(extrema.maximumValues().containsKey("none")),
                () -> assertEquals(1.0d, extrema.minimumValues().get("min")),
                () -> assertFalse(extrema.maximumValues().containsKey("min")),
                () -> assertEquals(9.0d, extrema.maximumValues().get("max")),
                () -> assertFalse(extrema.minimumValues().containsKey("max")),
                () -> assertEquals(5.0d, extrema.minimumValues().get("both")),
                () -> assertEquals(9.0d, extrema.maximumValues().get("both"))
        );
    }

    @Test
    void testConstructorRejectsDuplicateMetricKeys() {
        assertThrows(IllegalArgumentException.class, () -> new StatisticExtremaTracker(List.of(
                new StatisticMetric<>("dup", "k1", _ -> 1.0d, StatisticExtremaMode.NONE),
                new StatisticMetric<>("dup", "k2", _ -> 2.0d, StatisticExtremaMode.MAX)
        )));
    }

}

