package de.mkalb.etpetssim.engine;

/**
 * Represents the size of a grid in the simulation.
 * The grid size is defined by its width and height, both of which must be even numbers.
 * The minimum size is 16x16 and the maximum size is 16384x16384.
 *
 * @param width the width of the grid
 * @param height the height of the grid
 */
public record GridSize(int width, int height) {

    public static final int MIN_SIZE = 16;
    public static final int MAX_SIZE = MIN_SIZE * 1_024; // 16 * 1024 = 16384

    public static final GridSize SMALL_SQUARE = square(MIN_SIZE * 2);
    public static final GridSize MEDIUM_SQUARE = square(MIN_SIZE * 4);
    public static final GridSize LARGE_SQUARE = square(MIN_SIZE * 8);

    public static final GridSize SMALL_RECTANGLE = new GridSize(MIN_SIZE * 4, MIN_SIZE * 2);
    public static final GridSize MEDIUM_RECTANGLE = new GridSize(MIN_SIZE * 8, MIN_SIZE * 4);
    public static final GridSize LARGE_RECTANGLE = new GridSize(MIN_SIZE * 16, MIN_SIZE * 8);

    /**
     * Creates a new GridSize instance with the specified width and height.
     * Both width and height must be even numbers within the range of MIN_SIZE to MAX_SIZE.
     *
     * @param width the width of the grid
     * @param height the height of the grid
     * @throws java.lang.IllegalArgumentException if width or height is not even, or if they are outside the range of MIN_SIZE to MAX_SIZE
     */
    public GridSize {
        if (isIllegalSize(width)) {
            throw new IllegalArgumentException(String.format("Width must be an even number between %d and %d, but was: %d", MIN_SIZE, MAX_SIZE, width));
        }
        if (isIllegalSize(height)) {
            throw new IllegalArgumentException(String.format("Height must be an even number between %d and %d, but was: %d", MIN_SIZE, MAX_SIZE, height));
        }
    }

    /**
     * Checks if the given size is an illegal grid size.
     * The size must be even and within the range of MIN_SIZE to MAX_SIZE.
     *
     * @param size the size to check
     * @return true if the size is illegal, false otherwise
     */
    public static boolean isIllegalSize(int size) {
        return (size < MIN_SIZE) || (size > MAX_SIZE) || ((size % 2) != 0);
    }

    /**
     * Creates a square grid size with the given size for both width and height.
     * @param size the size of the square grid
     * @return a new GridSize instance representing a square grid
     * @throws java.lang.IllegalArgumentException if size is not even or is outside the range of MIN_SIZE to MAX_SIZE
     */
    public static GridSize square(int size) {
        return new GridSize(size, size);
    }

    /**
     * Calculates the perimeter of the grid.
     *
     * @return the perimeter of the grid
     */
    public int getPerimeter() {
        return 2 * (width + height);
    }

    /**
     * Calculates the area of the grid.
     *
     * @return the area of the grid
     */
    public int getArea() {
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
     * Returns a string representation of the grid size in the format "[width x height]".
     * Example: [32x16]
     *
     * @return a string representation of the grid size
     */
    public String asString() {
        return String.format("[%dx%d]", width, height);
    }

}
