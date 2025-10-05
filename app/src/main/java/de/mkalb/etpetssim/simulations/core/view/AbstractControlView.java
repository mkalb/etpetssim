package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationControlViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.*;

public abstract class AbstractControlView<VM extends SimulationControlViewModel>
        implements SimulationControlView {

    protected final VM viewModel;

    protected AbstractControlView(VM viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public Region buildControlRegion() {
        HBox controlHBox = new HBox();
        controlHBox.getStyleClass().add(FXStyleClasses.CONTROL_HBOX);

        // Button section (always present)
        Pane buttonPane = createControlButtonPane();
        controlHBox.getChildren().add(buttonPane);

        // Config section (optional)
        createControlConfigPane().ifPresent(configRegion -> controlHBox.getChildren().add(configRegion));

        // Spacer before observation section (if present)
        Optional<Pane> observationRegion = createControlObservationPane();
        if (observationRegion.isPresent()) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            controlHBox.getChildren().add(spacer);
            controlHBox.getChildren().add(observationRegion.get());
        }

        ScrollPane controlScrollPane = new ScrollPane();
        controlScrollPane.getStyleClass().add(FXStyleClasses.CONTROL_SCROLLPANE);
        controlScrollPane.setContent(controlHBox);
        controlScrollPane.setFitToHeight(false);
        controlScrollPane.setFitToWidth(true);
        controlScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        controlScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        controlScrollPane.setPannable(false);

        return controlScrollPane;
    }

    protected abstract Pane createControlButtonPane();

    protected abstract Optional<Pane> createControlConfigPane();

    protected abstract Optional<Pane> createControlObservationPane();

    protected final Button createControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
        controlButton.setDisable(disabled);
        return controlButton;
    }

}
