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

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label wallCellsLabel = new Label();
    private final Label movingEntityCellsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label cellTypeLabel = new Label();
    private final Label directionLabel = new Label();
    private @Nullable VBox selectedCellSection;

    public ReboundingObservationView(DefaultObservationViewModel<ReboundingEntity, ReboundingStatistics> viewModel,
                                     GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<ReboundingEntity> gridCell) {
        updateSelectedCellSectionVisibility(selectedCellSection, gridCell != null);

        if (gridCell != null) {
            updateSelectedCellBasicLabels(coordinateLabel, cellTypeLabel, gridCell);
            if (gridCell.entity() instanceof Rebounder entity) {
                directionLabel.setText(entity.getDirection().arrow());
            } else {
                clearValues(directionLabel);
            }
        } else {
            updateSelectedCellBasicLabels(coordinateLabel, cellTypeLabel, null);
            clearValues(directionLabel);
        }
    }

    private void updateSelectedCellSectionVisibility(boolean visible) {
        if (selectedCellSection != null) {
            selectedCellSection.setManaged(visible);
            selectedCellSection.setVisible(visible);
        }
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
                        REBOUNDING_OBSERVATION_WALL_CELLS,
                        REBOUNDING_OBSERVATION_MOVING_ENTITY_CELLS
                },
                new Label[]{
                        wallCellsLabel,
                        movingEntityCellsLabel
                }
        );
        selectedCellSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_COORDINATE,
                        AppLocalizationKeys.OBSERVATION_CELL_TYPE,
                        REBOUNDING_OBSERVATION_DIRECTION
                },
                new Label[]{
                        coordinateLabel,
                        cellTypeLabel,
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

        if (statistics.isPresent()) {
            ReboundingStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(wallCellsLabel, current.getWallCells());
            setFormattedIntegerValue(movingEntityCellsLabel, current.getMovingEntityCells());
        } else {
            setUnknownValues(stepCountLabel, wallCellsLabel, movingEntityCellsLabel);
        }
        updateGridSectionLabel(totalCellsLabel);
    }

}