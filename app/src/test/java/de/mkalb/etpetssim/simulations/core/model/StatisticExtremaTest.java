package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticExtremaTest {

    @Test
    void testConstructorDefensivelyCopiesMapsAndPreservesOrder() {
        var first = new StatisticExtremum(1.0d, 1L);
        var second = new StatisticExtremum(2.0d, 2L);
        var minimumValues = new LinkedHashMap<String, StatisticExtremum>();
        minimumValues.put("first", first);
        minimumValues.put("second", second);
        var maximumValues = new LinkedHashMap<String, StatisticExtremum>();
        maximumValues.put("second", second);
        maximumValues.put("first", first);

        var extrema = new StatisticExtrema(minimumValues, maximumValues);
        minimumValues.clear();
        maximumValues.clear();

        assertAll(
                () -> assertEquals(List.of("first", "second"), List.copyOf(extrema.minimumValues().keySet())),
                () -> assertEquals(List.of("second", "first"), List.copyOf(extrema.maximumValues().keySet())),
                () -> assertEquals(first, extrema.minimumValues().get("first")),
                () -> assertEquals(second, extrema.maximumValues().get("second"))
        );
    }

    @Test
    void testMinimumValuesIsUnmodifiable() {
        var extrema = new StatisticExtrema(Map.of("metric", new StatisticExtremum(1.0d, 1L)), Map.of());

        assertNotNull(assertThrows(
                UnsupportedOperationException.class,
                () -> extrema.minimumValues().put("other", new StatisticExtremum(2.0d, 2L))
        ));
    }

    @Test
    void testMaximumValuesIsUnmodifiable() {
        var extrema = new StatisticExtrema(Map.of(), Map.of("metric", new StatisticExtremum(1.0d, 1L)));

        assertNotNull(assertThrows(
                UnsupportedOperationException.class,
                () -> extrema.maximumValues().put("other", new StatisticExtremum(2.0d, 2L))
        ));
    }

    @Test
    void testEmptyHasNoValues() {
        var extrema = StatisticExtrema.empty();

        assertAll(
                () -> assertTrue(extrema.minimumValues().isEmpty()),
                () -> assertTrue(extrema.maximumValues().isEmpty())
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testConstructorRejectsNullMaps() {
        assertAll(
                () -> assertNotNull(assertThrows(
                        NullPointerException.class,
                        () -> new StatisticExtrema(null, Map.of())
                )),
                () -> assertNotNull(assertThrows(
                        NullPointerException.class,
                        () -> new StatisticExtrema(Map.of(), null)
                ))
        );
    }

}
