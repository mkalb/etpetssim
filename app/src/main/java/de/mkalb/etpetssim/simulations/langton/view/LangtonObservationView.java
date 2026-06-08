package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.*;

public final class LangtonObservationView
        extends AbstractObservationView<
        LangtonEntity,
        LangtonCell,
        LangtonStatistics,
        DefaultObservationViewModel<LangtonEntity, LangtonCell, LangtonStatistics>> {

    private static final String LANGTON_OBSERVATION_ANT_CELLS = "langton.observation.cells.ant";
    private static final String LANGTON_OBSERVATION_VISITED_CELLS = "langton.observation.cells.visited";

    private final Label antCellsLabel = new Label();
    private final Label visitedCellsLabel = new Label();

    public LangtonObservationView(DefaultObservationViewModel<LangtonEntity, LangtonCell, LangtonStatistics> viewModel,
                                  GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region currentSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_CURRENT,
                new String[]{
                        LANGTON_OBSERVATION_ANT_CELLS,
                        LANGTON_OBSERVATION_VISITED_CELLS
                },
                new Label[]{
                        antCellsLabel,
                        visitedCellsLabel
                }
        );
        Region selectedCellSection = createSelectedCellSection();
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<LangtonStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(antCellsLabel, current.getAntCells());
            setFormattedIntegerValue(visitedCellsLabel, current.getVisitedCells());
        } else {
            setUnknownValues(
                    antCellsLabel,
                    visitedCellsLabel);
        }
    }

}
