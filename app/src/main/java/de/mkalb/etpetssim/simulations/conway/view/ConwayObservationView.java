package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.*;

public final class ConwayObservationView
        extends AbstractObservationView<ConwayStatistics, DefaultObservationViewModel<ConwayEntity, ConwayStatistics>> {

    private static final String CONWAY_OBSERVATION_TOTAL_CELLS = "conway.observation.cells.total";
    private static final String CONWAY_OBSERVATION_MAX_ALIVE_CELLS = "conway.observation.cells.maxalive";
    private static final String CONWAY_OBSERVATION_ALIVE_CELLS = "conway.observation.cells.alive";
    private static final String CONWAY_OBSERVATION_DEAD_CELLS = "conway.observation.cells.dead";
    private static final String CONWAY_OBSERVATION_CHANGED_CELLS = "conway.observation.cells.changed";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label maxAliveCellsLabel = new Label();
    private final Label aliveCellsLabel = new Label();
    private final Label deadCellsLabel = new Label();
    private final Label changedCellsLabel = new Label();

    public ConwayObservationView(DefaultObservationViewModel<ConwayEntity, ConwayStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                CONWAY_OBSERVATION_TOTAL_CELLS,
                CONWAY_OBSERVATION_MAX_ALIVE_CELLS,
                CONWAY_OBSERVATION_ALIVE_CELLS,
                CONWAY_OBSERVATION_DEAD_CELLS,
                CONWAY_OBSERVATION_CHANGED_CELLS};
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                maxAliveCellsLabel,
                aliveCellsLabel,
                deadCellsLabel,
                changedCellsLabel};

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ConwayStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            ConwayStatistics current = statistics.get();
            stepCountLabel.setText(integerFormat().format(current.getStepCount()));
            totalCellsLabel.setText(integerFormat().format(current.getTotalCells()));
            maxAliveCellsLabel.setText(integerFormat().format(current.getMaxAliveCells()));
            aliveCellsLabel.setText(integerFormat().format(current.getAliveCells()));
            deadCellsLabel.setText(integerFormat().format(current.getDeadCells()));
            changedCellsLabel.setText(integerFormat().format(current.getChangedCells()));
        } else {
            setUnknownValues(stepCountLabel, totalCellsLabel, maxAliveCellsLabel, aliveCellsLabel, deadCellsLabel, changedCellsLabel);
        }
    }

}