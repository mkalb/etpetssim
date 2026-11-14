package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.SimulationCategory;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Content of the start screen, which {@link EtpetssimApplication} shows below its header.
 *
 * <p>The start screen shows one card per {@link SimulationType} with a category, grouped under a heading per
 * {@link SimulationCategory} in a vertically scrolling pane. The groups follow the declaration order of
 * {@code SimulationCategory}, the cards of a group the declaration order of {@code SimulationType}. The class only
 * reports the selected simulation type to its callback and holds no state.
 */
final class StartScreen {

    private StartScreen() {
    }

    /**
     * Creates the region of the start screen.
     *
     * @param simulationSelection receives the simulation type of a pressed card
     * @return a new region of the start screen
     */
    static Region createRegion(Consumer<SimulationType> simulationSelection) {
        VBox contentBox = new VBox();
        contentBox.getStyleClass().add(FXStyleClasses.START_CONTENT_VBOX);

        for (SimulationCategory category : SimulationCategory.values()) {
            FlowPane cardPane = new FlowPane();
            cardPane.getStyleClass().add(FXStyleClasses.START_CARD_FLOWPANE);
            for (SimulationType type : SimulationType.values()) {
                if (type.category().equals(Optional.of(category))) {
                    cardPane.getChildren().add(createCard(type, simulationSelection));
                }
            }

            contentBox.getChildren().addAll(
                    FXComponentFactory.createLabel(AppLocalization.getText(category.resourceKey()),
                            FXStyleClasses.START_CATEGORY_LABEL),
                    cardPane);
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add(FXStyleClasses.START_SCROLLPANE);
        return scrollPane;
    }

    private static Button createCard(SimulationType type, Consumer<SimulationType> simulationSelection) {
        VBox cardContent = new VBox(FXComponentFactory.createLabel(type.title(), FXStyleClasses.START_CARD_TITLE_LABEL));
        type.subtitle()
                .ifPresent(subtitle -> cardContent.getChildren()
                        .add(FXComponentFactory.createLabel(subtitle, FXStyleClasses.START_CARD_SUBTITLE_LABEL)));

        Button card = new Button();
        card.setGraphic(cardContent);
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.setAccessibleText(type.title());
        card.getStyleClass().add(FXStyleClasses.START_CARD_BUTTON);
        card.setOnAction(_ -> simulationSelection.accept(type));
        return card;
    }

}
