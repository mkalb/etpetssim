package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class FXDialogStylerTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    @Test
    void testApplyAppStyle() {
        FXTestSupport.runAndWait(() -> {
            Dialog<Void> dialog = new Dialog<>();
            Image icon = new WritableImage(1, 1);

            FXDialogStyler.applyAppStyle(dialog, List.of(icon));

            DialogPane dialogPane = dialog.getDialogPane();
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            assertAll(
                    () -> assertTrue(dialogPane.getStylesheets().stream().anyMatch(url -> url.endsWith("scene.css"))),
                    () -> assertTrue(dialogPane.getStyleClass().contains(FXStyleClasses.APP_DIALOG_PANE)),
                    () -> assertEquals(List.of(icon), stage.getIcons())
            );
        });
    }

}
