package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticsExtremaTrackerTest {

    private static StatisticsExtremaTracker createCountTracker() {
        return new StatisticsExtremaTracker(List.of(
                new StatisticsMetric<>("count", "count.key", _ -> 0.0d, StatisticsExtremaMode.MIN_AND_MAX)
        ));
    }

    @Test
    void testUpdateRespectsExtremaModes() {
        StatisticsExtremaTracker tracker = new StatisticsExtremaTracker(List.of(
                new StatisticsMetric<>("none", "none.key", _ -> 0.0d, StatisticsExtremaMode.NONE),
                new StatisticsMetric<>("min", "min.key", _ -> 0.0d, StatisticsExtremaMode.MIN),
                new StatisticsMetric<>("max", "max.key", _ -> 0.0d, StatisticsExtremaMode.MAX),
                new StatisticsMetric<>("both", "both.key", _ -> 0.0d, StatisticsExtremaMode.MIN_AND_MAX)
        ));

        tracker.recordSample(Map.of("none", 5.0d, "min", 5.0d, "max", 5.0d, "both", 5.0d), 1L);
        tracker.recordSample(Map.of("none", 1.0d, "min", 1.0d, "max", 9.0d, "both", 9.0d), 2L);

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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsExtremaTracker(List.of(
                new StatisticsMetric<>("dup", "k1", _ -> 1.0d, StatisticsExtremaMode.NONE),
                new StatisticsMetric<>("dup", "k2", _ -> 2.0d, StatisticsExtremaMode.MAX)
        )));
        assertTrue(exception.getMessage().contains("dup"));
    }

    @Test
    void testEqualValuesKeepFirstStepCount() {
        StatisticsExtremaTracker tracker = createCountTracker();

        tracker.recordSample(Map.of("count", 5.0d), 1L);
        tracker.recordSample(Map.of("count", 5.0d), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertEquals(1L, extrema.minimumValues().get("count").stepCount()),
                () -> assertEquals(1L, extrema.maximumValues().get("count").stepCount())
        );
    }

    @Test
    void testSnapshotIsUnaffectedByLaterUpdates() {
        StatisticsExtremaTracker tracker = createCountTracker();
        tracker.recordSample(Map.of("count", 5.0d), 1L);
        StatisticsExtrema snapshot = tracker.snapshot();

        tracker.recordSample(Map.of("count", 1.0d), 2L);
        tracker.recordSample(Map.of("count", 9.0d), 3L);

        assertAll(
                () -> assertEquals(new StatisticsExtremum(5.0d, 1L), snapshot.minimumValues().get("count")),
                () -> assertEquals(new StatisticsExtremum(5.0d, 1L), snapshot.maximumValues().get("count"))
        );
    }

    @Test
    void testUpdateSkipsNaNValues() {
        StatisticsExtremaTracker tracker = createCountTracker();

        tracker.recordSample(Map.of("count", 5.0d), 1L);
        tracker.recordSample(Map.of("count", Double.NaN), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertEquals(5.0d, extrema.minimumValues().get("count").value()),
                () -> assertEquals(1L, extrema.minimumValues().get("count").stepCount()),
                () -> assertEquals(5.0d, extrema.maximumValues().get("count").value()),
                () -> assertEquals(1L, extrema.maximumValues().get("count").stepCount())
        );
    }

    @Test
    void testUpdateSkipsPositiveInfinityValues() {
        StatisticsExtremaTracker tracker = createCountTracker();

        tracker.recordSample(Map.of("count", 3.0d), 1L);
        tracker.recordSample(Map.of("count", Double.POSITIVE_INFINITY), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertEquals(3.0d, extrema.minimumValues().get("count").value()),
                () -> assertEquals(1L, extrema.minimumValues().get("count").stepCount()),
                () -> assertEquals(3.0d, extrema.maximumValues().get("count").value()),
                () -> assertEquals(1L, extrema.maximumValues().get("count").stepCount())
        );
    }

    @Test
    void testUpdateSkipsNegativeInfinityValues() {
        StatisticsExtremaTracker tracker = createCountTracker();

        tracker.recordSample(Map.of("count", 7.0d), 1L);
        tracker.recordSample(Map.of("count", Double.NEGATIVE_INFINITY), 2L);

        var extrema = tracker.snapshot();
        assertAll(
                () -> assertEquals(7.0d, extrema.minimumValues().get("count").value()),
                () -> assertEquals(1L, extrema.minimumValues().get("count").stepCount()),
                () -> assertEquals(7.0d, extrema.maximumValues().get("count").value()),
                () -> assertEquals(1L, extrema.maximumValues().get("count").stepCount())
        );
    }

}

