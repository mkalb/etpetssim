package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppResources;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

/**
 * Applies the application look to dialogs and alerts.
 *
 * <p>Dialogs have their own scene and therefore do not inherit the stylesheet of the primary stage.
 */
public final class FXDialogStyler {

    private FXDialogStyler() {
    }

    /**
     * Applies the application stylesheet, the dialog style class, and the window icons to a dialog.
     *
     * <p>Call it before the dialog is shown.
     *
     * @param dialog dialog or alert to style
     * @param icons  window icons to add to the dialog stage; may be empty
     */
    public static void applyAppStyle(Dialog<?> dialog, List<Image> icons) {
        DialogPane dialogPane = dialog.getDialogPane();
        AppResources.getCssUrl("scene.css").ifPresent(dialogPane.getStylesheets()::add);
        dialogPane.getStyleClass().add(FXStyleClasses.APP_DIALOG_PANE);
        if (!icons.isEmpty() && (dialogPane.getScene().getWindow() instanceof Stage stage)) {
            stage.getIcons().addAll(icons);
        }
    }

}
