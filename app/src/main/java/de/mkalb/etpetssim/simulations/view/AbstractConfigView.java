package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.SimulationConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentBuilder;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class AbstractConfigView<C, T extends SimulationConfigViewModel<C>> implements SimulationConfigView {

    protected final T viewModel;

    protected AbstractConfigView(T viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public abstract Region buildConfigRegion();

    protected final Region createConfigMainBox(TitledPane... titledPanes) {
        HBox mainBox = new HBox(titledPanes);
        mainBox.getStyleClass().add(FXStyleClasses.CONFIG_HBOX);

        return mainBox;
    }

    @SafeVarargs
    protected final TitledPane createConfigTitledPane(String title,
                                                      FXComponentBuilder.LabeledControl<? extends Region>... content) {
        return createConfigTitledPane(title, true, content);
    }

    @SafeVarargs
    protected final TitledPane createConfigTitledPane(String title, boolean bindDisableToSimulationStateReady,
                                                      FXComponentBuilder.LabeledControl<? extends Region>... content) {
        VBox box = new VBox();
        for (FXComponentBuilder.LabeledControl<? extends Region> labeledControl : content) {
            box.getChildren().addAll(labeledControl.label(), labeledControl.controlRegion());
            if (bindDisableToSimulationStateReady) {
                labeledControl.controlRegion().disableProperty().bind(
                        viewModel.simulationStateProperty().isNotEqualTo(SimulationState.READY));
            }
        }
        box.getStyleClass().add(FXStyleClasses.CONFIG_VBOX);

        TitledPane pane = new TitledPane(title, box);
        pane.setCollapsible(content.length > 0);
        pane.setExpanded(content.length > 0);
        pane.setDisable(content.length == 0);
        pane.getStyleClass().add(FXStyleClasses.CONFIG_TITLEDPANE);
        return pane;
    }

}