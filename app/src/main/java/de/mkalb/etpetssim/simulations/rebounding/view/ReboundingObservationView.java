package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ReboundingObservationView
        extends
        AbstractObservationView<ReboundingStatistics, DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics>> {

    private static final String REBOUNDING_OBSERVATION_DIRECTION = "rebounding.observation.direction";
    private static final String REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS = "rebounding.observation.cells.movingentity";
    private static final String REBOUNDING_OBSERVATION_WALL_CELLS = "rebounding.observation.cells.wall";

    private final Label wallCellsLabel = new Label();
    private final Label movingEntityCellsLabel = new Label();
    private final Label directionLabel = new Label();

    public ReboundingObservationView(DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics> viewModel,
                                     GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<ReboundingEntity> gridCell) {
        updateSelectedCellSectionVisibility(gridCell != null);

        if (gridCell != null) {
            updateSelectedCellBasicLabels(gridCell);
            if (gridCell.entity() instanceof Rebounder entity) {
                directionLabel.setText(entity.getDirection().arrow());
            } else {
                clearValues(directionLabel);
            }
        } else {
            updateSelectedCellBasicLabels(null);
            clearValues(directionLabel);
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region currentSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_CURRENT,
                new String[]{
                        REBOUNDING_OBSERVATION_WALL_CELLS,
                        REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS
                },
                new Label[]{
                        wallCellsLabel,
                        movingEntityCellsLabel
                }
        );
        VBox selectedCellSection = createExtendedSelectedCellSection(
                new String[]{
                        REBOUNDING_OBSERVATION_DIRECTION
                },
                new Label[]{
                        directionLabel
                }
        );

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
        Optional<ReboundingStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            ReboundingStatistics current = statistics.get();
            setFormattedIntegerValue(wallCellsLabel, current.getWallCells());
            setFormattedIntegerValue(movingEntityCellsLabel, current.getMovingEntityCells());
        } else {
            setUnknownValues(wallCellsLabel, movingEntityCellsLabel);
        }
    }

}