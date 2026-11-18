package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.engine.support.GridPatterns;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.shared.LabPattern;

/**
 * Patterns of highlighted cells of the Simulation Lab, built with {@link GridPatterns} and the transformations of
 * {@link GridPattern} and scaled to the grid size.
 *
 * <p>All patterns are normalized and designed for an anchor with an even column and row.
 */
final class LabPatterns {

    private static final int CROSS_LENGTH_MIN = 3;
    private static final int NESTED_RECTANGLE_COUNT = 4;
    private static final int NESTED_RECTANGLE_INSET = 2;
    private static final int QUADRANT_ARM_LENGTH_MIN = 2;
    private static final int QUADRANT_ARM_LENGTH_DIVISOR = 8;
    private static final int QUADRANT_GAP = 2;

    private LabPatterns() {
    }

    /**
     * Creates the given pattern of highlighted cells, scaled to the given grid size.
     *
     * <p>The pattern fits into the grid; on the smallest grids, a pattern may keep a minimum size and need the whole
     * grid.
     *
     * @param pattern the pattern to create
     * @param size    the size of the grid that the pattern is scaled to
     * @return a normalized, non-empty pattern of {@link LabEntity#HIGHLIGHTED} cells
     */
    static GridPattern<LabEntity> createPattern(LabPattern pattern, GridSize size) {
        int minSide = Math.min(size.width(), size.height());
        return switch (pattern) {
            case CROSS -> createCross(minSide);
            case RECTANGLE_OUTLINE -> GridPatterns.rectangleOutline(LabEntity.HIGHLIGHTED,
                    size.width() / 2, size.height() / 2);
            case NESTED_RECTANGLES -> createNestedRectangles(size);
            case ROTATIONS -> createRotations(minSide);
            case MIRRORS -> createMirrors(minSide);
        };
    }

    /**
     * Computes the anchor that places the pattern at the center of the grid.
     *
     * <p>The column and row of the anchor are rounded down to even values, so that the pattern keeps the parity it is
     * designed for; they are never negative.
     *
     * @param pattern the normalized pattern to place
     * @param size    the size of the grid
     * @return the anchor coordinate with an even column and row
     */
    static GridCoordinate computeAnchor(GridPattern<LabEntity> pattern, GridSize size) {
        return new GridCoordinate(computeEvenStart(size.width(), pattern.width()),
                computeEvenStart(size.height(), pattern.height()));
    }

    private static int computeEvenStart(int gridLength, int patternLength) {
        int start = Math.max(0, (gridLength - patternLength) / 2);
        return start - (start % 2);
    }

    private static GridPattern<LabEntity> createCross(int minSide) {
        int length = Math.max(CROSS_LENGTH_MIN, minSide / 2);
        if ((length % 2) == 0) {
            length--;
        }
        int center = length / 2;
        return GridPatterns.combine(
                GridPatterns.horizontalLine(LabEntity.HIGHLIGHTED, length).shifted(new GridOffset(0, center)),
                GridPatterns.verticalLine(LabEntity.HIGHLIGHTED, length).shifted(new GridOffset(center, 0)));
    }

    private static GridPattern<LabEntity> createNestedRectangles(GridSize size) {
        int outerWidth = (size.width() * 3) / 4;
        int outerHeight = (size.height() * 3) / 4;
        GridPattern<LabEntity> combined = GridPatterns.empty();
        for (int index = 0; index < NESTED_RECTANGLE_COUNT; index++) {
            int inset = index * NESTED_RECTANGLE_INSET;
            int width = outerWidth - (2 * inset);
            int height = outerHeight - (2 * inset);
            if ((width < 1) || (height < 1)) {
                break;
            }
            combined = GridPatterns.combine(combined,
                    GridPatterns.rectangleOutline(LabEntity.HIGHLIGHTED, width, height)
                            .shifted(new GridOffset(inset, inset)));
        }
        return combined;
    }

    private static GridPattern<LabEntity> createRotations(int minSide) {
        GridPattern<LabEntity> shape = createLShape(minSide);
        int step = computeQuadrantStep(minSide);
        return GridPatterns.combine(
                shape,
                shape.rotate90().shifted(new GridOffset(step, 0)),
                shape.rotate180().shifted(new GridOffset(step, step)),
                shape.rotate270().shifted(new GridOffset(0, step)));
    }

    private static GridPattern<LabEntity> createMirrors(int minSide) {
        GridPattern<LabEntity> shape = createLShape(minSide);
        int step = computeQuadrantStep(minSide);
        return GridPatterns.combine(
                shape,
                shape.flipX().shifted(new GridOffset(step, 0)),
                shape.flipY().shifted(new GridOffset(0, step)),
                shape.flipX().flipY().shifted(new GridOffset(step, step)));
    }

    private static GridPattern<LabEntity> createLShape(int minSide) {
        int armLength = computeQuadrantArmLength(minSide);
        return GridPatterns.combine(
                GridPatterns.verticalLine(LabEntity.HIGHLIGHTED, armLength),
                GridPatterns.horizontalLine(LabEntity.HIGHLIGHTED, (armLength / 2) + 1)
                        .shifted(new GridOffset(0, armLength - 1)));
    }

    private static int computeQuadrantArmLength(int minSide) {
        return Math.max(QUADRANT_ARM_LENGTH_MIN, minSide / QUADRANT_ARM_LENGTH_DIVISOR);
    }

    private static int computeQuadrantStep(int minSide) {
        int step = computeQuadrantArmLength(minSide) + QUADRANT_GAP;
        return step + (step % 2);
    }

}
