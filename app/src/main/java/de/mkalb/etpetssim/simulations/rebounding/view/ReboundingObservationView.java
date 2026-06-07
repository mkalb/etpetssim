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
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ReboundingObservationView
        extends AbstractObservationView<
        ReboundingEntity,
        GridCell<ReboundingEntity>,
        ReboundingStatistics,
        DefaultObservationViewModel<ReboundingEntity, GridCell<ReboundingEntity>, ReboundingStatistics>> {

    private static final String REBOUNDING_OBSERVATION_WALL_CELLS = "rebounding.observation.cells.wall";
    private static final String REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS = "rebounding.observation.cells.movingentity";
    private static final String REBOUNDING_OBSERVATION_DIRECTION = "rebounding.observation.direction";

    private final Label wallCellsLabel = new Label();
    private final Label movingEntityCellsLabel = new Label();
    private final Label directionLabel = new Label();

    public ReboundingObservationView(DefaultObservationViewModel<ReboundingEntity, GridCell<ReboundingEntity>, ReboundingStatistics> viewModel,
                                     GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable GridCell<ReboundingEntity> gridCell) {
        super.onSelectedCellChanged(gridCell);
        setUnknownValues(directionLabel);

        if (gridCell != null) {
            if (gridCell.entity() instanceof Rebounder entity) {
                directionLabel.setText(entity.getDirection().arrow());
            }
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
        Region selectedCellSection = createExtendedSelectedCellSection(
                new String[]{
                        REBOUNDING_OBSERVATION_DIRECTION
                },
                new Label[]{
                        directionLabel
                }
        );
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
        Optional<ReboundingStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(wallCellsLabel, current.getWallCells());
            setFormattedIntegerValue(movingEntityCellsLabel, current.getMovingEntityCells());
        } else {
            setUnknownValues(
                    wallCellsLabel,
                    movingEntityCellsLabel);
        }
    }

}