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

public final class SnakeConfigView
        extends AbstractConfigView<SnakeConfig, SnakeConfigViewModel> {

    // Initialization
    static final String SNAKE_CONFIG_INITIAL_FOOD_CELLS = "snake.config.initialfoodcells";
    static final String SNAKE_CONFIG_INITIAL_FOOD_CELLS_TOOLTIP = "snake.config.initialfoodcells.tooltip";
    static final String SNAKE_CONFIG_INITIAL_SNAKES = "snake.config.initialsnakes";
    static final String SNAKE_CONFIG_INITIAL_SNAKES_TOOLTIP = "snake.config.initialsnakes.tooltip";
    static final String SNAKE_CONFIG_INITIAL_PENDING_GROWTH = "snake.config.initialpendinggrowth";
    static final String SNAKE_CONFIG_INITIAL_PENDING_GROWTH_TOOLTIP = "snake.config.initialpendinggrowth.tooltip";

    // Rules
    static final String SNAKE_CONFIG_DEATH_MODE_TOOLTIP = "snake.config.deathmode.tooltip";
    static final String SNAKE_CONFIG_GROWTH_PER_FOOD = "snake.config.growthperfood";
    static final String SNAKE_CONFIG_GROWTH_PER_FOOD_TOOLTIP = "snake.config.growthperfood.tooltip";

    public SnakeConfigView(SnakeConfigViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildConfigRegion() {
        TitledPane structurePane = createStructurePane(true);
        TitledPane layoutPane = createLayoutPane(true);

        // --- Initialization Group ---
        var seedControl = FXComponentFactory.createLabeledStringTextBox(
                viewModel.seedProperty().stringProperty(),
                viewModel.seedProperty().labelProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED_PROMPT),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED_TOOLTIP),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_SEED_CLEAR_TOOLTIP),
                FXStyleClasses.CONFIG_TEXTBOX
        );
        var initialFoodCellsControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.initialFoodCellsProperty(),
                AppLocalization.getText(SNAKE_CONFIG_INITIAL_FOOD_CELLS),
                AppLocalization.getFormattedText(SNAKE_CONFIG_INITIAL_FOOD_CELLS_TOOLTIP, viewModel.initialFoodCellsProperty().min(), viewModel.initialFoodCellsProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );
        var initialSnakesControl = FXComponentFactory.createLabeledIntSpinner(
                viewModel.initialSnakesProperty(),
                AppLocalization.getText(SNAKE_CONFIG_INITIAL_SNAKES),
                AppLocalization.getFormattedText(SNAKE_CONFIG_INITIAL_SNAKES_TOOLTIP, viewModel.initialSnakesProperty().min(), viewModel.initialSnakesProperty().max()),
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
                seedControl, initialFoodCellsControl, initialSnakesControl, initialPendingGrowthControl);

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

        TitledPane rulesPane = createConfigTitledPane(AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_RULES),
                true, deathModeControl, growthPerFoodControl);

        return createConfigMainBox(structurePane, layoutPane, initPane, rulesPane);
    }

}