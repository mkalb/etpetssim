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
 * Displays the application's About dialog.
 * <p>
 * The dialog presents build metadata, the README, the project license, and
 * third-party license information in separate tabs.
 */
public final class AboutDialog {

    private static final int DEFAULT_TEXT_AREA_COLUMNS = 120;
    private static final int DEFAULT_TEXT_AREA_ROWS = 30;

    private final List<Image> icons;
    private final Font monospacedFont;

    /**
     * Creates a new About dialog helper.
     *
     * @param icons window icons to apply to the dialog stage
     */
    public AboutDialog(List<Image> icons) {
        this.icons = icons;
        monospacedFont = Font.font("Monospaced", 12);
    }

    /**
     * Shows the About dialog.
     * <p>
     * The dialog contains tabs for version information, the README, the project
     * license, and third-party licenses.
     */
    public void showAboutDialog() {
        Tab tabManifest = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_VERSION),
                buildManifestSummary());
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
     * Creates a tab containing a read-only monospaced text area.
     *
     * @param tabTitle title shown on the tab
     * @param text text displayed inside the tab content area
     * @return configured tab instance
     */
    private Tab createTextAreaTab(String tabTitle, String text) {
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefColumnCount(DEFAULT_TEXT_AREA_COLUMNS);
        textArea.setPrefRowCount(DEFAULT_TEXT_AREA_ROWS);
        textArea.setFont(monospacedFont);

        Tab tab = new Tab(tabTitle);
        tab.setContent(textArea);
        tab.setClosable(false);

        return tab;
    }

    /**
     * Builds a textual summary of selected manifest attributes.
     *
     * @return formatted manifest summary text
     */
    private String buildManifestSummary() {
        Map<String, String> mf = readManifestInfo();
        String lineSeparator = System.lineSeparator();
        return AppLocalization.getFormattedText(
                AppLocalizationKeys.ABOUT_MANIFEST_TITLE,
                mf.getOrDefault("Implementation-Title", "")) + lineSeparator +
                AppLocalization.getFormattedText(
                        AppLocalizationKeys.ABOUT_MANIFEST_VERSION,
                        mf.getOrDefault("Implementation-Version", "")) + lineSeparator +
                AppLocalization.getFormattedText(
                        AppLocalizationKeys.ABOUT_MANIFEST_BUILD_TIMESTAMP,
                        mf.getOrDefault("Build-Timestamp", "")) + lineSeparator +
                AppLocalization.getFormattedText(
                        AppLocalizationKeys.ABOUT_MANIFEST_BUILD_JDK,
                        mf.getOrDefault("Build-Jdk", "")) + lineSeparator +
                AppLocalization.getFormattedText(
                        AppLocalizationKeys.ABOUT_MANIFEST_BUILT_BY,
                        mf.getOrDefault("Built-By", "")) + lineSeparator;
    }

    /**
     * Reads selected attributes from {@code META-INF/MANIFEST.MF}.
     *
     * @return map of manifest attribute names to values, or an empty map if unavailable
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
     * Loads a text resource using UTF-8.
     *
     * @param resourceRelativePath classpath-relative resource path
     * @return resource content, or a fallback message if the resource cannot be read
     */
    private String getResourceAsString(String resourceRelativePath) {
        return AppResources.getResourceAsString(resourceRelativePath, StandardCharsets.UTF_8)
                           .orElse(AppLocalization.getFormattedText(
                                   AppLocalizationKeys.ABOUT_RESOURCE_NOT_FOUND,
                                   resourceRelativePath));
    }

}
