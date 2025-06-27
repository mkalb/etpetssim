package de.mkalb.etpetssim.ui;

import javafx.scene.control.Label;

import java.util.*;

/**
 * A utility class for creating JavaFX UI components with specific styles.
 * This class provides methods to create commonly used UI components with predefined styles.
 *
 * It will grow during the development of the Extraterrestrial Pets Simulation application.
 */
public final class FXComponentBuilder {

    /**
     * Private constructor to prevent instantiation.
     */
    private FXComponentBuilder() {
    }

    /**
     * Creates a Label with the specified text and CSS class.
     * @param text the text to display in the label
     * @param cssClass the CSS class to apply to the label
     * @return a Label with the specified text and CSS class
     */
    public static Label createLabel(String text, String cssClass) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(cssClass);
        Label label = new Label(text);
        label.getStyleClass().add(cssClass);
        return label;
    }

}
