package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayObservationView
        extends AbstractObservationView<ConwayStatistics, DefaultObservationViewModel<ConwayEntity, ConwayStatistics>> {

    private static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    private static final String CONWAY_OBSERVATION_CHANGED_CELLS = "conway.observation.cells.changed";
    private static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";
    private static final String CONWAY_OBSERVATION_MAX_ALIVE_CELLS = "conway.observation.cells.maxalive";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label maxAliveCellsLabel = new Label();
    private final Label aliveCellsLabel = new Label();
    private final Label deadCellsLabel = new Label();
    private final Label changedCellsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label cellTypeLabel = new Label();
    private @Nullable VBox selectedCellSection;

    public ConwayObservationView(DefaultObservationViewModel<ConwayEntity, ConwayStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<ConwayEntity> gridCell) {
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
                        CONWAY_OBSERVATION_ALIVE_CELLS,
                        CONWAY_OBSERVATION_DEAD_CELLS,
                        CONWAY_OBSERVATION_CHANGED_CELLS
                },
                new Label[]{
                        aliveCellsLabel,
                        deadCellsLabel,
                        changedCellsLabel
                }
        );
        Region statisticsSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_STATISTICS,
                new String[]{
                        CONWAY_OBSERVATION_MAX_ALIVE_CELLS
                },
                new Label[]{
                        maxAliveCellsLabel
                }
        );
        selectedCellSection = createSelectedCellSection(coordinateLabel, cellTypeLabel);
        updateSelectedGridCell(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                statisticsSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ConwayStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            ConwayStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(aliveCellsLabel, current.getAliveCells());
            setFormattedIntegerValue(deadCellsLabel, current.getDeadCells());
            setFormattedIntegerValue(changedCellsLabel, current.getChangedCells());
            setFormattedIntegerValue(maxAliveCellsLabel, current.getMaxAliveCells());
        } else {
            setUnknownValues(stepCountLabel, aliveCellsLabel, deadCellsLabel, changedCellsLabel, maxAliveCellsLabel);
        }
        updateGridSectionLabel(totalCellsLabel);
    }

}