package de.mkalb.etpetssim.engine;

/**
 * Represents the logical size of a two-dimensional simulation grid.
 * <p>
 * The grid size is defined by its {@code width} (number of columns) and {@code height} (number of rows).
 * Both values must be even numbers within the valid range from {@value #MIN_SIZE} to {@value #MAX_SIZE}.
 * <p>
 * This record provides utility methods for size validation, area and perimeter calculation,
 * aspect ratio, orientation checks, and standard grid sizes.
 *
 * @param width  the number of columns (horizontal cells), must be even and within valid range
 * @param height the number of rows (vertical cells), must be even and within valid range
 *
 * @see #isInvalidSize(int)
 * @see #square(int)
 * @see #area()
 * @see #perimeter()
 * @see #aspectRatio()
 * @see #isSquare()
 * @see #isLandscape()
 * @see #isPortrait()
 */
public record GridSize(int width, int height) {

    /**
     * The minimum valid size for grid dimensions.
     * This is set to 8 (2^3), which is the smallest even number allowed.
     */
    public static final int MIN_SIZE = 8;
    /**
     * The maximum valid size for grid dimensions.
     * This is set to 16384 (2^14), which is the largest even number allowed.
     */
    public static final int MAX_SIZE = 16_384; // 2^14

    public static final GridSize SMALL_SQUARE = square(MIN_SIZE * 2);
    public static final GridSize MEDIUM_SQUARE = square(MIN_SIZE * 4);
    public static final GridSize LARGE_SQUARE = square(MIN_SIZE * 8);

    public static final GridSize SMALL_RECTANGLE = new GridSize(MIN_SIZE * 4, MIN_SIZE * 2);
    public static final GridSize MEDIUM_RECTANGLE = new GridSize(MIN_SIZE * 8, MIN_SIZE * 4);
    public static final GridSize LARGE_RECTANGLE = new GridSize(MIN_SIZE * 16, MIN_SIZE * 8);

    /**
     * Constructs a new {@code GridSize} with the specified width and height.
     * <p>
     * Both values must be even numbers within the valid range defined by {@link #MIN_SIZE} and {@link #MAX_SIZE}.
     *
     * @param width  the number of columns (horizontal cells)
     * @param height the number of rows (vertical cells)
     * @throws IllegalArgumentException if width or height is not even or out of bounds
     */
    public GridSize {
        if (isInvalidSize(width)) {
            throw new IllegalArgumentException(String.format("Width must be an even number between %d and %d, but was: %d", MIN_SIZE, MAX_SIZE, width));
        }
        if (isInvalidSize(height)) {
            throw new IllegalArgumentException(String.format("Height must be an even number between %d and %d, but was: %d", MIN_SIZE, MAX_SIZE, height));
        }
    }

    /**
     * Checks whether a given size value is invalid.
     * A valid size must be even and within the allowed range.
     *
     * @param size the size value to validate
     * @return {@code true} if the size is invalid, {@code false} otherwise
     */
    public static boolean isInvalidSize(int size) {
        return (size < MIN_SIZE) || (size > MAX_SIZE) || ((size % 2) != 0);
    }

    /**
     * Creates a square grid where width and height are equal to the given size.
     *
     * @param size the dimension of both width and height
     * @return a new {@code GridSize} representing a square grid
     * @throws IllegalArgumentException if the size is not even or out of bounds
     */
    public static GridSize square(int size) {
        return new GridSize(size, size);
    }

    /**
     * Calculates the perimeter of the grid.
     *
     * @return the perimeter of the grid
     */
    public int perimeter() {
        return 2 * (width + height);
    }

    /**
     * Calculates the area of the grid.
     *
     * @return the area of the grid
     */
    public int area() {
        return width * height;
    }

    /**
     * Checks if the grid is a square.
     *
     * @return true if the grid is a square (width equals height), false otherwise
     */
    public boolean isSquare() {
        return width == height;
    }

    /**
     * Checks if the grid is in landscape orientation.
     *
     * @return true if the grid is landscape (width greater than height), false otherwise
     */
    public boolean isLandscape() {
        return width > height;
    }

    /**
     * Checks if the grid is in portrait orientation.
     *
     * @return true if the grid is portrait (height greater than width), false otherwise
     */
    public boolean isPortrait() {
        return height > width;
    }

    /**
     * Computes the aspect ratio of the grid.
     * <p>
     * The aspect ratio is defined as {@code width / height}.
     * A square grid has an aspect ratio of 1.0.
     *
     * @return the aspect ratio of the grid as a double
     */
    public double aspectRatio() {
        return (double) width / height;
    }

    /**
     * Returns a string representation of the grid size in the format {@code [width x height]}.
     * Useful for debugging or display purposes.
     *
     * @return a formatted string representing the grid size
     */
    public String asString() {
        return String.format("[%dx%d]", width, height);
    }

}
