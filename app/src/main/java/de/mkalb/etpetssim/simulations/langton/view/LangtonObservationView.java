package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LangtonObservationView
        extends
        AbstractObservationView<LangtonStatistics, DefaultObservationViewModel<LangtonEntity, LangtonStatistics>> {

    private static final String LANGTON_OBSERVATION_ANT_CELLS = "langton.observation.cells.ant";
    private static final String LANGTON_OBSERVATION_VISITED_CELLS = "langton.observation.cells.visited";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label antCellsLabel = new Label();
    private final Label visitedCellsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label cellTypeLabel = new Label();
    private @Nullable VBox selectedCellSection;

    public LangtonObservationView(DefaultObservationViewModel<LangtonEntity, LangtonStatistics> viewModel,
                                  GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<LangtonEntity> gridCell) {
        updateSelectedCellSectionVisibility(selectedCellSection, gridCell != null);

        updateSelectedCellBasicLabels(coordinateLabel, cellTypeLabel, gridCell);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_STATUS,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_STEP
                },
                new Label[]{
                        stepCountLabel
                }
        );
        Region gridSection = createGridSection(totalCellsLabel);
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
        selectedCellSection = createSelectedCellSection(coordinateLabel, cellTypeLabel);
        updateSelectedGridCell(viewModel.selectedGridCellProperty().get());

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

        if (statistics.isPresent()) {
            LangtonStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(antCellsLabel, current.getAntCells());
            setFormattedIntegerValue(visitedCellsLabel, current.getVisitedCells());
        } else {
            setUnknownValues(stepCountLabel, antCellsLabel, visitedCellsLabel);
        }
        updateGridSectionLabel(totalCellsLabel);
    }

}