package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonConfigViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractConfigView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

public final class LangtonConfigView
        extends AbstractConfigView<LangtonConfig, LangtonConfigViewModel> {

    public LangtonConfigView(LangtonConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        return createConfigMainBox(structurePane, layoutPane);
    }

}