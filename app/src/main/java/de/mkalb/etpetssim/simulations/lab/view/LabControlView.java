package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabControlViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public final class LabControlView {

    private final LabControlViewModel viewModel;

    public LabControlView(LabControlViewModel viewModel) {
        this.viewModel = viewModel;
    }

    Region buildRegion() {
        Button drawButton = buildControlButton("draw", false);
        Button drawButtonModel = buildControlButton("draw model", true);
        Button drawButtonTest = buildControlButton("draw test", true);

        drawButton.setOnAction(_ -> viewModel.requestDraw());
        drawButtonModel.setOnAction(_ -> viewModel.requestDrawModel());
        drawButtonTest.setOnAction(_ -> viewModel.requestDrawTest());

        drawButtonModel.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.READY));
        drawButtonTest.disableProperty().bind(viewModel.simulationStateProperty().isEqualTo(SimulationState.READY));

        HBox hbox = new HBox();
        hbox.getChildren().addAll(drawButton, drawButtonModel, drawButtonTest);
        hbox.getStyleClass().add(FXStyleClasses.CONTROL_HBOX);

        return hbox;
    }

    private Button buildControlButton(String text, boolean disabled) {
        Button controlButton = new Button(text);
        controlButton.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);
        controlButton.setDisable(disabled);
        return controlButton;
    }

}
