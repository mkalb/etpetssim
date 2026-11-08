package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.core.AppLogger;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Displays an alert for uncaught exceptions.
 *
 * <p>At most one alert is open at a time: further errors while it is open are counted in its content instead of
 * opening another alert. The methods must be called on the JavaFX Application Thread.
 */
public final class UnexpectedErrorDialog {

    private final List<Image> icons;
    private @Nullable Alert openAlert;
    private @Nullable Throwable shownThrowable;
    private int furtherErrorCount;

    /**
     * Creates an unexpected error dialog without an open alert.
     *
     * @param icons window icons to apply to the alert stage
     */
    public UnexpectedErrorDialog(List<Image> icons) {
        this.icons = icons;
    }

    /**
     * Formats the content text of the alert.
     *
     * @param throwable         uncaught exception shown in the alert
     * @param furtherErrorCount number of further errors since the alert opened
     * @return the exception message (or its simple class name if the message is {@code null} or blank), an empty
     * line, the log hint, and, if {@code furtherErrorCount} is positive, a line with the count
     */
    @SuppressWarnings("HardcodedLineSeparator")
    static String formatContent(Throwable throwable, int furtherErrorCount) {
        String message = throwable.getMessage();
        StringBuilder content = new StringBuilder(((message == null) || message.isBlank())
                ? throwable.getClass().getSimpleName()
                : message);
        content.append("\n\n").append(AppLocalization.getText(AppLocalizationKeys.ALERT_UNEXPECTED_ERROR_LOG));
        if (furtherErrorCount > 0) {
            content.append('\n').append(AppLocalization.getFormattedText(
                    AppLocalizationKeys.ALERT_UNEXPECTED_ERROR_FURTHER, furtherErrorCount));
        }
        return content.toString();
    }

    /**
     * Handles an uncaught exception.
     *
     * <p>If an alert is already open, the further error is counted and the content of the open alert is updated; its
     * window is resized so that the added counter line is not truncated. Otherwise, a new alert is shown, and this
     * method waits until it is closed.
     *
     * @param throwable uncaught exception
     */
    public void handleUncaughtException(Throwable throwable) {
        Alert currentAlert = openAlert;
        Throwable currentThrowable = shownThrowable;
        if ((currentAlert != null) && (currentThrowable != null)) {
            furtherErrorCount++;
            try {
                currentAlert.setContentText(formatContent(currentThrowable, furtherErrorCount));
                currentAlert.getDialogPane().getScene().getWindow().sizeToScene();
            } catch (RuntimeException e) {
                AppLogger.error(e, "UnexpectedErrorDialog: Failed to update alert");
            }
        } else {
            showAlert(throwable);
        }
    }

    /**
     * Shows an alert for an uncaught exception and waits until it is closed.
     *
     * <p>The alert and its exception are kept until it is closed, so that further errors can update it. A failure
     * to create or show the alert is only logged, so that it cannot trigger another alert.
     *
     * @param throwable uncaught exception shown in the alert
     */
    private void showAlert(Throwable throwable) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            FXDialogStyler.applyAppStyle(alert, icons);
            alert.setTitle(AppLocalization.getFormattedText(AppLocalizationKeys.WINDOW_TITLE,
                    AppLocalization.getText(AppLocalizationKeys.ALERT_UNEXPECTED_ERROR_TITLE)));
            alert.setHeaderText(AppLocalization.getText(AppLocalizationKeys.ALERT_UNEXPECTED_ERROR_HEADER));
            alert.setGraphic(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            openAlert = alert;
            shownThrowable = throwable;
            alert.setContentText(formatContent(throwable, 0));
            alert.showAndWait();
        } catch (RuntimeException e) {
            AppLogger.error(e, "UnexpectedErrorDialog: Failed to show alert");
        } finally {
            openAlert = null;
            shownThrowable = null;
            furtherErrorCount = 0;
        }
    }

}
