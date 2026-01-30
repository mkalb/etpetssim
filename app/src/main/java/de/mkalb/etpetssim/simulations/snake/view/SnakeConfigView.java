package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractConfigView;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.model.SnakeDeathMode;
import de.mkalb.etpetssim.simulations.snake.viewmodel.SnakeConfigViewModel;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;

@SuppressWarnings("SpellCheckingInspection")
public final class SnakeConfigView
        extends AbstractConfigView<SnakeConfig, SnakeConfigViewModel> {

    // Initialization
    private static final String SNAKE_CONFIG_VERTICAL_WALLS = "snake.config.verticalwalls";
    private static final String SNAKE_CONFIG_VERTICAL_WALLS_TOOLTIP = "snake.config.verticalwalls.tooltip";
    private static final String SNAKE_CONFIG_FOOD_CELLS = "snake.config.foodcells";
    private static final String SNAKE_CONFIG_FOOD_CELLS_TOOLTIP = "snake.config.foodcells.tooltip";
    private static final String SNAKE_CONFIG_SNAKES = "snake.config.snakes";
    private static final String SNAKE_CONFIG_SNAKES_TOOLTIP = "snake.config.snakes.tooltip";
    private static final String SNAKE_CONFIG_INITIAL_PENDING_GROWTH = "snake.config.initialpendinggrowth";
    private static final String SNAKE_CONFIG_INITIAL_PENDING_GROWTH_TOOLTIP = "snake.config.initialpendinggrowth.tooltip";

    // Rules
    private static final String SNAKE_CONFIG_DEATH_MODE_TOOLTIP = "snake.config.deathmode.tooltip";
    private static final String SNAKE_CONFIG_GROWTH_PER_FOOD = "snake.config.growthperfood";
    private static final String SNAKE_CONFIG_GROWTH_PER_FOOD_TOOLTIP = "snake.config.growthperfood.tooltip";
    private static final String SNAKE_CONFIG_BASE_POINTS_PER_FOOD = "snake.config.basepointsperfood";
    private static final String SNAKE_CONFIG_BASE_POINTS_PER_FOOD_TOOLTIP = "snake.config.basepointsperfood.tooltip";
    private static final String SNAKE_CONFIG_SEGMENT_LENGTH_MULTIPLIER = "snake.config.segmentlengthmultiplier";
    private static final String SNAKE_CONFIG_SEGMENT_LENGTH_MULTIPLIER_TOOLTIP = "snake.config.segmentlengthmultiplier.tooltip";

    public SnakeConfigView(SnakeConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        // --- Initialization Group ---
        var seedControl = createSeedControl();
        var verticalWallsControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.verticalWallsProperty(),
                AppLocalization.getText(SNAKE_CONFIG_VERTICAL_WALLS),
                AppLocalization.getFormattedText(SNAKE_CONFIG_VERTICAL_WALLS_TOOLTIP, viewModel.verticalWallsProperty().min(), viewModel.verticalWallsProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var foodCellsControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.foodCellsProperty(),
                AppLocalization.getText(SNAKE_CONFIG_FOOD_CELLS),
                AppLocalization.getFormattedText(SNAKE_CONFIG_FOOD_CELLS_TOOLTIP, viewModel.foodCellsProperty().min(), viewModel.foodCellsProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var snakesControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.snakesProperty(),
                AppLocalization.getText(SNAKE_CONFIG_SNAKES),
                AppLocalization.getFormattedText(SNAKE_CONFIG_SNAKES_TOOLTIP, viewModel.snakesProperty().min(), viewModel.snakesProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var initialPendingGrowthControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.initialPendingGrowthProperty(),
                AppLocalization.getText(SNAKE_CONFIG_INITIAL_PENDING_GROWTH),
                AppLocalization.getFormattedText(SNAKE_CONFIG_INITIAL_PENDING_GROWTH_TOOLTIP, viewModel.initialPendingGrowthProperty().min(), viewModel.initialPendingGrowthProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        TitledPane initPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_INITIALIZATION),
                true,
                seedControl,
                verticalWallsControl, foodCellsControl, snakesControl,
                initialPendingGrowthControl);

        // --- Rules Group ---
        var deathModeControl = FXComponentFactory.createLabeledEnumComboBox(
                viewModel.deathModeProperty(),
                viewModel.deathModeProperty().displayNameProvider(),
                AppLocalization.getText(SnakeDeathMode.labelResourceKey()),
                AppLocalization.getText(SNAKE_CONFIG_DEATH_MODE_TOOLTIP),
                FXStyleClasses.CONFIG_COMBOBOX
        );
        var growthPerFoodControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.growthPerFoodProperty(),
                AppLocalization.getText(SNAKE_CONFIG_GROWTH_PER_FOOD),
                AppLocalization.getFormattedText(SNAKE_CONFIG_GROWTH_PER_FOOD_TOOLTIP, viewModel.growthPerFoodProperty().min(), viewModel.growthPerFoodProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var basePointsPerFoodControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.basePointsPerFoodProperty(),
                AppLocalization.getText(SNAKE_CONFIG_BASE_POINTS_PER_FOOD),
                AppLocalization.getFormattedText(SNAKE_CONFIG_BASE_POINTS_PER_FOOD_TOOLTIP, viewModel.basePointsPerFoodProperty().min(), viewModel.basePointsPerFoodProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var segmentLengthMultiplierControl = FXComponentFactory.createLabeledDoubleSlider(
                viewModel.segmentLengthMultiplierProperty(),
                SnakeConfigViewModel.SEGMENT_LENGTH_MULTIPLIER_DECIMALS,
                AppLocalization.getText(SNAKE_CONFIG_SEGMENT_LENGTH_MULTIPLIER),
                AppLocalization.getText(SNAKE_CONFIG_SEGMENT_LENGTH_MULTIPLIER_TOOLTIP),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true,
                deathModeControl, growthPerFoodControl, basePointsPerFoodControl, segmentLengthMultiplierControl);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}