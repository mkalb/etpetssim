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

import java.util.*;

public final class ConwayObservationView
        extends AbstractObservationView<
        ConwayEntity,
        GridCell<ConwayEntity>,
        ConwayStatistics,
        DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics>> {

    private static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    private static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";
    private static final String CONWAY_OBSERVATION_CHANGED_CELLS = "conway.observation.cells.changed";
    private static final String CONWAY_OBSERVATION_MAX_ALIVE_CELLS = "conway.observation.cells.maxalive";

    private final Label aliveCellsLabel = new Label();
    private final Label deadCellsLabel = new Label();
    private final Label changedCellsLabel = new Label();
    private final Label maxAliveCellsLabel = new Label();

    public ConwayObservationView(DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics> viewModel,
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
        Region selectedCellSection = createSelectedCellSection();
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

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
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(aliveCellsLabel, current.getAliveCells());
            setFormattedIntegerValue(deadCellsLabel, current.getDeadCells());
            setFormattedIntegerValue(changedCellsLabel, current.getChangedCells());
            setFormattedIntegerValue(maxAliveCellsLabel, current.getMaxAliveCells());
        } else {
            setUnknownValues(
                    aliveCellsLabel,
                    deadCellsLabel,
                    changedCellsLabel,
                    maxAliveCellsLabel);
        }
    }

}