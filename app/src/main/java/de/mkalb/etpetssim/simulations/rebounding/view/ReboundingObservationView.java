package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingMovingEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class ReboundingObservationView
        extends
        AbstractObservationView<ReboundingStatistics, DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics>> {

    static final String REBOUNDING_OBSERVATION_TOTAL_CELLS = "rebounding.observation.cells.total";
    static final String REBOUNDING_OBSERVATION_WALL_CELLS = "rebounding.observation.cells.wall";
    static final String REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS = "rebounding.observation.cells.movingentity";
    static final String REBOUNDING_OBSERVATION_DIRECTION = "rebounding.observation.direction";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label wallCellsLabel = new Label();
    private final Label movingEntityCellsLabel = new Label();
    private final Label directionLabel = new Label();

    private @Nullable NumberFormat intFormat;

    public ReboundingObservationView(DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics> viewModel) {
        super(viewModel);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<ReboundingEntity> gridCell) {
        if ((gridCell != null) && (gridCell.entity() instanceof ReboundingMovingEntity entity)) {
            directionLabel.setText(entity.getDirection().arrow());
        } else {
            directionLabel.setText("");
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

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ReboundingStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            stepCountLabel.setText(intFormat.format(statistics.get().getStepCount()));
            totalCellsLabel.setText(intFormat.format(statistics.get().getTotalCells()));
            wallCellsLabel.setText(intFormat.format(statistics.get().getWallCells()));
            movingEntityCellsLabel.setText(intFormat.format(statistics.get().getMovingEntityCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            wallCellsLabel.setText(valueUnknown);
            movingEntityCellsLabel.setText(valueUnknown);
        }
    }

}