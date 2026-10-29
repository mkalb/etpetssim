package de.mkalb.etpetssim.simulations.core.model;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class StatisticsExtremaTest {

    @Test
    void testRecord() {
        var first = new StatisticsExtremum(1.0d, 1);
        var second = new StatisticsExtremum(2.0d, 2);
        var minimumValues = new LinkedHashMap<String, StatisticsExtremum>();
        minimumValues.put("first", first);
        minimumValues.put("second", second);
        var maximumValues = new LinkedHashMap<String, StatisticsExtremum>();
        maximumValues.put("second", second);
        maximumValues.put("first", first);

        var extrema = new StatisticsExtrema(minimumValues, maximumValues);
        minimumValues.clear();
        maximumValues.clear();

        assertAll(
                () -> assertEquals(List.of("first", "second"), List.copyOf(extrema.minimumValues().keySet())),
                () -> assertEquals(List.of("second", "first"), List.copyOf(extrema.maximumValues().keySet())),
                () -> assertEquals(first, extrema.minimumValues().get("first")),
                () -> assertEquals(second, extrema.maximumValues().get("second")),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> extrema.minimumValues().put("other", first)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> extrema.maximumValues().put("other", second))
        );
    }

    @Test
    void testEmpty() {
        var extrema = StatisticsExtrema.empty();

        assertAll(
                () -> assertTrue(extrema.minimumValues().isEmpty()),
                () -> assertTrue(extrema.maximumValues().isEmpty())
        );
    }

}
