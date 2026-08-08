package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticExtremaTrackerTest {

    @Test
    void testUpdateRespectsExtremaModes() {
        StatisticExtremaTracker tracker = new StatisticExtremaTracker(List.of(
                new StatisticMetric<>("none", "none.key", _ -> 0.0d, StatisticExtremaMode.NONE),
                new StatisticMetric<>("min", "min.key", _ -> 0.0d, StatisticExtremaMode.MIN),
                new StatisticMetric<>("max", "max.key", _ -> 0.0d, StatisticExtremaMode.MAX),
                new StatisticMetric<>("both", "both.key", _ -> 0.0d, StatisticExtremaMode.MIN_AND_MAX)
        ));

        tracker.update(Map.of("none", 5.0d, "min", 5.0d, "max", 5.0d, "both", 5.0d), 1L);
        tracker.update(Map.of("none", 1.0d, "min", 1.0d, "max", 9.0d, "both", 9.0d), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertFalse(extrema.minimumValues().containsKey("none")),
                () -> assertFalse(extrema.maximumValues().containsKey("none")),
                () -> assertEquals(1.0d, extrema.minimumValues().get("min").value()),
                () -> assertEquals(2L, extrema.minimumValues().get("min").stepCount()),
                () -> assertFalse(extrema.maximumValues().containsKey("min")),
                () -> assertEquals(9.0d, extrema.maximumValues().get("max").value()),
                () -> assertEquals(2L, extrema.maximumValues().get("max").stepCount()),
                () -> assertFalse(extrema.minimumValues().containsKey("max")),
                () -> assertEquals(5.0d, extrema.minimumValues().get("both").value()),
                () -> assertEquals(1L, extrema.minimumValues().get("both").stepCount()),
                () -> assertEquals(9.0d, extrema.maximumValues().get("both").value()),
                () -> assertEquals(2L, extrema.maximumValues().get("both").stepCount())
        );
    }

    @Test
    void testConstructorRejectsDuplicateMetricKeys() {
        assertThrows(IllegalArgumentException.class, () -> new StatisticExtremaTracker(List.of(
                new StatisticMetric<>("dup", "k1", _ -> 1.0d, StatisticExtremaMode.NONE),
                new StatisticMetric<>("dup", "k2", _ -> 2.0d, StatisticExtremaMode.MAX)
        )));
    }

    @Test
    void testUpdateSkipsNaNValues() {
        StatisticExtremaTracker tracker = new StatisticExtremaTracker(List.of(
                new StatisticMetric<>("count", "count.key", _ -> 0.0d, StatisticExtremaMode.MIN_AND_MAX)
        ));

        // First update with a valid value to establish an extremum.
        tracker.update(Map.of("count", 5.0d), 1L);
        // Second update with NaN should be skipped; extrema should remain unchanged.
        tracker.update(Map.of("count", Double.NaN), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertEquals(5.0d, extrema.minimumValues().get("count").value()),
                () -> assertEquals(5.0d, extrema.maximumValues().get("count").value())
        );
    }

    @Test
    void testUpdateSkipsPositiveInfinityValues() {
        StatisticExtremaTracker tracker = new StatisticExtremaTracker(List.of(
                new StatisticMetric<>("count", "count.key", _ -> 0.0d, StatisticExtremaMode.MIN_AND_MAX)
        ));

        tracker.update(Map.of("count", 3.0d), 1L);
        tracker.update(Map.of("count", Double.POSITIVE_INFINITY), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertEquals(3.0d, extrema.minimumValues().get("count").value()),
                () -> assertEquals(3.0d, extrema.maximumValues().get("count").value())
        );
    }

    @Test
    void testUpdateSkipsNegativeInfinityValues() {
        StatisticExtremaTracker tracker = new StatisticExtremaTracker(List.of(
                new StatisticMetric<>("count", "count.key", _ -> 0.0d, StatisticExtremaMode.MIN_AND_MAX)
        ));

        tracker.update(Map.of("count", 7.0d), 1L);
        tracker.update(Map.of("count", Double.NEGATIVE_INFINITY), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertEquals(7.0d, extrema.minimumValues().get("count").value()),
                () -> assertEquals(7.0d, extrema.maximumValues().get("count").value())
        );
    }

}

