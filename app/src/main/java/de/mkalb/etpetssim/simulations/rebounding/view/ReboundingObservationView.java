package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ReboundingObservationView
        extends
        AbstractObservationView<ReboundingStatistics, DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics>> {

    private static final String REBOUNDING_OBSERVATION_TOTAL_CELLS = "rebounding.observation.cells.total";
    private static final String REBOUNDING_OBSERVATION_WALL_CELLS = "rebounding.observation.cells.wall";
    private static final String REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS = "rebounding.observation.cells.movingentity";
    private static final String REBOUNDING_OBSERVATION_DIRECTION = "rebounding.observation.direction";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label wallCellsLabel = new Label();
    private final Label movingEntityCellsLabel = new Label();
    private final Label directionLabel = new Label();

    public ReboundingObservationView(DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics> viewModel) {
        super(viewModel);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<ReboundingEntity> gridCell) {
        if ((gridCell != null) && (gridCell.entity() instanceof Rebounder entity)) {
            directionLabel.setText(entity.getDirection().arrow());
        } else {
            clearValues(directionLabel);
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                REBOUNDING_OBSERVATION_TOTAL_CELLS,
                REBOUNDING_OBSERVATION_WALL_CELLS,
                REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS,
                REBOUNDING_OBSERVATION_DIRECTION
        };
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                wallCellsLabel,
                movingEntityCellsLabel,
                directionLabel
        };

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ReboundingStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            ReboundingStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(totalCellsLabel, current.getTotalCells());
            setFormattedIntegerValue(wallCellsLabel, current.getWallCells());
            setFormattedIntegerValue(movingEntityCellsLabel, current.getMovingEntityCells());
        } else {
            setUnknownValues(stepCountLabel, totalCellsLabel, wallCellsLabel, movingEntityCellsLabel);
        }
    }

}