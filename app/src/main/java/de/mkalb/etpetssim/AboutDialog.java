package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.*;

/**
 * Displays an "About" dialog for the Extraterrestrial Pets Simulation application.
 * Shows version, readme, license, and third-party license information in tabs.
 */
public final class AboutDialog {

    private final List<Image> icons;
    private final Font monospaced;

    /**
     * Constructs an AboutDialog with the specified application icons.
     *
     * @param icons the list of images to use as window icons
     */
    public AboutDialog(List<Image> icons) {
        this.icons = icons;
        monospaced = Font.font("Monospaced", 12);
    }

    /**
     * Shows the About dialog with application information.
     * The dialog contains tabs for version, readme, license, and third-party licenses.
     */
    public void showAboutDialog() {
        Tab tabManifest = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_VERSION),
                buildManifestContent());
        Tab tabReadme = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_README),
                getResourceAsString("README.md"));
        Tab tabLicense = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_LICENSE),
                getResourceAsString("LICENSE"));
        Tab tabThirdParty = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_THIRD_PARTY_LICENSES),
                getResourceAsString("THIRD-PARTY-LICENSES"));

        TabPane tabPane = new TabPane(tabManifest, tabReadme, tabLicense, tabThirdParty);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String title = AppLocalization.getText(AppLocalizationKeys.ABOUT_TITLE);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.getDialogPane().setContent(tabPane);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (!icons.isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons);
        }
        alert.showAndWait();
    }

    /**
     * Creates a tab containing a non-editable, monospaced text area with the given content.
     *
     * @param tabTitle the title of the tab
     * @param text the text to display in the tab
     * @return a Tab containing the text area
     */
    @SuppressWarnings("MagicNumber")
    private Tab createTextAreaTab(String tabTitle, String text) {
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefColumnCount(120);
        textArea.setPrefRowCount(30);
        textArea.setFont(monospaced);

        Tab tab = new Tab(tabTitle);
        tab.setContent(textArea);
        tab.setClosable(false);

        return tab;
    }

    /**
     * Builds a string containing manifest information such as title, version, build timestamp, JDK, and builder.
     *
     * @return a formatted string with manifest details
     */
    @SuppressWarnings("HardcodedLineSeparator")
    private String buildManifestContent() {
        Map<String, String> mf = readManifestInfo();
        return "Title: " + mf.getOrDefault("Implementation-Title", "") + "\n" +
                "Version: " + mf.getOrDefault("Implementation-Version", "") + "\n" +
                "Build Timestamp: " + mf.getOrDefault("Build-Timestamp", "") + "\n" +
                "Build JDK: " + mf.getOrDefault("Build-Jdk", "") + "\n" +
                "Built by: " + mf.getOrDefault("Built-By", "") + "\n";
    }

    /**
     * Reads manifest information from the application's MANIFEST.MF file.
     *
     * @return a map containing manifest attributes, or an empty map if not found or on error
     */
    private Map<String, String> readManifestInfo() {
        Optional<InputStream> resource = AppResources.getResourceAsStream("META-INF/MANIFEST.MF");
        if (resource.isEmpty()) {
            return Collections.emptyMap();
        }
        try (InputStream is = resource.get()) {
            Manifest manifest = new Manifest(is);
            Attributes attributes = manifest.getMainAttributes();
            Map<String, String> manifestInfo = HashMap.newHashMap(5);
            for (String k : List.of(
                    "Implementation-Title",
                    "Implementation-Version",
                    "Build-Timestamp",
                    "Built-By",
                    "Build-Jdk")) {
                String v = attributes.getValue(k);
                if ((v != null) && !v.isBlank()) {
                    manifestInfo.put(k, v);
                }
            }
            return manifestInfo;
        } catch (IOException e) {
            AppLogger.error("AboutDialog: Failed to read MANIFEST.MF", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Loads the content of a resource file as a string using UTF-8 encoding.
     *
     * @param resourceRelativePath the relative path to the resource
     * @return the content of the resource, or an error message if not found
     */
    private String getResourceAsString(String resourceRelativePath) {
        return AppResources.getResourceAsString(resourceRelativePath, StandardCharsets.UTF_8)
                           .orElse("Resource not found: %1$s".formatted(resourceRelativePath));
    }

}
