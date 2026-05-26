package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;

import java.util.*;

/**
 * Manual analyzer for cell dimension calculations and stroke line width values.
 * Generates tabular output of computed cell dimensions and corresponding stroke line widths
 * for various edge lengths and cell shapes to support iterative optimization and comparison
 * of {@code GridGeometry} calculations and {@code CellDimension} derived values.
 */
@SuppressWarnings("MagicNumber")
public final class CellDimensionAnalyzer {

    private static final String HEADER_CELL_DIMENSIONS = "=== CELL DIMENSIONS ===";
    private static final String HEADER_STROKE_LINE_WIDTH = "=== STROKE LINE WIDTH ===";
    private static final String COLUMNS_DIMENSIONS = "edgeLength,    width,   height,    innerRadius, outerRadius";
    private static final String COLUMNS_STROKE = "edgeLength,      SQUARE,     HEXAGON,    TRIANGLE";

    private CellDimensionAnalyzer() {
    }

    static void main() {
        Locale.setDefault(Locale.ROOT);

        runCellDimensionSamples();
        runStrokeLineWidthSamples();
    }

    private static double computeStrokeLineWidth(double innerRadius) {
        if (innerRadius < 2.0d) {
            return 0.0d;
        }
        return Math.log(innerRadius);
    }

    private static void runCellDimensionSamples() {
        System.out.println(HEADER_CELL_DIMENSIONS);
        System.out.println();

        for (CellShape shape : CellShape.values()) {
            System.out.println("--- " + shape.name() + " ---");
            System.out.println(COLUMNS_DIMENSIONS);

            for (int edgeLength = 1; edgeLength < 20; edgeLength++) {
                CellDimension dimension = GridGeometry.computeCellDimension(edgeLength, shape);
                System.out.printf(Locale.ROOT,
                        "%10d, %8.3f, %8.3f, %12.3f, %11.3f%n",
                        edgeLength,
                        dimension.width(),
                        dimension.height(),
                        dimension.innerRadius(),
                        dimension.outerRadius());
            }

            System.out.println();
        }
    }

    private static void runStrokeLineWidthSamples() {
        System.out.println(HEADER_STROKE_LINE_WIDTH);
        System.out.println(COLUMNS_STROKE);

        for (int edgeLength = 1; edgeLength < 20; edgeLength++) {
            CellDimension square = GridGeometry.computeCellDimension(edgeLength, CellShape.SQUARE);
            CellDimension hexagon = GridGeometry.computeCellDimension(edgeLength, CellShape.HEXAGON);
            CellDimension triangle = GridGeometry.computeCellDimension(edgeLength, CellShape.TRIANGLE);

            double squareStroke = computeStrokeLineWidth(square.innerRadius());
            double hexagonStroke = computeStrokeLineWidth(hexagon.innerRadius());
            double triangleStroke = computeStrokeLineWidth(triangle.innerRadius());

            System.out.printf(Locale.ROOT,
                    "%10d, %12.3f, %12.3f, %12.3f%n",
                    edgeLength,
                    squareStroke,
                    hexagonStroke,
                    triangleStroke);
        }

        System.out.println();
    }

}

